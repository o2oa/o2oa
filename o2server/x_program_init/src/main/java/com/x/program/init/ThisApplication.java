package com.x.program.init;

import com.x.base.core.project.cache.CacheManager;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	private static MissionRestore missionRestore;
	private static MissionSetSecret missionSetSecret;
	private static MissionExternalDataSources missionExternalDataSources;

	protected static String path;

	public static String getPath() {
		return path;
	}

	public static MissionRestore getMissionRestore() {
		return missionRestore;
	}

	public static void setMissionRestore(MissionRestore missionRestore) {
		ThisApplication.missionRestore = missionRestore;
	}

	public static MissionSetSecret getMissionSetSecret() {
		return missionSetSecret;
	}

	public static void setMissionSetSecret(MissionSetSecret missionSetSecret) {
		ThisApplication.missionSetSecret = missionSetSecret;
	}

	public static MissionExternalDataSources getMissionExternalDataSources() {
		return missionExternalDataSources;
	}

	public static void setMissionExternalDataSources(MissionExternalDataSources missionExternalDataSources) {
		ThisApplication.missionExternalDataSources = missionExternalDataSources;
	}

	public static void init() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
