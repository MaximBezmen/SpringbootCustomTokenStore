package com.spring.SpringbootCustomTokenStore.entites;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
public class JWTTokenStore extends AbstractEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "JWTTokenStore_id_seq")
    @SequenceGenerator(name = "JWTTokenStore_id_seq", sequenceName = "JWTTokenStore_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "token", length = 1000)
    private String token;
    @Column(name = "validate")
    private Boolean validate;
    @Column(name = "expiryDate")
    private Date expiryDate;
    @ManyToOne
    private Account account;

}
