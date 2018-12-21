package com.x.okr.assemble.control.service.update;

import java.util.HashMap;
import java.util.Map;

public class UpdateUnitMap {
	
	private static Map<String, String> unitMap = new HashMap<>();
	static {
		unitMap.put("", "");
	}
	public static String getUnitWithOldName( String _oldName ) {
		if( unitMap.get( _oldName ) != null ) {
			return (String)unitMap.get( _oldName );
		}
		return null;
	}

	public static Map<String, String> getUnitMap() {
		return unitMap;
	}

	public static void setUnitMap(Map<String, String> unitMap) {
		UpdateUnitMap.unitMap = unitMap;
	}
}