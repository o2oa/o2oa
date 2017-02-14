package com.x.processplatform.core.entity.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.core.entity.content.DataItem;
import com.x.processplatform.core.entity.content.DataItem_;

public class DateRangeEntryTools {
	public static Predicate toPredicate(CriteriaBuilder cb, Root<DataItem> root, DateRangeEntry dateRangeEntry)
			throws Exception {
		if (null == dateRangeEntry || (!dateRangeEntry.available())) {
			return cb.conjunction();
		}
		switch (dateRangeEntry.getDateEffectType()) {
		case start:
			return cb.between(root.get(DataItem_.startTime), dateRangeEntry.getStart(), dateRangeEntry.getCompleted());
		case completed:
			return cb.between(root.get(DataItem_.completedTime), dateRangeEntry.getStart(),
					dateRangeEntry.getCompleted());
		default:
			return cb.conjunction();
		}
	}
}
