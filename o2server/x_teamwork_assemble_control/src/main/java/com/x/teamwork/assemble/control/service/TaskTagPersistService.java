package com.x.teamwork.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.teamwork.core.entity.TaskTag;

public class TaskTagPersistService {

	private TaskTagService taskTagService = new TaskTagService();
	
	/**
	 * 删除工作任务标签信息
	 * @param flag
	 * @param effectivePerson
	 * @throws Exception
	 */
	public void delete( String flag, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("flag is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			taskTagService.delete( emc, flag );	
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 保存工作任务标签信息
	 * @param object
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public TaskTag save( TaskTag  object,  EffectivePerson effectivePerson ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		
		TaskTag result = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			result = taskTagService.save( emc, object );
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
}
