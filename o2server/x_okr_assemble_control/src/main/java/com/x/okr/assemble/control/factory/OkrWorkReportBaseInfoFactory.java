package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.WrapInFilter;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo_;

/**
 * 类   名：OkrWorkReportBaseInfoFactory<br/>
 * 实体类：OkrWorkReportBaseInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkReportBaseInfoFactory extends AbstractFactory {

	public OkrWorkReportBaseInfoFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的OkrWorkReportBaseInfo实体信息对象" )
	public OkrWorkReportBaseInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkReportBaseInfo.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的OkrWorkReportBaseInfo实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportBaseInfo> root = cq.from( OkrWorkReportBaseInfo.class);
		cq.select(root.get(OkrWorkReportBaseInfo_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrWorkReportBaseInfo实体信息列表" )
	public List<OkrWorkReportBaseInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrWorkReportBaseInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkReportBaseInfo> cq = cb.createQuery(OkrWorkReportBaseInfo.class);
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
		Predicate p = root.get(OkrWorkReportBaseInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据工作ID，查询该工作的最大汇报次序
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe( "根据工作ID，查询该工作的最大汇报次序" )
	public Integer getMaxReportCount( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is empty, system can not excute query!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<OkrWorkReportBaseInfo> cq = cb.createQuery( OkrWorkReportBaseInfo.class );
		Root<OkrWorkReportBaseInfo> root = cq.from( OkrWorkReportBaseInfo.class);		
		cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.reportCount) ) );	
		
		Predicate p = cb.equal( root.get( OkrWorkReportBaseInfo_.workId), workId);
		
		List<OkrWorkReportBaseInfo> resultList = em.createQuery(cq.where(p)).getResultList();
		if( resultList == null || resultList.size() == 0 ){
			return 0;
		}else{
			return resultList.get(0).getReportCount();
		}
	}

	/**
	 * 根据工作信息ID，获取汇报基础信息ID列表
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe( "根据工作信息ID，获取汇报基础信息ID列表" )
	public List<String> listByWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportBaseInfo> root = cq.from( OkrWorkReportBaseInfo.class);
		Predicate p = cb.equal( root.get(OkrWorkReportBaseInfo_.workId), workId );
		cq.select(root.get( OkrWorkReportBaseInfo_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，列示所有的数据信息
	 * @param centerId 中心工作
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe( "根据中心工作ID，列示所有的信息" )
	public List<String> listByCenterWorkId(String centerId) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( " centerId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
		Predicate p = cb.equal( root.get( OkrWorkReportBaseInfo_.centerId ), centerId );
		cq.select(root.get( OkrWorkReportBaseInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<OkrWorkReportBaseInfo> listNextWithFilter(String id,  Integer count, Object sequence, WrapInFilter wrapIn) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkReportBaseInfo.class);
		String sequenceField = wrapIn.getSequenceField();
		String order = wrapIn.getOrder(); // 排序方式
		if ( StringUtils.isEmpty( order ) ) {
			order = "DESC";
		}
		if ( StringUtils.isEmpty( sequenceField ) ) {
			sequenceField =  JpaObject.sequence_FIELDNAME;
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkReportBaseInfo> cq = cb.createQuery(OkrWorkReportBaseInfo.class);
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
		Predicate p = cb.isNotNull( root.get( OkrWorkReportBaseInfo_.id ) );
		if( sequence != null ) {
			if( StringUtils.equalsIgnoreCase(order, "DESC" )) {
				if( "createTime".equals( sequenceField )) {
					p = cb.and( p, cb.lessThan(root.get( OkrWorkReportBaseInfo_.createTime ), (Date)sequence));
				}else if(  JpaObject.sequence_FIELDNAME.equals( sequenceField )) {
					p = cb.and( p, cb.lessThan(root.get( OkrWorkReportBaseInfo_.sequence ), sequence.toString()));
				}else if( "workTitle".equals( sequenceField )) {
					p = cb.and( p, cb.lessThan(root.get( OkrWorkReportBaseInfo_.workTitle ), sequence.toString()));
				}else if( "activityName".equals( sequenceField )) {
					p = cb.and( p, cb.lessThan(root.get( OkrWorkReportBaseInfo_.activityName ), sequence.toString()));
				}else if( "centerTitle".equals( sequenceField )) {
					p = cb.and( p, cb.lessThan(root.get( OkrWorkReportBaseInfo_.centerTitle ), sequence.toString()));
				}else if( "workType".equals( sequenceField )) {
					p = cb.and( p, cb.lessThan(root.get( OkrWorkReportBaseInfo_.workType ), sequence.toString()));
				}
			}else {
				if( "createTime".equals( sequenceField )) {
					p = cb.and( p, cb.greaterThan(root.get( OkrWorkReportBaseInfo_.createTime ), (Date)sequence));
				}else if(  JpaObject.sequence_FIELDNAME.equals( sequenceField )) {
					p = cb.and( p, cb.greaterThan(root.get( OkrWorkReportBaseInfo_.sequence ), sequence.toString()));
				}else if( "workTitle".equals( sequenceField )) {
					p = cb.and( p, cb.greaterThan(root.get( OkrWorkReportBaseInfo_.workTitle ), sequence.toString()));
				}else if( "activityName".equals( sequenceField )) {
					p = cb.and( p, cb.greaterThan(root.get( OkrWorkReportBaseInfo_.activityName ), sequence.toString()));
				}else if( "centerTitle".equals( sequenceField )) {
					p = cb.and( p, cb.greaterThan(root.get( OkrWorkReportBaseInfo_.centerTitle ), sequence.toString()));
				}else if( "workType".equals( sequenceField )) {
					p = cb.and( p, cb.greaterThan(root.get( OkrWorkReportBaseInfo_.workType ), sequence.toString()));
				}
			}
		}
		
		if( StringUtils.isNotEmpty( wrapIn.getTitle() )) {
			p = cb.and( p, cb.like(root.get( OkrWorkReportBaseInfo_.centerTitle ), wrapIn.getTitle()));
		}
		//当前处理人身份
		if( StringUtils.isNotEmpty( wrapIn.getProcessIdentity() )) {
			p = cb.and( p, cb.isMember(wrapIn.getProcessIdentity(), root.get( OkrWorkReportBaseInfo_.currentProcessorIdentityList )));
		}		
		//工作处理状态
		if( ListTools.isNotEmpty( wrapIn.getProcessStatusList() )) {
			p = cb.and( p,  root.get( OkrWorkReportBaseInfo_.processStatus).in( wrapIn.getProcessStatusList() ));
		}

		if( ListTools.isNotEmpty( wrapIn.getQ_statuses() )) {
			p = cb.and( p,  root.get( OkrWorkReportBaseInfo_.status).in( wrapIn.getQ_statuses() ));
		}
		
		if(StringUtils.equalsIgnoreCase(order, "DESC" )) {
			if( "createTime".equals( sequenceField )) {
				cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.createTime) ) );	
			}else if(  JpaObject.sequence_FIELDNAME.equals( sequenceField )) {
				cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.sequence) ) );	
			}else if( "workTitle".equals( sequenceField )) {
				cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.workTitle) ) );	
			}else if( "activityName".equals( sequenceField )) {
				cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.activityName) ) );	
			}else if( "centerTitle".equals( sequenceField )) {
				cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.centerTitle) ) );	
			}else if( "workType".equals( sequenceField )) {
				cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.workType) ) );	
			}
		}else {
			if( "createTime".equals( sequenceField )) {
				cq.orderBy( cb.asc( root.get( OkrWorkReportBaseInfo_.createTime) ) );	
			}else if(  JpaObject.sequence_FIELDNAME.equals( sequenceField )) {
				cq.orderBy( cb.asc( root.get( OkrWorkReportBaseInfo_.sequence) ) );	
			}else if( "workTitle".equals( sequenceField )) {
				cq.orderBy( cb.asc( root.get( OkrWorkReportBaseInfo_.workTitle) ) );	
			}else if( "activityName".equals( sequenceField )) {
				cq.orderBy( cb.asc( root.get( OkrWorkReportBaseInfo_.activityName) ) );	
			}else if( "centerTitle".equals( sequenceField )) {
				cq.orderBy( cb.asc( root.get( OkrWorkReportBaseInfo_.centerTitle) ) );	
			}else if( "workType".equals( sequenceField )) {
				cq.orderBy( cb.asc( root.get( OkrWorkReportBaseInfo_.workType) ) );	
			}
		}
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<OkrWorkReportBaseInfo> listPrevWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
				EntityManager em = this.entityManagerContainer().get( OkrWorkReportBaseInfo.class);
				String sequenceField = wrapIn.getSequenceField();
				String order = wrapIn.getOrder(); // 排序方式
				if ( StringUtils.isEmpty( order ) ) {
					order = "DESC";
				}
				if ( StringUtils.isEmpty( sequenceField ) ) {
					sequenceField =  JpaObject.sequence_FIELDNAME;
				}
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<OkrWorkReportBaseInfo> cq = cb.createQuery(OkrWorkReportBaseInfo.class);
				Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
				Predicate p = cb.isNotNull( root.get( OkrWorkReportBaseInfo_.id ) );
				if( sequence != null ) {
					if( StringUtils.equalsIgnoreCase(order, "DESC" )) {
						if( "createTime".equals( sequenceField )) {
							p = cb.and( p, cb.greaterThan(root.get( OkrWorkReportBaseInfo_.createTime ), (Date)sequence));
						}else if(  JpaObject.sequence_FIELDNAME.equals( sequenceField )) {
							p = cb.and( p, cb.greaterThan(root.get( OkrWorkReportBaseInfo_.sequence ), sequence.toString()));
						}else if( "workTitle".equals( sequenceField )) {
							p = cb.and( p, cb.greaterThan(root.get( OkrWorkReportBaseInfo_.workTitle ), sequence.toString()));
						}else if( "activityName".equals( sequenceField )) {
							p = cb.and( p, cb.greaterThan(root.get( OkrWorkReportBaseInfo_.activityName ), sequence.toString()));
						}else if( "centerTitle".equals( sequenceField )) {
							p = cb.and( p, cb.greaterThan(root.get( OkrWorkReportBaseInfo_.centerTitle ), sequence.toString()));
						}else if( "workType".equals( sequenceField )) {
							p = cb.and( p, cb.greaterThan(root.get( OkrWorkReportBaseInfo_.workType ), sequence.toString()));
						}
					}else {
						if( "createTime".equals( sequenceField )) {
							p = cb.and( p, cb.lessThan(root.get( OkrWorkReportBaseInfo_.createTime ), (Date)sequence));
						}else if(  JpaObject.sequence_FIELDNAME.equals( sequenceField )) {
							p = cb.and( p, cb.lessThan(root.get( OkrWorkReportBaseInfo_.sequence ), sequence.toString()));
						}else if( "workTitle".equals( sequenceField )) {
							p = cb.and( p, cb.lessThan(root.get( OkrWorkReportBaseInfo_.workTitle ), sequence.toString()));
						}else if( "activityName".equals( sequenceField )) {
							p = cb.and( p, cb.lessThan(root.get( OkrWorkReportBaseInfo_.activityName ), sequence.toString()));
						}else if( "centerTitle".equals( sequenceField )) {
							p = cb.and( p, cb.lessThan(root.get( OkrWorkReportBaseInfo_.centerTitle ), sequence.toString()));
						}else if( "workType".equals( sequenceField )) {
							p = cb.and( p, cb.lessThan(root.get( OkrWorkReportBaseInfo_.workType ), sequence.toString()));
						}
					}
				}
				
				if( StringUtils.isNotEmpty( wrapIn.getTitle() )) {
					p = cb.and( p, cb.like(root.get( OkrWorkReportBaseInfo_.centerTitle ), wrapIn.getTitle()));
				}
				//当前处理人身份
				if( StringUtils.isNotEmpty( wrapIn.getProcessIdentity() )) {
					p = cb.and( p, cb.isMember(wrapIn.getProcessIdentity(), root.get( OkrWorkReportBaseInfo_.currentProcessorIdentityList )));
				}		
				//工作处理状态
				if( ListTools.isNotEmpty( wrapIn.getProcessStatusList() )) {
					p = cb.and( p,  root.get( OkrWorkReportBaseInfo_.processStatus).in( wrapIn.getProcessStatusList() ));
				}

				if( ListTools.isNotEmpty( wrapIn.getQ_statuses() )) {
					p = cb.and( p,  root.get( OkrWorkReportBaseInfo_.status).in( wrapIn.getQ_statuses() ));
				}
				
				if(StringUtils.equalsIgnoreCase(order, "DESC" )) {
					if( "createTime".equals( sequenceField )) {
						cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.createTime) ) );	
					}else if(  JpaObject.sequence_FIELDNAME.equals( sequenceField )) {
						cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.sequence) ) );	
					}else if( "workTitle".equals( sequenceField )) {
						cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.workTitle) ) );	
					}else if( "activityName".equals( sequenceField )) {
						cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.activityName) ) );	
					}else if( "centerTitle".equals( sequenceField )) {
						cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.centerTitle) ) );	
					}else if( "workType".equals( sequenceField )) {
						cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.workType) ) );	
					}
				}else {
					if( "createTime".equals( sequenceField )) {
						cq.orderBy( cb.asc( root.get( OkrWorkReportBaseInfo_.createTime) ) );	
					}else if(  JpaObject.sequence_FIELDNAME.equals( sequenceField )) {
						cq.orderBy( cb.asc( root.get( OkrWorkReportBaseInfo_.sequence) ) );	
					}else if( "workTitle".equals( sequenceField )) {
						cq.orderBy( cb.asc( root.get( OkrWorkReportBaseInfo_.workTitle) ) );	
					}else if( "activityName".equals( sequenceField )) {
						cq.orderBy( cb.asc( root.get( OkrWorkReportBaseInfo_.activityName) ) );	
					}else if( "centerTitle".equals( sequenceField )) {
						cq.orderBy( cb.asc( root.get( OkrWorkReportBaseInfo_.centerTitle) ) );	
					}else if( "workType".equals( sequenceField )) {
						cq.orderBy( cb.asc( root.get( OkrWorkReportBaseInfo_.workType) ) );	
					}
				}
				return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 查询符合的信息总数
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public Long getCountWithFilter( WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkReportBaseInfo.class);
		String sequenceField = wrapIn.getSequenceField();
		String order = wrapIn.getOrder(); // 排序方式
		if ( StringUtils.isEmpty( order ) ) {
			order = "DESC";
		}
		if ( StringUtils.isEmpty( sequenceField ) ) {
			sequenceField =  JpaObject.sequence_FIELDNAME;
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
		Predicate p = cb.isNotNull( root.get( OkrWorkReportBaseInfo_.id ) );		
		if( StringUtils.isNotEmpty( wrapIn.getTitle() )) {
			p = cb.and( p, cb.like(root.get( OkrWorkReportBaseInfo_.centerTitle ), wrapIn.getTitle()));
		}
		//当前处理人身份
		if( StringUtils.isNotEmpty( wrapIn.getProcessIdentity() )) {
			p = cb.and( p, cb.isMember(wrapIn.getProcessIdentity(), root.get( OkrWorkReportBaseInfo_.currentProcessorIdentityList )));
		}		
		//工作处理状态
		if( ListTools.isNotEmpty( wrapIn.getProcessStatusList() )) {
			p = cb.and( p,  root.get( OkrWorkReportBaseInfo_.processStatus).in( wrapIn.getProcessStatusList() ));
		}

		if( ListTools.isNotEmpty( wrapIn.getQ_statuses() )) {
			p = cb.and( p,  root.get( OkrWorkReportBaseInfo_.status).in( wrapIn.getQ_statuses() ));
		}
		cq.select( cb.count( root ) );	
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据WorkId查询该工作所有汇报中已经提交的最后一次汇报的内容，如果没有则返回NULL
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public OkrWorkReportBaseInfo getLastCompletedReport( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is empty, system can not excute query!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<OkrWorkReportBaseInfo> cq = cb.createQuery( OkrWorkReportBaseInfo.class );
		Root<OkrWorkReportBaseInfo> root = cq.from( OkrWorkReportBaseInfo.class);		
		cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.reportCount) ) );
		Predicate p = cb.equal( root.get( OkrWorkReportBaseInfo_.workId ), workId);
		p = cb.and( p, cb.equal( root.get( OkrWorkReportBaseInfo_.activityName ), "已完成" ) );
		List<OkrWorkReportBaseInfo> resultList = em.createQuery(cq.where(p)).getResultList();
		if( resultList == null || resultList.size() == 0 ){
			return null;
		}else{
			return resultList.get(0);
		}
	}

	/**
	 * 根据条件查询汇报ID列表
	 * @param workId
	 * @param activityName
	 * @param processStatus
	 * @param processIdentity
	 * @return
	 * @throws Exception 
	 */
	public List<String> listByWorkId(String workId, String activityName, String processStatus, String processorIdentity ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is empty, system can not excute query!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportBaseInfo> root = cq.from( OkrWorkReportBaseInfo.class);
		Predicate p = cb.equal( root.get( OkrWorkReportBaseInfo_.workId), workId );
		p = cb.and( p, cb.equal( root.get( OkrWorkReportBaseInfo_.status ), "正常" ) );
		if( processorIdentity != null && !processorIdentity.isEmpty() ){
			p = cb.and( p, cb.isMember( processorIdentity, root.get( OkrWorkReportBaseInfo_.currentProcessorIdentityList )));
		}
		if( activityName != null && !activityName.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrWorkReportBaseInfo_.activityName ), activityName ) );
		}
		if( processStatus != null && !processStatus.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrWorkReportBaseInfo_.processStatus ), processStatus ) );
		}
		cq.select(root.get( OkrWorkReportBaseInfo_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 根据工作ID获取该工作最后一次工作汇报
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public OkrWorkReportBaseInfo getLastReportBaseInfo( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is empty, system can not excute query!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<OkrWorkReportBaseInfo> cq = cb.createQuery( OkrWorkReportBaseInfo.class );
		Root<OkrWorkReportBaseInfo> root = cq.from( OkrWorkReportBaseInfo.class);
		cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.submitTime ) ) );	
		
		Predicate p = cb.equal( root.get( OkrWorkReportBaseInfo_.workId ), workId );
		p = cb.and( p, cb.equal( root.get( OkrWorkReportBaseInfo_.status ), "正常" ));
		
		List<OkrWorkReportBaseInfo> resultList = em.createQuery(cq.where(p)).getResultList();
		if( resultList == null || resultList.size() == 0 ){
			return null;
		}else{
			return resultList.get(0);
		}
	}

	public List<String> listProcessingReportIdsByWorkId(String workId) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( " workId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
		Predicate p = cb.equal( root.get( OkrWorkReportBaseInfo_.workId ), workId );
		p = cb.and( p, cb.notEqual( root.get( OkrWorkReportBaseInfo_.activityName ), "已完成" ));
		p = cb.and( p, cb.equal( root.get( OkrWorkReportBaseInfo_.status ), "正常" ));
		cq.select(root.get( OkrWorkReportBaseInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 查询工作汇报创建者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctCreatorIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
		
		Predicate p = cb.isNotNull( root.get( OkrWorkReportBaseInfo_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkReportBaseInfo_.creatorIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkReportBaseInfo_.creatorIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrWorkReportBaseInfo_.creatorIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 查询工作汇报当前处理者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctCurrentProcessorIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		/*
		CriteriaQuery<List> cq = cb.createQuery( List.class );
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
		cq.select(root.get( OkrWorkReportBaseInfo_.currentProcessorIdentityList ));
		List<List> allList = em.createQuery(cq).getResultList();
		*/
		
		CriteriaQuery<OkrWorkReportBaseInfo> cq = cb.createQuery( OkrWorkReportBaseInfo.class );
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);		
		List<OkrWorkReportBaseInfo> os = em.createQuery(cq.select(root)).getResultList();
		List<List> allList = new ArrayList<>();
		for (OkrWorkReportBaseInfo o : os) {
			allList.add(o.getCurrentProcessorIdentityList());
		}
		
		if(ListTools.isNotEmpty( allList )) {
			HashSet hashSet = new  HashSet();
			for( List<String> identities : allList ) {
				if(ListTools.isNotEmpty( identities )) {
					for( String identity : identities ) {
						if( ListTools.isNotEmpty(identities_ok) && identities_ok.contains( identity ) ){
							continue;
						}
						if( ListTools.isNotEmpty(identities_error) && identities_error.contains( identity ) ){
							continue;
						}
						hashSet.add( identity );
					}
				}
			}
			List<String> result = new ArrayList<>();
			result.addAll(hashSet);
			return result;
		}
		return null;
		
	}
	/**
	 * 查询工作汇报阅知领导身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctReadleadersIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		/*
		CriteriaQuery<List> cq = cb.createQuery( List.class );
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
		cq.select(root.get( OkrWorkReportBaseInfo_.readLeadersIdentityList ));
		List<List> allList = em.createQuery(cq).getResultList();
		*/
		
		CriteriaQuery<OkrWorkReportBaseInfo> cq = cb.createQuery( OkrWorkReportBaseInfo.class );
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);		
		List<OkrWorkReportBaseInfo> os = em.createQuery(cq.select(root)).getResultList();
		List<List> allList = new ArrayList<>();
		for (OkrWorkReportBaseInfo o : os) {
			allList.add(o.getReadLeadersIdentityList());
		}
		
		if(ListTools.isNotEmpty( allList )) {
			HashSet hashSet = new  HashSet();
			for( List<String> identities : allList ) {
				if(ListTools.isNotEmpty( identities )) {
					for( String identity : identities ) {
						if( ListTools.isNotEmpty(identities_ok) && identities_ok.contains( identity ) ){
							continue;
						}
						if( ListTools.isNotEmpty(identities_error) && identities_error.contains( identity ) ){
							continue;
						}
						hashSet.add( identity );
					}
				}
			}
			List<String> result = new ArrayList<>();
			result.addAll(hashSet);
			return result;
		}
		return null;
	}
	/**
	 * 查询工作汇报者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctReporterIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
		
		Predicate p = cb.isNotNull( root.get( OkrWorkReportBaseInfo_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkReportBaseInfo_.reporterIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkReportBaseInfo_.reporterIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrWorkReportBaseInfo_.reporterIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 查询工作管理者，督办员身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctWorkAdminIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
		
		Predicate p = cb.isNotNull( root.get( OkrWorkReportBaseInfo_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkReportBaseInfo_.workAdminIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkReportBaseInfo_.workAdminIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrWorkReportBaseInfo_.workAdminIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据身份名称，从工作汇报信息中查询与该身份有关的所有信息列表
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkReportBaseInfo> listErrorIdentitiesInReportBaseInfo( String identity, String recordId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkReportBaseInfo> cq = cb.createQuery( OkrWorkReportBaseInfo.class );
		Root<OkrWorkReportBaseInfo> root = cq.from( OkrWorkReportBaseInfo.class );
		Predicate p = cb.isNotNull(root.get( OkrWorkReportBaseInfo_.id ));
		
		if( recordId != null && !recordId.isEmpty() && !"all".equals( recordId ) ){
			p = cb.and( p, cb.equal( root.get( OkrWorkReportBaseInfo_.id ), recordId ) );
		}
		
		Predicate p_creatorIdentity = cb.isNotNull(root.get( OkrWorkReportBaseInfo_.creatorIdentity ));
		p_creatorIdentity = cb.and( p_creatorIdentity, cb.equal( root.get( OkrWorkReportBaseInfo_.creatorIdentity ), identity ) );
		
		Predicate p_reporterIdentity = cb.isNotNull(root.get( OkrWorkReportBaseInfo_.reporterIdentity ));
		p_reporterIdentity = cb.and( p_reporterIdentity, cb.equal( root.get( OkrWorkReportBaseInfo_.reporterIdentity ), identity ) );
		
		Predicate p_currentProcessorIdentity = cb.isNotNull(root.get( OkrWorkReportBaseInfo_.currentProcessorIdentityList ));
		p_currentProcessorIdentity = cb.and( p_currentProcessorIdentity, cb.isMember( identity,  root.get( OkrWorkReportBaseInfo_.currentProcessorIdentityList ) ) );
		
		Predicate p_readleadersIdentity = cb.isNotNull(root.get( OkrWorkReportBaseInfo_.readLeadersIdentityList ));
		p_readleadersIdentity = cb.and( p_readleadersIdentity, cb.isMember( identity, root.get( OkrWorkReportBaseInfo_.readLeadersIdentityList )) );
		
		Predicate p_workAdminIdentity = cb.isNotNull(root.get( OkrWorkReportBaseInfo_.workAdminIdentity ));
		p_workAdminIdentity = cb.and( p_workAdminIdentity, cb.equal( root.get( OkrWorkReportBaseInfo_.workAdminIdentity ), identity ) );

		Predicate p_identity = cb.or( p_creatorIdentity, p_reporterIdentity, p_currentProcessorIdentity, p_readleadersIdentity, p_workAdminIdentity );
			
		p = cb.and( p, p_identity );

		return em.createQuery(cq.where(p)).getResultList();
	}
}
