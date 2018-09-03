package com.x.query.core.entity.plan;

import java.util.Date;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.DateTools;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public class FilterEntry extends GsonPropertyObject {

	public static final String FORMAT_TEXTVALUE = "textValue";

	public static final String FORMAT_NUMBERVALUE = "numberValue";

	public static final String FORMAT_BOOLEANVALUE = "booleanValue";

	public static final String FORMAT_DATETIMEVALUE = "dateTimeValue";

	public static final String DEFINE_TIME = "@time";

	public static final String DEFINE_DATE = "@date";

	public static final String DEFINE_MONTH = "@month";

	public static final String DEFINE_YEAR = "@year";

	public static final String DEFINE_SEASON = "@season";

	public static final String DEFINE_PERSON = "@person";

	public static final String DEFINE_IDENTITYLIST = "@identityList";

	public static final String DEFINE_UNITLIST = "@unitList";

	public static final String DEFINE_UNITALLLIST = "@unitAllList";

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
		case FORMAT_NUMBERVALUE:
			if (NumberUtils.isNumber(value)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public Predicate toPredicate(CriteriaBuilder cb, Root<Item> root, Runtime runtime, ItemCategory itemCategory)
			throws Exception {
		String[] paths = StringUtils.split(this.path, ".");
		Predicate p = cb.equal(root.get(Item_.itemCategory), itemCategory);
		p = cb.and(p, cb.equal(root.get(Item_.path0), paths.length > 0 ? paths[0] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path1), paths.length > 1 ? paths[1] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path2), paths.length > 2 ? paths[2] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path3), paths.length > 3 ? paths[3] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path4), paths.length > 4 ? paths[4] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path5), paths.length > 5 ? paths[5] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path6), paths.length > 6 ? paths[6] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path7), paths.length > 7 ? paths[7] : ""));
		String compareValue = this.compareValue(runtime);
		String compareOtherValue = this.compareOtherValue(runtime);
		if (StringUtils.equals(this.formatType, FORMAT_BOOLEANVALUE)) {
			Boolean booleanValue = BooleanUtils.toBoolean(compareValue);
			if (null != booleanValue) {
				if (Comparison.isNotEquals(this.comparison)) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, cb.equal(root.get(Item_.booleanValue), booleanValue));
				} else {
					p = cb.and(p, cb.equal(root.get(Item_.booleanValue), booleanValue));
				}
			}
		} else if (StringUtils.equals(this.formatType, FORMAT_NUMBERVALUE)) {
			Double doubleValue = NumberUtils.toDouble(compareValue);
			if (null != doubleValue) {
				if (Comparison.isNotEquals(this.comparison)) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, cb.equal(root.get(Item_.numberValue), doubleValue));
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
		} else if (StringUtils.equals(this.formatType, FORMAT_DATETIMEVALUE)) {
			/* 时间值比较 */
			if (StringUtils.isNotEmpty(compareValue)) {
				if (DateTools.isDateTime(compareValue)) {
					Date dateTimeValue = DateTools.parseDateTime(compareValue);
					if (Comparison.isNotEquals(this.comparison)) {
						/** 不等于返回等于值,在外部运算 */
						p = cb.and(p, cb.equal(root.get(Item_.dateTimeValue), dateTimeValue));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(Item_.dateTimeValue), dateTimeValue));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.dateTimeValue), dateTimeValue));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(Item_.dateTimeValue), dateTimeValue));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.dateTimeValue), dateTimeValue));
					} else if (Comparison.isBetween(this.comparison)) {
						if (StringUtils.isNotEmpty(compareOtherValue) && DateTools.isDateTime(compareOtherValue)) {
							Date dateTimeOtherValue = DateTools.parseDateTime(compareOtherValue);
							if (null != dateTimeOtherValue) {
								p = cb.and(p,
										cb.between(root.get(Item_.dateTimeValue), dateTimeValue, dateTimeOtherValue));
							}
						}
					} else {
						p = cb.and(p, cb.equal(root.get(Item_.dateTimeValue), dateTimeValue));
					}
				} else if (DateTools.isDate(compareValue)) {
					Date dateValue = DateTools.parseDate(compareValue);
					if (Comparison.isNotEquals(this.comparison)) {
						/** 不等于返回等于值,在外部运算 */
						p = cb.and(p, cb.equal(root.get(Item_.dateValue), dateValue));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(Item_.dateValue), dateValue));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.dateValue), dateValue));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(Item_.dateValue), dateValue));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.dateValue), dateValue));
					} else if (Comparison.isBetween(this.comparison)) {
						if (StringUtils.isNotEmpty(compareOtherValue) && DateTools.isDate(compareOtherValue)) {
							Date dateOtherValue = DateTools.parseDate(compareOtherValue);
							if (null != dateOtherValue) {
								p = cb.and(p, cb.between(root.get(Item_.dateTimeValue), dateValue, dateOtherValue));
							}
						}
					} else {
						p = cb.and(p, cb.equal(root.get(Item_.dateValue), value));
					}
				} else if (DateTools.isTime(compareValue)) {
					Date timeValue = DateTools.parseTime(compareValue);
					if (Comparison.isNotEquals(this.comparison)) {
						/** 不等于返回等于值,在外部运算 */
						p = cb.and(p, cb.equal(root.get(Item_.timeValue), timeValue));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(Item_.timeValue), timeValue));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.timeValue), timeValue));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(Item_.timeValue), timeValue));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.timeValue), timeValue));
					} else if (Comparison.isBetween(this.comparison)) {
						if (StringUtils.isNotEmpty(compareOtherValue) && DateTools.isTime(compareOtherValue)) {
							Date timeOtherValue = DateTools.parseTime(compareOtherValue);
							if (null != timeOtherValue) {
								p = cb.and(p, cb.between(root.get(Item_.dateTimeValue), timeValue, timeOtherValue));
							}
						}
					} else {
						p = cb.and(p, cb.equal(root.get(Item_.timeValue), value));
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
						/** 不等于返回等于值,在外部运算 */
						p = cb.and(p, cb.between(root.get(Item_.dateTimeValue), floor, ceil));
					} else if (Comparison.isGreaterThan(this.comparison)) {
						p = cb.and(p, cb.greaterThan(root.get(Item_.dateTimeValue), ceil));
					} else if (Comparison.isGreaterThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.dateTimeValue), floor));
					} else if (Comparison.isLessThan(this.comparison)) {
						p = cb.and(p, cb.lessThan(root.get(Item_.dateTimeValue), floor));
					} else if (Comparison.isLessThanOrEqualTo(this.comparison)) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.dateTimeValue), ceil));
					} else if (Comparison.isBetween(this.comparison)) {
						p = cb.and(p, cb.between(root.get(Item_.dateTimeValue), floor, ceil));
					} else {
						throw new Exception("unkown comparison:" + this.comparison);
					}
				} else if (StringUtils.equals(compareValue, DEFINE_TIME)) {
					if (Comparison.isNotEquals(this.comparison)) {
						/** 不等于返回等于值,在外部运算 */
						throw new Exception("unkown comparison:" + this.comparison);
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
			}
		} else {
			/* TEXT 内容值 */
			if (StringUtils.equals(compareValue, DEFINE_PERSON)) {
				if (Comparison.isNotEquals(this.comparison)) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, cb.equal(root.get(Item_.stringShortValue), runtime.person));
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
					p = cb.and(p, root.get(Item_.stringShortValue).in(runtime.unitList));
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
					p = cb.and(p, root.get(Item_.stringShortValue).in(runtime.unitList));
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
					p = cb.and(p, cb.equal(root.get(Item_.stringShortValue), compareValue));
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
				} else {
					p = cb.and(p, cb.equal(root.get(Item_.stringShortValue), compareValue));
				}
			}
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

}