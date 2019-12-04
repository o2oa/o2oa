package com.x.base.core.project.jaxrs.metrics;

public class MetricsTimer {
    private String timerName;
    private double rate;
    private String unit;
    private String showMessage;

    public MetricsTimer() {
    }

    public MetricsTimer(final String timerName, final double rate, final String unit, String showMessage) {
        this.timerName = timerName;
        this.rate = rate;
        this.unit = unit;
        this.showMessage = showMessage;
    }

    public String getTimerName() {
        return this.timerName;
    }

    public void setTimerName(final String timerName) {
        this.timerName = timerName;
    }

    public double getRate() {
        return this.rate;
    }

    public void setRate(final double rate) {
        this.rate = rate;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }

    public String getShowMessage() {
        return this.showMessage;
    }

    public void setShowMessage(final String showMessage) {
        this.showMessage = showMessage;
    }
}
