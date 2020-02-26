package com.dili.alm.domain.dto.apply;

import java.io.Serializable;
import java.util.List;

public class ApplyMajorResourceMove implements Serializable {

    private String mainUser;
    private Integer mainNumber;
    private Integer mainWorkTime;

    List<ApplyRelatedResource> relatedResources;

    public List<ApplyRelatedResource> getRelatedResources() {
        return relatedResources;
    }

    public void setRelatedResources(List<ApplyRelatedResource> relatedResources) {
        this.relatedResources = relatedResources;
    }

    public String getMainUser() {
        return mainUser;
    }

    public void setMainUser(String mainUser) {
        this.mainUser = mainUser;
    }

    public Integer getMainNumber() {
        return mainNumber;
    }

    public void setMainNumber(Integer mainNumber) {
        this.mainNumber = mainNumber;
    }

    public Integer getMainWorkTime() {
        return mainWorkTime;
    }

    public void setMainWorkTime(Integer mainWorkTime) {
        this.mainWorkTime = mainWorkTime;
    }
}
