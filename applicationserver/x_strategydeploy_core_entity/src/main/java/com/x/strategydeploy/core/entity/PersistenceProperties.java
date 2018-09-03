package com.x.strategydeploy.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class StrategyDeployInfo {
		public static final String table = "STRATEGY_STRATEGYDEPLOY_INFO";
	}

	public static class MeasuresInfo {
		public static final String table = "STRATEGY_MEASURES_INFO";
	}

	public static class KeyWorkInfo {
		public static final String table = "STRATEGY_KEYWORK_INFO";
	}

	public static class Keywork_Measures_Relation {
		public static final String table = "STRATEGY_KEYWORK2MEASURES_RELATION";
	}

	public static class StrategyConfigSys {
		public static final String table = "STRATEGY_CONFIGSYS";
	}
	
	public static class Strategy_Attachment{
		public static final String table = "STRATEGY_ATTACHMENT";
	}
	
}