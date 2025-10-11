package com.hoomoomoo.im.dto;

public class ParamRealtimeInterfaceDescDto {

    private String agreeName;

    private String agreeIndex;

    private String agreeContent;

    public ParamRealtimeInterfaceDescDto(String agreeName, String agreeIndex, String agreeContent) {
        this.agreeName = agreeName;
        this.agreeIndex = agreeIndex;
        this.agreeContent = agreeContent;
    }

    public String getAgreeName() {
        return agreeName;
    }

    public void setAgreeName(String agreeName) {
        this.agreeName = agreeName;
    }

    public String getAgreeIndex() {
        return agreeIndex;
    }

    public void setAgreeIndex(String agreeIndex) {
        this.agreeIndex = agreeIndex;
    }

    public String getAgreeContent() {
        return agreeContent;
    }

    public void setAgreeContent(String agreeContent) {
        this.agreeContent = agreeContent;
    }
}
