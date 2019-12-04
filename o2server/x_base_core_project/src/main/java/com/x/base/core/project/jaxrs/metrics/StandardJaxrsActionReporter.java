package com.x.base.core.project.jaxrs.metrics;

import com.codahale.metrics.Timer;
import com.codahale.metrics.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.XGsonBuilder;

import java.io.PrintStream;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StandardJaxrsActionReporter extends ScheduledReporter {
    private Gson gson = XGsonBuilder.instance();
    private static final int CONSOLE_WIDTH = 80;
    private final PrintStream output;
    private final Locale locale;
    private final Clock clock;
    private final DateFormat dateFormat;
    private Context context;

    public static StandardJaxrsActionReporter.Builder forRegistry( MetricRegistry registry ) {
        return new StandardJaxrsActionReporter.Builder(registry);
    }

    private StandardJaxrsActionReporter(MetricRegistry registry, PrintStream output, Locale locale, Clock clock, TimeZone timeZone, TimeUnit rateUnit, TimeUnit durationUnit, MetricFilter filter, ScheduledExecutorService executor, boolean shutdownExecutorOnStop, Set<MetricAttribute> disabledMetricAttributes, Context context) {
        super(registry, "jaxrs-action-reporter", filter, rateUnit, durationUnit, executor, shutdownExecutorOnStop, disabledMetricAttributes);
        this.context = context;
        this.output = output;
        this.locale = locale;
        this.clock = clock;
        this.dateFormat = DateFormat.getDateTimeInstance(3, 2, locale);
        this.dateFormat.setTimeZone(timeZone);
    }

    public void report( SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
        String nodeName = null;
        JsonElement jsonData = null;

        //将timers数据全部转为Json发送到center指定接口
        try {
            nodeName = Config.node();
            jsonData = gson.toJsonTree( getMetricsTimers(timers) );
            ActionResponse respone = CipherConnectionAction.put(false,
                    Config.url_x_program_center_jaxrs("center", "metrics" ) + "/" + nodeName, jsonData);
        } catch (Exception e) {
            System.out.println("metrics report to center server got an error!" + e.getMessage() );
        }
    }

    private Map<String , MetricsTimerReport> getMetricsTimers(SortedMap<String, Timer> timers ){
        String dateTime = this.dateFormat.format(new Date(this.clock.getTime()));
        Map<String, MetricsTimerReport> timerReportMap = new HashMap<>();
        MetricsTimerReport metricsTimerReport = null;
        if (!timers.isEmpty()) {
            Map.Entry entry;
            Iterator var7 = timers.entrySet().iterator();
            while(var7.hasNext()) {
                try{
                    entry = (Map.Entry)var7.next();
                    metricsTimerReport = getMetricsTimerReport((Timer)entry.getValue());
                    metricsTimerReport.setTargetContextName( context.servletContextName() );
                    metricsTimerReport.setTargetContextCNName( context.name() );
                    metricsTimerReport.setTargetClassName( (String)entry.getKey() );
                    metricsTimerReport.setDateTime( dateTime );
                    timerReportMap.put( (String)entry.getKey(), metricsTimerReport );
                }catch(Exception e ){
                    e.printStackTrace();
                }
            }
        }
        return timerReportMap;
    }

    private MetricsTimerReport getMetricsTimerReport(Timer timer) {
        Snapshot snapshot = timer.getSnapshot();

        MetricsTimerReport metricsTimerReport = new MetricsTimerReport();
        metricsTimerReport.setCount(timer.getCount());
        metricsTimerReport.setMean_rate(new MetricsTimer( MetricAttribute.MEAN_RATE.name(), this.convertRate(timer.getMeanRate()), this.getRateUnit(),
                        String.format(this.locale, "mean rate = %2.2f calls/%s", this.convertRate(timer.getMeanRate()), this.getRateUnit())));
        metricsTimerReport.setM1_rate(new MetricsTimer( MetricAttribute.M1_RATE.name(), this.convertRate(timer.getOneMinuteRate()), this.getRateUnit(),
                String.format(this.locale, "1-minute rate = %2.2f calls/%s", this.convertRate(timer.getOneMinuteRate()), this.getRateUnit())));
        metricsTimerReport.setM5_rate(new MetricsTimer( MetricAttribute.M5_RATE.name(), this.convertRate(timer.getFiveMinuteRate()), this.getRateUnit(),
                String.format(this.locale, "5-minute rate = %2.2f calls/%s", this.convertRate(timer.getFiveMinuteRate()), this.getRateUnit())));
        metricsTimerReport.setM15_rate(new MetricsTimer( MetricAttribute.M15_RATE.name(), this.convertRate(timer.getFifteenMinuteRate()), this.getRateUnit(),
                String.format(this.locale, "15-minute rate = %2.2f calls/%s", this.convertRate(timer.getFifteenMinuteRate()), this.getRateUnit())));
        metricsTimerReport.setMin(new MetricsTimer( MetricAttribute.MIN.name(), this.convertDuration((double)snapshot.getMin()), this.getDurationUnit(),
                String.format(this.locale, "min = %2.2f %s", this.convertDuration((double)snapshot.getMin()), this.getDurationUnit())));
        metricsTimerReport.setMax(new MetricsTimer( MetricAttribute.MAX.name(), this.convertDuration((double)snapshot.getMax()), this.getDurationUnit(),
                String.format(this.locale, "max = %2.2f %s", this.convertDuration((double)snapshot.getMax()), this.getDurationUnit())));
        metricsTimerReport.setMean(new MetricsTimer( MetricAttribute.MEAN.name(), this.convertDuration((double)snapshot.getMean()), this.getDurationUnit(),
                String.format(this.locale, "mean = %2.2f %s", this.convertDuration((double)snapshot.getMean()), this.getDurationUnit())));
        metricsTimerReport.setStddev(new MetricsTimer( MetricAttribute.STDDEV.name(), this.convertDuration((double)snapshot.getStdDev()), this.getDurationUnit(),
                String.format(this.locale, "stddev = %2.2f %s", this.convertDuration((double)snapshot.getStdDev()), this.getDurationUnit())));
        metricsTimerReport.setP50(new MetricsTimer( MetricAttribute.P50.name(), this.convertDuration((double)snapshot.getMedian()), this.getDurationUnit(),
                String.format(this.locale, "median = %2.2f %s", this.convertDuration((double)snapshot.getMedian()), this.getDurationUnit())));
        metricsTimerReport.setP75(new MetricsTimer( MetricAttribute.P75.name(), this.convertDuration((double)snapshot.get75thPercentile()), this.getDurationUnit(),
                String.format(this.locale, "75%% <= %2.2f %s", this.convertDuration((double)snapshot.get75thPercentile()), this.getDurationUnit())));
        metricsTimerReport.setP95(new MetricsTimer( MetricAttribute.P95.name(), this.convertDuration((double)snapshot.get95thPercentile()), this.getDurationUnit(),
                String.format(this.locale, "95%% <= %2.2f %s", this.convertDuration((double)snapshot.get95thPercentile()), this.getDurationUnit())));
        metricsTimerReport.setP98(new MetricsTimer( MetricAttribute.P98.name(), this.convertDuration((double)snapshot.get98thPercentile()), this.getDurationUnit(),
                String.format(this.locale, "98%% <= %2.2f %s", this.convertDuration((double)snapshot.get98thPercentile()), this.getDurationUnit())));
        metricsTimerReport.setP99(new MetricsTimer( MetricAttribute.P99.name(), this.convertDuration((double)snapshot.get99thPercentile()), this.getDurationUnit(),
                String.format(this.locale, "99%% <= %2.2f %s", this.convertDuration((double)snapshot.get99thPercentile()), this.getDurationUnit())));
        metricsTimerReport.setP999(new MetricsTimer( MetricAttribute.P999.name(), this.convertDuration((double)snapshot.get999thPercentile()), this.getDurationUnit(),
                String.format(this.locale, "99.9%% <= %2.2f %s", this.convertDuration((double)snapshot.get999thPercentile()), this.getDurationUnit())));
        return metricsTimerReport;
    }

    public static class Builder {
        private final MetricRegistry registry;
        private PrintStream output;
        private Locale locale;
        private Clock clock;
        private TimeZone timeZone;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private ScheduledExecutorService executor;
        private boolean shutdownExecutorOnStop;
        private Set<MetricAttribute> disabledMetricAttributes;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.output = System.out;
            this.locale = Locale.getDefault();
            this.clock = Clock.defaultClock();
            this.timeZone = TimeZone.getDefault();
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
            this.executor = null;
            this.shutdownExecutorOnStop = true;
            this.disabledMetricAttributes = Collections.emptySet();
        }

        public StandardJaxrsActionReporter.Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        public StandardJaxrsActionReporter.Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public StandardJaxrsActionReporter.Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        public StandardJaxrsActionReporter build( Context context ) {
            return new StandardJaxrsActionReporter(this.registry, this.output, this.locale, this.clock, this.timeZone, this.rateUnit,
                    this.durationUnit, this.filter, this.executor, this.shutdownExecutorOnStop, this.disabledMetricAttributes, context);
        }
    }
}
