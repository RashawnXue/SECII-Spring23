package org.fffd.l23o6.pojo.entity;

import cn.dev33.satoken.annotation.SaCheckRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import org.fffd.l23o6.pojo.enum_.UserType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;


@Entity
@Table(name = "user1")  // table name "user" is a reserved word in H2
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String name;
    @NotNull
    private String phone;
    @NotNull
    private String type;
    @NotNull
    private UserType userType;
    @NotNull
    private String idn;
    @NotNull
    @Column(columnDefinition = "integer default 0")
    private Integer points;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
