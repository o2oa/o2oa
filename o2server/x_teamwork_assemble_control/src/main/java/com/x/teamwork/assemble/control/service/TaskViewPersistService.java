package com.x.teamwork.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.teamwork.core.entity.TaskView;

/**
 * 对工作任务信息查询的服务
 */
public class TaskViewPersistService {

	private TaskViewService taskViewService = new TaskViewService();
	
	/**
	 * 根据工作任务视图标识删除工作任务视图信息
	 * @param id
	 * @param effectivePerson
	 * @throws Exception
	 */
	public void delete( String id, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TaskView taskView = taskViewService.get(emc, id);
			if( taskView.getCreatorPerson().equals( effectivePerson.getDistinguishedName() ) 
					|| taskView.getOwner().equals( effectivePerson.getDistinguishedName()  )) {
				taskViewService.delete( emc, id );
			}else {
				throw new Exception("taskView delete permission denied.");
			}		
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 保存一个工作任务视图信息
	 * @param taskView
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public TaskView save( TaskView taskView, EffectivePerson effectivePerson ) throws Exception {
		if ( taskView == null) {
			throw new Exception( "taskView is null." );
		}
		taskView.setCreatorPerson( effectivePerson.getDistinguishedName() );
		if( StringUtils.isEmpty( taskView.getOwner() )) {
			taskView.setOwner( effectivePerson.getDistinguishedName() );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			taskView = taskViewService.save( emc, taskView );
		} catch (Exception e) {
			throw e;
		}
		return taskView;
	}
}
