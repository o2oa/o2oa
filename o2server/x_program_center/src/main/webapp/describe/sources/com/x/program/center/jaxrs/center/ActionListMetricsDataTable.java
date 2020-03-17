package com.x.program.center.jaxrs.center;

import com.google.gson.internal.LinkedTreeMap;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.program.center.ThisApplication;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class ActionListMetricsDataTable extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wo wo = null;

		//遍历map里所有的数据，组织成list返回
		LinkedTreeMap metricsTimerReport = null;
		Map<String, LinkedTreeMap> serverReportMap = null;
		Map<String, Map<String, LinkedTreeMap>> metricsReportMap = ThisApplication.metricsReportMap;
		if( metricsReportMap != null && !metricsReportMap.isEmpty() ){
			Iterator<Map.Entry<String, Map<String, LinkedTreeMap>>> serverMap_entries = metricsReportMap.entrySet().iterator();
			while (serverMap_entries.hasNext()) {
				Map.Entry<String, Map<String, LinkedTreeMap>> serverMap_entry = serverMap_entries.next();
				serverReportMap = serverMap_entry.getValue();
				if( serverReportMap != null && !serverReportMap.isEmpty() ){
					Iterator<Map.Entry<String, LinkedTreeMap>> entries = serverReportMap.entrySet().iterator();
					while (entries.hasNext()) {
						wo = new Wo();
						wo.setServerName( serverMap_entry.getKey() );
						Map.Entry<String, LinkedTreeMap> entry = entries.next();
						metricsTimerReport = entry.getValue();
						if( metricsTimerReport != null ){
							wo.setTargetClassName( (String) metricsTimerReport.get("targetClassName") );
							wo.setTargetContextName( (String) metricsTimerReport.get("targetContextName") );
							wo.setTargetContextCNName( (String) metricsTimerReport.get("targetContextCNName") );
							wo.setDateTime( (String) metricsTimerReport.get("dateTime") );
							wo.setCount( (Double) metricsTimerReport.get("count") );
							wo.setMean_call( (Double)((Map)metricsTimerReport.get("mean_rate")).get("rate") );
							wo.setMean_call_unit( (String)((Map)metricsTimerReport.get("mean_rate")).get("unit") );
							wo.setM1_rate( (Double)((Map)metricsTimerReport.get("m1_rate")).get("rate") );
							wo.setM1_rate_unit( (String)((Map)metricsTimerReport.get("m1_rate")).get("unit") );
							wo.setM5_rate( (Double)((Map)metricsTimerReport.get("m5_rate")).get("rate") );
							wo.setM5_rate_unit( (String)((Map)metricsTimerReport.get("m5_rate")).get("unit") );
							wo.setM15_rate( (Double)((Map)metricsTimerReport.get("m15_rate")).get("rate") );
							wo.setM15_rate_unit( (String)((Map)metricsTimerReport.get("m15_rate")).get("unit") );
							wo.setStddev_rate( (Double)((Map)metricsTimerReport.get("stddev")).get("rate") );
							wo.setStddev_rate_unit( (String)((Map)metricsTimerReport.get("stddev")).get("unit") );
							wo.setMean_rate( (Double)((Map)metricsTimerReport.get("mean")).get("rate") );
							wo.setMean_rate_unit( (String)((Map)metricsTimerReport.get("mean")).get("unit") );
							wo.setMin_rate( (Double)((Map)metricsTimerReport.get("min")).get("rate") );
							wo.setMin_rate_unit( (String)((Map)metricsTimerReport.get("min")).get("unit") );
							wo.setMax_rate( (Double)((Map)metricsTimerReport.get("max")).get("rate") );
							wo.setMax_rate_unit( (String)((Map)metricsTimerReport.get("max")).get("unit") );
							wo.setP50_rate( (Double)((Map)metricsTimerReport.get("p50")).get("rate") );
							wo.setP50_rate_unit( (String)((Map)metricsTimerReport.get("p50")).get("unit") );
							wo.setP75_rate( (Double)((Map)metricsTimerReport.get("p75")).get("rate") );
							wo.setP75_rate_unit( (String)((Map)metricsTimerReport.get("p75")).get("unit") );
							wo.setP95_rate( (Double)((Map)metricsTimerReport.get("p95")).get("rate") );
							wo.setP95_rate_unit( (String)((Map)metricsTimerReport.get("p95")).get("unit") );
							wo.setP98_rate( (Double)((Map)metricsTimerReport.get("p98")).get("rate") );
							wo.setP98_rate_unit( (String)((Map)metricsTimerReport.get("p98")).get("unit") );
							wo.setP99_rate( (Double)((Map)metricsTimerReport.get("p99")).get("rate") );
							wo.setP99_rate_unit( (String)((Map)metricsTimerReport.get("p99")).get("unit") );
							wo.setP999_rate( (Double)((Map)metricsTimerReport.get("p999")).get("rate") );
							wo.setP999_rate_unit( (String)((Map)metricsTimerReport.get("p999")).get("unit") );
							wos.add( wo );
						}
					}
				}
			}
		}
		result.setData( wos );
		return result;
	}

	public static class Wo {

		private String serverName;
		private String targetContextName;
		private String targetContextCNName;
		private String targetClassName;
		private String dateTime;
		private double count;

		private double mean_call;
		private String mean_call_unit;
		private double m1_rate;
		private String m1_rate_unit;
		private double m5_rate;
		private String m5_rate_unit;
		private double m15_rate;
		private String m15_rate_unit;
		private double min_rate;
		private String min_rate_unit;
		private double max_rate;
		private String max_rate_unit;
		private double mean_rate;
		private String mean_rate_unit;
		private double stddev_rate;
		private String stddev_rate_unit;
		private double p50_rate;
		private String p50_rate_unit;
		private double p75_rate;
		private String p75_rate_unit;
		private double p95_rate;
		private String p95_rate_unit;
		private double p98_rate;
		private String p98_rate_unit;
		private double p99_rate;
		private String p99_rate_unit;
		private double p999_rate;
		private String p999_rate_unit;

		public String getServerName() {
			return this.serverName;
		}

		public void setServerName(final String serverName) {
			this.serverName = serverName;
		}

		public String getTargetContextName() {
			return this.targetContextName;
		}

		public void setTargetContextName(final String targetContextName) {
			this.targetContextName = targetContextName;
		}

		public String getTargetContextCNName() {
			return this.targetContextCNName;
		}

		public void setTargetContextCNName(final String targetContextCNName) {
			this.targetContextCNName = targetContextCNName;
		}

		public String getTargetClassName() {
			return this.targetClassName;
		}

		public void setTargetClassName(final String targetClassName) {
			this.targetClassName = targetClassName;
		}

		public String getDateTime() {
			return this.dateTime;
		}

		public void setDateTime(final String dateTime) {
			this.dateTime = dateTime;
		}

		public double getCount() {
			return this.count;
		}

		public void setCount(final double count) {
			this.count = count;
		}

		public double getMean_call() {
			return this.mean_call;
		}

		public void setMean_call(final double mean_call) {
			this.mean_call = mean_call;
		}

		public String getMean_call_unit() {
			return this.mean_call_unit;
		}

		public void setMean_call_unit(final String mean_call_unit) {
			this.mean_call_unit = mean_call_unit;
		}

		public double getM1_rate() {
			return this.m1_rate;
		}

		public void setM1_rate(final double m1_rate) {
			this.m1_rate = m1_rate;
		}

		public String getM1_rate_unit() {
			return this.m1_rate_unit;
		}

		public void setM1_rate_unit(final String m1_rate_unit) {
			this.m1_rate_unit = m1_rate_unit;
		}

		public double getM5_rate() {
			return this.m5_rate;
		}

		public void setM5_rate(final double m5_rate) {
			this.m5_rate = m5_rate;
		}

		public String getM5_rate_unit() {
			return this.m5_rate_unit;
		}

		public void setM5_rate_unit(final String m5_rate_unit) {
			this.m5_rate_unit = m5_rate_unit;
		}

		public double getM15_rate() {
			return this.m15_rate;
		}

		public void setM15_rate(final double m15_rate) {
			this.m15_rate = m15_rate;
		}

		public String getM15_rate_unit() {
			return this.m15_rate_unit;
		}

		public void setM15_rate_unit(final String m15_rate_unit) {
			this.m15_rate_unit = m15_rate_unit;
		}

		public double getMin_rate() {
			return this.min_rate;
		}

		public void setMin_rate(final double min_rate) {
			this.min_rate = min_rate;
		}

		public String getMin_rate_unit() {
			return this.min_rate_unit;
		}

		public void setMin_rate_unit(final String min_rate_unit) {
			this.min_rate_unit = min_rate_unit;
		}

		public double getMax_rate() {
			return this.max_rate;
		}

		public void setMax_rate(final double max_rate) {
			this.max_rate = max_rate;
		}

		public String getMax_rate_unit() {
			return this.max_rate_unit;
		}

		public void setMax_rate_unit(final String max_rate_unit) {
			this.max_rate_unit = max_rate_unit;
		}

		public double getMean_rate() {
			return this.mean_rate;
		}

		public void setMean_rate(final double mean_rate) {
			this.mean_rate = mean_rate;
		}

		public String getMean_rate_unit() {
			return this.mean_rate_unit;
		}

		public void setMean_rate_unit(final String mean_rate_unit) {
			this.mean_rate_unit = mean_rate_unit;
		}

		public double getStddev_rate() {
			return this.stddev_rate;
		}

		public void setStddev_rate(final double stddev_rate) {
			this.stddev_rate = stddev_rate;
		}

		public String getStddev_rate_unit() {
			return this.stddev_rate_unit;
		}

		public void setStddev_rate_unit(final String stddev_rate_unit) {
			this.stddev_rate_unit = stddev_rate_unit;
		}

		public double getP50_rate() {
			return this.p50_rate;
		}

		public void setP50_rate(final double p50_rate) {
			this.p50_rate = p50_rate;
		}

		public String getP50_rate_unit() {
			return this.p50_rate_unit;
		}

		public void setP50_rate_unit(final String p50_rate_unit) {
			this.p50_rate_unit = p50_rate_unit;
		}

		public double getP75_rate() {
			return this.p75_rate;
		}

		public void setP75_rate(final double p75_rate) {
			this.p75_rate = p75_rate;
		}

		public String getP75_rate_unit() {
			return this.p75_rate_unit;
		}

		public void setP75_rate_unit(final String p75_rate_unit) {
			this.p75_rate_unit = p75_rate_unit;
		}

		public double getP95_rate() {
			return this.p95_rate;
		}

		public void setP95_rate(final double p95_rate) {
			this.p95_rate = p95_rate;
		}

		public String getP95_rate_unit() {
			return this.p95_rate_unit;
		}

		public void setP95_rate_unit(final String p95_rate_unit) {
			this.p95_rate_unit = p95_rate_unit;
		}

		public double getP98_rate() {
			return this.p98_rate;
		}

		public void setP98_rate(final double p98_rate) {
			this.p98_rate = p98_rate;
		}

		public String getP98_rate_unit() {
			return this.p98_rate_unit;
		}

		public void setP98_rate_unit(final String p98_rate_unit) {
			this.p98_rate_unit = p98_rate_unit;
		}

		public double getP99_rate() {
			return this.p99_rate;
		}

		public void setP99_rate(final double p99_rate) {
			this.p99_rate = p99_rate;
		}

		public String getP99_rate_unit() {
			return this.p99_rate_unit;
		}

		public void setP99_rate_unit(final String p99_rate_unit) {
			this.p99_rate_unit = p99_rate_unit;
		}

		public double getP999_rate() {
			return this.p999_rate;
		}

		public void setP999_rate(final double p999_rate) {
			this.p999_rate = p999_rate;
		}

		public String getP999_rate_unit() {
			return this.p999_rate_unit;
		}

		public void setP999_rate_unit(final String p999_rate_unit) {
			this.p999_rate_unit = p999_rate_unit;
		}
	}
}