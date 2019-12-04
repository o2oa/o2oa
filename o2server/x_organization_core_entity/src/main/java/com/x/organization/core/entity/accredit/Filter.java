package com.x.organization.core.entity.accredit;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.DateTools;

public class Filter extends GsonPropertyObject {

	public static Type LISTTYPE = new TypeToken<List<Filter>>() {
	}.getType();

	public static final String FORMAT_TEXTVALUE = "textValue";

	public static final String FORMAT_NUMBERVALUE = "numberValue";

	public static final String FORMAT_BOOLEANVALUE = "booleanValue";

	public static final String FORMAT_DATETIMEVALUE = "dateTimeValue";

	public static final String FORMAT_DATEVALUE = "dateValue";

	public static final String FORMAT_TIMEVALUE = "timeValue";

	/* 用于customFilterEntry */
	public String title;

	public String value;

	public String otherValue;

	public String path;

	public String formatType;

	public String logic;

	public String comparison;

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
		switch (StringUtils.trimToEmpty(formatType)) {
		case FORMAT_TEXTVALUE:
			return true;
		case FORMAT_BOOLEANVALUE:
			if (null == BooleanUtils.toBooleanObject(value)) {
				return false;
			} else {
				return true;
			}
		case FORMAT_DATETIMEVALUE:
			if (DateTools.isDateTimeOrDateOrTime(value)) {
				return true;
			} else {
				return false;
			}
		case FORMAT_DATEVALUE:
			if (DateTools.isDateTimeOrDateOrTime(value)) {
				return true;
			} else {
				return false;
			}
		case FORMAT_TIMEVALUE:
			if (DateTools.isDateTimeOrDateOrTime(value)) {
				return true;
			} else {
				return false;
			}
		case FORMAT_NUMBERVALUE:
			if (NumberUtils.isCreatable(value)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

}