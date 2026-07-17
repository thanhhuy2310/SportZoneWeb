package com.sportzone.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "SizeGiay")
public class SizeGiay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaSize")
    private Integer maSize;

    @Column(name = "TenSize")
    private String tenSize;

    public Integer getMaSize() {
        return maSize;
    }

    public void setMaSize(Integer maSize) {
        this.maSize = maSize;
    }

    public String getTenSize() {
        return tenSize;
    }

    public void setTenSize(String tenSize) {
        this.tenSize = tenSize;
    }
}
