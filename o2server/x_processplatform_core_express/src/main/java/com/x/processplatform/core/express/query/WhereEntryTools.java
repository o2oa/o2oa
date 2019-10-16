package com.x.processplatform.core.express.query;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.NameIdPair;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.query.WhereEntry;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public class WhereEntryTools {

//	public static Predicate toPredicate11(CriteriaBuilder cb, Root<Item> root, WhereEntry whereEntry)
//			throws Exception {
//		if ((null == whereEntry) || (!whereEntry.available())) {
//			return cb.conjunction();
//		}
//		return cb.or(predicateApplication(cb, root, whereEntry.getApplicationList()),
//				predicateProcess(cb, root, whereEntry.getProcessList()),
//				predicateUnit(cb, root, whereEntry.getUnitList()),
//				predicatePerson(cb, root, whereEntry.getPersonList()),
//				predicateIdentity(cb, root, whereEntry.getIdentityList()));
//	}

//	private static Predicate predicateApplication11(CriteriaBuilder cb, Root<Item> root,
//			List<NameIdPair> applications) throws Exception {
//		if (ListTools.isEmpty(applications)) {
//			return cb.disjunction();
//		}
//		return root.get(Item_.application)
//				.in(ListTools.extractProperty(applications, JpaObject.id_FIELDNAME, String.class, true, true));
//	}
//
//	private static Predicate predicateProcess11(CriteriaBuilder cb, Root<Item> root, List<NameIdPair> processes)
//			throws Exception {
//		if (ListTools.isEmpty(processes)) {
//			return cb.disjunction();
//		}
//		return root.get(Item_.process)
//				.in(ListTools.extractProperty(processes, JpaObject.id_FIELDNAME, String.class, true, true));
//	}
//
//	private static Predicate predicateUnit(CriteriaBuilder cb, Root<Item> root, List<NameIdPair> Units)
//			throws Exception {
//		if (ListTools.isEmpty(Units)) {
//			return cb.disjunction();
//		}
//		return root.get(Item_.creatorUnit).in(ListTools.extractProperty(Units, "name", String.class, true, true));
//	}
//
//	private static Predicate predicatePerson(CriteriaBuilder cb, Root<Item> root, List<NameIdPair> people)
//			throws Exception {
//		if (ListTools.isEmpty(people)) {
//			return cb.disjunction();
//		}
//		return root.get(Item_.creatorPerson)
//				.in(ListTools.extractProperty(people, "name", String.class, true, true));
//	}

//	private static Predicate predicateIdentity(CriteriaBuilder cb, Root<Item> root, List<NameIdPair> identities)
//			throws Exception {
//		if (ListTools.isEmpty(identities)) {
//			return cb.disjunction();
//		}
//		return root.get(Item_.creatorIdentity)
//				.in(ListTools.extractProperty(identities, "name", String.class, true, true));
//	}

	public static Predicate toWorkPredicate(CriteriaBuilder cb, Root<Work> root, WhereEntry whereEntry)
			throws Exception {
		if ((null == whereEntry) || (!whereEntry.available())) {
			return cb.conjunction();
		}
		return cb.or(workPredicateApplication(cb, root, whereEntry.getApplicationList()),
				workPredicateProcess(cb, root, whereEntry.getProcessList()),
				workPredicateUnit(cb, root, whereEntry.getUnitList()),
				workPredicatePerson(cb, root, whereEntry.getPersonList()),
				workPredicateIdentity(cb, root, whereEntry.getIdentityList()));
	}

	private static Predicate workPredicateApplication(CriteriaBuilder cb, Root<Work> root,
			List<NameIdPair> applications) throws Exception {
		if (ListTools.isEmpty(applications)) {
			return cb.disjunction();
		}
		return root.get(Work_.application)
				.in(ListTools.extractProperty(applications, JpaObject.id_FIELDNAME, String.class, true, true));
	}

	private static Predicate workPredicateProcess(CriteriaBuilder cb, Root<Work> root, List<NameIdPair> processes)
			throws Exception {
		if (ListTools.isEmpty(processes)) {
			return cb.disjunction();
		}
		return root.get(Work_.process)
				.in(ListTools.extractProperty(processes, JpaObject.id_FIELDNAME, String.class, true, true));
	}

	private static Predicate workPredicateUnit(CriteriaBuilder cb, Root<Work> root, List<NameIdPair> Units)
			throws Exception {
		if (ListTools.isEmpty(Units)) {
			return cb.disjunction();
		}
		return root.get(Work_.creatorUnit).in(ListTools.extractProperty(Units, "name", String.class, true, true));
	}

	private static Predicate workPredicatePerson(CriteriaBuilder cb, Root<Work> root, List<NameIdPair> people)
			throws Exception {
		if (ListTools.isEmpty(people)) {
			return cb.disjunction();
		}
		return root.get(Work_.creatorPerson).in(ListTools.extractProperty(people, "name", String.class, true, true));
	}

	private static Predicate workPredicateIdentity(CriteriaBuilder cb, Root<Work> root, List<NameIdPair> identities)
			throws Exception {
		if (ListTools.isEmpty(identities)) {
			return cb.disjunction();
		}
		return root.get(Work_.creatorIdentity)
				.in(ListTools.extractProperty(identities, "name", String.class, true, true));
	}

	public static Predicate toWorkCompletedPredicate(CriteriaBuilder cb, Root<WorkCompleted> root,
			WhereEntry whereEntry) throws Exception {
		if ((null == whereEntry) || (!whereEntry.available())) {
			return cb.conjunction();
		}
		return cb.or(workCompletedPredicateApplication(cb, root, whereEntry.getApplicationList()),
				workCompletedPredicateProcess(cb, root, whereEntry.getProcessList()),
				workCompletedPredicateUnit(cb, root, whereEntry.getUnitList()),
				workCompletedPredicatePerson(cb, root, whereEntry.getPersonList()),
				workCompletedPredicateIdentity(cb, root, whereEntry.getIdentityList()));
	}

	private static Predicate workCompletedPredicateApplication(CriteriaBuilder cb, Root<WorkCompleted> root,
			List<NameIdPair> applications) throws Exception {
		if (ListTools.isEmpty(applications)) {
			return cb.disjunction();
		}
		return root.get(WorkCompleted_.application)
				.in(ListTools.extractProperty(applications, JpaObject.id_FIELDNAME, String.class, true, true));
	}

	private static Predicate workCompletedPredicateProcess(CriteriaBuilder cb, Root<WorkCompleted> root,
			List<NameIdPair> processes) throws Exception {
		if (ListTools.isEmpty(processes)) {
			return cb.disjunction();
		}
		return root.get(WorkCompleted_.process)
				.in(ListTools.extractProperty(processes, JpaObject.id_FIELDNAME, String.class, true, true));
	}

	private static Predicate workCompletedPredicateUnit(CriteriaBuilder cb, Root<WorkCompleted> root,
			List<NameIdPair> Units) throws Exception {
		if (ListTools.isEmpty(Units)) {
			return cb.disjunction();
		}
		return root.get(WorkCompleted_.creatorUnit)
				.in(ListTools.extractProperty(Units, "name", String.class, true, true));
	}

	private static Predicate workCompletedPredicatePerson(CriteriaBuilder cb, Root<WorkCompleted> root,
			List<NameIdPair> people) throws Exception {
		if (ListTools.isEmpty(people)) {
			return cb.disjunction();
		}
		return root.get(WorkCompleted_.creatorPerson)
				.in(ListTools.extractProperty(people, "name", String.class, true, true));
	}

	private static Predicate workCompletedPredicateIdentity(CriteriaBuilder cb, Root<WorkCompleted> root,
			List<NameIdPair> identities) throws Exception {
		if (ListTools.isEmpty(identities)) {
			return cb.disjunction();
		}
		return root.get(WorkCompleted_.creatorIdentity)
				.in(ListTools.extractProperty(identities, "name", String.class, true, true));
	}

}