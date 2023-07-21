package com.x.base.core.container;

public enum FieldType {
	/* string */
	stringValue, stringValueList, stringValueMap,
	/* boolean */
	booleanValue, booleanValueList, booleanValueMap,
	/* integer */
	integerValue, integerValueList, integerValueMap,
	/* double */
	doubleValue, doubleValueList, doubleValueMap,
	/* long */
	longValue, longValueList, longValueMap,
	/* float */
	floatValue, floatValueList, floatValueMap,
	/* date */
	dateValue, dateValueList, dateValueMap,
	/* byte */
	byteValueArray,
	/* enum */
	enumValue,
	/* JsonProperties */
	JsonPropertiesValue;
}
