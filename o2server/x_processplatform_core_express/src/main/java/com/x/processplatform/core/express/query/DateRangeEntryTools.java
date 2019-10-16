package com.x.processplatform.core.express.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.query.DateRangeEntry;
import com.x.query.core.entity.Item;

public class DateRangeEntryTools {
//	public static Predicate toPredicate11(CriteriaBuilder cb, Root<Item> root, DateRangeEntry dateRangeEntry)
//			throws Exception {
//		if (null == dateRangeEntry || (!dateRangeEntry.available())) {
//			return cb.conjunction();
//		}
//		switch (dateRangeEntry.getDateEffectType()) {
//		case start:
//			return cb.between(root.get(Item.startTime_FIELDNAME), dateRangeEntry.getStart(),
//					dateRangeEntry.getCompleted());
//		case completed:
//			return cb.between(root.get(Item.completedTime_FIELDNAME), dateRangeEntry.getStart(),
//					dateRangeEntry.getCompleted());
//		default:
//			return cb.conjunction();
//		}
//	}

	public static Predicate toWorkPredicate(CriteriaBuilder cb, Root<Work> root, DateRangeEntry dateRangeEntry)
			throws Exception {
		if (null == dateRangeEntry || (!dateRangeEntry.available())) {
			return cb.conjunction();
		}
		switch (dateRangeEntry.getDateEffectType()) {
		case start:
			return cb.between(root.get(Work.startTime_FIELDNAME), dateRangeEntry.getStart(),
					dateRangeEntry.getCompleted());
		default:
			return cb.conjunction();
		}
	}

	public static Predicate toWorkCompletedPredicate(CriteriaBuilder cb, Root<WorkCompleted> root,
			DateRangeEntry dateRangeEntry) throws Exception {
		if (null == dateRangeEntry || (!dateRangeEntry.available())) {
			return cb.conjunction();
		}
		switch (dateRangeEntry.getDateEffectType()) {
		case start:
			return cb.between(root.get(WorkCompleted.startTime_FIELDNAME), dateRangeEntry.getStart(),
					dateRangeEntry.getCompleted());
		case completed:
			return cb.between(root.get(WorkCompleted.completedTime_FIELDNAME), dateRangeEntry.getStart(),
					dateRangeEntry.getCompleted());
		default:
			return cb.conjunction();
		}
	}
}
