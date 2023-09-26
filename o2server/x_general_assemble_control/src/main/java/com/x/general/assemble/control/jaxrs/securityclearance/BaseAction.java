package com.x.general.assemble.control.jaxrs.securityclearance;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;

abstract class BaseAction extends StandardJaxrsAction {

	public static class PredicateWithSystemSecurityClearance implements Predicate<Map.Entry<String, Integer>> {

		private Integer systemSecurityClearance;

		public PredicateWithSystemSecurityClearance(Integer systemSecurityClearance) {
			this.systemSecurityClearance = systemSecurityClearance;
		}

		@Override
		public boolean test(Entry<String, Integer> o) {
			return (null != o.getValue()) && o.getValue() >= systemSecurityClearance;
		}

	}

	public static class PredicateWithoutSystemSecurityClearance implements Predicate<Map.Entry<String, Integer>> {

		@Override
		public boolean test(Entry<String, Integer> o) {
			return (null != o.getValue());
		}

	}

}
