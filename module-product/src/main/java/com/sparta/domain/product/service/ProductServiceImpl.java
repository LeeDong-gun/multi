package com.sparta.domain.product.service;

import static com.sparta.exception.common.ErrorCode.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.domain.game.entity.GameEntity;
import com.sparta.domain.game.repository.GameRepository;
import com.sparta.domain.product.document.ProductDocument;
import com.sparta.domain.product.dto.requestDto.ProductCreateRequestDto;
import com.sparta.domain.product.dto.requestDto.ProductUpdateRequestDto;
import com.sparta.domain.product.dto.responseDto.ProductCreateResponseDto;
import com.sparta.domain.product.dto.responseDto.ProductDeleteResponseDto;
import com.sparta.domain.product.dto.responseDto.ProductResponseDto;
import com.sparta.domain.product.dto.responseDto.ProductUpdateResponseDto;
import com.sparta.domain.product.entity.ProductEntity;
import com.sparta.domain.product.repository.ProductRepository;
import com.sparta.domain.product.repositoryES.ProductESRepository;
import com.sparta.domain.user.entity.UserEntity;
import com.sparta.domain.user.repository.UserRepository;
import com.sparta.exception.common.DuplicateException;
import com.sparta.exception.common.ErrorCode;
import com.sparta.exception.common.NotFoundException;
import com.sparta.utill.ProductStatus;
import com.sparta.utill.UserRole;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.MultiBucketBase;
import co.elastic.clients.elasticsearch._types.aggregations.SignificantStringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.SignificantStringTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import scala.collection.JavaConverters;
import scala.collection.Seq;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final GameRepository gameRepository;
	private final ProductESRepository productESRepository;
	private final ElasticsearchClient elasticsearchClient;
	private final RedissonClient redissonClient;

	/**
	 * 모든 활성화된 상품 정보를 조회합니다.
	 * @return 활성화된 상품들의 리스트(ProductResponseDto)
	 */
	@Override
	public List<ProductResponseDto> getAllProducts() {
		return productRepository.findAllByIsDeletedFalseAndStatus(ProductStatus.ACTIVE)
			.stream()
			.map(ProductResponseDto::new)
			.collect(Collectors.toList());
	}

	/**
	 * 특정 상품을 ID로 조회합니다.
	 * 소유주나 관리자일 경우 비활성화 상품도 조회 가능합니다.
	 * @param id 상품 ID
	 * @param userId 사용자 ID
	 * @return 조회된 상품의 상세 정보(ProductResponseDto)
	 */
	@Override
	public ProductResponseDto getProductById(Long id, Long userId) {
		ProductEntity product = productRepository.findByIdOrElseThrow(id);
		if (product.getIsDeleted()) {
			throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		if (isOwner(product, userId) || isAdmin(userId)) {
			return new ProductResponseDto(product);
		}

		if (product.getStatus() != ProductStatus.ACTIVE) {
			throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		return new ProductResponseDto(product);
	}

	/**
	 * 상품을 새로 등록합니다.
	 * @param userId 사용자 ID
	 * @param dto 상품 생성 요청 DTO
	 * @return 생성된 상품에 대한 응답(ProductCreateResponseDto)
	 */
	@Transactional
	@Override
	public ProductCreateResponseDto saveProduct(Long userId, ProductCreateRequestDto dto) {
		UserEntity user = userRepository.findByIdOrElseThrow(userId);
		GameEntity game = gameRepository.findByIdOrElseThrow(dto.getGameId());
		ProductEntity product = new ProductEntity(dto, user, game);
		ProductEntity savedProduct = productRepository.save(product);
		ProductDocument document = ProductDocument.fromEntity(savedProduct);
		productESRepository.save(document);
		return new ProductCreateResponseDto(document);
	}

	/**
	 * 기존 상품 정보를 업데이트합니다.
	 * 분산 락으로 중복 수정 방지를 처리합니다.
	 * @param id 상품 ID
	 * @param userId 사용자 ID
	 * @param requestDto 업데이트 요청 DTO
	 * @return 업데이트된 상품 정보(ProductUpdateResponseDto)
	 */
	@Transactional
	@Override
	public ProductUpdateResponseDto updateProduct(Long id, Long userId, ProductUpdateRequestDto requestDto) {
		RLock lock = redissonClient.getLock("stock_lock_" + id);
		UserEntity user = userRepository.findByIdOrElseThrow(userId);
		ProductEntity saveProduct = null;
		ProductDocument updatedDocument = null;
		try {
			boolean available = lock.tryLock(1, 10, TimeUnit.SECONDS);
			if (!available) {
				throw new RuntimeException("Lock acquisition failed");
			}
			ProductEntity product = getFindByIdWithLock(id);
			if (!isOwner(product, userId) && !isAdmin(userId)) {
				throw new DuplicateException(FORBIDDEN_ACCESS);
			}
			product.update(requestDto);
			saveProduct = productRepository.save(product);
			updatedDocument = ProductDocument.fromEntity(saveProduct);
			productESRepository.save(updatedDocument);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Thread interrupted during locking", e);
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
		return new ProductUpdateResponseDto(updatedDocument);
	}

	/**
	 * 상품을 삭제 처리합니다.
	 * 실제 데이터는 물리 삭제되지 않고 isDeleted 플래그를 변경합니다.
	 * @param id 상품 ID
	 * @param userId 사용자 ID
	 * @return 삭제 처리된 상품 정보(ProductDeleteResponseDto)
	 */
	@Transactional
	@Override
	public ProductDeleteResponseDto deleteProduct(Long id, Long userId) {
		ProductEntity product = productRepository.findByIdOrElseThrow(id);
		UserEntity user = userRepository.findByIdOrElseThrow(userId);
		if (product.getIsDeleted()) {
			throw new DuplicateException(PRODUCT_ISDELETED);
		}
		if (!product.getUser().getId().equals(user.getId()) && !user.getRole().equals(UserRole.ADMIN)) {
			throw new DuplicateException(FORBIDDEN_ACCESS);
		}

		ProductDocument document = ProductDocument.fromEntity(product);
		document.updateIsDeleted(true);
		productESRepository.save(document);
		return new ProductDeleteResponseDto(document);
	}

	/**
	 * Elasticsearch에서 상품을 ID로 조회합니다.
	 * 캐시를 적용하여 자주 조회되는 상품에 대해 성능을 향상시킵니다.
	 * @param id 상품 ID
	 * @return 조회된 상품의 Elasticsearch 문서(ProductDocument)
	 */
	@Cacheable(value = "product", key = "#productId")
	public ProductDocument getProductByIdES(Long id) {
		return productESRepository.findByProductIdAndIsDeletedFalseAndStatus(id, ProductStatus.ACTIVE);
	}

	/**
	 * Elasticsearch에서 활성화된 모든 상품을 조회합니다.
	 * 캐시를 적용하여 자주 조회되는 상품에 대해 성능을 향상시킵니다.
	 * @return 활성화된 상품의 Elasticsearch 문서 목록(List<ProductDocument>)
	 */
	@Cacheable(value = "product")
	public List<ProductDocument> getAllProductsES() {
		return productESRepository.findByIsDeletedFalseAndStatus(ProductStatus.ACTIVE);
	}

	/**
	 * Elasticsearch에서 상품명을 기준으로 검색합니다.
	 * @param productName 검색할 상품명
	 * @return 검색된 상품 문서 목록(List<ProductDocument>)
	 */
	public List<ProductDocument> searchByProductNameES(String productName) {
		SearchRequest request = SearchRequest.of(s -> s
			.index("product")
			.query(q -> q
				.matchPhrasePrefix(m -> m
					.field("productName")
					.query(productName)
				)
			)
		);

		SearchResponse<ProductDocument> response;
		try {
			response = elasticsearchClient.search(request, ProductDocument.class);
		} catch (IOException e) {
			throw new RuntimeException("Elasticsearch 검색 실패", e);
		}

		return response.hits().hits().stream()
			.map(Hit::source)
			.collect(Collectors.toList());
	}

	/**
	 * Elasticsearch에서 특정 게임 ID에 해당하는 활성화된 상품을 조회합니다.
	 * 캐시를 적용하여 성능을 향상시킵니다.
	 * @param gameId 게임 ID
	 * @return 해당 게임의 상품 문서 목록(List<ProductDocument>)
	 */
	@Cacheable(value = "product", key = "#gameId")
	public List<ProductDocument> searchByGameIdES(Long gameId) {
		return productESRepository.findByGameIdAndIsDeletedFalseAndStatus(gameId, ProductStatus.ACTIVE);
	}

	/**
	 * Elasticsearch에서 특정 상태의 상품을 조회합니다.
	 * 캐시를 적용하여 성능을 향상시킵니다.
	 * @param status 상품 상태
	 * @return 해당 상태의 상품 문서 목록(List<ProductDocument>)
	 */
	@Cacheable(value = "product", key = "#status")
	public List<ProductDocument> searchByStatusES(String status) {
		return productESRepository.findByStatusAndIsDeletedFalse(status, ProductStatus.ACTIVE);
	}

	/**
	 * Elasticsearch에서 특정 사용자가 등록한 상품을 조회합니다.
	 * 캐시를 적용하여 성능을 향상시킵니다.
	 * @param userId 사용자 ID
	 * @return 해당 사용자가 등록한 활성화된 상품 목록(List<ProductDocument>)
	 */
	@Cacheable(value = "product", key = "#userId")
	public List<ProductDocument> searchByUserIdES(Long userId) {
		return productESRepository.findByUserIdAndIsDeletedFalseAndStatus(userId, ProductStatus.ACTIVE);
	}

	/**
	 * Elasticsearch에서 장르별 상품 개수를 집계합니다.
	 * 캐시를 사용하여 자주 쓰이는 통계를 빠르게 조회합니다.
	 * @return 장르별 상품 개수를 담은 Map<String, Long>
	 */
	@Cacheable(value = "product_aggregations", key = "'genre_counts'")
	public Map<String, Long> getGenreAggregationsES() {
		try {
			SearchRequest request = SearchRequest.of(s -> s
				.index("product")
				.size(0)
				.requestCache(true)
				.aggregations("genre_counts", a -> a
					.terms(t -> t.field("gameGenre").size(10))
				)
			);

			SearchResponse<Void> response = elasticsearchClient.search(request, Void.class);

			StringTermsAggregate genreAggregation = response.aggregations()
				.get("genre_counts")
				.sterms();

			return genreAggregation.buckets().array().stream()
				.collect(Collectors.toMap(
					bucket -> bucket.key().stringValue(),
					MultiBucketBase::docCount
				));
		} catch (IOException e) {
			throw new RuntimeException("Error executing aggregation query", e);
		}
	}

	/**
	 * 특정 키워드를 기반으로 인기 상품 Top 10을 Elasticsearch에서 조회합니다.
	 * 의미 있는 키워드를 추출해 검색 가중치로 사용합니다.
	 * @return 인기 상품 상위 10개(List<ProductDocument>)
	 */
	@Override
	public List<ProductDocument> getTop10PopularProductsES() {
		Map<String, Double> keywords = extractImportantKeywords(10);
		BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
		for (Map.Entry<String, Double> entry : keywords.entrySet()) {
			String keyword = entry.getKey();
			float boost = entry.getValue().floatValue();
			boolQueryBuilder.should(q -> q
				.match(m -> m
					.field("contents")
					.query(keyword)
					.boost(boost)
				)
			);
		}
		SearchRequest searchRequest = new SearchRequest.Builder()
			.index("product")
			.query(q -> q.bool(boolQueryBuilder.build()))
			.from(0)
			.size(10)
			.build();
		try {
			SearchResponse<ProductDocument> searchResponse =
				elasticsearchClient.search(searchRequest, ProductDocument.class);
			return searchResponse.hits().hits().stream()
				.map(Hit::source)
				.collect(Collectors.toList());
		} catch (IOException e) {
			System.err.println("Elasticsearch 검색 중 오류 발생: " + e.getMessage());
			throw new RuntimeException("Elasticsearch 검색 실패", e);
		}
	}

	/**
	 * Elasticsearch를 이용해 중요 키워드를 추출합니다.
	 * @param topN 추출할 키워드 개수
	 * @return 키워드와 가중치를 담은 Map<String, Double>
	 */
	private Map<String, Double> extractImportantKeywords(int topN) {
		Aggregation significantTermsAgg = Aggregation.of(a -> a
			.significantTerms(st -> st
				.field("contents.keyword")
				.size(topN)
			)
		);

		SearchRequest searchRequest = new SearchRequest.Builder()
			.index("product")
			.query(q -> q.bool(b -> b
				.must(m -> m.matchAll(ma -> ma))
				.filter(f -> f.term(t -> t.field("status").value(ProductStatus.ACTIVE.name())))
				.filter(f -> f.term(t -> t.field("isDeleted").value(false)))
			))
			.aggregations("important_keywords", significantTermsAgg)
			.size(0)
			.build();

		try {
			SearchResponse<Void> searchResponse = elasticsearchClient.search(searchRequest, Void.class);
			Aggregate aggregate = searchResponse.aggregations().get("important_keywords");

			if (aggregate.isSigsterms()) {
				SignificantStringTermsAggregate significantTermsAggResult = aggregate.sigsterms();
				if (significantTermsAggResult == null) {
					throw new IllegalStateException("Significant terms aggregation result is null");
				}

				Map<String, Double> keywordMap = new LinkedHashMap<>();
				if (significantTermsAggResult.buckets() != null) {
					List<SignificantStringTermsBucket> buckets = significantTermsAggResult.buckets().array();
					for (SignificantStringTermsBucket bucket : buckets) {
						keywordMap.put(bucket.key(), bucket.score());
					}
				}
				return keywordMap;
			} else {
				throw new RuntimeException(
					"잘못된 Aggregation 타입: expected SignificantStringTermsAggregate but got " + aggregate.getClass()
						.getSimpleName());
			}
		} catch (IOException e) {
			System.err.println("Elasticsearch Aggregation 실행 중 오류 발생: " + e.getMessage());
			throw new RuntimeException("Elasticsearch Aggregation 실패", e);
		}
	}

	private boolean isAdmin(Long userId) {
		UserEntity user = userRepository.findByIdOrElseThrow(userId);
		return user.getRole().equals(UserRole.ADMIN);
	}

	private boolean isOwner(ProductEntity product, Long userId) {
		return product.getUser().getId().equals(userId);
	}

	@Transactional
	public ProductEntity getFindByIdWithLock(Long productId) {
		return productRepository.findByIdWithLock(productId)
			.orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
	}

	public ProductEntity findById(Long productId) {
		return productRepository.findByIdOrElseThrow(productId);
	}
}
