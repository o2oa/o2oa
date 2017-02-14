package com.x.processplatform.core.entity.query;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.utils.DateTools;
import com.x.base.core.utils.ListTools;
import com.x.processplatform.core.entity.content.DataItem;
import com.x.processplatform.core.entity.content.DataItem_;

public class FilterEntryTools {
	public static Predicate toPredicate(CriteriaBuilder cb, Root<DataItem> root, List<FilterEntry> filterEntries)
			throws Exception {
		Predicate predicate = null;
		for (FilterEntry filter : ListTools.nullToEmpty(filterEntries)) {
			if (!filter.available()) {
				continue;
			}
			String[] paths = StringUtils.split(filter.getPath(), ".");
			Predicate p = cb.conjunction();
			p = cb.and(p, cb.equal(root.get(DataItem_.path0), paths.length > 0 ? paths[0] : ""));
			p = cb.and(p, cb.equal(root.get(DataItem_.path1), paths.length > 1 ? paths[1] : ""));
			p = cb.and(p, cb.equal(root.get(DataItem_.path2), paths.length > 2 ? paths[2] : ""));
			p = cb.and(p, cb.equal(root.get(DataItem_.path3), paths.length > 3 ? paths[3] : ""));
			p = cb.and(p, cb.equal(root.get(DataItem_.path4), paths.length > 4 ? paths[4] : ""));
			p = cb.and(p, cb.equal(root.get(DataItem_.path5), paths.length > 5 ? paths[5] : ""));
			p = cb.and(p, cb.equal(root.get(DataItem_.path6), paths.length > 6 ? paths[6] : ""));
			p = cb.and(p, cb.equal(root.get(DataItem_.path7), paths.length > 7 ? paths[7] : ""));
			if (filter.getFormatType().equals(FormatType.booleanValue)) {
				if (StringUtils.isNotEmpty(filter.getValue())) {
					if (Comparison.isNotEquals(filter.getComparison())) {
						p = cb.and(p, cb.notEqual(root.get(DataItem_.booleanValue),
								BooleanUtils.toBoolean(filter.getValue())));
					} else {
						p = cb.and(p,
								cb.equal(root.get(DataItem_.booleanValue), BooleanUtils.toBoolean(filter.getValue())));
					}
				}
			} else if (filter.getFormatType().equals(FormatType.numberValue)) {
				if (StringUtils.isNotEmpty(filter.getValue())) {
					Double value = NumberUtils.toDouble(filter.getValue());
					if (Comparison.isNotEquals(filter.getComparison())) {
						p = cb.and(p, cb.notEqual(root.get(DataItem_.numberValue), value));
					} else if (Comparison.isGreaterThan(filter.getComparison())) {
						p = cb.and(p, cb.greaterThan(root.get(DataItem_.numberValue),
								NumberUtils.toDouble(filter.getValue())));
					} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
						p = cb.and(p, cb.greaterThanOrEqualTo(root.get(DataItem_.numberValue), value));
					} else if (Comparison.isLessThan(filter.getComparison())) {
						p = cb.and(p, cb.lessThan(root.get(DataItem_.numberValue), value));
					} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
						p = cb.and(p, cb.lessThanOrEqualTo(root.get(DataItem_.numberValue), value));
					} else {
						p = cb.and(p, cb.equal(root.get(DataItem_.numberValue), value));
					}
				}
			} else if (filter.getFormatType().equals(FormatType.dateTimeValue)) {
				if (StringUtils.isNotEmpty(filter.getValue())) {
					if (DateTools.isDateTime(filter.getValue())) {
						Date value = DateTools.parseDateTime(filter.getValue());
						if (Comparison.isNotEquals(filter.getComparison())) {
							p = cb.and(p, cb.notEqual(root.get(DataItem_.dateTimeValue), value));
						} else if (Comparison.isGreaterThan(filter.getComparison())) {
							p = cb.and(p, cb.greaterThan(root.get(DataItem_.dateTimeValue), value));
						} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
							p = cb.and(p, cb.greaterThanOrEqualTo(root.get(DataItem_.dateTimeValue), value));
						} else if (Comparison.isLessThan(filter.getComparison())) {
							p = cb.and(p, cb.lessThan(root.get(DataItem_.dateTimeValue), value));
						} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
							p = cb.and(p, cb.lessThanOrEqualTo(root.get(DataItem_.dateTimeValue), value));
						} else {
							p = cb.and(p, cb.equal(root.get(DataItem_.dateTimeValue), value));
						}
					} else if (DateTools.isDate(filter.getValue())) {
						Date value = DateTools.parseDate(filter.getValue());
						if (Comparison.isNotEquals(filter.getComparison())) {
							p = cb.and(p, cb.notEqual(root.get(DataItem_.dateValue), value));
						} else if (Comparison.isGreaterThan(filter.getComparison())) {
							p = cb.and(p, cb.greaterThan(root.get(DataItem_.dateValue), value));
						} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
							p = cb.and(p, cb.greaterThanOrEqualTo(root.get(DataItem_.dateValue), value));
						} else if (Comparison.isLessThan(filter.getComparison())) {
							p = cb.and(p, cb.lessThan(root.get(DataItem_.dateValue), value));
						} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
							p = cb.and(p, cb.lessThanOrEqualTo(root.get(DataItem_.dateValue), value));
						} else {
							p = cb.and(p, cb.equal(root.get(DataItem_.dateValue), value));
						}
					} else if (DateTools.isTime(filter.getValue())) {
						Date value = DateTools.parseTime(filter.getValue());
						if (Comparison.isNotEquals(filter.getComparison())) {
							p = cb.and(p, cb.notEqual(root.get(DataItem_.timeValue), value));
						} else if (Comparison.isGreaterThan(filter.getComparison())) {
							p = cb.and(p, cb.greaterThan(root.get(DataItem_.timeValue), value));
						} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
							p = cb.and(p, cb.greaterThanOrEqualTo(root.get(DataItem_.timeValue), value));
						} else if (Comparison.isLessThan(filter.getComparison())) {
							p = cb.and(p, cb.lessThan(root.get(DataItem_.timeValue), value));
						} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
							p = cb.and(p, cb.lessThanOrEqualTo(root.get(DataItem_.timeValue), value));
						} else {
							p = cb.and(p, cb.equal(root.get(DataItem_.timeValue), value));
						}
					}
				}
			} else {
				String value = filter.getValue();
				if (Comparison.isNotEquals(filter.getComparison())) {
					p = cb.and(p, cb.notEqual(root.get(DataItem_.stringValue), value));
				} else if (Comparison.isGreaterThan(filter.getComparison())) {
					p = cb.and(p, cb.greaterThan(root.get(DataItem_.stringValue), value));
				} else if (Comparison.isGreaterThanOrEqualTo(filter.getComparison())) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(DataItem_.stringValue), value));
				} else if (Comparison.isLessThan(filter.getComparison())) {
					p = cb.and(p, cb.lessThan(root.get(DataItem_.stringValue), value));
				} else if (Comparison.isLessThanOrEqualTo(filter.getComparison())) {
					p = cb.and(p, cb.lessThanOrEqualTo(root.get(DataItem_.stringValue), value));
				} else if (Comparison.isLike(filter.getComparison())) {
					p = cb.and(p, cb.like(root.get(DataItem_.stringValue), "%" + value + "%"));
				} else if (Comparison.isNotLike(filter.getComparison())) {
					p = cb.and(p, cb.notLike(root.get(DataItem_.stringValue), "%" + value + "%"));
				} else {
					p = cb.and(p, cb.equal(root.get(DataItem_.stringValue), value));
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
}