package com.x.query.core.express.plan;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.DateTools;
import com.x.cms.core.entity.Document;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public class FilterEntry extends GsonPropertyObject {

	public static final String FORMAT_TEXTVALUE = "textValue";

	public static final String FORMAT_NUMBERVALUE = "numberValue";

	public static final String FORMAT_BOOLEANVALUE = "booleanValue";

	public static final String FORMAT_DATETIMEVALUE = "dateTimeValue";

	public static final String FORMAT_DATEVALUE = "dateValue";

	public static final String FORMAT_TIMEVALUE = "timeValue";

	public static final String DEFINE_TIME = "@time";

	public static final String DEFINE_DATE = "@date";

	public static final String DEFINE_MONTH = "@month";

	public static final String DEFINE_YEAR = "@year";

	public static final String DEFINE_SEASON = "@season";

	public static final String DEFINE_PERSON = "@person";

	public static final String DEFINE_IDENTITYLIST = "@identityList";

	public static final String DEFINE_UNITLIST = "@unitList";

	public static final String DEFINE_UNITALLLIST = "@unitAllList";

	public static final String WILDCARD = "*";

	/** 用于customFilterEntry */
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
		if(path.indexOf("(")>-1 && path.indexOf(")")>-1){
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
			case FORMAT_DATEVALUE:
			case FORMAT_TIMEVALUE:
				boolean flag = DateTools.isDateTimeOrDateOrTime(value) || StringUtils.equalsIgnoreCase(DEFINE_TIME, value)
						|| StringUtils.equalsIgnoreCase(DEFINE_DATE, value)
						|| StringUtils.equalsIgnoreCase(DEFINE_MONTH, value)
						|| StringUtils.equalsIgnoreCase(DEFINE_SEASON, value)
						|| StringUtils.equalsIgnoreCase(DEFINE_YEAR, value);
				if (flag) {
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

	public Predicate toPredicate(CriteriaBuilder cb, Root<Item> root, Runtime runtime, Predicate p)
			throws Exception {
		String compareValue = this.compareValue(runtime);
		String compareOtherValue = this.compareOtherValue(runtime);
		if (StringUtils.equals(this.formatType, FORMAT_BOOLEANVALUE)) {
			Boolean booleanValue = BooleanUtils.toBoolean(compareValue);
			if (null != booleanValue) {
				if (Comparison.isNotEquals(this.comparison)) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, cb.or(cb.isNull(root.get(Item_.booleanValue)),
							cb.equal(root.get(Item_.booleanValue), booleanValue)));
				} else {
					p = cb.and(p, cb.equal(root.get(Item_.booleanValue), booleanValue));
				}
			}
		} else if (StringUtils.equals(this.formatType, FORMAT_NUMBERVALUE)) {
			Double doubleValue = NumberUtils.toDouble(compareValue);
			if (null != doubleValue) {
				if (Comparison.isNotEquals(this.comparison)) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, cb.or(cb.isNull(root.get(Item_.numberValue)),
							cb.equal(root.get(Item_.numberValue), doubleValue)));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					p = cb.and(p, cb.greaterThan(root.get(Item_.numberValue), doubleValue));
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.numberValue), doubleValue));
				} else if (Comparison.isLessThan(this.comparison)) {
					p = cb.and(p, cb.lessThan(root.get(Item_.numberValue), doubleValue));
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.numberValue), doubleValue));
				} else if (Comparison.isBetween(this.comparison)) {
					Double doubleOtherValue = NumberUtils.toDouble(compareOtherValue);
					if (null != doubleOtherValue) {
						p = cb.and(p, cb.between(root.get(Item_.numberValue), doubleValue, doubleOtherValue));
					}
				} else {
					p = cb.and(p, cb.equal(root.get(Item_.numberValue), doubleValue));
				}
			}
		} else if (StringUtils.equals(this.formatType, FORMAT_DATEVALUE)) {
			/* 日期值比较 */
			if (StringUtils.isNotEmpty(compareValue)) {
				Date value = null;
				Date otherValue = null;
				if (DateTools.isDateTime(compareValue)) {
					value = DateTools.parseDateTime(compareValue);
				} else if (DateTools.isDate(compareValue)) {
					value = DateTools.parseDate(compareValue);
				}
				if (DateTools.isDateTime(compareOtherValue)) {
					otherValue = DateTools.parseDateTime(compareOtherValue);
				} else if (DateTools.isDate(compareOtherValue)) {
					otherValue = DateTools.parseDate(compareOtherValue);
				}
				if (null != otherValue) {
					otherValue = DateTools.ceilDate(otherValue, 0);
				}
				if (null != value) {
					value = DateTools.floorDate(value, 0);
					if (Comparison.isNotEquals(this.comparison)) {
						/** 不等于返回等于值,在外部运算 */
						p = cb.and(p, cb.or(cb.isNull(root.get(Item_.dateValue)),
								cb.equal(root.get(Item_.dateValue), value)));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(Item_.dateValue), value));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.dateValue), value));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(Item_.dateValue), value));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.dateValue), value));
					} else if (Comparison.isBetween(this.comparison)) {
						if (null != otherValue) {
							p = cb.and(p, cb.between(root.get(Item_.dateValue), value, otherValue));
						} else {
							throw new Exception("unkown comparison:" + this.comparison);
						}
					} else {
						p = cb.and(p, cb.equal(root.get(Item_.dateValue), value));
					}
				} else if (StringUtils.equals(compareValue, DEFINE_DATE)
						|| StringUtils.equals(compareValue, DEFINE_MONTH)
						|| StringUtils.equals(compareValue, DEFINE_SEASON)
						|| StringUtils.equals(compareValue, DEFINE_YEAR)) {
					Date floor = null;
					Date ceil = null;
					if (StringUtils.equals(compareValue, DEFINE_DATE)) {
						floor = DateTools.floorDate(new Date(), 0);
						ceil = floor;
					} else if (StringUtils.equals(compareValue, DEFINE_MONTH)) {
						floor = DateTools.floorMonth(new Date(), 0);
						ceil = DateTools.ceilMonth(new Date(), 0);
					} else if (StringUtils.equals(compareValue, DEFINE_SEASON)) {
						floor = DateTools.floorSeason(new Date(), 0);
						ceil = DateTools.ceilSeason(new Date(), 0);
					} else {
						floor = DateTools.floorYear(new Date(), 0);
						ceil = DateTools.ceilYear(new Date(), 0);
					}
					if (Comparison.isNotEquals(this.comparison)) {
						/** 不等于返回等于值,在外部运算 */
						p = cb.and(p, cb.or(cb.isNull(root.get(Item_.dateValue)),
								cb.between(root.get(Item_.dateValue), floor, ceil)));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(Item_.dateValue), ceil));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.dateValue), floor));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(Item_.dateValue), floor));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.dateValue), ceil));
					} else if (Comparison.isBetween(this.comparison)) {
						// throw new Exception("unkown comparison:" + this.comparison);
					} else {
						throw new Exception("unkown comparison:" + this.comparison);
					}
				} else if (StringUtils.equals(compareValue, DEFINE_TIME)) {
					if (Comparison.isNotEquals(this.comparison)) {
						/** 不等于返回等于值,在外部运算 */
						p = cb.and(p, cb.or(cb.isNull(root.get(Item_.dateValue)),
								cb.equal(root.get(Item_.dateValue), new Date())));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(Item_.dateValue), new Date()));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.dateValue), new Date()));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(Item_.dateValue), new Date()));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.dateValue), new Date()));
					} else if (Comparison.isBetween(this.comparison)) {
						// throw new Exception("unkown comparison:" + this.comparison);
					} else {
						throw new Exception("unkown comparison:" + this.comparison);
					}
				}
			}
		} else if (StringUtils.equals(this.formatType, FORMAT_TIMEVALUE)) {
			/* 时间值比较 */
			if (StringUtils.isNotEmpty(compareValue)) {
				Date value = null;
				Date otherValue = null;
				if (DateTools.isDateTime(compareValue)) {
					value = DateTools.parseDateTime(compareValue);
				} else if (DateTools.isTime(compareValue)) {
					value = DateTools.parseTime(compareValue);
				}
				if (DateTools.isDateTime(compareOtherValue)) {
					otherValue = DateTools.parseDateTime(compareOtherValue);
				} else if (DateTools.isTime(compareOtherValue)) {
					otherValue = DateTools.parseTime(compareOtherValue);
				}
				if (null != value) {
					if (Comparison.isNotEquals(this.comparison)) {
						/** 不等于返回等于值,在外部运算 */
						p = cb.and(p, cb.or(cb.isNull(root.get(Item_.timeValue)),
								cb.equal(root.get(Item_.timeValue), value)));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(Item_.timeValue), value));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.timeValue), value));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(Item_.timeValue), value));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.timeValue), value));
					} else if (Comparison.isBetween(this.comparison)) {
						if (null != otherValue) {
							p = cb.and(p, cb.between(root.get(Item_.timeValue), value, otherValue));
						}
					} else {
						p = cb.and(p, cb.equal(root.get(Item_.timeValue), value));
					}
				} else if (StringUtils.equals(compareValue, DEFINE_TIME)) {
					if (Comparison.isNotEquals(this.comparison)) {
						/** 不等于返回等于值,在外部运算 */
						p = cb.and(p, cb.or(cb.isNull(root.get(Item_.timeValue)),
								cb.equal(root.get(Item_.timeValue), new Date())));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(Item_.timeValue), new Date()));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.timeValue), new Date()));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(Item_.timeValue), new Date()));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.timeValue), new Date()));
					} else if (Comparison.isBetween(this.comparison)) {
						throw new Exception("unkown comparison:" + this.comparison);
					} else {
						throw new Exception("unkown comparison:" + this.comparison);
					}
				}
			}
		} else if (StringUtils.equals(this.formatType, FORMAT_DATETIMEVALUE)) {
			Date value = null;
			Date otherValue = null;
			if (DateTools.isDateTime(compareValue)) {
				value = DateTools.parseDateTime(compareValue);
			} else if (DateTools.isDate(compareValue)) {
				value = DateTools.parseDate(compareValue);
			}
			if (DateTools.isDateTime(compareOtherValue)) {
				otherValue = DateTools.parseDateTime(compareOtherValue);
			} else if (DateTools.isDate(compareOtherValue)) {
				otherValue = DateTools.parseDate(compareOtherValue);
			}
			if (null != value) {
				if (Comparison.isNotEquals(this.comparison)) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, cb.or(cb.isNull(root.get(Item_.dateTimeValue)),
							cb.equal(root.get(Item_.dateTimeValue), value)));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					p = cb.and(p, cb.greaterThan(root.get(Item_.dateTimeValue), value));
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.dateTimeValue), value));
				} else if (Comparison.isLessThan(this.comparison)) {
					p = cb.and(p, cb.lessThan(root.get(Item_.dateTimeValue), value));
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.dateTimeValue), value));
				} else if (Comparison.isBetween(this.comparison)) {
					if (null != otherValue) {
						p = cb.and(p, cb.between(root.get(Item_.dateTimeValue), value, otherValue));
					} else {
						throw new Exception("unkown comparison:" + this.comparison);
					}
				} else {
					p = cb.and(p, cb.equal(root.get(Item_.dateTimeValue), value));
				}
			} else if (StringUtils.equals(compareValue, DEFINE_DATE) || StringUtils.equals(compareValue, DEFINE_MONTH)
					|| StringUtils.equals(compareValue, DEFINE_SEASON)
					|| StringUtils.equals(compareValue, DEFINE_YEAR)) {
				Date floor = null;
				Date ceil = null;
				if (StringUtils.equals(compareValue, DEFINE_DATE)) {
					floor = DateTools.floorDate(new Date(), 0);
					ceil = DateTools.ceilDate(new Date(), 0);
				} else if (StringUtils.equals(compareValue, DEFINE_MONTH)) {
					floor = DateTools.floorMonth(new Date(), 0);
					ceil = DateTools.ceilMonth(new Date(), 0);
				} else if (StringUtils.equals(compareValue, DEFINE_SEASON)) {
					floor = DateTools.floorSeason(new Date(), 0);
					ceil = DateTools.ceilSeason(new Date(), 0);
				} else {
					floor = DateTools.floorYear(new Date(), 0);
					ceil = DateTools.ceilYear(new Date(), 0);
				}
				if (Comparison.isNotEquals(this.comparison)) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, cb.or(cb.isNull(root.get(Item_.dateTimeValue)),
							cb.between(root.get(Item_.dateTimeValue), floor, ceil)));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					p = cb.and(p, cb.greaterThan(root.get(Item_.dateTimeValue), ceil));
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.dateTimeValue), floor));
				} else if (Comparison.isLessThan(this.comparison)) {
					p = cb.and(p, cb.lessThan(root.get(Item_.dateTimeValue), floor));
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.dateTimeValue), ceil));
				} else if (Comparison.isBetween(this.comparison)) {
					// p = cb.and(p, cb.between(root.get(Item_.dateTimeValue), floor, ceil));
				} else {
					throw new Exception("unkown comparison:" + this.comparison);
				}
			} else if (StringUtils.equals(compareValue, DEFINE_TIME)) {
				if (Comparison.isNotEquals(this.comparison)) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, cb.or(cb.isNull(root.get(Item_.dateTimeValue)),
							cb.equal(root.get(Item_.dateTimeValue), new Date())));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					p = cb.and(p, cb.greaterThan(root.get(Item_.dateTimeValue), new Date()));
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.dateTimeValue), new Date()));
				} else if (Comparison.isLessThan(this.comparison)) {
					p = cb.and(p, cb.lessThan(root.get(Item_.dateTimeValue), new Date()));
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.dateTimeValue), new Date()));
				} else if (Comparison.isBetween(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else {
					throw new Exception("unkown comparison:" + this.comparison);
				}
			}
		} else {
			/* TEXT 内容值 */
			if (StringUtils.equals(compareValue, DEFINE_PERSON)) {
				if (Comparison.isNotEquals(this.comparison)) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, cb.or(cb.isNull(root.get(Item_.stringShortValue)),
							cb.equal(root.get(Item_.stringShortValue), runtime.person)));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					p = cb.and(p, cb.greaterThan(root.get(Item_.stringShortValue), runtime.person));
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.stringShortValue), runtime.person));
				} else if (Comparison.isLessThan(this.comparison)) {
					p = cb.and(p, cb.lessThan(root.get(Item_.stringShortValue), runtime.person));
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.stringShortValue), runtime.person));
				} else if (Comparison.isLike(this.comparison)) {
					p = cb.and(p, cb.like(root.get(Item_.stringShortValue), "%" + runtime.person + "%"));
				} else if (Comparison.isNotLike(this.comparison)) {
					p = cb.and(p, cb.notLike(root.get(Item_.stringShortValue), "%" + runtime.person + "%"));
				} else if (Comparison.isBetween(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else {
					p = cb.and(p, cb.equal(root.get(Item_.stringShortValue), runtime.person));
				}
			} else if (StringUtils.equals(compareValue, DEFINE_UNITLIST)) {
				if (Comparison.isNotEquals(this.comparison)) {
					/** 不等于返回等于值,在外部运算 */
					if(runtime.unitList.size()==1){
						p = cb.and(p, cb.or(cb.isNull(root.get(Item_.stringShortValue)),
								cb.equal(root.get(Item_.stringShortValue), runtime.unitList.get(0))));
					}else {
						p = cb.and(p, root.get(Item_.stringShortValue).in(runtime.unitList));
					}
				} else if (Comparison.isGreaterThan(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLessThan(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLike(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isNotLike(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isBetween(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else {
					if(runtime.unitList.size()==1){
						p = cb.and(p, cb.equal(root.get(Item_.stringShortValue), runtime.unitList.get(0)));
					}else {
						p = cb.and(p, root.get(Item_.stringShortValue).in(runtime.unitList));
					}
				}
			} else if (StringUtils.equals(compareValue, DEFINE_UNITALLLIST)) {
				if (Comparison.isNotEquals(this.comparison)) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, root.get(Item_.stringShortValue).in(runtime.unitAllList));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLessThan(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLike(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isNotLike(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isBetween(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else {
					p = cb.and(p, root.get(Item_.stringShortValue).in(runtime.unitAllList));
				}
			} else if (StringUtils.equals(compareValue, DEFINE_IDENTITYLIST)) {
				if (Comparison.isNotEquals(this.comparison)) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, root.get(Item_.stringShortValue).in(runtime.identityList));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLessThan(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLike(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isNotLike(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isBetween(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else {
					p = cb.and(p, root.get(Item_.stringShortValue).in(runtime.identityList));
				}
			} else {
				if (Comparison.isNotEquals(this.comparison)) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, cb.or(cb.isNull(root.get(Item_.stringShortValue)),
							cb.equal(root.get(Item_.stringShortValue), compareValue)));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					p = cb.and(p, cb.greaterThan(root.get(Item_.stringShortValue), compareValue));
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.stringShortValue), compareValue));
				} else if (Comparison.isLessThan(this.comparison)) {
					p = cb.and(p, cb.lessThan(root.get(Item_.stringShortValue), compareValue));
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.stringShortValue), compareValue));
				} else if (Comparison.isLike(this.comparison)) {
					p = cb.and(p, cb.like(root.get(Item_.stringShortValue), "%" + compareValue + "%"));
				} else if (Comparison.isNotLike(this.comparison)) {
					p = cb.and(p, cb.notLike(root.get(Item_.stringShortValue), "%" + compareValue + "%"));
				} else if (Comparison.isBetween(this.comparison)) {
					p = cb.and(p, cb.between(root.get(Item_.stringShortValue), compareValue, compareOtherValue));
				} else if (Comparison.isIsMember(this.comparison)) {
					if(compareValue.indexOf(",") > -1){
						p = cb.and(p,  root.get(Item_.stringShortValue).in(Arrays.asList(compareValue.split(","))));
					}else{
						p = cb.and(p, cb.equal(root.get(Item_.stringShortValue), compareValue));
					}
				} else if (Comparison.isListLike(this.comparison)) {
					if(compareValue.indexOf(",") > -1){
						Predicate op = null;
						for(String cv : compareValue.split(",")){
							if(StringUtils.isNotBlank(cv)) {
								if(op == null){
									op = cb.like(root.get(Item_.stringShortValue), cv.trim());
								}else {
									op = cb.or(op, cb.like(root.get(Item_.stringShortValue), cv.trim()));
								}
							}
						}
						if(op!=null) {
							p = cb.and(p, op);
						}
					}else{
						p = cb.and(p, cb.like(root.get(Item_.stringShortValue), compareValue));
					}
				} else {
					p = cb.and(p, cb.equal(root.get(Item_.stringShortValue), compareValue));
				}
			}
		}

		String[] paths = StringUtils.split(this.path, ".");
		if ((paths.length > 0) && StringUtils.isNotEmpty(paths[0])) {
			p = cb.and(p, cb.equal(root.get(Item_.path0), paths[0]));
		}

		if ((paths.length > 1) && StringUtils.isNotEmpty(paths[1]) && !WILDCARD.equals(paths[1])) {
			p = cb.and(p, cb.equal(root.get(Item_.path1), paths[1]));
		}

		if ((paths.length > 2) && StringUtils.isNotEmpty(paths[2]) && !WILDCARD.equals(paths[2])) {
			p = cb.and(p, cb.equal(root.get(Item_.path2), paths[2]));
		}

		if ((paths.length > 3) && StringUtils.isNotEmpty(paths[3]) && !WILDCARD.equals(paths[3])) {
			p = cb.and(p, cb.equal(root.get(Item_.path3), paths[3]));
		}

		if ((paths.length > 4) && StringUtils.isNotEmpty(paths[4]) && !WILDCARD.equals(paths[4])) {
			p = cb.and(p, cb.equal(root.get(Item_.path4), paths[4]));
		}

		if ((paths.length > 5) && StringUtils.isNotEmpty(paths[5]) && !WILDCARD.equals(paths[5])) {
			p = cb.and(p, cb.equal(root.get(Item_.path5), paths[5]));
		}

		if ((paths.length > 6) && StringUtils.isNotEmpty(paths[6]) && !WILDCARD.equals(paths[6])) {
			p = cb.and(p, cb.equal(root.get(Item_.path6), paths[6]));
		}

		if ((paths.length > 7) && StringUtils.isNotEmpty(paths[7]) && !WILDCARD.equals(paths[7])) {
			p = cb.and(p, cb.equal(root.get(Item_.path7), paths[7]));
		}

		return p;
	}

	private String compareValue(Runtime runtime) {
		if (StringUtils.startsWith(this.value, "@")) {
			if ((!StringUtils.equals(this.value, DEFINE_TIME)) && (!StringUtils.equals(this.value, DEFINE_DATE))
					&& (!StringUtils.equals(this.value, DEFINE_MONTH))
					&& (!StringUtils.equals(this.value, DEFINE_SEASON))
					&& (!StringUtils.equals(this.value, DEFINE_YEAR))
					&& (!StringUtils.equals(this.value, DEFINE_PERSON))
					&& (!StringUtils.equals(this.value, DEFINE_IDENTITYLIST))
					&& (!StringUtils.equals(this.value, DEFINE_UNITALLLIST))
					&& (!StringUtils.equals(this.value, DEFINE_UNITLIST))) {
				String key = StringUtils.substring(this.value, 1);
				return Objects.toString(runtime.parameter.get(key), "");
			}
		}
		return this.value;
	}

	private String compareOtherValue(Runtime runtime) {
		if (StringUtils.startsWith(this.otherValue, "@")) {
			if ((!StringUtils.equals(this.otherValue, DEFINE_TIME))
					&& (!StringUtils.equals(this.otherValue, DEFINE_DATE))
					&& (!StringUtils.equals(this.otherValue, DEFINE_MONTH))
					&& (!StringUtils.equals(this.otherValue, DEFINE_SEASON))
					&& (!StringUtils.equals(this.otherValue, DEFINE_YEAR))
					&& (!StringUtils.equals(this.otherValue, DEFINE_PERSON))
					&& (!StringUtils.equals(this.otherValue, DEFINE_IDENTITYLIST))
					&& (!StringUtils.equals(this.otherValue, DEFINE_UNITALLLIST))
					&& (!StringUtils.equals(this.otherValue, DEFINE_UNITLIST))) {
				String key = StringUtils.substring(this.otherValue, 1);
				return Objects.toString(runtime.parameter.get(key), "");
			}
		}
		return this.otherValue;
	}

	public Predicate toCmsDocumentPredicate(CriteriaBuilder cb, Root<Document> root, Runtime runtime, String paramName)
			throws Exception {
		Predicate p = cb.conjunction();
		String compareValue = this.compareValue(runtime);
		String compareOtherValue = this.compareOtherValue(runtime);
		if (StringUtils.equals(this.formatType, FORMAT_BOOLEANVALUE)) {
			Boolean booleanValue = BooleanUtils.toBoolean(compareValue);
			if (null != booleanValue) {
				if (Comparison.isNotEquals(this.comparison)) {
					p = cb.and(p, cb.or(cb.isNull(root.get(paramName)),
							cb.equal(root.get(paramName), !booleanValue)));
				} else {
					p = cb.and(p, cb.equal(root.get(paramName), booleanValue));
				}
			}
		} else if (StringUtils.equals(this.formatType, FORMAT_NUMBERVALUE)) {
			Double doubleValue = NumberUtils.toDouble(compareValue);
			if (null != doubleValue) {
				if (Comparison.isNotEquals(this.comparison)) {
					p = cb.and(p, cb.or(cb.isNull(root.get(paramName)),
							cb.not(cb.equal(root.get(paramName), doubleValue))));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					p = cb.and(p, cb.greaterThan(root.get(paramName), doubleValue));
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(paramName), doubleValue));
				} else if (Comparison.isLessThan(this.comparison)) {
					p = cb.and(p, cb.lessThan(root.get(paramName), doubleValue));
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(paramName), doubleValue));
				} else if (Comparison.isBetween(this.comparison)) {
					Double doubleOtherValue = NumberUtils.toDouble(compareOtherValue);
					if (null != doubleOtherValue) {
						p = cb.and(p, cb.between(root.get(paramName), doubleValue, doubleOtherValue));
					}
				} else {
					p = cb.and(p, cb.equal(root.get(paramName), doubleValue));
				}
			}
		} else if (StringUtils.equals(this.formatType, FORMAT_DATEVALUE)) {
			/* 日期值比较 */
			if (StringUtils.isNotEmpty(compareValue)) {
				Date value = null;
				Date otherValue = null;
				if (DateTools.isDateTime(compareValue)) {
					value = DateTools.parseDateTime(compareValue);
				} else if (DateTools.isDate(compareValue)) {
					value = DateTools.parseDate(compareValue);
				}
				if (DateTools.isDateTime(compareOtherValue)) {
					otherValue = DateTools.parseDateTime(compareOtherValue);
				} else if (DateTools.isDate(compareOtherValue)) {
					otherValue = DateTools.parseDate(compareOtherValue);
				}
				if (null != otherValue) {
					otherValue = DateTools.ceilDate(otherValue, 0);
				}
				if (null != value) {
					value = DateTools.floorDate(value, 0);
					if (Comparison.isNotEquals(this.comparison)) {
						p = cb.and(p, cb.or(cb.isNull(root.get(paramName)),
								cb.not(cb.equal(root.get(paramName), value))));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(paramName), value));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(paramName), value));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(paramName), value));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(paramName), value));
					} else if (Comparison.isBetween(this.comparison)) {
						if (null != otherValue) {
							p = cb.and(p, cb.between(root.get(paramName), value, otherValue));
						} else {
							throw new Exception("unkown comparison:" + this.comparison);
						}
					} else {
						p = cb.and(p, cb.equal(root.get(paramName), value));
					}
				} else if (StringUtils.equals(compareValue, DEFINE_DATE)
						|| StringUtils.equals(compareValue, DEFINE_MONTH)
						|| StringUtils.equals(compareValue, DEFINE_SEASON)
						|| StringUtils.equals(compareValue, DEFINE_YEAR)) {
					Date floor = null;
					Date ceil = null;
					if (StringUtils.equals(compareValue, DEFINE_DATE)) {
						floor = DateTools.floorDate(new Date(), 0);
						ceil = DateTools.ceilDate(new Date(), 0);
					} else if (StringUtils.equals(compareValue, DEFINE_MONTH)) {
						floor = DateTools.floorMonth(new Date(), 0);
						ceil = DateTools.ceilMonth(new Date(), 0);
					} else if (StringUtils.equals(compareValue, DEFINE_SEASON)) {
						floor = DateTools.floorSeason(new Date(), 0);
						ceil = DateTools.ceilSeason(new Date(), 0);
					} else {
						floor = DateTools.floorYear(new Date(), 0);
						ceil = DateTools.ceilYear(new Date(), 0);
					}
					if (Comparison.isNotEquals(this.comparison)) {
						p = cb.and(p, cb.or(cb.isNull(root.get(paramName)),
								cb.not(cb.between(root.get(paramName), floor, ceil))));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(paramName), ceil));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(paramName), floor));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(paramName), floor));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(paramName), ceil));
					} else if (Comparison.isBetween(this.comparison)) {
						// throw new Exception("unkown comparison:" + this.comparison);
					} else {
						throw new Exception("unkown comparison:" + this.comparison);
					}
				} else if (StringUtils.equals(compareValue, DEFINE_TIME)) {
					if (Comparison.isNotEquals(this.comparison)) {
						p = cb.and(p, cb.or(cb.isNull(root.get(paramName)),
								cb.not(cb.equal(root.get(paramName), new Date()))));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(paramName), new Date()));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(paramName), new Date()));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(paramName), new Date()));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(paramName), new Date()));
					} else if (Comparison.isBetween(this.comparison)) {
						// throw new Exception("unkown comparison:" + this.comparison);
					} else {
						throw new Exception("unkown comparison:" + this.comparison);
					}
				}
			}
		} else if (StringUtils.equals(this.formatType, FORMAT_TIMEVALUE)) {
			/* 时间值比较 */
			if (StringUtils.isNotEmpty(compareValue)) {
				Date value = null;
				Date otherValue = null;
				if (DateTools.isDateTime(compareValue)) {
					value = DateTools.parseDateTime(compareValue);
				} else if (DateTools.isTime(compareValue)) {
					value = DateTools.parseTime(compareValue);
				}
				if (DateTools.isDateTime(compareOtherValue)) {
					otherValue = DateTools.parseDateTime(compareOtherValue);
				} else if (DateTools.isTime(compareOtherValue)) {
					otherValue = DateTools.parseTime(compareOtherValue);
				}
				if (null != value) {
					if (Comparison.isNotEquals(this.comparison)) {
						p = cb.and(p, cb.or(cb.isNull(root.get(paramName)),
								cb.not(cb.equal(root.get(paramName), value))));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(paramName), value));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(paramName), value));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(paramName), value));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(paramName), value));
					} else if (Comparison.isBetween(this.comparison)) {
						if (null != otherValue) {
							p = cb.and(p, cb.between(root.get(paramName), value, otherValue));
						}
					} else {
						p = cb.and(p, cb.equal(root.get(paramName), value));
					}
				} else if (StringUtils.equals(compareValue, DEFINE_TIME)) {
					if (Comparison.isNotEquals(this.comparison)) {
						p = cb.and(p, cb.or(cb.isNull(root.get(paramName)),
								cb.not(cb.equal(root.get(paramName), new Date()))));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(paramName), new Date()));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(paramName), new Date()));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(paramName), new Date()));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(paramName), new Date()));
					} else if (Comparison.isBetween(this.comparison)) {
						throw new Exception("unkown comparison:" + this.comparison);
					} else {
						throw new Exception("unkown comparison:" + this.comparison);
					}
				}
			}
		} else if (StringUtils.equals(this.formatType, FORMAT_DATETIMEVALUE)) {
			Date value = null;
			Date otherValue = null;
			if (DateTools.isDateTime(compareValue)) {
				value = DateTools.parseDateTime(compareValue);
			} else if (DateTools.isDate(compareValue)) {
				value = DateTools.parseDate(compareValue);
			}
			if (DateTools.isDateTime(compareOtherValue)) {
				otherValue = DateTools.parseDateTime(compareOtherValue);
			} else if (DateTools.isDate(compareOtherValue)) {
				otherValue = DateTools.parseDate(compareOtherValue);
			}
			if (null != value) {
				if (Comparison.isNotEquals(this.comparison)) {
					p = cb.and(p, cb.or(cb.isNull(root.get(paramName)),
							cb.not(cb.equal(root.get(paramName), value))));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					p = cb.and(p, cb.greaterThan(root.get(paramName), value));
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(paramName), value));
				} else if (Comparison.isLessThan(this.comparison)) {
					p = cb.and(p, cb.lessThan(root.get(paramName), value));
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(paramName), value));
				} else if (Comparison.isBetween(this.comparison)) {
					if (null != otherValue) {
						p = cb.and(p, cb.between(root.get(paramName), value, otherValue));
					} else {
						throw new Exception("unkown comparison:" + this.comparison);
					}
				} else {
					p = cb.and(p, cb.equal(root.get(paramName), value));
				}
			} else if (StringUtils.equals(compareValue, DEFINE_DATE) || StringUtils.equals(compareValue, DEFINE_MONTH)
					|| StringUtils.equals(compareValue, DEFINE_SEASON)
					|| StringUtils.equals(compareValue, DEFINE_YEAR)) {
				Date floor = null;
				Date ceil = null;
				if (StringUtils.equals(compareValue, DEFINE_DATE)) {
					floor = DateTools.floorDate(new Date(), 0);
					ceil = DateTools.ceilDate(new Date(), 0);
				} else if (StringUtils.equals(compareValue, DEFINE_MONTH)) {
					floor = DateTools.floorMonth(new Date(), 0);
					ceil = DateTools.ceilMonth(new Date(), 0);
				} else if (StringUtils.equals(compareValue, DEFINE_SEASON)) {
					floor = DateTools.floorSeason(new Date(), 0);
					ceil = DateTools.ceilSeason(new Date(), 0);
				} else {
					floor = DateTools.floorYear(new Date(), 0);
					ceil = DateTools.ceilYear(new Date(), 0);
				}
				if (Comparison.isNotEquals(this.comparison)) {
					p = cb.and(p, cb.or(cb.isNull(root.get(paramName)),
							cb.not(cb.between(root.get(paramName), floor, ceil))));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					p = cb.and(p, cb.greaterThan(root.get(paramName), ceil));
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(paramName), floor));
				} else if (Comparison.isLessThan(this.comparison)) {
					p = cb.and(p, cb.lessThan(root.get(paramName), floor));
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(paramName), ceil));
				} else if (Comparison.isBetween(this.comparison)) {
					// p = cb.and(p, cb.between(root.get(Item_.dateTimeValue), floor, ceil));
				} else {
					throw new Exception("unkown comparison:" + this.comparison);
				}
			} else if (StringUtils.equals(compareValue, DEFINE_TIME)) {
				if (Comparison.isNotEquals(this.comparison)) {
					p = cb.and(p, cb.or(cb.isNull(root.get(paramName)),
							cb.not(cb.equal(root.get(paramName), new Date()))));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					p = cb.and(p, cb.greaterThan(root.get(paramName), new Date()));
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(paramName), new Date()));
				} else if (Comparison.isLessThan(this.comparison)) {
					p = cb.and(p, cb.lessThan(root.get(paramName), new Date()));
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(paramName), new Date()));
				} else if (Comparison.isBetween(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else {
					throw new Exception("unkown comparison:" + this.comparison);
				}
			}
		} else {
			/* TEXT 内容值 */
			if (StringUtils.equals(compareValue, DEFINE_PERSON)) {
				if (Comparison.isNotEquals(this.comparison)) {
					p = cb.and(p, cb.or(cb.isNull(root.get(paramName)),
							cb.not(cb.equal(root.get(paramName), runtime.person))));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					p = cb.and(p, cb.greaterThan(root.get(paramName), runtime.person));
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(paramName), runtime.person));
				} else if (Comparison.isLessThan(this.comparison)) {
					p = cb.and(p, cb.lessThan(root.get(paramName), runtime.person));
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(paramName), runtime.person));
				} else if (Comparison.isLike(this.comparison)) {
					p = cb.and(p, cb.like(root.get(paramName), "%" + runtime.person + "%"));
				} else if (Comparison.isNotLike(this.comparison)) {
					p = cb.and(p, cb.notLike(root.get(paramName), "%" + runtime.person + "%"));
				} else if (Comparison.isBetween(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else {
					p = cb.and(p, cb.equal(root.get(paramName), runtime.person));
				}
			} else if (StringUtils.equals(compareValue, DEFINE_UNITLIST)) {
				if (Comparison.isNotEquals(this.comparison)) {
					p = cb.and(p, cb.not(root.get(paramName).in(runtime.unitList)));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLessThan(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLike(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isNotLike(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isBetween(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else {
					p = cb.and(p, root.get(paramName).in(runtime.unitList));
				}
			} else if (StringUtils.equals(compareValue, DEFINE_UNITALLLIST)) {
				if (Comparison.isNotEquals(this.comparison)) {
					p = cb.and(p, cb.not(root.get(paramName).in(runtime.unitAllList)));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLessThan(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLike(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isNotLike(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isBetween(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else {
					p = cb.and(p, root.get(paramName).in(runtime.unitAllList));
				}
			} else if (StringUtils.equals(compareValue, DEFINE_IDENTITYLIST)) {
				if (Comparison.isNotEquals(this.comparison)) {
					p = cb.and(p, cb.not(root.get(paramName).in(runtime.identityList)));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLessThan(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isLike(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isNotLike(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else if (Comparison.isBetween(this.comparison)) {
					throw new Exception("unkown comparison:" + this.comparison);
				} else {
					p = cb.and(p, root.get(paramName).in(runtime.identityList));
				}
			} else {
				if (Comparison.isNotEquals(this.comparison)) {
					p = cb.and(p, cb.or(cb.isNull(root.get(paramName)),
							cb.not(cb.equal(root.get(paramName), compareValue))));
				} else if (Comparison.isGreaterThan(this.comparison)) {
					p = cb.and(p, cb.greaterThan(root.get(paramName), compareValue));
				} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(paramName), compareValue));
				} else if (Comparison.isLessThan(this.comparison)) {
					p = cb.and(p, cb.lessThan(root.get(paramName), compareValue));
				} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(paramName), compareValue));
				} else if (Comparison.isLike(this.comparison)) {
					p = cb.and(p, cb.like(root.get(paramName), "%" + compareValue + "%"));
				} else if (Comparison.isNotLike(this.comparison)) {
					p = cb.and(p, cb.notLike(root.get(paramName), "%" + compareValue + "%"));
				} else if (Comparison.isBetween(this.comparison)) {
					p = cb.and(p, cb.between(root.get(paramName), compareValue, compareOtherValue));
				} else if (Comparison.isIsMember(this.comparison)) {
					if(compareValue.indexOf(",") > -1){
						p = cb.and(p,  root.get(paramName).in(Arrays.asList(compareValue.split(","))));
					}else{
						p = cb.and(p, cb.equal(root.get(paramName), compareValue));
					}
				} else {
					p = cb.and(p, cb.equal(root.get(paramName), compareValue));
				}
			}
		}
		return p;
	}

}
