package com.x.cms.core.entity.query;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.NameIdPair;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;

public class WhereEntryTools {

	public static Predicate toDocumentPredicate(CriteriaBuilder cb, Root<Document> root, WhereEntry whereEntry)
			throws Exception {
		if ((null == whereEntry) || (!whereEntry.available())) {
			return cb.conjunction();
		}
		return cb.or(documentPredicateAppInfo(cb, root, whereEntry.getAppInfoList()),
				documentPredicateCategories(cb, root, whereEntry.getCategoryList()),
				documentPredicateUnit(cb, root, whereEntry.getUnitList()),
				documentPredicatePerson(cb, root, whereEntry.getPersonList()),
				documentPredicateIdentity(cb, root, whereEntry.getIdentityList()));
	}

	private static Predicate documentPredicateAppInfo(CriteriaBuilder cb, Root<Document> root, List<NameIdPair> appIds)
			throws Exception {
		if (ListTools.isEmpty(appIds)) {
			return cb.disjunction();
		}
		return root.get(Document.appId_FIELDNAME)
				.in(ListTools.extractProperty(appIds, JpaObject.id_FIELDNAME, String.class, true, true));
	}

	private static Predicate documentPredicateCategories(CriteriaBuilder cb, Root<Document> root,
			List<NameIdPair> categoryIds) throws Exception {
		if (ListTools.isEmpty(categoryIds)) {
			return cb.disjunction();
		}
		return root.get(Document.categoryId_FIELDNAME)
				.in(ListTools.extractProperty(categoryIds, JpaObject.id_FIELDNAME, String.class, true, true));
	}

	private static Predicate documentPredicateUnit(CriteriaBuilder cb, Root<Document> root, List<NameIdPair> Units)
			throws Exception {
		if (ListTools.isEmpty(Units)) {
			return cb.disjunction();
		}
		return root.get(Document.creatorUnitName_FIELDNAME)
				.in(ListTools.extractProperty(Units, "name", String.class, true, true));
	}

	private static Predicate documentPredicatePerson(CriteriaBuilder cb, Root<Document> root, List<NameIdPair> people)
			throws Exception {
		if (ListTools.isEmpty(people)) {
			return cb.disjunction();
		}
		return root.get(Document.creatorPerson_FIELDNAME)
				.in(ListTools.extractProperty(people, "name", String.class, true, true));
	}

	private static Predicate documentPredicateIdentity(CriteriaBuilder cb, Root<Document> root,
			List<NameIdPair> identities) throws Exception {
		if (ListTools.isEmpty(identities)) {
			return cb.disjunction();
		}
		return root.get(Document.creatorIdentity_FIELDNAME)
				.in(ListTools.extractProperty(identities, "name", String.class, true, true));
	}
}