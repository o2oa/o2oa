package com.x.base.core.project.jaxrs.metrics;

public class MetricsTimerReport {

    private String targetClassName;

    private String targetContextName;

    private String targetContextCNName;

    private String dateTime;

    private Long count;

    private MetricsTimer mean_rate;

    private MetricsTimer m1_rate;

    private MetricsTimer m5_rate;

    private MetricsTimer m15_rate;

    private MetricsTimer min;

    private MetricsTimer max;

    private MetricsTimer mean;

    private MetricsTimer stddev;

    private MetricsTimer p50;

    private MetricsTimer p75;

    private MetricsTimer p95;

    private MetricsTimer p98;

    private MetricsTimer p99;

    private MetricsTimer p999;

    public String getTargetContextCNName() {
        return this.targetContextCNName;
    }

    public void setTargetContextCNName(final String targetContextCNName) {
        this.targetContextCNName = targetContextCNName;
    }

    public String getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(final String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTargetClassName() {
        return this.targetClassName;
    }

    public void setTargetClassName(final String targetClassName) {
        this.targetClassName = targetClassName;
    }

    public String getTargetContextName() {
        return this.targetContextName;
    }

    public void setTargetContextName(final String targetContextName) {
        this.targetContextName = targetContextName;
    }

    public Long getCount() {
        return this.count;
    }

    public void setCount(final Long count) {
        this.count = count;
    }

    public MetricsTimer getMean_rate() {
        return this.mean_rate;
    }

    public void setMean_rate(final MetricsTimer mean_rate) {
        this.mean_rate = mean_rate;
    }

    public MetricsTimer getM1_rate() {
        return this.m1_rate;
    }

    public void setM1_rate(final MetricsTimer m1_rate) {
        this.m1_rate = m1_rate;
    }

    public MetricsTimer getM5_rate() {
        return this.m5_rate;
    }

    public void setM5_rate(final MetricsTimer m5_rate) {
        this.m5_rate = m5_rate;
    }

    public MetricsTimer getM15_rate() {
        return this.m15_rate;
    }

    public void setM15_rate(final MetricsTimer m15_rate) {
        this.m15_rate = m15_rate;
    }

    public MetricsTimer getMin() {
        return this.min;
    }

    public void setMin(final MetricsTimer min) {
        this.min = min;
    }

    public MetricsTimer getMax() {
        return this.max;
    }

    public void setMax(final MetricsTimer max) {
        this.max = max;
    }

    public MetricsTimer getMean() {
        return this.mean;
    }

    public void setMean(final MetricsTimer mean) {
        this.mean = mean;
    }

    public MetricsTimer getStddev() {
        return this.stddev;
    }

    public void setStddev(final MetricsTimer stddev) {
        this.stddev = stddev;
    }

    public MetricsTimer getP50() {
        return this.p50;
    }

    public void setP50(final MetricsTimer p50) {
        this.p50 = p50;
    }

    public MetricsTimer getP75() {
        return this.p75;
    }

    public void setP75(final MetricsTimer p75) {
        this.p75 = p75;
    }

    public MetricsTimer getP95() {
        return this.p95;
    }

    public void setP95(final MetricsTimer p95) {
        this.p95 = p95;
    }

    public MetricsTimer getP98() {
        return this.p98;
    }

    public void setP98(final MetricsTimer p98) {
        this.p98 = p98;
    }

    public MetricsTimer getP99() {
        return this.p99;
    }

    public void setP99(final MetricsTimer p99) {
        this.p99 = p99;
    }

    public MetricsTimer getP999() {
        return this.p999;
    }

    public void setP999(final MetricsTimer p999) {
        this.p999 = p999;
    }
}
