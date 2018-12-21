package com.x.base.core.entity.dataitem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;

public abstract class DataItem extends SliceJpaObject {

	private static final long serialVersionUID = -709083599148957321L;

	public static final int pathLength = JpaObject.length_64B;

	public static final int STRING_VALUE_MAX_LENGTH = JpaObject.length_255B;

	public String getStringValue() {
		if (StringUtils.isNotEmpty(this.getStringLongValue())) {
			return this.getStringLongValue();
		} else {
			return this.getStringShortValue();
		}
	}

	public void setStringValue(String stringValue) {
		if (StringTools.utf8Length(stringValue) > STRING_VALUE_MAX_LENGTH) {
			this.setStringShortValue(StringTools.utf8SubString(stringValue, STRING_VALUE_MAX_LENGTH));
			this.setStringLongValue(stringValue);
		} else {
			this.setStringShortValue(stringValue);
			this.setStringLongValue(null);
		}
	}

	public abstract ItemType getItemType();

	public abstract void setItemType(ItemType itemType);

	public abstract ItemPrimitiveType getItemPrimitiveType();

	public abstract void setItemPrimitiveType(ItemPrimitiveType itemPrimitiveType);

	public abstract ItemStringValueType getItemStringValueType();

	public abstract void setItemStringValueType(ItemStringValueType itemStringValueType);

	public static final String path0_FIELDNAME = "path0";

	public static final String path1_FIELDNAME = "path1";

	public static final String path2_FIELDNAME = "path2";

	public static final String path3_FIELDNAME = "path3";

	public static final String path4_FIELDNAME = "path4";

	public static final String path5_FIELDNAME = "path5";

	public static final String path6_FIELDNAME = "path6";

	public static final String path7_FIELDNAME = "path7";

	public static final String path0Location_FIELDNAME = "path0Location";

	public static final String path1Location_FIELDNAME = "path1Location";

	public static final String path2Location_FIELDNAME = "path2Location";

	public static final String path3Location_FIELDNAME = "path3Location";

	public static final String path4Location_FIELDNAME = "path4Location";

	public static final String path5Location_FIELDNAME = "path5Location";

	public static final String path6Location_FIELDNAME = "path6Location";

	public static final String path7Location_FIELDNAME = "path7Location";

	public static final String itemCategory_FIELDNAME = "itemCategory";

	public static final String itemType_FIELDNAME = "itemType";

	public static final String itemPrimitiveType_FIELDNAME = "itemPrimitiveType";

	public static final String itemStringValueType_FIELDNAME = "itemStringValueType";

	public static final String bundle_FIELDNAME = "bundle";

	public static final String stringShortValue_FIELDNAME = "stringShortValue";

	public static final String stringLongValue_FIELDNAME = "stringLongValue";

	public static final String numberValue_FIELDNAME = "numberValue";

	public static final String dateTimeValue_FIELDNAME = "dateTimeValue";

	public static final String dateValue_FIELDNAME = "dateValue";

	public static final String timeValue_FIELDNAME = "timeValue";

	public static final String booleanValue_FIELDNAME = "booleanValue";

	public static final String BUNDLECOLUMN = ColumnNamePrefix + bundle_FIELDNAME;
	public static final String ITEMCATEGORYCOLUMN = ColumnNamePrefix + itemCategory_FIELDNAME;
	public static final String PATH0COLUMN = ColumnNamePrefix + path0_FIELDNAME;
	public static final String PATH1COLUMN = ColumnNamePrefix + path1_FIELDNAME;
	public static final String PATH2COLUMN = ColumnNamePrefix + path2_FIELDNAME;
	public static final String PATH3COLUMN = ColumnNamePrefix + path3_FIELDNAME;
	public static final String PATH4COLUMN = ColumnNamePrefix + path4_FIELDNAME;
	public static final String PATH5COLUMN = ColumnNamePrefix + path5_FIELDNAME;
	public static final String PATH6COLUMN = ColumnNamePrefix + path6_FIELDNAME;
	public static final String PATH7COLUMN = ColumnNamePrefix + path7_FIELDNAME;
	public static final String ITEMTYPECOLUMN = ColumnNamePrefix + itemType_FIELDNAME;
	public static final String ITEMPRIMITIVETYPECOLUMN = ColumnNamePrefix + itemPrimitiveType_FIELDNAME;
	public static final String ITEMSTRINGVALUETYPECOLUMN = ColumnNamePrefix + itemStringValueType_FIELDNAME;

	public abstract String getPath0();

	public abstract void setPath0(String path0);

	public abstract String getPath1();

	public abstract void setPath1(String path1);

	public abstract String getPath2();

	public abstract void setPath2(String path2);

	public abstract String getPath3();

	public abstract void setPath3(String path3);

	public abstract String getPath4();

	public abstract void setPath4(String path4);

	public abstract String getPath5();

	public abstract void setPath5(String path5);

	public abstract String getPath6();

	public abstract void setPath6(String path6);

	public abstract String getPath7();

	public abstract void setPath7(String path7);

	public abstract String getStringShortValue();

	public abstract void setStringShortValue(String stringValue);

	public abstract String getStringLongValue();

	public abstract void setStringLongValue(String stringValue);

	public abstract Double getNumberValue();

	public abstract void setNumberValue(Double numberValue);

	public abstract Date getDateTimeValue();

	public abstract void setDateTimeValue(Date dateTimeValue);

	public abstract Date getDateValue();

	public abstract void setDateValue(Date dateValue);

	public abstract Date getTimeValue();

	public abstract void setTimeValue(Date timeValue);

	public abstract Boolean getBooleanValue();

