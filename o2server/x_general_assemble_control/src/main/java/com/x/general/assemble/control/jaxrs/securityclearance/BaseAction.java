package com.x.general.assemble.control.jaxrs.securityclearance;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;

abstract class BaseAction extends StandardJaxrsAction {

	public static class PredicateWithLimitSecurityClearance implements Predicate<Map.Entry<String, Integer>> {

		private Integer limitSecurityClearance;

		public PredicateWithLimitSecurityClearance(Integer limitSecurityClearance) {
			this.limitSecurityClearance = limitSecurityClearance;
		}

		@Override
		public boolean test(Entry<String, Integer> o) {
			return (null != o.getValue()) && o.getValue() <= limitSecurityClearance;
		}

	}

	public static class PredicateWithoutLimitSecurityClearance implements Predicate<Map.Entry<String, Integer>> {

		@Override
		public boolean test(Entry<String, Integer> o) {
			return (null != o.getValue());
		}

	}

}
