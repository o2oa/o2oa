package com.x.processplatform.service.processing.processor.manual;

public class TaskIdentity {

    public static final String FIELD_IGNOREEMPOWER = "ignoreEmpower";

    private String identity;

    private String fromIdentity;

    private Boolean ignoreEmpower = false;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getFromIdentity() {
        return fromIdentity;
    }

    public void setFromIdentity(String fromIdentity) {
        this.fromIdentity = fromIdentity;
    }

    public Boolean getIgnoreEmpower() {
        return ignoreEmpower;
    }

    public void setIgnoreEmpower(Boolean ignoreEmpower) {
        this.ignoreEmpower = ignoreEmpower;
    }

}
