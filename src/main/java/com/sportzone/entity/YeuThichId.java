package com.sportzone.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class YeuThichId implements Serializable {

    private Integer maND;
    private Integer maSP;

    public YeuThichId() {}

    public YeuThichId(Integer maND, Integer maSP) {
        this.maND = maND;
        this.maSP = maSP;
    }

    public Integer getMaND() {
        return maND;
    }

    public void setMaND(Integer maND) {
        this.maND = maND;
    }

    public Integer getMaSP() {
        return maSP;
    }

    public void setMaSP(Integer maSP) {
        this.maSP = maSP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YeuThichId that)) return false;
        return Objects.equals(maND, that.maND) && Objects.equals(maSP, that.maSP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maND, maSP);
    }
}
