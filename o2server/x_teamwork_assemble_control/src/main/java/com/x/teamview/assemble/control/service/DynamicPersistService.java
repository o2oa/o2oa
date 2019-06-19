package com.x.teamview.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.teamwork.core.entity.Chat;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectExtFieldRele;
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskList;

public class DynamicPersistService {

	private DynamicService dynamicService = new DynamicService();
	
	/**
	 * 删除动态信息（管理员可删除）
	 * @param flag
	 * @param effectivePerson
	 * @throws Exception
	 */
	public void delete( String flag, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("flag is empty.");
		}
		Boolean hasDeletePermission = false;
		if( effectivePerson.isManager() ) {
			hasDeletePermission = true;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if( !hasDeletePermission ) {
				throw new Exception("dynamic delete permission denied.");
			}else {
				dynamicService.delete( emc, flag );
			}			
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 保存动态信息
	 * @param dynamic
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Dynamic save( Project object, String optType, EffectivePerson effectivePerson, String content ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( StringUtils.isEmpty( optType )) {
			throw new Exception("optType is empty.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		
		Dynamic dynamic = dynamicService.getProjectDynamic( object, optType, effectivePerson );	
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, content );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存动态信息
	 * @param dynamic
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Dynamic save( ProjectExtFieldRele object, String optType, EffectivePerson effectivePerson, String content ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( StringUtils.isEmpty( optType )) {
			throw new Exception("optType is empty.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		
		Dynamic dynamic = dynamicService.getProjectExtFieldReleDynamic( object, optType, effectivePerson );	
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, content );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存动态信息
	 * @param object
	 * @param optType
	 * @param effectivePerson
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public Dynamic save( TaskList object, String optType, EffectivePerson effectivePerson, String content ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( StringUtils.isEmpty( optType )) {
			throw new Exception("optType is empty.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		
		Dynamic dynamic = dynamicService.getProjectDynamic( object, optType, effectivePerson );	
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, content );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存动态信息
	 * @param object
	 * @param optType
	 * @param effectivePerson
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public Dynamic save( Task object, String optType, EffectivePerson effectivePerson, String content ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( StringUtils.isEmpty( optType )) {
			throw new Exception("optType is empty.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		
		Dynamic dynamic = dynamicService.getTaskDynamic( object, optType, effectivePerson );		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, content );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存动态信息
	 * @param dynamic
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Dynamic save( ProjectGroup object, String optType, EffectivePerson effectivePerson, String content ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( StringUtils.isEmpty( optType )) {
			throw new Exception("optType is empty.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		
		Dynamic dynamic = dynamicService.getProjectGroupDynamic( object, optType, effectivePerson );	
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, content );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存动态信息
	 * @param object
	 * @param optType
	 * @param effectivePerson
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public Dynamic save( Chat object, String optType, EffectivePerson effectivePerson, String content ) throws Exception {
		if ( object == null) {
			throw new Exception("chat object is null.");
		}
		if ( StringUtils.isEmpty( optType )) {
			throw new Exception("optType is empty.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		
		Dynamic dynamic = dynamicService.getChatDynamic( object, optType, effectivePerson );	
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, content );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
}
