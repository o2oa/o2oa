package com.x.okr.assemble.control.service.update;

import java.util.HashMap;
import java.util.Map;

public class UpdatePersonMap {
	
	private static Map<String, String> personMap = new HashMap<>();
	static {
		personMap.put("", "");
	}
	public static String getPersonWithOldName( String _oldName ) {
		if( personMap.get( _oldName ) != null ) {
			return (String)personMap.get( _oldName );
		}
		return null;
	}

	public static Map<String, String> getPersonMap() {
		return personMap;
	}

	public static void setPersonMap(Map<String, String> personMap) {
		UpdatePersonMap.personMap = personMap;
	}
}