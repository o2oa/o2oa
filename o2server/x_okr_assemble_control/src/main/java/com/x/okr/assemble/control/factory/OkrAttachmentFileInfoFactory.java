package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrAttachmentFileInfo_;

/**
 * 类   名：OkrAttachmentFileInfoFactory<br/>
 * 实体类：OkrAttachmentFileInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrAttachmentFileInfoFactory extends AbstractFactory {

	public OkrAttachmentFileInfoFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的OkrAttachmentFileInfo实体信息对象" )
	public OkrAttachmentFileInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrAttachmentFileInfo.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的OkrAttachmentFileInfo实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrAttachmentFileInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrAttachmentFileInfo> root = cq.from( OkrAttachmentFileInfo.class);
		cq.select(root.get(OkrAttachmentFileInfo_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrAttachmentFileInfo实体信息列表" )
	public List<OkrAttachmentFileInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrAttachmentFileInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrAttachmentFileInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrAttachmentFileInfo> cq = cb.createQuery(OkrAttachmentFileInfo.class);
		Root<OkrAttachmentFileInfo> root = cq.from(OkrAttachmentFileInfo.class);
		Predicate p = root.get(OkrAttachmentFileInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
}
