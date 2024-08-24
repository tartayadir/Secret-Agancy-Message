package com.tartayadir.cryptoservice.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoreEntity implements java.io.Serializable {
    @Id
    private Long id;

    private LocalDateTime createTime;

    public CoreEntity(CoreEntity that) {
        this.id = that.id;
        this.createTime = that.createTime;
    }
}