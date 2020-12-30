package com.x.base.core.project.jaxrs;

import com.x.base.core.entity.enums.DesignerType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WrapDesigner extends GsonPropertyObject {

	@FieldDescribe("应用Id.")
	private String appId;

	@FieldDescribe("应用名称.")
	private String appName;

	@FieldDescribe("设计Id.")
	private String designerId;

	@FieldDescribe("设计名称.")
	private String designerName;

	@FieldDescribe("设计类型.")
	private String designerType;

	private Date updateTime;

	@FieldDescribe("匹配信息.")
	private List<DesignerPattern> patternList = new ArrayList<>();

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getDesignerId() {
		return designerId;
	}

	public void setDesignerId(String designerId) {
		this.designerId = designerId;
	}

	public String getDesignerName() {
		return designerName;
	}

	public void setDesignerName(String designerName) {
		this.designerName = designerName;
	}

	public String getDesignerType() {
		return designerType;
	}

	public void setDesignerType(String designerType) {
		this.designerType = designerType;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public List<DesignerPattern> getPatternList() {
		return patternList;
	}

	public void setPatternList(List<DesignerPattern> patternList) {
		this.patternList = patternList;
	}

	public void setPatternList(Map<String, String> map) {
		if(map!=null && !map.isEmpty()){
			List<DesignerPattern> patternList = new ArrayList<>();
			for (String key : map.keySet()){
				DesignerPattern pattern = new DesignerPattern();
				pattern.setProperty(key);
				pattern.setPropertyValue(map.get(key));
				patternList.add(pattern);
			}
			this.patternList = patternList;
		}
	}

	public void addPatternList(String elementType, Map<String, String> map) {
		if(map!=null && !map.isEmpty()){
			for (String key : map.keySet()){
				DesignerPattern pattern = new DesignerPattern();
				pattern.setElementType(elementType);
				pattern.setProperty(key);
				pattern.setPropertyValue(map.get(key));
				this.patternList.add(pattern);
			}
		}
	}

	public void addPatternList(String elementType, String elementId, String elementName, Map<String, String> map) {
		if(map!=null && !map.isEmpty()){
			for (String key : map.keySet()){
				DesignerPattern pattern = new DesignerPattern();
				pattern.setElementType(elementType);
				pattern.setElementId(elementId);
				pattern.setElementName(elementName);
				pattern.setProperty(key);
				pattern.setPropertyValue(map.get(key));
				this.patternList.add(pattern);
			}
		}
	}

	public void clearPatternValue(){
		for (DesignerPattern pattern : this.patternList){
			pattern.setPropertyValue(null);
		}
	}

	public DesignerPattern getScriptDesigner(){
		DesignerPattern designerPattern = null;
		if(DesignerType.script.toString().equals(this.getDesignerType())){
			for (DesignerPattern pattern : this.patternList){
				if ("text".equals(pattern.getProperty())){
					designerPattern = pattern;
				}else{
					pattern.setPropertyValue(null);
				}
			}
		}else{
			this.clearPatternValue();
		}
		return designerPattern;
	}

	public class DesignerPattern extends GsonPropertyObject {

		@FieldDescribe("元素类型（activity | process）.")
		private String elementType;
		@FieldDescribe("元素ID.")
		private String elementId;
		@FieldDescribe("元素名称.")
		private String elementName;

		@FieldDescribe("设计属性.")
		private String property;

		@FieldDescribe("设计属性.")
		private String propertyValue;

		@FieldDescribe("匹配行")
		private List<Integer> lines;

		public String getElementType() {
			return elementType;
		}

		public void setElementType(String elementType) {
			this.elementType = elementType;
		}

		public String getElementId() {
			return elementId;
		}

		public void setElementId(String elementId) {
			this.elementId = elementId;
		}

		public String getElementName() {
			return elementName;
		}

		public void setElementName(String elementName) {
			this.elementName = elementName;
		}

		public String getProperty() {
			return property;
		}

		public void setProperty(String property) {
			this.property = property;
		}

		public List<Integer> getLines() {
			return lines;
		}

		public void setLines(List<Integer> lines) {
			this.lines = lines;
		}

		public String getPropertyValue() {
			return propertyValue;
		}

		public void setPropertyValue(String propertyValue) {
			this.propertyValue = propertyValue;
		}
	}

}
