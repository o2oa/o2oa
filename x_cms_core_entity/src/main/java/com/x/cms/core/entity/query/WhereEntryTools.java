package com.x.cms.core.entity.query;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.bean.NameIdPair;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.utils.ListTools;
import com.x.cms.core.entity.content.DataItem;
import com.x.cms.core.entity.content.DataItem_;


public class WhereEntryTools {

	public static Predicate toPredicate(CriteriaBuilder cb, Root<DataItem> root, WhereEntry whereEntry)
			throws Exception {
		if ((null == whereEntry) || (!whereEntry.available())) {
			return cb.conjunction();
		}
		return cb.or( predicateAppInfo(cb, root, whereEntry.getAppInfoList()),
				predicateCategory(cb, root, whereEntry.getCategoryList()),
				predicateCompany(cb, root, whereEntry.getCompanyList()),
				predicateDepartment(cb, root, whereEntry.getDepartmentList()),
				predicatePerson(cb, root, whereEntry.getPersonList()),
				predicateIdentity(cb, root, whereEntry.getIdentityList()));
	}

	private static Predicate predicateAppInfo(CriteriaBuilder cb, Root<DataItem> root,
			List<NameIdPair> appInfoList ) throws Exception {
		if (ListTools.isEmpty(appInfoList)) {
			return cb.disjunction();
		}
		return root.get( DataItem_.appId ).in(ListTools.extractProperty( appInfoList, JpaObject.ID, String.class, true, true));
	}

	private static Predicate predicateCategory(CriteriaBuilder cb, Root<DataItem> root, List<NameIdPair> categoryList )
			throws Exception {
		if (ListTools.isEmpty(categoryList)) {
			return cb.disjunction();
		}
		return root.get(DataItem_.categoryId ).in(ListTools.extractProperty( categoryList, JpaObject.ID, String.class, true, true));
	}

	private static Predicate predicateCompany(CriteriaBuilder cb, Root<DataItem> root, List<NameIdPair> companies)
			throws Exception {
		if (ListTools.isEmpty(companies)) {
			return cb.disjunction();
		}
		return root.get(DataItem_.creatorCompany).in(ListTools.extractProperty(companies, "name", String.class, true, true));
	}

	private static Predicate predicateDepartment(CriteriaBuilder cb, Root<DataItem> root, List<NameIdPair> departments)
			throws Exception {
		if (ListTools.isEmpty(departments)) {
			return cb.disjunction();
		}
		return root.get(DataItem_.creatorDepartment).in(ListTools.extractProperty(departments, "name", String.class, true, true));
	}

	private static Predicate predicatePerson(CriteriaBuilder cb, Root<DataItem> root, List<NameIdPair> people)
			throws Exception {
		if (ListTools.isEmpty(people)) {
			return cb.disjunction();
		}
		return root.get(DataItem_.creatorPerson).in(ListTools.extractProperty(people, "name", String.class, true, true));
	}

	private static Predicate predicateIdentity(CriteriaBuilder cb, Root<DataItem> root, List<NameIdPair> identities)
			throws Exception {
		if (ListTools.isEmpty(identities)) {
			return cb.disjunction();
		}
		return root.get(DataItem_.creatorIdentity).in(ListTools.extractProperty(identities, "name", String.class, true, true));
	}

}