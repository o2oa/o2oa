package com.x.processplatform.core.express.query;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.core.entity.query.FormatType;

public class FilterEntry extends GsonPropertyObject {

	private String path;

	private String value;

	private FormatType formatType;

	private String logic;

	private String comparison;

	public Boolean available() {
		if (StringUtils.isEmpty(path)) {
			return false;
		}
		if (StringUtils.isEmpty(logic)) {
			return false;
		}
		if (StringUtils.isEmpty(comparison)) {
			return false;
		}
		if (null == formatType) {
			return false;
		}
		switch (formatType) {
		case textValue:
			return true;
		case booleanValue:
			if (null == BooleanUtils.toBooleanObject(value)) {
				return false;
			} else {
				return true;
			}
		case dateTimeValue:
			if (DateTools.isDateTimeOrDateOrTime(value)) {
				return true;
			} else {
				return false;
			}
		case numberValue:
			if (NumberUtils.isNumber(value)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getComparison() {
		return comparison;
	}

	public void setComparison(String comparison) {
		this.comparison = comparison;
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public FormatType getFormatType() {
		return formatType;
	}

	public void setFormatType(FormatType formatType) {
		this.formatType = formatType;
	}

}
