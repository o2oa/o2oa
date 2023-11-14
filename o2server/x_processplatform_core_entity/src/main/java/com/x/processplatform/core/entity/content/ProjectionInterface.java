package com.x.processplatform.core.entity.content;

import java.util.Date;

public interface ProjectionInterface {

	public abstract String getStringValue01();

	public abstract void setStringValue01(String stringValue01);

	public abstract String getStringValue02();

	public abstract void setStringValue02(String stringValue02);

	public abstract String getStringValue03();

	public abstract void setStringValue03(String stringValue03);

	public abstract String getStringValue04();

	public abstract void setStringValue04(String stringValue04);

	public abstract String getStringValue05();

	public abstract void setStringValue05(String stringValue05);

	public abstract String getStringValue06();

	public abstract void setStringValue06(String stringValue06);

	public abstract String getStringValue07();

	public abstract void setStringValue07(String stringValue07);

	public abstract String getStringValue08();

	public abstract void setStringValue08(String stringValue08);

	public abstract String getStringValue09();

	public abstract void setStringValue09(String stringValue09);

	public abstract String getStringValue10();

	public abstract void setStringValue10(String stringValue10);

	public abstract Double getDoubleValue01();

	public abstract void setDoubleValue01(Double doubleValue01);

	public abstract Double getDoubleValue02();

	public abstract void setDoubleValue02(Double doubleValue02);

	public abstract Double getDoubleValue03();

	public abstract void setDoubleValue03(Double doubleValue03);

	public abstract Double getDoubleValue04();

	public abstract void setDoubleValue04(Double doubleValue04);

	public abstract Double getDoubleValue05();

	public abstract void setDoubleValue05(Double doubleValue05);

	public abstract Long getLongValue01();

	public abstract void setLongValue01(Long longValue01);

	public abstract Long getLongValue02();

	public abstract void setLongValue02(Long longValue02);

	public abstract Long getLongValue03();

	public abstract void setLongValue03(Long longValue03);

	public abstract Long getLongValue04();

	public abstract void setLongValue04(Long longValue04);

	public abstract Long getLongValue05();

	public abstract void setLongValue05(Long longValue05);

	public abstract Date getDateTimeValue01();

	public abstract void setDateTimeValue01(Date dateTimeValue01);

	public abstract Date getDateTimeValue02();

	public abstract void setDateTimeValue02(Date dateTimeValue02);

	public abstract Date getDateTimeValue03();

	public abstract void setDateTimeValue03(Date dateTimeValue03);

	public abstract Date getDateTimeValue04();

	public abstract void setDateTimeValue04(Date dateTimeValue04);

	public abstract Date getDateTimeValue05();

	public abstract void setDateTimeValue05(Date dateTimeValue05);

	public abstract Date getDateValue01();

	public abstract void setDateValue01(Date dateValue01);

	public abstract Date getDateValue02();

	public abstract void setDateValue02(Date dateValue02);

	public abstract Date getTimeValue01();

	public abstract void setTimeValue01(Date timeValue01);

	public abstract Date getTimeValue02();

	public abstract void setTimeValue02(Date timeValue02);

	public abstract Boolean getBooleanValue01();

	public abstract void setBooleanValue01(Boolean booleanValue01);

	public abstract Boolean getBooleanValue02();

	public abstract void setBooleanValue02(Boolean booleanValue02);

	public default void copyProjectionFields(ProjectionInterface source) {

		setStringValue01(source.getStringValue01());

		setStringValue02(source.getStringValue02());

		setStringValue03(source.getStringValue03());

		setStringValue04(source.getStringValue04());

		setStringValue05(source.getStringValue05());

		setStringValue06(source.getStringValue06());

		setStringValue07(source.getStringValue07());

		setStringValue08(source.getStringValue08());

		setStringValue09(source.getStringValue09());

		setStringValue10(source.getStringValue10());

		setDoubleValue01(source.getDoubleValue01());

		setDoubleValue02(source.getDoubleValue02());

		setDoubleValue03(source.getDoubleValue03());

		setDoubleValue04(source.getDoubleValue04());

		setDoubleValue05(source.getDoubleValue05());

		setLongValue01(source.getLongValue01());

		setLongValue02(source.getLongValue02());

		setLongValue03(source.getLongValue03());

		setLongValue04(source.getLongValue04());

		setLongValue05(source.getLongValue05());

		setDateTimeValue01(source.getDateTimeValue01());

		setDateTimeValue02(source.getDateTimeValue02());

		setDateTimeValue03(source.getDateTimeValue03());

		setDateTimeValue04(source.getDateTimeValue04());

		setDateTimeValue05(source.getDateTimeValue05());

		setTimeValue01(source.getTimeValue01());

		setTimeValue02(source.getTimeValue02());

		setDateValue01(source.getDateValue01());

		setDateValue02(source.getDateValue02());

		setBooleanValue01(source.getBooleanValue01());

		setBooleanValue02(source.getBooleanValue02());

	}

}
