package com.x.cms.assemble.search.es;

import com.x.base.core.project.annotation.FieldDescribe;

public class Criteria {
	
	@FieldDescribe("属性名称")
    private String fieldName; 
	
	@FieldDescribe("属性值")
    private Object fieldValue;  
  
    public Criteria(String fieldName, Object fieldValue) {  
        this.fieldName = fieldName;  
        this.fieldValue = fieldValue;  
    }  
  
    public String getFieldName() {  
        return fieldName;  
    }  
  
    public Object getFieldValue() {  
        return fieldValue;  
    }  
}  