package com.tartayadir.cryptoservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class CoreEntity implements java.io.Serializable {
    @Id
    @Column(updatable = false)
    private String id;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createTime;
}