	public abstract void setBooleanValue(Boolean booleanValue);

	public abstract Integer getPath0Location();

	public abstract void setPath0Location(Integer path0Location);

	public abstract Integer getPath1Location();

	public abstract void setPath1Location(Integer path1Location);

	public abstract Integer getPath2Location();

	public abstract void setPath2Location(Integer path2Location);

	public abstract Integer getPath3Location();

	public abstract void setPath3Location(Integer path3Location);

	public abstract Integer getPath4Location();

	public abstract void setPath4Location(Integer path4Location);

	public abstract Integer getPath5Location();

	public abstract void setPath5Location(Integer path5Location);

	public abstract Integer getPath6Location();

	public abstract void setPath6Location(Integer path6Location);

	public abstract Integer getPath7Location();

	public abstract void setPath7Location(Integer path7Location);

	public abstract ItemCategory getItemCategory();

	public abstract void setItemCategory(ItemCategory itemCategory);

	public abstract String getBundle();

	public abstract void setBundle(String bundle);

	public void value(Boolean value) {
		this.setItemPrimitiveType(ItemPrimitiveType.b);
		this.setItemStringValueType(ItemStringValueType.u);
		this.setBooleanValue(value);
	}

	public void value(Double value) {
		this.setItemPrimitiveType(ItemPrimitiveType.n);
		this.setItemStringValueType(ItemStringValueType.u);
		this.setNumberValue(value);
	}

	public void value(String value) throws Exception {
		this.setItemPrimitiveType(ItemPrimitiveType.s);
		this.setStringValue(value);
		this.setItemStringValueType(ItemStringValueType.s);
		if (StringTools.utf8Length(value) < STRING_VALUE_MAX_LENGTH) {
			Date dateTime = DateTools.parseDateTime(value);
			if (null != dateTime) {
				this.setItemStringValueType(ItemStringValueType.dt);
				this.setDateTimeValue(dateTime);
				return;
			}
			Date date = DateTools.parseDate(value);
			if (null != date) {
				this.setItemStringValueType(ItemStringValueType.d);
				this.setDateValue(date);
				return;
			}
			Date time = DateTools.parseTime(value);
			if (null != time) {
				this.setItemStringValueType(ItemStringValueType.t);
				this.setTimeValue(time);
				return;
			}
		}
	}

	public List<String> paths() {
		List<String> list = new ArrayList<>();
		if (StringUtils.isNotEmpty(this.getPath0())) {
			list.add(this.getPath0());
		}
		if (StringUtils.isNotEmpty(this.getPath1())) {
			list.add(this.getPath1());
		}
		if (StringUtils.isNotEmpty(this.getPath2())) {
			list.add(this.getPath2());
		}
		if (StringUtils.isNotEmpty(this.getPath3())) {
			list.add(this.getPath3());
		}
		if (StringUtils.isNotEmpty(this.getPath4())) {
			list.add(this.getPath4());
		}
		if (StringUtils.isNotEmpty(this.getPath5())) {
			list.add(this.getPath5());
		}
		if (StringUtils.isNotEmpty(this.getPath6())) {
			list.add(this.getPath6());
		}
		if (StringUtils.isNotEmpty(this.getPath7())) {
			list.add(this.getPath7());
		}
		return list;
	}

	public String path() {
		return StringUtils.join(this.paths().toArray(), ".");
	}

	public void path(List<String> paths) {
		int i = 0;
		for (String str : paths) {
			this.path(str, i++);
		}
		for (int p = i; p < 8; p++) {
			this.path("", p);
		}
	}

	public void path(String str, Integer p) {
		switch (p) {
		case 0:
			this.setPath0(str);
		case 1:
			this.setPath1(str);
		case 2:
			this.setPath2(str);
		case 3:
			this.setPath3(str);
		case 4:
			this.setPath4(str);
		case 5:
			this.setPath5(str);
		case 6:
			this.setPath6(str);
		case 7:
			this.setPath7(str);
		}
	}

	public String path(Integer p) {
		switch (p) {
		case 0:
			return this.getPath0();
		case 1:
			return this.getPath1();
		case 2:
			return this.getPath2();
		case 3:
			return this.getPath3();
		case 4:
			return this.getPath4();
		case 5:
			return this.getPath5();
		case 6:
			return this.getPath6();
		case 7:
			return this.getPath7();
		}
		return null;
	}

	public Integer pathLocation(Integer p) {
		switch (p) {
		case 0:
			return this.getPath0Location();
		case 1:
			return this.getPath1Location();
		case 2:
			return this.getPath2Location();
		case 3:
			return this.getPath3Location();
		case 4:
			return this.getPath4Location();
		case 5:
			return this.getPath5Location();
		case 6:
			return this.getPath6Location();
		case 7:
			return this.getPath7Location();
		}
		return null;
	}

	@Override
	public String toString() {
		return "Item [path0=" + getPath0() + ", path1=" + getPath1() + ", path2=" + getPath2() + ", path3=" + getPath3()
				+ ", path4=" + getPath4() + ", path5=" + getPath5() + ", path6=" + getPath6() + ", path7=" + getPath7()
				+ ", stringValue=" + getStringValue() + ", numberValue=" + getNumberValue() + ", dateTimeValue="
				+ getDateTimeValue() + ", dateValue=" + getDateValue() + ", timeValue=" + getTimeValue()
				+ ", booleanValue=" + getBooleanValue() + ", itemType=" + getItemType() + ", itemPrimitiveType="
				+ getItemPrimitiveType() + ", itemStringValueType=" + getItemStringValueType() + "]";
	}

}