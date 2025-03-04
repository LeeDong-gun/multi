package com.sparta.domain.community.repositoryES;

import com.sparta.domain.community.document.CommunityDocument;
import com.sparta.exception.common.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import static com.sparta.exception.common.ErrorCode.*;

@Repository
public interface CommunityESRepository
	extends ElasticsearchRepository<CommunityDocument, String> {
	Page<CommunityDocument> findByTitleAndIsDeletedFalse(String searchKeyword, Pageable pageable);

	default CommunityDocument findByIdOrElseThrow(String communityId) {
		return findById(communityId).orElseThrow(() -> new NotFoundException(COMMUNITY_NOT_FOUND));
	}
}