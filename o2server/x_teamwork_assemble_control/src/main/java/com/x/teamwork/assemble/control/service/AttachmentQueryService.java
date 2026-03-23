package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Attachment;

public class AttachmentQueryService{
	

	/**
	 * 根据传入的ID从数据库查询Attachment对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Attachment get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, Attachment.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据传入的ID从数据库查询Attachment对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<Attachment> list( List<String> ids ) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			return null;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.attachmentFactory().list(ids);
		}catch( Exception e ){
			throw e;
		}
	}

	public List<Attachment> listAttachmentWithProject( String project) throws Exception {
		if( StringUtils.isEmpty( project ) ){
			return null;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.attachmentFactory().listAttachmentWithProject( project );
		}catch( Exception e ){
			throw e;
		}
	}
	
	public List<Attachment> listAttachmentWithTask( String taskId ) throws Exception {
		if( StringUtils.isEmpty( taskId ) ){
			return null;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.attachmentFactory().listAttachmentWithTask( taskId );
		}catch( Exception e ){
			throw e;
		}
	}
	
}
