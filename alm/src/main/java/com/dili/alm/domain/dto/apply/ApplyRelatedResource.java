package com.dili.alm.domain.dto.apply;

public class ApplyRelatedResource {

    private Long relatedUser;
    private Integer relatedNumber;
    private Integer relatedWorkTime;

    public Long getRelatedUser() {
        return relatedUser;
    }

    public void setRelatedUser(Long relatedUser) {
        this.relatedUser = relatedUser;
    }

    public Integer getRelatedNumber() {
        return relatedNumber;
    }

    public void setRelatedNumber(Integer relatedNumber) {
        this.relatedNumber = relatedNumber;
    }

    public Integer getRelatedWorkTime() {
        return relatedWorkTime;
    }

    public void setRelatedWorkTime(Integer relatedWorkTime) {
        this.relatedWorkTime = relatedWorkTime;
    }
}
