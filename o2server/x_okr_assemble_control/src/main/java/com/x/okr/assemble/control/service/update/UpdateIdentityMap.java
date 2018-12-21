package com.x.okr.assemble.control.service.update;

import java.util.HashMap;
import java.util.Map;

public class UpdateIdentityMap {
	
	private static Map<String, String> identityMap = new HashMap<>();
	static {
		identityMap.put("", "");
	}
	public static String getIdentityWithOldName( String _oldName ) {
		if( identityMap.get( _oldName ) != null ) {
			return (String)identityMap.get( _oldName );
		}
		return null;
	}

	public static Map<String, String> getIdentityMap() {
		return identityMap;
	}

	public static void setIdentityMap(Map<String, String> identityMap) {
		UpdateIdentityMap.identityMap = identityMap;
	}	
}