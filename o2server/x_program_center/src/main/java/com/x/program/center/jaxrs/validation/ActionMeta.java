package com.x.program.center.jaxrs.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.core.entity.validation.Meta;

class ActionMeta extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		String id = StringTools.uniqueToken();
		Meta meta = new Meta();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			meta.setId(id);
			assignmentStringValue(meta);
			assignmentStringLobValue(meta);
			assignmentBooleanValue(meta);
			assignmentDateTimeValue(meta);
			assignmentDateValue(meta);
			assignmentTimeValue(meta);
			assignmentIntegerValue(meta);
			assignmentLongValue(meta);
			assignmentFloatValue(meta);
			assignmentDoubleValue(meta);
			assignmentListValue(meta);
			assignmentMapValue(meta);
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
			wo.setAssertBooleanValue(assertBooleanValue(o, meta));
			wo.setAssertDateTimeValue(assertDateTimeValue(o, meta));
			wo.setAssertDateValue(assertDateValue(o, meta));
			wo.setAssertTimeValue(assertTimeValue(o, meta));
			wo.setAssertIntegerValue(assertIntegerValue(o, meta));
			wo.setAssertLongValue(assertLongValue(o, meta));
			wo.setAssertFloatValue(assertFloatValue(o, meta));
			wo.setAssertDoubleValue(assertDoubleValue(o, meta));
			wo.setAssertListValue(assertListValue(o, meta));
			wo.setAssertMapValue(assertMapValue(o, meta));
			wo.setAssertProperties(assertProperties(o, meta));
		}
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -4468460971064420534L;

		private String assertStringValue;

		private String assertStringLobValue;

		private String assertBooleanValue;

		private String assertDateTimeValue;

		private String assertDateValue;

		private String assertTimeValue;

		private String assertIntegerValue;

		private String assertLongValue;

		private String assertFloatValue;

		private String assertDoubleValue;

		private String assertListValue;

		private String assertMapValue;

		private String assertProperties;

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

		public String getAssertListValue() {
			return assertListValue;
		}

		public void setAssertListValue(String assertListValue) {
			this.assertListValue = assertListValue;
		}

		public String getAssertMapValue() {
			return assertMapValue;
		}

		public void setAssertMapValue(String assertMapValue) {
			this.assertMapValue = assertMapValue;
		}

		public String getAssertProperties() {
			return assertProperties;
		}

		public void setAssertProperties(String assertProperties) {
			this.assertProperties = assertProperties;
		}

	}

	private static void assignmentStringValue(Meta persistObject) {
		persistObject.setStringValue("123456789");
	}

	private static void assignmentStringLobValue(Meta persistObject) {
		persistObject.setStringLobValue(StringUtils.repeat("123456789", 40));
	}

	private static void assignmentBooleanValue(Meta persistObject) {
		persistObject.setBooleanValue(true);
	}

	private static void assignmentDateTimeValue(Meta persistObject) {
		persistObject.setDateTimeValue(new Date());
	}

	private static void assignmentDateValue(Meta persistObject) {
		persistObject.setDateValue(new Date());
	}

	private static void assignmentTimeValue(Meta persistObject) {
		persistObject.setTimeValue(new Date());
	}

	private static void assignmentIntegerValue(Meta persistObject) {
		persistObject.setIntegerValue(123456789);
	}

	private static void assignmentLongValue(Meta persistObject) {
		persistObject.setLongValue(Integer.MAX_VALUE * 10L);
	}

	private static void assignmentFloatValue(Meta persistObject) {
		persistObject.setFloatValue(12345.6789);
	}

	private static void assignmentDoubleValue(Meta persistObject) {
		persistObject.setDoubleValue(12345.6789);
	}

	private static void assignmentListValue(Meta persistObject) {
		List<String> list = new ArrayList<>();
		list.add("aaaa");
		list.add("bbbb");
		list.add("cccc");
		list.add("dddd");
		persistObject.setListValueList(list);
	}

	private static void assignmentMapValue(Meta persistObject) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		map.put("1", "aaaa");
		map.put("2", "bbbb");
		map.put("3", "cccc");
		map.put("4", "dddd");
		persistObject.setMapValueMap(map);
	}

	private static void assignmentProperties(Meta persistObject) {
		persistObject.getProperties().setName("aaaa");
		persistObject.getProperties().setConut(1);
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

	private String assertBooleanValue(Meta fromDbObject, Meta persistObject) {
		String message = "booleanValue %s formDbObject:%s, persistObject:%s.";
		message = String.format(message,
				Objects.equals(fromDbObject.getBooleanValue(), persistObject.getBooleanValue()),
				fromDbObject.getBooleanValue(), persistObject.getBooleanValue());
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

	private String assertListValue(Meta fromDbObject, Meta persistObject) {
		String message = "listValue %s formDbObject:%s, persistObject:%s.";
		boolean check = true;
		for (int i = 0; i < fromDbObject.getListValueList().size(); i++) {
			if (!StringUtils.equals(fromDbObject.getListValueList().get(i), persistObject.getListValueList().get(i))) {
				check = false;
			}
		}
		for (int i = 0; i < persistObject.getListValueList().size(); i++) {
			if (!StringUtils.equals(persistObject.getListValueList().get(i), fromDbObject.getListValueList().get(i))) {
				check = false;
			}
		}
		message = String.format(message, check, XGsonBuilder.toJson(fromDbObject.getListValueList()),
				XGsonBuilder.toJson(persistObject.getListValueList()));
		return message;
	}

	private String assertMapValue(Meta fromDbObject, Meta persistObject) {
		String message = "mapValue %s formDbObject:%s, persistObject:%s.";
		boolean check = true;
		for (Entry<String, String> en : fromDbObject.getMapValueMap().entrySet()) {
			if (!StringUtils.equals(en.getValue(), persistObject.getMapValueMap().get(en.getKey()))) {
				check = false;
			}
		}
		for (Entry<String, String> en : persistObject.getMapValueMap().entrySet()) {
			if (!StringUtils.equals(en.getValue(), fromDbObject.getMapValueMap().get(en.getKey()))) {
				check = false;
			}
		}
		message = String.format(message, check, XGsonBuilder.toJson(fromDbObject.getMapValueMap()),
				XGsonBuilder.toJson(persistObject.getMapValueMap()));
		return message;
	}

	private String assertProperties(Meta fromDbObject, Meta persistObject) {
		String message = "properties %s formDbObject:%s, persistObject:%s.";
		message = String.format(message, Objects.equals(fromDbObject.getProperties(), persistObject.getProperties()),
				fromDbObject.getProperties(), persistObject.getProperties());
		return message;
	}

}
