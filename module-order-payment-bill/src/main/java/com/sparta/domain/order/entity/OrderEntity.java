package com.sparta.domain.order.entity;

import com.sparta.common.entity.BaseEntity;
import com.sparta.domain.payment.entity.PaymentEntity;
import com.sparta.domain.product.entity.ProductEntity;
import com.sparta.domain.user.entity.UserEntity;
import com.sparta.utill.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private PaymentEntity payment;

    public void orderDelete() {
        this.delete();
    }
}
