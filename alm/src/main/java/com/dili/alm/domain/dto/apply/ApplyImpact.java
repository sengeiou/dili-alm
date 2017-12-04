package com.dili.alm.domain.dto.apply;

public class ApplyImpact {

    /**
     * system : ALM
     * dep : 前台信息部12
     * memberId : 1
     * content : 1111
     * level : 重要
     */

    private String system;
    private String dep;
    private String memberId;
    private String content;
    private String level;

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
