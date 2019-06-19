package com.x.teamwork.core.entity.tools;

import java.util.ArrayList;
import java.util.List;

public class ProjectExtField {

	private static List<FieldInfo> fieldList = new ArrayList<>();
	
	static {
		fieldList.add( new FieldInfo("memoString64_1", "字符", 64 ));
		fieldList.add( new FieldInfo("memoString64_2", "字符", 64 ));
		fieldList.add( new FieldInfo("memoString64_3", "字符", 64 ));
		fieldList.add( new FieldInfo("memoString255_1", "字符", 255 ));
		fieldList.add( new FieldInfo("memoString255_2", "字符", 255 ));
		fieldList.add( new FieldInfo("memoString255_3", "字符", 255 ));
		fieldList.add( new FieldInfo("memoInteger1", "整数", 11 ));
		fieldList.add( new FieldInfo("memoInteger2", "整数", 11 ));
		fieldList.add( new FieldInfo("memoInteger3", "整数", 11 ));
		fieldList.add( new FieldInfo("memoDouble1", "小数", 16 ));
		fieldList.add( new FieldInfo("memoDouble2", "小数", 16 ));		
		fieldList.add( new FieldInfo("memoLob1", "长文本", 128000000 ));
		fieldList.add( new FieldInfo("memoLob2", "长文本", 128000000 ));
		fieldList.add( new FieldInfo("memoLob3", "长文本", 128000000 ));
	}
	
	public static List<FieldInfo> listAllExtField() {
		return fieldList;
	}
	
	public static FieldInfo getExtField( String name ) {
		for( FieldInfo fieldInfo : fieldList ) {
			if( fieldInfo.getFieldName().equalsIgnoreCase( name )) {
				return fieldInfo;
			}
		}
		return null;
	}
}