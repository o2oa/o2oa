package com.x.teamwork.core.entity.tools;

import java.util.ArrayList;
import java.util.List;

public class ProjectExtField {

	private static List<FieldInfo> fieldList = new ArrayList<>();
	
	static {
		fieldList.add( new FieldInfo("memoString_1", "备用属性1（最大长度：255）" ));
		fieldList.add( new FieldInfo("memoString_2", "备选属性2（最大长度：255）" ));
		fieldList.add( new FieldInfo("memoString_3", "备选属性3（最大长度：255）" ));
		fieldList.add( new FieldInfo("memoString_4", "备选属性4（最大长度：255）" ));
		fieldList.add( new FieldInfo("memoString_5", "备选属性5（最大长度：255）" ));
		fieldList.add( new FieldInfo("memoString_6", "备选属性6（最大长度：255）" ));
		fieldList.add( new FieldInfo("memoString_7", "备选属性7（最大长度：255）" ));
		fieldList.add( new FieldInfo("memoString_8", "备选属性8（最大长度：255）" ));
		fieldList.add( new FieldInfo("memoString_1_lob", "备用长文本1（最大长度：10M）" ));
		fieldList.add( new FieldInfo("memoString_2_lob", "备用长文本2（最大长度：10M）" ));
		fieldList.add( new FieldInfo("memoString_3_lob", "备用长文本3（最大长度：10M）" ));
		fieldList.add( new FieldInfo("memoString_4_lob", "备用长文本4（最大长度：10M）" ));
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