package com.x.processplatform.core.express.query;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.query.Comparison;
import com.x.processplatform.core.entity.query.FilterEntry;
import com.x.processplatform.core.entity.query.FormatType;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public class FilterEntryTools {
	public static Predicate toPredicate(CriteriaBuilder cb, Root<Item> root, List<FilterEntry> filterEntries)
			throws Exception {
		Predicate predicate = null;
		for (FilterEntry filter : ListTools.nullToEmpty(filterEntries)) {
			if (!filter.available()) {
				continue;
			}
			String[] paths = StringUtils.split(filter.getPath(), ".");
			Predicate p = cb.conjunction();
			p = cb.and(p, cb.equal(root.get(Item_.path0), paths.length > 0 ? paths[0] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path1), paths.length > 1 ? paths[1] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path2), paths.length > 2 ? paths[2] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path3), paths.length > 3 ? paths[3] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path4), paths.length > 4 ? paths[4] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path5), paths.length > 5 ? paths[5] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path6), paths.length > 6 ? paths[6] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path7), paths.length > 7 ? paths[7] : ""));
			if (filter.getFormatType().equals(FormatType.booleanValue)) {
				if (StringUtils.isNotEmpty(filter.getValue())) {
					if (Comparison.isNotEquals(filter.getComparison())) {
						p = cb.and(p,
								cb.notEqual(root.get(Item_.booleanValue), BooleanUtils.toBoolean(filter.getValue())));
					} else {
						p = cb.and(p,
								cb.equal(root.get(Item_.booleanValue), BooleanUtils.toBoolean(filter.getValue())));
					}
				}
			} else if (filter.getFormatType().equals(FormatType.numberValue)) {
				if (StringUtils.isNotEmpty(filter.getValue())) {
					Double value = NumberUtils.toDouble(filter.getValue());
					if (Comparison.isNotEquals(filter.getComparison())) {
						p = cb.and(p, cb.notEqual(root.get(Item_.numberValue), value));
					} else if (Comparison.isGreaterThan(filter.getComparison())) {
						p = cb.and(p,
								cb.greaterThan(root.get(Item_.numberValue), NumberUtils.toDouble(filter.getValue())));
					} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.numberValue), value));
					} else if (Comparison.isLessThan(filter.getComparison())) {
						p = cb.and(p, cb.lessThan(root.get(Item_.numberValue), value));
					} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.numberValue), value));
					} else {
						p = cb.and(p, cb.equal(root.get(Item_.numberValue), value));
					}
				}
			} else if (filter.getFormatType().equals(FormatType.dateTimeValue)) {
				if (StringUtils.isNotEmpty(filter.getValue())) {
					if (DateTools.isDateTime(filter.getValue())) {
						Date value = DateTools.parseDateTime(filter.getValue());
						if (Comparison.isNotEquals(filter.getComparison())) {
							p = cb.and(p, cb.notEqual(root.get(Item_.dateTimeValue), value));
						} else if (Comparison.isGreaterThan(filter.getComparison())) {
							p = cb.and(p, cb.greaterThan(root.get(Item_.dateTimeValue), value));
						} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
							p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.dateTimeValue), value));
						} else if (Comparison.isLessThan(filter.getComparison())) {
							p = cb.and(p, cb.lessThan(root.get(Item_.dateTimeValue), value));
						} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
							p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.dateTimeValue), value));
						} else {
							p = cb.and(p, cb.equal(root.get(Item_.dateTimeValue), value));
						}
					} else if (DateTools.isDate(filter.getValue())) {
						Date value = DateTools.parseDate(filter.getValue());
						if (Comparison.isNotEquals(filter.getComparison())) {
							p = cb.and(p, cb.notEqual(root.get(Item_.dateValue), value));
						} else if (Comparison.isGreaterThan(filter.getComparison())) {
							p = cb.and(p, cb.greaterThan(root.get(Item_.dateValue), value));
						} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
							p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.dateValue), value));
						} else if (Comparison.isLessThan(filter.getComparison())) {
							p = cb.and(p, cb.lessThan(root.get(Item_.dateValue), value));
						} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
							p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.dateValue), value));
						} else {
							p = cb.and(p, cb.equal(root.get(Item_.dateValue), value));
						}
					} else if (DateTools.isTime(filter.getValue())) {
						Date value = DateTools.parseTime(filter.getValue());
						if (Comparison.isNotEquals(filter.getComparison())) {
							p = cb.and(p, cb.notEqual(root.get(Item_.timeValue), value));
						} else if (Comparison.isGreaterThan(filter.getComparison())) {
							p = cb.and(p, cb.greaterThan(root.get(Item_.timeValue), value));
						} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
							p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.timeValue), value));
						} else if (Comparison.isLessThan(filter.getComparison())) {
							p = cb.and(p, cb.lessThan(root.get(Item_.timeValue), value));
						} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
							p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.timeValue), value));
						} else {
							p = cb.and(p, cb.equal(root.get(Item_.timeValue), value));
						}
					}
				}
			} else {
				String value = filter.getValue();
				if (Comparison.isNotEquals(filter.getComparison())) {
					p = cb.and(p, cb.notEqual(root.get(Item_.stringShortValue), value));
				} else if (Comparison.isGreaterThan(filter.getComparison())) {
					p = cb.and(p, cb.greaterThan(root.get(Item_.stringShortValue), value));
				} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.stringShortValue), value));
				} else if (Comparison.isLessThan(filter.getComparison())) {
					p = cb.and(p, cb.lessThan(root.get(Item_.stringShortValue), value));
				} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.stringShortValue), value));
				} else if (Comparison.isLike(filter.getComparison())) {
					p = cb.and(p, cb.like(root.get(Item_.stringShortValue), "%" + value + "%"));
				} else if (Comparison.isNotLike(filter.getComparison())) {
					p = cb.and(p, cb.notLike(root.get(Item_.stringShortValue), "%" + value + "%"));
				} else {
					p = cb.and(p, cb.equal(root.get(Item_.stringShortValue), value));
				}
			}
			if (null == predicate) {
				predicate = p;
			} else {
				if (StringUtils.equalsIgnoreCase(filter.getLogic(), "or")) {
					predicate = cb.or(predicate, p);
				} else {
					predicate = cb.and(predicate, p);
				}
			}
		}
		if (null == predicate) {
			predicate = cb.conjunction();
		}
		return predicate;
	}

	public static Predicate toPredicate(CriteriaBuilder cb, Root<Item> root, FilterEntry filter) throws Exception {
		String[] paths = StringUtils.split(filter.getPath(), ".");
		Predicate p = cb.conjunction();
		p = cb.and(p, cb.equal(root.get(Item_.path0), paths.length > 0 ? paths[0] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path1), paths.length > 1 ? paths[1] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path2), paths.length > 2 ? paths[2] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path3), paths.length > 3 ? paths[3] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path4), paths.length > 4 ? paths[4] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path5), paths.length > 5 ? paths[5] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path6), paths.length > 6 ? paths[6] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path7), paths.length > 7 ? paths[7] : ""));
		if (filter.getFormatType().equals(FormatType.booleanValue)) {
			if (StringUtils.isNotEmpty(filter.getValue())) {
				if (Comparison.isNotEquals(filter.getComparison())) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, cb.equal(root.get(Item_.booleanValue), BooleanUtils.toBoolean(filter.getValue())));
				} else {
					p = cb.and(p, cb.equal(root.get(Item_.booleanValue), BooleanUtils.toBoolean(filter.getValue())));
				}
			}
		} else if (filter.getFormatType().equals(FormatType.numberValue)) {
			if (StringUtils.isNotEmpty(filter.getValue())) {
				Double value = NumberUtils.toDouble(filter.getValue());
				if (Comparison.isNotEquals(filter.getComparison())) {
					/** 不等于返回等于值,在外部运算 */
					p = cb.and(p, cb.equal(root.get(Item_.numberValue), value));
				} else if (Comparison.isGreaterThan(filter.getComparison())) {
					p = cb.and(p, cb.greaterThan(root.get(Item_.numberValue), NumberUtils.toDouble(filter.getValue())));
				} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.numberValue), value));
				} else if (Comparison.isLessThan(filter.getComparison())) {
					p = cb.and(p, cb.lessThan(root.get(Item_.numberValue), value));
				} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.numberValue), value));
				} else {
					p = cb.and(p, cb.equal(root.get(Item_.numberValue), value));
				}
			}
		} else if (filter.getFormatType().equals(FormatType.dateTimeValue)) {
			if (StringUtils.isNotEmpty(filter.getValue())) {
				if (DateTools.isDateTime(filter.getValue())) {
					Date value = DateTools.parseDateTime(filter.getValue());
					if (Comparison.isNotEquals(filter.getComparison())) {
						/** 不等于返回等于值,在外部运算 */
						p = cb.and(p, cb.equal(root.get(Item_.dateTimeValue), value));
					} else if (Comparison.isGreaterThan(filter.getComparison())) {
						p = cb.and(p, cb.greaterThan(root.get(Item_.dateTimeValue), value));
					} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.dateTimeValue), value));
					} else if (Comparison.isLessThan(filter.getComparison())) {
						p = cb.and(p, cb.lessThan(root.get(Item_.dateTimeValue), value));
					} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.dateTimeValue), value));
					} else {
						p = cb.and(p, cb.equal(root.get(Item_.dateTimeValue), value));
					}
				} else if (DateTools.isDate(filter.getValue())) {
					Date value = DateTools.parseDate(filter.getValue());
					if (Comparison.isNotEquals(filter.getComparison())) {
						/** 不等于返回等于值,在外部运算 */
						p = cb.and(p, cb.equal(root.get(Item_.dateValue), value));
					} else if (Comparison.isGreaterThan(filter.getComparison())) {
						p = cb.and(p, cb.greaterThan(root.get(Item_.dateValue), value));
					} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.dateValue), value));
					} else if (Comparison.isLessThan(filter.getComparison())) {
						p = cb.and(p, cb.lessThan(root.get(Item_.dateValue), value));
					} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.dateValue), value));
					} else {
						p = cb.and(p, cb.equal(root.get(Item_.dateValue), value));
					}
				} else if (DateTools.isTime(filter.getValue())) {
					Date value = DateTools.parseTime(filter.getValue());
					if (Comparison.isNotEquals(filter.getComparison())) {
						/** 不等于返回等于值,在外部运算 */
						p = cb.and(p, cb.equal(root.get(Item_.timeValue), value));
					} else if (Comparison.isGreaterThan(filter.getComparison())) {
						p = cb.and(p, cb.greaterThan(root.get(Item_.timeValue), value));
					} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.timeValue), value));
					} else if (Comparison.isLessThan(filter.getComparison())) {
						p = cb.and(p, cb.lessThan(root.get(Item_.timeValue), value));
					} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.timeValue), value));
					} else {
						p = cb.and(p, cb.equal(root.get(Item_.timeValue), value));
					}
				}
			}
		} else {
			String value = filter.getValue();
			if (Comparison.isNotEquals(filter.getComparison())) {
				/** 不等于返回等于值,在外部运算 */
				p = cb.and(p, cb.equal(root.get(Item_.stringShortValue), value));
			} else if (Comparison.isGreaterThan(filter.getComparison())) {
				p = cb.and(p, cb.greaterThan(root.get(Item_.stringShortValue), value));
			} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
				p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Item_.stringShortValue), value));
			} else if (Comparison.isLessThan(filter.getComparison())) {
				p = cb.and(p, cb.lessThan(root.get(Item_.stringShortValue), value));
			} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
				p = cb.and(p, cb.lessThanOrEqualTo(root.get(Item_.stringShortValue), value));
			} else if (Comparison.isLike(filter.getComparison())) {
				p = cb.and(p, cb.like(root.get(Item_.stringShortValue), "%" + value + "%"));
			} else if (Comparison.isNotLike(filter.getComparison())) {
				p = cb.and(p, cb.notLike(root.get(Item_.stringShortValue), "%" + value + "%"));
			} else {
				p = cb.and(p, cb.equal(root.get(Item_.stringShortValue), value));
			}
		}
		return p;
	}
}