package com.x.cms.core.entity.tools;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject_;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.tools.filter.QueryFilter;
import com.x.cms.core.entity.tools.filter.term.DateBetweenTerm;
import com.x.cms.core.entity.tools.filter.term.EqualsTerm;
import com.x.cms.core.entity.tools.filter.term.InTerm;
import com.x.cms.core.entity.tools.filter.term.IsFalseTerm;
import com.x.cms.core.entity.tools.filter.term.IsTrueTerm;
import com.x.cms.core.entity.tools.filter.term.LikeTerm;
import com.x.cms.core.entity.tools.filter.term.MemberTerm;
import com.x.cms.core.entity.tools.filter.term.NotEqualsTerm;
import com.x.cms.core.entity.tools.filter.term.NotInTerm;
import com.x.cms.core.entity.tools.filter.term.NotMemberTerm;


public class CriteriaBuilderTools {
	
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
	
	/**
	 * 根据过滤条件组织查询语句
	 * @param cls  -查询的实体类名
	 * @param cls_  -查询的实体类对应的StaticMetamodel
	 * @param em  -EntityManager
	 * @param queryFilter  -查询过滤条件
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	public static <T extends JpaObject, T_ extends SliceJpaObject_> Predicate composePredicateWithQueryFilter( 
			Class<T> cls, Class<T_> cls_, EntityManager em,  QueryFilter queryFilter ) throws NoSuchFieldException, SecurityException {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery( cls );
		Root<T> root = cq.from( cls );
		Predicate p = null;
		return composePredicateWithQueryFilter( cls_, cb, p, root,  queryFilter ) ;
	}
	
	/**
	 * 根据过滤条件组织查询语句
	 * @param cls_  -查询的实体类对应的StaticMetamodel
	 * @param cb  -CriteriaBuilder
	 * @param p  -Predicate
	 * @param root  -查询根
	 * @param queryFilter  -查询过滤条件
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	public static <T extends JpaObject, T_ extends SliceJpaObject_> Predicate composePredicateWithQueryFilter( 
			Class<T_> cls_, CriteriaBuilder cb, Predicate p, Root<T> root,  QueryFilter queryFilter ) throws NoSuchFieldException, SecurityException {
		if( queryFilter == null ) {
			queryFilter = new QueryFilter();
		}
		//组装条件
		if( ListTools.isNotEmpty( queryFilter.getEqualsTerms() )) {
			for( EqualsTerm term : queryFilter.getEqualsTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || term.getValue() == null || StringUtils.isEmpty( term.getValue().toString() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal( root.get( cls_.getDeclaredField( term.getName() ).getName() ), term.getValue() ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.equal( root.get( cls_.getDeclaredField( term.getName() ).getName() ), term.getValue() ) );
				}
			}
		}
		if( ListTools.isNotEmpty( queryFilter.getNotEqualsTerms() )) {
			for( NotEqualsTerm term : queryFilter.getNotEqualsTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || term.getValue() == null || StringUtils.isEmpty( term.getValue().toString() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.notEqual( root.get( cls_.getDeclaredField( term.getName() ).getName() ), term.getValue() ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.notEqual( root.get( cls_.getDeclaredField( term.getName() ).getName() ), term.getValue() ) );
				}
			}
		}
		if( ListTools.isNotEmpty( queryFilter.getInTerms() )) {
			for( InTerm term : queryFilter.getInTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || ListTools.isEmpty( term.getValue())) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, root.get( cls_.getDeclaredField( term.getName() ).getName() ).in( term.getValue() ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, root.get( cls_.getDeclaredField( term.getName() ).getName() ).in( term.getValue() ) );
				}
			}
		}
		if( ListTools.isNotEmpty( queryFilter.getNotInTerms() )) {
			for( NotInTerm term : queryFilter.getNotInTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || ListTools.isEmpty( term.getValue())) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, root.get( cls_.getDeclaredField( term.getName() ).getName() ).in( term.getValue() ).not());
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, root.get( cls_.getDeclaredField( term.getName() ).getName() ).in( term.getValue() ).not() );
				}
			}
		}		
		if( ListTools.isNotEmpty( queryFilter.getMemberTerms() )) {
			for( MemberTerm term : queryFilter.getMemberTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || term.getValue() == null || StringUtils.isEmpty( term.getValue().toString() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.isMember( term.getValue(), root.get( cls_.getDeclaredField( term.getName() ).getName() ) ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.isMember( term.getValue(), root.get( cls_.getDeclaredField( term.getName() ).getName() ) ) );
				}
			}
		}		
		if( ListTools.isNotEmpty( queryFilter.getNotMemberTerms() )) {
			for( NotMemberTerm term : queryFilter.getNotMemberTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || term.getValue() == null || StringUtils.isEmpty( term.getValue().toString() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
						p = CriteriaBuilderTools.predicate_and( cb, p, cb.isNotMember( term.getValue(), root.get( cls_.getDeclaredField( term.getName() ).getName() ) ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.isNotMember( term.getValue(), root.get( cls_.getDeclaredField( term.getName() ).getName() ) ) );
				}
			}
		}
				
		if( ListTools.isNotEmpty( queryFilter.getLikeTerms())) {
			for( LikeTerm term : queryFilter.getLikeTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || term.getValue() == null || StringUtils.isEmpty( term.getValue().toString() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.like( root.get( cls_.getDeclaredField( term.getName() ).getName() ), "%"+term.getValue()+"%" ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.like( root.get( cls_.getDeclaredField( term.getName() ).getName() ), "%"+term.getValue()+"%" ));
				}
			}
		}
		
		if( ListTools.isNotEmpty( queryFilter.getIsTrueTerms())) {
			for( IsTrueTerm term : queryFilter.getIsTrueTerms() ) {
				if( StringUtils.isEmpty( term.getName() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.isTrue( root.get( cls_.getDeclaredField( term.getName() ).getName() )));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.isTrue( root.get( cls_.getDeclaredField( term.getName() ).getName() )));
				}
			}
		}
		
		if( ListTools.isNotEmpty( queryFilter.getIsFalseTerms())) {
			for( IsFalseTerm term : queryFilter.getIsFalseTerms() ) {
				if( StringUtils.isEmpty( term.getName() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.isFalse( root.get( cls_.getDeclaredField( term.getName() ).getName() )));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.isFalse( root.get( cls_.getDeclaredField( term.getName() ).getName() )));
				}
			}
		}
		
		if( ListTools.isNotEmpty( queryFilter.getDateBetweenTerms())) {
			for( DateBetweenTerm term : queryFilter.getDateBetweenTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || term.getValue() == null || ListTools.isEmpty( term.getValue() ) || term.getValue().size() < 2) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.between( root.get( cls_.getDeclaredField( term.getName() ).getName() ), term.getValue().get(0), term.getValue().get(1) ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.between( root.get( cls_.getDeclaredField( term.getName() ).getName() ), term.getValue().get(0), term.getValue().get(1) ));
				}
			}
		}
		
		//继续递归查询条件
		if( queryFilter.getAnd() != null  ) {
			queryFilter.setJoinType( "and" );
			composePredicateWithQueryFilter( cls_, cb, p, root,  queryFilter.getAnd() ) ;
		}
		
		if( queryFilter.getOr() != null  ) {
			queryFilter.setJoinType( "or" );
			composePredicateWithQueryFilter( cls_, cb, p, root,  queryFilter.getOr() ) ;
		}
		
		return p;
	}
	
	/**
	 * 通用的分页查询
	 * @param em  -EntityManager
	 * @param cls  -查询的实体类名
	 * @param cls_  -查询的实体类对应的StaticMetamodel
	 * @param maxCount  -输出最大条目数量
	 * @param queryFilter  -查询过滤条件
	 * @param sequenceFieldValue  -上一条的序列值
	 * @param orderField  -排序列名，默认createTime
	 * @param order  -排序方式，默认DESC
	 * @return
	 * @throws Exception
	 */
	public static <T extends JpaObject, T_ extends SliceJpaObject_> List<T> listNextWithCondition(  EntityManager em, Class<T> cls, Class<T_> cls_, Integer maxCount, 
			QueryFilter queryFilter, Object sequenceFieldValue,  String orderField, String order ) throws Exception {
		
		if( StringUtils.isEmpty( order ) ){
			order = "DESC";
		}
		if( StringUtils.isEmpty( orderField )){
			orderField = "createTime";
		}
		if( maxCount == null || maxCount == 0 ) {
			maxCount = 20;
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery( cls );
		Root<T> root = cq.from( cls );
		
		Predicate p = null;
		//根据序列来排序
		if( sequenceFieldValue != null ){
			if( "DESC".equalsIgnoreCase( order )){
				p = predicate_and( cb, p, cb.lessThan( root.get( cls_.getDeclaredField( orderField ).getName() ), sequenceFieldValue.toString() ));
			}else{
				p = predicate_and( cb, p, cb.greaterThan( root.get( cls_.getDeclaredField( orderField ).getName() ), sequenceFieldValue.toString() ));
			}
		}
		
		//根据过滤条件来组织查询语句
		p = CriteriaBuilderTools.composePredicateWithQueryFilter( cls_, cb, p, root, queryFilter );
		
		//按要求排序查询结果
		if( "DESC".equalsIgnoreCase( order )){
			cq.orderBy( cb.desc( root.get( cls_.getDeclaredField( orderField ).getName() )));
		}else{
			cq.orderBy( cb.asc( root.get( cls_.getDeclaredField( orderField ).getName() )) );
		}
		
		return em.createQuery(cq.where(p).distinct(true)).setMaxResults( maxCount ).getResultList();
	}
	
	/**
	 * 根据条件组织一个排序的语句
	 * @param <T>
	 * @param <T_>
	 * @param cb
	 * @param root
	 * @param cls_
	 * @param fieldName
	 * @param orderType
	 * @return
	 */
	public static <T extends JpaObject, T_ extends SliceJpaObject_>Order getOrder( CriteriaBuilder cb, Root<T> root, Class<T_> cls_, String fieldName, String orderType ) {
		if( StringUtils.isEmpty( fieldName )) {
			fieldName = Document.sequence_FIELDNAME;
		}
		
		Boolean fieldExists = false;
		if( Document.sequence_FIELDNAME.equalsIgnoreCase( fieldName )) {
			fieldExists = true;
			fieldName = Document.sequence_FIELDNAME;
		}else if( Document.createTime_FIELDNAME.equalsIgnoreCase( fieldName )) {
			fieldExists = true;
			fieldName = Document.createTime_FIELDNAME;
		}else if( Document.updateTime_FIELDNAME.equalsIgnoreCase( fieldName )) {
			fieldExists = true;
			fieldName = Document.updateTime_FIELDNAME;
		}else {
			Field[] fields = cls_.getDeclaredFields();
			for( Field field : fields ) {
				if( field.getName().equalsIgnoreCase( fieldName ) ) {
					fieldName = field.getName(); //校正排序列的名称避免大小写的影响
					fieldExists = true;
				}
			}
		}
		if( !fieldExists ) { //如果排序列不存在，就直接使用sequence，让SQL可以正常执行
			fieldName = Document.sequence_FIELDNAME;
		}
		if( StringUtils.isEmpty( orderType )) {
			orderType = "desc";
		}
		if( "desc".equalsIgnoreCase( orderType )) {
			return cb.desc( root.get( fieldName ).as(String.class));
		}else {
			return cb.asc( root.get( fieldName ).as(String.class));
		}
	}
}
