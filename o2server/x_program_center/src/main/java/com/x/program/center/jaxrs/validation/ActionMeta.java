package com.x.program.center.jaxrs.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.core.entity.validation.Meta;

class ActionMeta extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionMeta.class);

	public static final String DATETIMESTRING1 = "2021-01-01 01:01:01";
	public static final String DATETIMESTRING2 = "2022-02-02 02:02:02";

	public static final String DATESTRING1 = "2021-01-01";
	public static final String DATESTRING2 = "2022-02-02";

	public static final String TIMESTRING1 = "01:01:01";
	public static final String TIMESTRING2 = "02:02:02";

	public static final String STRING1 = "o2oa";
	public static final String STRING2 = "o2server";
	public static final Integer INTEGER1 = 1;
	public static final Integer INTEGER2 = 2;
	public static final Long LONG1 = 10000000000L;
	public static final Long LONG2 = 20000000000L;
	public static final Float FLOAT1 = 1.1f;
	public static final Float FLOAT2 = 2.2f;
	public static final Double DOUBLE1 = 3.3d;
	public static final Double DOUBLE2 = 4.4d;
	protected static Date dateTime1 = null;
	protected static Date dateTime2 = null;
	protected static Date date1 = null;
	protected static Date date2 = null;
	protected static Date time1 = null;
	protected static Date time2 = null;
	public static final Boolean BOOLEAN1 = true;
	public static final Boolean BOOLEAN2 = false;
	static {
		try {
			dateTime1 = DateTools.parse(DATETIMESTRING1, DateTools.format_yyyyMMddHHmmss);
			dateTime2 = DateTools.parse(DATETIMESTRING2, DateTools.format_yyyyMMddHHmmss);
			date1 = DateTools.parse(DATESTRING1, DateTools.format_yyyyMMdd);
			date2 = DateTools.parse(DATESTRING2, DateTools.format_yyyyMMdd);
			time1 = DateTools.parse(TIMESTRING1, DateTools.format_HHmmss);
			time2 = DateTools.parse(TIMESTRING2, DateTools.format_HHmmss);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		String id = StringTools.uniqueToken();
		Meta meta = new Meta();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			meta.setId(id);
			assignmentStringValue(meta);
			assignmentStringLobValue(meta);
			assignmentIntegerValue(meta);
			assignmentLongValue(meta);
			assignmentFloatValue(meta);
			assignmentDoubleValue(meta);
			assignmentDateTimeValue(meta);
			assignmentDateValue(meta);
			assignmentTimeValue(meta);
			assignmentBooleanValue(meta);
			assignmentStringValueList(meta);
			assignmentIntegerValueList(meta);
			assignmentLongValueList(meta);
			assignmentFloatValueList(meta);
			assignmentDoubleValueList(meta);
			assignmentDateTimeValueList(meta);
			assignmentBooleanValueList(meta);
			assignmentStringValueMap(meta);
			assignmentIntegerValueMap(meta);
			assignmentLongValueMap(meta);
			assignmentFloatValueMap(meta);
			assignmentDoubleValueMap(meta);
			assignmentDateTimeValueMap(meta);
			assignmentBooleanValueMap(meta);
			assignmentProperties(meta);
			emc.beginTransaction(Meta.class);
			emc.persist(meta, CheckPersistType.all);
			emc.commit();
		}
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Meta o = emc.find(id, Meta.class);
			wo.setAssertStringValue(assertStringValue(o, meta));
			wo.setAssertStringLobValue(assertStringLobValue(o, meta));
			wo.setAssertIntegerValue(assertIntegerValue(o, meta));
			wo.setAssertLongValue(assertLongValue(o, meta));
			wo.setAssertFloatValue(assertFloatValue(o, meta));
			wo.setAssertDoubleValue(assertDoubleValue(o, meta));
			wo.setAssertDateTimeValue(assertDateTimeValue(o, meta));
			wo.setAssertDateValue(assertDateValue(o, meta));
			wo.setAssertTimeValue(assertTimeValue(o, meta));
			wo.setAssertBooleanValue(assertBooleanValue(o, meta));
		}
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -4468460971064420534L;

		private String assertStringValue;

		private String assertStringLobValue;

		private String assertIntegerValue;

		private String assertLongValue;

		private String assertFloatValue;

		private String assertDoubleValue;

		private String assertDateTimeValue;

		private String assertDateValue;

		private String assertTimeValue;
		private String assertBooleanValue;

		public String getAssertStringValue() {
			return assertStringValue;
		}

		public void setAssertStringValue(String assertStringValue) {
			this.assertStringValue = assertStringValue;
		}

		public String getAssertStringLobValue() {
			return assertStringLobValue;
		}

		public void setAssertStringLobValue(String assertStringLobValue) {
			this.assertStringLobValue = assertStringLobValue;
		}

		public String getAssertBooleanValue() {
			return assertBooleanValue;
		}

		public void setAssertBooleanValue(String assertBooleanValue) {
			this.assertBooleanValue = assertBooleanValue;
		}

		public String getAssertDateTimeValue() {
			return assertDateTimeValue;
		}

		public void setAssertDateTimeValue(String assertDateTimeValue) {
			this.assertDateTimeValue = assertDateTimeValue;
		}

		public String getAssertDateValue() {
			return assertDateValue;
		}

		public void setAssertDateValue(String assertDateValue) {
			this.assertDateValue = assertDateValue;
		}

		public String getAssertTimeValue() {
			return assertTimeValue;
		}

		public void setAssertTimeValue(String assertTimeValue) {
			this.assertTimeValue = assertTimeValue;
		}

		public String getAssertIntegerValue() {
			return assertIntegerValue;
		}

		public void setAssertIntegerValue(String assertIntegerValue) {
			this.assertIntegerValue = assertIntegerValue;
		}

		public String getAssertLongValue() {
			return assertLongValue;
		}

		public void setAssertLongValue(String assertLongValue) {
			this.assertLongValue = assertLongValue;
		}

		public String getAssertFloatValue() {
			return assertFloatValue;
		}

		public void setAssertFloatValue(String assertFloatValue) {
			this.assertFloatValue = assertFloatValue;
		}

		public String getAssertDoubleValue() {
			return assertDoubleValue;
		}

		public void setAssertDoubleValue(String assertDoubleValue) {
			this.assertDoubleValue = assertDoubleValue;
		}

	}

	private static void assignmentStringValue(Meta persistObject) {
		persistObject.setStringValue(STRING1);
	}

	private static void assignmentStringLobValue(Meta persistObject) {
		persistObject.setStringLobValue(StringUtils.repeat(STRING2, 40));
	}

	private static void assignmentIntegerValue(Meta persistObject) {
		persistObject.setIntegerValue(INTEGER1);
	}

	private static void assignmentLongValue(Meta persistObject) {
		persistObject.setLongValue(LONG1);
	}

	private static void assignmentFloatValue(Meta persistObject) {
		persistObject.setFloatValue(FLOAT1);
	}

	private static void assignmentDoubleValue(Meta persistObject) {
		persistObject.setDoubleValue(DOUBLE1);
	}

	private static void assignmentDateTimeValue(Meta persistObject) {
		persistObject.setDateTimeValue(dateTime1);
	}

	private static void assignmentDateValue(Meta persistObject) {
		persistObject.setDateValue(date1);
	}

	private static void assignmentTimeValue(Meta persistObject) {
		persistObject.setTimeValue(time1);
	}

	private static void assignmentBooleanValue(Meta persistObject) {
		persistObject.setBooleanValue(BOOLEAN1);
	}

	private static void assignmentStringValueList(Meta persistObject) {
		List<String> list = new ArrayList<>();
		list.add(STRING1);
		list.add(STRING2);
		persistObject.setStringValueList(list);
	}

	private static void assignmentIntegerValueList(Meta persistObject) {
		List<Integer> list = new ArrayList<>();
		list.add(INTEGER1);
		list.add(INTEGER2);
		persistObject.setIntegerValueList(list);
	}

	private static void assignmentLongValueList(Meta persistObject) {
		List<Long> list = new ArrayList<>();
		list.add(LONG1);
		list.add(LONG2);
		persistObject.setLongValueList(list);
	}

	private static void assignmentFloatValueList(Meta persistObject) {
		List<Float> list = new ArrayList<>();
		list.add(FLOAT1);
		list.add(FLOAT2);
		persistObject.setFloatValueList(list);
	}

	private static void assignmentDoubleValueList(Meta persistObject) {
		List<Double> list = new ArrayList<>();
		list.add(DOUBLE1);
		list.add(DOUBLE2);
		persistObject.setDoubleValueList(list);
	}

	private static void assignmentDateTimeValueList(Meta persistObject) {
		List<Date> list = new ArrayList<>();
		list.add(dateTime1);
		list.add(dateTime2);
		persistObject.setDateTimeValueList(list);
	}

	private static void assignmentBooleanValueList(Meta persistObject) {
		List<Boolean> list = new ArrayList<>();
		list.add(BOOLEAN1);
		list.add(BOOLEAN1);
		persistObject.setBooleanValueList(list);
	}

	private static void assignmentStringValueMap(Meta persistObject) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		map.put(STRING1, STRING1);
		map.put(STRING2, STRING2);
		persistObject.setStringValueMap(map);
	}

	private static void assignmentIntegerValueMap(Meta persistObject) {
		LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
		map.put(STRING1, INTEGER1);
		map.put(STRING2, INTEGER2);
		persistObject.setIntegerValueMap(map);
	}

	private static void assignmentLongValueMap(Meta persistObject) {
		LinkedHashMap<String, Long> map = new LinkedHashMap<>();
		map.put(STRING1, LONG1);
		map.put(STRING2, LONG2);
		persistObject.setLongValueMap(map);
	}

	private static void assignmentFloatValueMap(Meta persistObject) {
		LinkedHashMap<String, Float> map = new LinkedHashMap<>();
		map.put(STRING1, FLOAT1);
		map.put(STRING2, FLOAT2);
		persistObject.setFloatValueMap(map);
	}

	private static void assignmentDoubleValueMap(Meta persistObject) {
		LinkedHashMap<String, Double> map = new LinkedHashMap<>();
		map.put(STRING1, DOUBLE1);
		map.put(STRING2, DOUBLE2);
		persistObject.setDoubleValueMap(map);
	}

	private static void assignmentDateTimeValueMap(Meta persistObject) {
		LinkedHashMap<String, Date> map = new LinkedHashMap<>();
		map.put(STRING1, dateTime1);
		map.put(STRING2, dateTime2);
		persistObject.setDateTimeValueMap(map);
	}

	private static void assignmentBooleanValueMap(Meta persistObject) {
		LinkedHashMap<String, Boolean> map = new LinkedHashMap<>();
		map.put(STRING1, BOOLEAN1);
		map.put(STRING2, BOOLEAN2);
		persistObject.setBooleanValueMap(map);
	}

	private static void assignmentProperties(Meta persistObject) {
		persistObject.getProperties().setName(STRING1);
		persistObject.getProperties().setConut(INTEGER1);
	}

	private String assertStringValue(Meta fromDbObject, Meta persistObject) {
		String message = "stringValue %s formDbObject:%s, persistObject:%s.";
		message = String.format(message,
				StringUtils.equals(fromDbObject.getStringValue(), persistObject.getStringValue()),
				fromDbObject.getStringValue(), persistObject.getStringValue());
		return message;
	}

	private String assertStringLobValue(Meta fromDbObject, Meta persistObject) {
		String message = "stringLobValue %s formDbObject:%s, persistObject:%s.";
		message = String.format(message,
				StringUtils.equals(fromDbObject.getStringLobValue(), persistObject.getStringLobValue()),
				fromDbObject.getStringLobValue(), persistObject.getStringLobValue());
		return message;
	}

	private String assertIntegerValue(Meta fromDbObject, Meta persistObject) {
		String message = "integerValue %s formDbObject:%s, persistObject:%s.";
		message = String.format(message,
				Objects.equals(fromDbObject.getIntegerValue(), persistObject.getIntegerValue()),
				fromDbObject.getIntegerValue(), persistObject.getIntegerValue());
		return message;
	}

	private String assertLongValue(Meta fromDbObject, Meta persistObject) {
		String message = "longValue %s formDbObject:%s, persistObject:%s.";
		message = String.format(message, Objects.equals(fromDbObject.getLongValue(), persistObject.getLongValue()),
				fromDbObject.getLongValue(), persistObject.getLongValue());
		return message;
	}

	private String assertFloatValue(Meta fromDbObject, Meta persistObject) {
		String message = "floatValue %s formDbObject:%s, persistObject:%s.";
		message = String.format(message, Objects.equals(fromDbObject.getFloatValue(), persistObject.getFloatValue()),
				fromDbObject.getFloatValue(), persistObject.getFloatValue());
		return message;
	}

	private String assertDoubleValue(Meta fromDbObject, Meta persistObject) {
		String message = "doubleValue %s formDbObject:%s, persistObject:%s.";
		message = String.format(message, Objects.equals(fromDbObject.getDoubleValue(), persistObject.getDoubleValue()),
				fromDbObject.getDoubleValue(), persistObject.getDoubleValue());
		return message;
	}

	private String assertDateTimeValue(Meta fromDbObject, Meta persistObject) {
		String message = "dateTimeValue %s formDbObject:%s, persistObject:%s.";
		message = String.format(message,
				Objects.equals(fromDbObject.getDateTimeValue().getTime(), persistObject.getDateTimeValue().getTime()),
				fromDbObject.getDateTimeValue(), persistObject.getDateTimeValue());
		return message;
	}

	private String assertDateValue(Meta fromDbObject, Meta persistObject) {
		String message = "dateValue %s formDbObject:%s, persistObject:%s.";
		message = String.format(message,
				Objects.equals(fromDbObject.getDateValue().getTime(), persistObject.getDateValue().getTime()),
				fromDbObject.getDateValue(), persistObject.getDateValue());
		return message;
	}

	private String assertTimeValue(Meta fromDbObject, Meta persistObject) {
		String message = "timeValue %s formDbObject:%s, persistObject:%s.";
		message = String.format(message,
				Objects.equals(fromDbObject.getTimeValue().getTime(), persistObject.getTimeValue().getTime()),
				fromDbObject.getTimeValue(), persistObject.getTimeValue());
		return message;
	}

	private String assertBooleanValue(Meta fromDbObject, Meta persistObject) {
		String message = "booleanValue %s formDbObject:%s, persistObject:%s.";
		message = String.format(message,
				Objects.equals(fromDbObject.getBooleanValue(), persistObject.getBooleanValue()),
				fromDbObject.getBooleanValue(), persistObject.getBooleanValue());
		return message;
	}

}
