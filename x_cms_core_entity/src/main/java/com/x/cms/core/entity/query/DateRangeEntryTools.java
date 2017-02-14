package com.x.cms.core.entity.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.cms.core.entity.content.DataItem;
import com.x.cms.core.entity.content.DataItem_;



public class DateRangeEntryTools {
	public static Predicate toPredicate(CriteriaBuilder cb, Root<DataItem> root, DateRangeEntry dateRangeEntry )
			throws Exception {
		if (null == dateRangeEntry || (!dateRangeEntry.available())) {
			return cb.conjunction();
		}
		return cb.between(root.get( DataItem_.createTime ), dateRangeEntry.getStart(), dateRangeEntry.getCompleted());
	}
}
