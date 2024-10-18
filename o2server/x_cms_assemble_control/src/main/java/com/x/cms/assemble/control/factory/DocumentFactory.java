package com.x.cms.assemble.control.factory;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.document.WrapInDocumentFilter;
import com.x.cms.core.entity.*;
import com.x.cms.core.entity.enums.DocumentStatus;
import com.x.cms.core.express.tools.CriteriaBuilderTools;
import com.x.cms.core.express.tools.filter.QueryFilter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 文档信息基础功能服务类
 *
 * @author O2LEE
 */
public class DocumentFactory extends AbstractFactory {

	public DocumentFactory(Business business) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的Document信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Document get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Document.class, ExceptionWhen.none);
	}

	/**
	 * 获取指定Id的Document的sequence值
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getSequence( String id ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = cb.equal( root.get(Document_.id ), id );
		cq.select(root.get( Document_.sequence));
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 列示全部的Document信息列表
	 * @return
	 * @throws Exception
	 */
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		cq.select( root.get( Document_.id ));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * 根据应用ID列示所有的Document信息列表
	 * @param appId
	 * @param documentType
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listByAppId( String appId, String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal(root.get( Document_.appId ), appId );
		cq.select( root.get( Document_.id) ).where(p);
		if( StringUtils.isNotEmpty( documentType) && !"全部".equals(documentType)&& !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( Document_.documentType), documentType));
		}
		if (maxCount!=null && maxCount > 0) {
			return em.createQuery(cq).setMaxResults(maxCount).getResultList();
		}else{
			return em.createQuery(cq).getResultList();
		}
	}

	public List<String> listByCategoryId( String categoryId, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal(root.get( Document_.categoryId ), categoryId );
		cq.select(root.get( Document_.id)).where(p);
		if (maxCount!=null && maxCount > 0) {
			return em.createQuery(cq).setMaxResults(maxCount).getResultList();
		}else{
			return em.createQuery(cq).getResultList();
		}
	}

	/**
	 * 根据指定分类里的文档ID，支持按指定列排序
	 * @param categoryId
	 * @param orderField
	 * @param orderType
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listByCategoryId( String categoryId, String orderField, String orderType, Integer maxCount ) throws Exception {
		List<String> docIds = new ArrayList<>();
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery( Document.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal(root.get( Document_.categoryId ), categoryId );

		if( StringUtils.isNotEmpty( orderField ) && StringUtils.isNotEmpty( orderType ) ) {
			Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, Document_.class, orderField, orderType );
			if( orderWithField != null ) {
				cq.orderBy( orderWithField );
			}
		}
		//SELECT * FROM Document d WHERE d.categoryId = 'a1385543-c077-4db4-bc54-e9753ca0db73' ORDER BY d.createTime
		List<Document> docmentList = em.createQuery( cq.where(p) ).setMaxResults(maxCount).getResultList();

		if( ListTools.isNotEmpty( docmentList )) {
			for( Document document : docmentList ) {
				docIds.add( document.getId() );
			}
		}
		return docIds;
	}

	public List<String> listByCategoryIdAndNotEqualAppName( String categoryId, String appName, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal(root.get( Document_.categoryId ), categoryId );
		p = cb.and( p, cb.notEqual(root.get( Document_.appName ), appName ) );
		cq.select(root.get( Document_.id)).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}

	public List<String> listByCategoryIdAndCategoryName( String categoryId, String categoryName, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal(root.get( Document_.categoryId ), categoryId );
		p = cb.and( p, cb.equal(root.get( Document_.categoryName ), categoryName ) );
		cq.select(root.get( Document_.id)).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}


	public Long countByCategoryId( String categoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = cb.equal( root.get(Document_.categoryId), categoryId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countByCategoryIdNotContainDraft( String categoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = cb.equal( root.get(Document_.categoryId), categoryId );
		p = cb.and(p, cb.notEqual(root.get(Document_.docStatus), DocumentStatus.DRAFT.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countByCategoryIdAndNotEqualAppName( String categoryId, String appName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = cb.equal( root.get(Document_.categoryId), categoryId );
		p = cb.and( p, cb.notEqual(root.get( Document_.appName ), appName ) );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countByCategoryIdAndNotEqualsCategoryName( String categoryId, String categoryName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = cb.equal( root.get(Document_.categoryId), categoryId );
		p = cb.and( p, cb.notEqual(root.get( Document_.categoryName ), categoryName ) );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countByAppId( String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = cb.equal( root.get(Document_.appId), appId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * 根据条件查询符合条件的文档信息，根据上一条的sequnce查询指定数量的信息
	 * @param maxCount
	 * @param sequenceFieldValue
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Document> listNextWithCondition( Integer maxCount, String sequenceFieldValue, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery(Document.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Document_.class, cb, null, root, queryFilter );
		if( StringUtils.isNotEmpty( sequenceFieldValue ) ) {
			Predicate p_seq = cb.isNotNull( root.get( orderField ) );
			if( "desc".equalsIgnoreCase( orderType )){
				p_seq = cb.and( p_seq, cb.lessThan( root.get( orderField ), sequenceFieldValue.toString() ));
			}else{
				p_seq = cb.and( p_seq, cb.greaterThan( root.get( orderField ), sequenceFieldValue.toString() ));
			}
			p = cb.and( p, p_seq);
		}

		List<Order> orders = new ArrayList<>();
		Order isTopOrder = CriteriaBuilderTools.getOrder( cb, root, Document_.class, Document.isTop_FIELDNAME, "desc" );
		if( isTopOrder != null ){
			orders.add( isTopOrder );
		}
		Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, Document_.class, orderField, orderType );
		if( orderWithField != null ){
			orders.add( orderWithField );
		}
		if( ListTools.isNotEmpty( orders )) {
			cq.orderBy( orders );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}

	/**
	 * 根据条件查询符合条件的文档信息，根据上一条的sequnce查询指定数量的信息
	 * @param maxCount
	 * @param sequenceFieldValue
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Document> listPrevWithCondition( Integer maxCount, String sequenceFieldValue, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery(Document.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Document_.class, cb, null, root, queryFilter );
		if( StringUtils.isNotEmpty( sequenceFieldValue ) ) {
			Predicate p_seq = cb.isNotNull( root.get( orderField ) );
			if( "desc".equalsIgnoreCase( orderType )){
				p_seq = cb.and( p_seq, cb.greaterThan( root.get( orderField ), sequenceFieldValue.toString() ));
			}else{
				p_seq = cb.and( p_seq, cb.lessThan( root.get( orderField ), sequenceFieldValue.toString() ));
			}
			p = cb.and( p, p_seq);
		}

		List<Order> orders = new ArrayList<>();
		Order isTopOrder = CriteriaBuilderTools.getOrder( cb, root, Document_.class, Document.isTop_FIELDNAME, "desc" );
		if( isTopOrder != null ){
			orders.add( isTopOrder );
		}
		Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, Document_.class, orderField, orderType );
		if( orderWithField != null ){
			orders.add( orderWithField );
		}
		if( ListTools.isNotEmpty( orders )) {
			cq.orderBy( orders );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}

	/**
	 * 根据条件查询指定数量的符合条件的文档信息列表
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @param maxCount
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public List<Document> listNextWithCondition( String orderField, String orderType, QueryFilter queryFilter, int maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery(Document.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Document_.class, cb, null, root, queryFilter );

		List<Order> orders = new ArrayList<>();
		Order isTopOrder = CriteriaBuilderTools.getOrder( cb, root, Document_.class, Document.isTop_FIELDNAME, "desc" );
		if( isTopOrder != null ){
			orders.add( isTopOrder );
		}

		Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, Document_.class, orderField, orderType );
		if( orderWithField != null ){
			orders.add( orderWithField );
		}

		if( !Document.isFieldInSequence(orderField)) {
			//如果是没有做sequence的列，很可能排序值不唯一，比如有多个文档有相同的值，所以使用多一列排序列来确定每次查询的顺序
			orderWithField = CriteriaBuilderTools.getOrder( cb, root, Document_.class, Document.id_FIELDNAME, orderType );
			if( orderWithField != null ){
				orders.add( orderWithField );
			}
		}

		if( ListTools.isNotEmpty(  orders )){
			cq.orderBy( orders );
		}

		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}

	public List<Document> listMyDraft( String name, List<String> categoryIdList, String documentType ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery( Document.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.creatorPerson ), name );
		p = cb.and( p, cb.equal(root.get( Document_.docStatus ), "draft"));
		if(ListTools.isNotEmpty( categoryIdList )) {
			p = cb.and( p, root.get( Document_.categoryId ).in( categoryIdList ));
		}
		if(StringUtils.isNotEmpty( documentType ) && !"全部".equals(documentType)&& !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( Document_.documentType ), documentType));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listWithImportBatchName(String importBatchName) throws Exception {
		if( StringUtils.isEmpty( importBatchName ) ){
			throw new Exception("importBatchName is empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.importBatchName ), importBatchName );
		cq.select(root.get( Document_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listReviewedIdsByCategoryId( String categoryId, Integer maxCount) throws Exception {
		if( StringUtils.isEmpty( categoryId ) ){
			throw new Exception("categoryId is empty!");
		}
		if( maxCount == null ){
			maxCount = 1000;
		}
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.documentType ), "信息" );
		p = cb.and( p, cb.equal( root.get( Document_.categoryId ), categoryId ));
		p = cb.and( p, cb.isTrue(  root.get( Document_.reviewed ) ));
		cq.select(root.get( Document_.id));
		return em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
	}

	public List<String> listReviewedIdsByAppId( String appId, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( appId ) ){
			throw new Exception("appId is empty!");
		}
		if( maxCount == null ){
			maxCount = 1000;
		}
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.documentType ), "信息" );
		p = cb.and( p, cb.equal( root.get( Document_.appId ), appId ));
		p = cb.and( p, cb.isTrue(  root.get( Document_.reviewed ) ));
		cq.select(root.get( Document_.id));
		return em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
	}

	public List<String> listUnReviewIds(Integer maxCount) throws Exception {
		if( maxCount == null ){
			maxCount = 500;
		}
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.isFalse( root.get(Document_.reviewed ));
		p = cb.or(p,  cb.isNull(root.get(Document_.reviewed )));
		cq.select(root.get( Document_.id));
		return em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
	}

	public List<String> listUnReviewIds(Integer maxCount, Integer minutesAgo) throws Exception {
		if( maxCount == null ){
			maxCount = 500;
		}
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.lessThan(root.get(Document_.createTime),
				DateTools.getAdjustTimeDay(new Date(), 0, 0, -minutesAgo, 0));
		p = cb.and(p, cb.or(cb.isFalse( root.get(Document_.reviewed )), cb.isNull(root.get(Document_.reviewed ))));
		cq.select(root.get( Document_.id));
		cq.where(p).orderBy( cb.asc( root.get( CmsBatchOperation_.createTime ) ) );
		return em.createQuery(cq).setMaxResults(maxCount).getResultList();
	}

	public Long countWithConditionOutofPermission(QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Document_.class, cb, null, root, queryFilter );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * 根据条件分页查询符合条件的文档信息列表
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @param adjustPage
	 * @param adjustPageSize
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public List<Document> listPagingWithCondition( String personName, String orderField, String orderType, QueryFilter queryFilter, Integer adjustPage,
												   Integer adjustPageSize, Boolean isAuthor, Boolean excludeAllRead, String readFlag, Boolean isAdmin) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		EntityManager em1 = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb1 = em1.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Document_.class, cb, null, root, queryFilter );

		if(BooleanUtils.isTrue(isAdmin)){
			if(!BooleanUtils.isTrue(isAuthor)) {
				Subquery<Review> subQuery = cq.subquery(Review.class);
				Root<Review> root1 = subQuery.from(em1.getMetamodel().entity(Review.class));
				subQuery.select(root1);
				Predicate p_permission = cb1.equal(root1.get(Review_.permissionObj), personName);
				p_permission = cb1.and(p_permission, cb1.equal(root1.get(Review_.docId), root.get(Document_.id)));
				subQuery.where(p_permission);
				if(BooleanUtils.isTrue(excludeAllRead)){
					p = cb.and(p, cb.exists(subQuery));
				}else {
					Predicate orP = cb.or(cb.isTrue(root.get(Document_.isAllRead)), cb.exists(subQuery));
					p = cb.and(p, orP);
				}
			}else {
				List<String> list = new ArrayList<>();
				List<String> unitAllList = this.business().organization().unit().listWithPersonSupNested(personName);
				if(unitAllList!=null) {
					for (String item : unitAllList) {
						if (item.indexOf("@") > -1) {
							list.add(StringUtils.substringAfter(item, "@"));
						}
					}
					unitAllList.addAll(list);
					list.clear();
				}
				List<String> groupList = this.business().organization().group().listWithPersonReference(
						ListTools.toList(personName), true, true, true);
				if(groupList!=null) {
					for (String item : groupList) {
						if (item.indexOf("@") > -1) {
							list.add(StringUtils.substringAfter(item, "@"));
						}
					}
					groupList.addAll(list);
					list.clear();
				}
				Predicate ep = cb.disjunction();
				ep = cb.or(ep, cb.isMember(personName, root.get(Document_.authorPersonList)));
				if(ListTools.isNotEmpty(unitAllList)){
					ep = cb.or(ep, root.get(Document_.authorUnitList).in(unitAllList));
				}
				if(ListTools.isNotEmpty(groupList)){
					ep = cb.or(ep, root.get(Document_.authorGroupList).in(groupList));
				}
				p = cb.and(p, ep);
			}
		}
		if(StringUtils.isNotBlank(readFlag)) {
			EntityManager em2 = this.entityManagerContainer().get(DocumentViewRecord.class);
			CriteriaBuilder cb2 = em2.getCriteriaBuilder();
			Subquery<DocumentViewRecord> subQuery2 = cq.subquery(DocumentViewRecord.class);
			Root<DocumentViewRecord> root2 = subQuery2.from(
					em2.getMetamodel().entity(DocumentViewRecord.class));
			subQuery2.select(root2);
			Predicate p2 = cb2.equal(root2.get(DocumentViewRecord_.viewerName), personName);
			p2 = cb2.and(p2, cb2.equal(root2.get(DocumentViewRecord_.documentId),
					root.get(Document_.id)));
			subQuery2.where(p2);
			if (WrapInDocumentFilter.READ_FLAG_READ.equalsIgnoreCase(readFlag)) {
				p = cb.and(p, cb.exists(subQuery2));
			} else if (WrapInDocumentFilter.READ_FLAG_UNREAD.equalsIgnoreCase(readFlag)) {
				p = cb.and(p, cb.not(cb.exists(subQuery2)));
			}
		}
		List<String> fields = DocumentWo.copier.getCopyFields();
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		cq.multiselect(selections).where(p);

		//排序，添加排序列，先按是否置顶，再按指定字段，最后按照序列号
		List<Order> orders = new ArrayList<>();
		Order isTopOrder = CriteriaBuilderTools.getOrder( cb, root, Document_.class, Document.isTop_FIELDNAME, "desc" );
		if( isTopOrder != null ){
			orders.add( isTopOrder );
		}

		if(StringUtils.isNotBlank(orderField) && fields.contains(orderField)) {
			String defaultOrderBy = "asc";
			if( defaultOrderBy.equalsIgnoreCase( orderType )) {
				orders.add(cb.asc( root.get( orderField )));
			}else {
				orders.add(cb.desc( root.get( orderField )));
			}
		}
		if(!Document.sequence_FIELDNAME.equals(orderField)) {
			orders.add(cb.desc(root.get(Document.sequence_FIELDNAME)));
		}
		cq.orderBy( orders );

		//return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize).getResultList();
		List<Tuple> list = em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
				.getResultList();
		List<Document> documentList = new ArrayList<>();
		for (Tuple o : list){
			Document doc = new Document();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(doc, fields.get(i), o.get(selections.get(i)));
			}
			documentList.add(doc);
		}
		return documentList;
	}

	/**
	 * 根据条件统计文档数目
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithCondition( String personName, QueryFilter queryFilter, Boolean isAuthor, Boolean excludeAllRead, String readFlag, Boolean isAdmin) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		EntityManager em1 = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb1 = em1.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Document_.class, cb, null, root, queryFilter );

		if(BooleanUtils.isTrue(isAdmin)){
			if(!BooleanUtils.isTrue(isAuthor)) {
				Subquery<Review> subquery = cq.subquery(Review.class);
				Root<Review> root1 = subquery.from(em1.getMetamodel().entity(Review.class));
				subquery.select(root1);
				Predicate p_permission = cb1.equal(root1.get(Review_.permissionObj), personName);
				p_permission = cb1.and(p_permission, cb1.equal(root1.get(Review_.docId), root.get(Document_.id)));
				subquery.where(p_permission);
				if(BooleanUtils.isTrue(excludeAllRead)){
					p = cb.and(p, cb.exists(subquery));
				}else {
					Predicate orP = cb.or(cb.isTrue(root.get(Document_.isAllRead)), cb.exists(subquery));
					p = cb.and(p, orP);
				}
			}else {
				List<String> list = new ArrayList<>();
				List<String> unitAllList = this.business().organization().unit().listWithPersonSupNested(personName);
				if(unitAllList!=null) {
					for (String item : unitAllList) {
						if (item.indexOf("@") > -1) {
							list.add(StringUtils.substringAfter(item, "@"));
						}
					}
					unitAllList.addAll(list);
					list.clear();
				}
				List<String> groupList = this.business().organization().group().listWithPersonReference(
						ListTools.toList(personName), true, true, true);
				if(groupList!=null) {
					for (String item : groupList) {
						if (item.indexOf("@") > -1) {
							list.add(StringUtils.substringAfter(item, "@"));
						}
					}
					groupList.addAll(list);
					list.clear();
				}
				Predicate ep = cb.disjunction();
				ep = cb.or(ep, cb.isMember(personName, root.get(Document_.authorPersonList)));
				if(ListTools.isNotEmpty(unitAllList)){
					ep = cb.or(ep, root.get(Document_.authorUnitList).in(unitAllList));
				}
				if(ListTools.isNotEmpty(groupList)){
					ep = cb.or(ep, root.get(Document_.authorGroupList).in(groupList));
				}
				p = cb.and(p, ep);
			}
		}
		if(StringUtils.isNotBlank(readFlag)) {
			EntityManager em2 = this.entityManagerContainer().get(DocumentViewRecord.class);
			CriteriaBuilder cb2 = em2.getCriteriaBuilder();
			Subquery<DocumentViewRecord> subQuery2 = cq.subquery(DocumentViewRecord.class);
			Root<DocumentViewRecord> root2 = subQuery2.from(
					em2.getMetamodel().entity(DocumentViewRecord.class));
			subQuery2.select(root2);
			Predicate p2 = cb2.equal(root2.get(DocumentViewRecord_.viewerName), personName);
			p2 = cb2.and(p2, cb2.equal(root2.get(DocumentViewRecord_.documentId),
					root.get(Document_.id)));
			subQuery2.where(p2);
			if (WrapInDocumentFilter.READ_FLAG_READ.equalsIgnoreCase(readFlag)) {
				p = cb.and(p, cb.exists(subQuery2));
			} else if (WrapInDocumentFilter.READ_FLAG_UNREAD.equalsIgnoreCase(readFlag)) {
				p = cb.and(p, cb.not(cb.exists(subQuery2)));
			}
		}

		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}

	/**
	 * 查询所有isTop属性为空的文档ID列表
	 * @return
	 * @throws Exception
	 */
	public List<String> listNULLIsTopDocIds() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.isNull( root.get( Document_.isTop ) );
		cq.select(root.get( Document_.id));
		return em.createQuery(cq.where(p)).setMaxResults(999).getResultList();
	}

	public List<String> listApp() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal(root.get(Document_.docStatus), DocumentStatus.PUBLISHED.getValue());
		cq.select(root.get( Document_.appId)).distinct(true).where(p);
		return em.createQuery( cq ).getResultList();
	}

	public static class DocumentWo extends Document{

		static WrapCopier<Document, DocumentWo> copier = WrapCopierFactory.wo(Document.class, DocumentWo.class,
				JpaObject.singularAttributeField(Document.class, true, true), null);
	}

}
