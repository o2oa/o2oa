package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.FileInfo_;

public class FileInfoFactory extends AbstractFactory {

	public FileInfoFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * @param id
	 * @return FileInfo
	 * @throws Exception
	 */
	//@MethodDescribe("获取指定Id的FileInfo文件附件信息对象")
	public FileInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, FileInfo.class, ExceptionWhen.none );
	}
	
	/**
	 * @return List：String
	 * @throws Exception
	 */
	//@MethodDescribe("列示全部的FileInfo文件附件信息ID列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( FileInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<FileInfo> root = cq.from( FileInfo.class );
		cq.select(root.get(FileInfo_.id));
		return em.createQuery(cq).getResultList();
	}
	
	/**
	 * 
	 * @param ids 需要查询的ID列表
	 * @return List：FileInfo
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定Id的FileInfo文件附件信息ID列表")
//	public List<FileInfo> list(List<String> ids) throws Exception {
//		EntityManager em = this.entityManagerContainer().get( FileInfo.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<FileInfo> cq = cb.createQuery( FileInfo.class );
//		Root<FileInfo> root = cq.from( FileInfo.class );
//		Predicate p = root.get(FileInfo_.id).in(ids);
//		return em.createQuery(cq.where(p)).getResultList();
//	}
	
	/**
	 * 列示指定文档的所有附件以及文件信息ID列表
	 * @param doucmentId 指定的文档ID
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe("列示指定文档的所有附件以及文件信息ID列表")
	public List<String> listAllByDocument( String doucmentId ) throws Exception {		
		if( StringUtils.isEmpty(doucmentId) ){
			throw new Exception("内容管理listByDocument方法不接受document为空的查询操作！");
		}		
		EntityManager em = this.entityManagerContainer().get( FileInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<FileInfo> root = cq.from( FileInfo.class );		
		Predicate p = cb.equal(root.get( FileInfo_.documentId ), doucmentId);		
		cq.select(root.get(FileInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示指定文档的所有附件信息ID列表
	 * @param doucmentId 指定的文档ID
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe("列示指定文档的所有附件信息ID列表")
	public List<String> listAttachmentByDocument( String doucmentId ) throws Exception {		
		if( StringUtils.isEmpty(doucmentId) ){
			throw new Exception("内容管理listByDocument方法不接受document为空的查询操作！");
		}		
		EntityManager em = this.entityManagerContainer().get( FileInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<FileInfo> root = cq.from( FileInfo.class );		
		Predicate p = cb.equal(root.get( FileInfo_.documentId ), doucmentId);
		p = cb.and( p, cb.equal(root.get( FileInfo_.fileType ), "ATTACHMENT") );
		cq.select(root.get(FileInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示指定文档的所有附件信息ID列表
	 * @param doucmentId 指定的文档ID
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe("列示指定文档的所有附件信息ID列表")
	public List<String> listPictureByDocument( String documentId ) throws Exception {		
		if( StringUtils.isEmpty(documentId) ){
			throw new Exception("内容管理listByDocument方法不接受document为空的查询操作！");
		}		
		EntityManager em = this.entityManagerContainer().get( FileInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<FileInfo> root = cq.from( FileInfo.class );		
		Predicate p = cb.equal(root.get( FileInfo_.documentId ), documentId);
		p = cb.and( p, cb.equal(root.get( FileInfo_.fileExtType ), "PICTURE") );
		cq.select(root.get(FileInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 列示指定文档的所有云文件图片信息ID列表
	 * @param doucmentId 指定的文档ID
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe("列示指定文档的所有云文件图片信息ID列表")
	public List<String> listCloudPictureByDocument(String documentId) throws Exception {
		if( StringUtils.isEmpty(documentId) ){
			throw new Exception("内容管理listByDocument方法不接受document为空的查询操作！");
		}		
		EntityManager em = this.entityManagerContainer().get( FileInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<FileInfo> root = cq.from( FileInfo.class );		
		Predicate p = cb.equal(root.get( FileInfo_.documentId ), documentId);
		p = cb.and( p, cb.equal(root.get( FileInfo_.fileExtType ), "PICTURE") );
		p = cb.and( p, cb.equal(root.get( FileInfo_.fileType ), "CLOUD") );
		cq.select(root.get(FileInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}