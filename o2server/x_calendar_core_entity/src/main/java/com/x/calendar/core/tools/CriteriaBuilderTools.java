package com.x.calendar.core.tools;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

/**
 * 数据查询语句构建辅助类
 * @author O2LEE
 *
 */
public class CriteriaBuilderTools {
	
	/**
	 * 或 or
	 * @param criteriaBuilder
	 * @param predicate
	 * @param predicate_target
	 * @return
	 */
	public static Predicate predicate_or( CriteriaBuilder criteriaBuilder, Predicate predicate, Predicate predicate_target ) {
		if( predicate == null ) {
			return predicate_target;	
		}else {
			if( predicate_target != null ) {
				return criteriaBuilder.or( predicate, predicate_target );
			}else {
				return predicate;	
			}
		}
	}
	
	/**
	 * 并且  and
	 * @param criteriaBuilder
	 * @param predicate
	 * @param predicate_target
	 * @return
	 */
	public static Predicate predicate_and( CriteriaBuilder criteriaBuilder, Predicate predicate, Predicate predicate_target ) {
		if( predicate == null ) {
			return predicate_target;	
		}else {
			if( predicate_target != null ) {
				return criteriaBuilder.and( predicate, predicate_target );	
			}else {
				return predicate;	
			}
		}
	}
}
