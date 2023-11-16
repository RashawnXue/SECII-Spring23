package org.fffd.l23o6.pojo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long trainId;

    @NotNull
    private Long departureStationId;

    @NotNull
    private Long arrivalStationId;

    @NotNull
    private OrderStatus status;

    @NotNull
    private String seat;

    @NotNull
    private BigDecimal price;

    @NotNull
    @Column(columnDefinition = "integer default 0")
    private Integer points;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
