package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Attachment;
import com.x.teamwork.core.entity.Chat;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectExtFieldRele;
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;
import com.x.teamwork.core.entity.TaskList;
import com.x.teamwork.core.entity.TaskTag;

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
	 * 保存项目创建或者更新动态信息
	 * @param old_object
	 * @param object
	 * @param effectivePerson
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public List<Dynamic> projectSaveDynamic( Project old_object, Project object, EffectivePerson effectivePerson, String content ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		List<Dynamic> dynamics = dynamicService.getProjectSaveDynamic( old_object, object, effectivePerson );	
		List<Dynamic> result = new ArrayList<>();
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			if( ListTools.isNotEmpty( dynamics )) {
				 for( Dynamic dynamic : dynamics ) {
					 dynamic = dynamicService.save( emc, dynamic, content );
					 result.add( dynamic );
				 }
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	/**
	 * 保存项目创建或者更新动态信息
	 * @param old_object
	 * @param object
	 * @param effectivePerson
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public Dynamic projectIconSaveDynamic( Project object, EffectivePerson effectivePerson, String content ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}		
		Dynamic dynamic = dynamicService.getProjectIconSaveDynamic( object, effectivePerson );	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			 dynamic = dynamicService.save( emc, dynamic, content );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存项目删除操作动态
	 * @param object
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Dynamic projectDeleteDynamic( Project object, EffectivePerson effectivePerson ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}		
		Dynamic dynamic = dynamicService.getProjectDeleteDynamic( object, effectivePerson );		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存项目扩展信息保存操作动态信息
	 * @param object_old
	 * @param object
	 * @param optType
	 * @param effectivePerson
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public Dynamic projectExtFieldReleSaveDynamic( ProjectExtFieldRele object_old, ProjectExtFieldRele object, EffectivePerson effectivePerson ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}		
		Dynamic dynamic = dynamicService.getProjectSaveExtFieldReleDynamic( object_old, object, effectivePerson);
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存项目删除操作动态
	 * @param object
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Dynamic projectExtFieldReleDeleteDynamic( ProjectExtFieldRele object, EffectivePerson effectivePerson ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}		
		Dynamic dynamic = dynamicService.getProjectDeleteExtFieldReleDynamic( object, effectivePerson );		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
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
	public Dynamic taskListSaveDynamic( TaskList object_old, TaskList object, EffectivePerson effectivePerson, String content ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}		
		Dynamic dynamic = dynamicService.getTaskListSaveDynamic(object_old, object, effectivePerson);
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, content );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存工作任务列表删除操作动态
	 * @param object
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Dynamic taskListDeleteDynamic( TaskList object, EffectivePerson effectivePerson ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}		
		Dynamic dynamic = dynamicService.getTaskListDeleteDynamic(object, effectivePerson);
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存动态信息
	 * @param object_old
	 * @param object_new
	 * @param effectivePerson
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public List<Dynamic> taskSaveDynamic( Task object_old, Task object_new, TaskDetail oldDetail , EffectivePerson effectivePerson, String content ) throws Exception {
		if ( object_new == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		List<Dynamic> result = new ArrayList<>();
		List<Dynamic> dynamics = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TaskDetail newDetail = emc.find( object_new.getId(), TaskDetail.class );
			dynamics = dynamicService.getTaskDynamic( object_old, object_new, oldDetail,  newDetail, effectivePerson );	
			if( ListTools.isNotEmpty( dynamics )) {
				 for( Dynamic dynamic : dynamics ) {
					 dynamic = dynamicService.save( emc, dynamic, content );
					 result.add( dynamic );
				 }
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	/**
	 * 保存动态信息
	 * @param dynamic
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Dynamic projectGroupSaveDynamic( ProjectGroup object_old, ProjectGroup object, EffectivePerson effectivePerson, String content ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		
		Dynamic dynamic = dynamicService.getProjectGroupSaveDynamic(object_old, object, effectivePerson);
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, content );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存项目组删除动态信息
	 * @param object
	 * @param effectivePerson
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public Dynamic projectGroupDeleteDynamic( ProjectGroup object, EffectivePerson effectivePerson ) throws Exception {
		if ( object == null) {
			throw new Exception("ProjectGroup object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		Dynamic dynamic = dynamicService.getProjectGroupDeleteDynamic(object, effectivePerson);
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存工作任务删除动态信息
	 * @param object
	 * @param effectivePerson
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public Dynamic taskDeleteDynamic( Task object, EffectivePerson effectivePerson ) throws Exception {
		if ( object == null) {
			throw new Exception("Task object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		
		Dynamic dynamic = dynamicService.getTaskDeleteDynamic( object, effectivePerson);		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}

	/**
	 * 保存工作管理员变更动态
	 * @param task
	 * @param addManagers
	 * @param removeManagers
	 * @param effectivePerson
	 * @param content
	 * @throws Exception
	 */
	public List<Dynamic> taskManagerUpdateDynamic(Task task, List<String> addManagers, List<String> removeManagers, EffectivePerson effectivePerson ) throws Exception {
		if ( task == null) {
			throw new Exception("task object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		List<Dynamic> result = new ArrayList<>();
		List<Dynamic> dynamics = dynamicService.getTaskManagerDynamic( task, addManagers, removeManagers, effectivePerson );		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if ( ListTools.isNotEmpty( dynamics )) {
				for( Dynamic dynamic : dynamics ) {
					dynamicService.save( emc, dynamic, null );
					result.add( emc.find( dynamic.getId(), Dynamic.class ));
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
//	
//	/**
//	 * 保存工作标签变更动态
//	 * @param task
//	 * @param addTags
//	 * @param removeTags
//	 * @param effectivePerson
//	 * @param content
//	 * @throws Exception
//	 */
//	public void taskTagUpdateDynamic(Task task, List<String> addTags, List<String> removeTags, EffectivePerson effectivePerson ) throws Exception {
//		if ( task == null) {
//			throw new Exception("task object is null.");
//		}
//		if ( effectivePerson == null ) {
//			throw new Exception("effectivePerson is null.");
//		}
//		List<Dynamic> dynamics = dynamicService.getTaskTagsDynamic( task, addTags, removeTags, effectivePerson );		
//		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			if ( ListTools.isNotEmpty( dynamics )) {
//				for( Dynamic dynamic : dynamics ) {
//					dynamicService.save( emc, dynamic, null );
//				}
//			}
//		} catch (Exception e) {
//			throw e;
//		}
//	}
//	
//	/**
//	 * 删除工作标签操作动态
//	 * @param task
//	 * @param tag
//	 * @param effectivePerson
//	 * @throws Exception
//	 */
//	public void taskTagDeleteDynamic( TaskTag tag, EffectivePerson effectivePerson ) throws Exception {
//		if ( tag == null) {
//			throw new Exception("TaskTag object is null.");
//		}
//		if ( effectivePerson == null ) {
//			throw new Exception("effectivePerson is null.");
//		}
//		Dynamic dynamic = dynamicService.getTaskTagDeleteDynamic( tag, effectivePerson);
//		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			if ( dynamic != null ) {
//				dynamicService.save( emc, dynamic, null );
//			}
//		} catch (Exception e) {
//			throw e;
//		}
//	}
	
	/**
	 * 保存工作参与者变更动态
	 * @param task
	 * @param addParticipants
	 * @param removeParticipants
	 * @param effectivePerson
	 * @param content
	 * @throws Exception
	 */
	public List<Dynamic> taskParticipantsUpdateDynamic(Task task, List<String> addParticipants, List<String> removeParticipants, EffectivePerson effectivePerson ) throws Exception {
		if ( task == null) {
			throw new Exception("task object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		List<Dynamic> result = new ArrayList<>();
		List<Dynamic> dynamics = dynamicService.getTaskParticipantsDynamic( task, addParticipants, removeParticipants, effectivePerson );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if ( ListTools.isNotEmpty( dynamics )) {
				for( Dynamic dynamic : dynamics ) {
					dynamicService.save( emc, dynamic, null );
					result.add( emc.find( dynamic.getId() , Dynamic.class ) );
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * 任务分解，添加子任务的动态
	 * @param parentTask
	 * @param task
	 * @param effectivePerson
	 * @throws Exception 
	 */
	public Dynamic taskSplitDynamic(Task parentTask, Task task, EffectivePerson effectivePerson ) throws Exception {
		if ( parentTask == null) {
			throw new Exception("parentTask object is null.");
		}
		if ( task == null) {
			throw new Exception("task object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		Dynamic dynamic = dynamicService.getTaskSplitDynamic( parentTask, task, effectivePerson );		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if ( dynamic != null ) {
				dynamic = dynamicService.save( emc, dynamic, null );
			}
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	public void subTaskDeleteDynamic(Task parentTask, Task task, EffectivePerson effectivePerson) throws Exception {
		if ( parentTask == null) {
			throw new Exception("parentTask object is null.");
		}
		if ( task == null) {
			throw new Exception("task object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		Dynamic dynamic = dynamicService.subTaskDeleteDynamic( parentTask, task, effectivePerson );		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if ( dynamic != null ) {
				dynamicService.save( emc, dynamic, null );
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Dynamic uploadAttachmentDynamic(Attachment attachment, EffectivePerson effectivePerson) throws Exception {
		if ( attachment == null) {
			throw new Exception("attachment is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}		
		Dynamic dynamic = dynamicService.getAttachmentUploadDynamic( attachment, effectivePerson );		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	public Dynamic downloadAttachmentDynamic(Attachment attachment, EffectivePerson effectivePerson) throws Exception {
		if ( attachment == null) {
			throw new Exception("attachment is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}		
		Dynamic dynamic = dynamicService.getAttachmentDownloadDynamic( attachment, effectivePerson );		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	public Dynamic deleteAttachment(Attachment attachment, EffectivePerson effectivePerson) throws Exception {
		if ( attachment == null) {
			throw new Exception("attachment is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}		
		Dynamic dynamic = dynamicService.getAttachmentDeleteDynamic( attachment, effectivePerson );		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存任务评论动态信息
	 * @param object
	 * @param effectivePerson
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public Dynamic chatPublishDynamic( Chat object, EffectivePerson effectivePerson, String content ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}		
		Dynamic dynamic = dynamicService.getChatPublishDynamic( object, effectivePerson );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, content );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	/**
	 * 保存工作任务评论删除动态信息
	 * @param object
	 * @param effectivePerson
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public Dynamic chatDeleteDynamic( Chat object, EffectivePerson effectivePerson ) throws Exception {
		if ( object == null) {
			throw new Exception("Task object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		
		Dynamic dynamic = dynamicService.getChatDeleteDynamic( object, effectivePerson);		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}

	public List<Dynamic> taskUpdatePropertyDynamic(Task task, String dynamicTitle, String dynamicOptType, String dynamicDescription,  EffectivePerson effectivePerson) throws Exception {
		if ( StringUtils.isEmpty( dynamicTitle ) || StringUtils.isEmpty( dynamicOptType ) || StringUtils.isEmpty( dynamicDescription ) ) {
			return null;
		}
		if ( task == null) {
			return null;
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		if( dynamicDescription.length() > 70 ) {
			dynamicDescription = dynamicDescription.substring( 0, 60 )+ "...";
		}
		List<Dynamic> dynamics = dynamicService.getTaskPropertyUpdateDynamic( task, dynamicTitle, dynamicOptType, dynamicDescription, effectivePerson);		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			if( ListTools.isNotEmpty( dynamics )) {
				for( Dynamic dynamic : dynamics ) {
					dynamic = dynamicService.save( emc, dynamic, null );
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return dynamics;
	}

	/**
	 * 为任务关联新的标签动态
	 * @param task
	 * @param taskTag
	 * @param effectivePerson
	 * @return
	 * @throws Exception 
	 */
	public Dynamic addTaskTagReleDynamic(Task task, TaskTag taskTag, EffectivePerson effectivePerson) throws Exception {
		if ( task == null) {
			throw new Exception("task object is null.");
		}
		if ( taskTag == null) {
			throw new Exception("taskTag object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		
		Dynamic dynamic = dynamicService.getAddTaskTagReleDynamic( task, taskTag, effectivePerson);		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}

	/**
	 * 移除工作任务标签操作动态
	 * @param task
	 * @param taskTag
	 * @param effectivePerson
	 * @return
	 * @throws Exception 
	 */
	public Dynamic removeTaskTagReleDynamic(Task task, TaskTag taskTag, EffectivePerson effectivePerson) throws Exception {
		if ( task == null) {
			throw new Exception("task object is null.");
		}
		if ( taskTag == null) {
			throw new Exception("taskTag object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		
		Dynamic dynamic = dynamicService.geRemoveTaskTagReleDynamic( task, taskTag, effectivePerson);		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}

	public Dynamic taskTagCreateDynamic(TaskTag taskTag, EffectivePerson effectivePerson) throws Exception {
		if ( taskTag == null) {
			throw new Exception("taskTag object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}		
		Dynamic dynamic = dynamicService.getTagCreateDynamic( taskTag, effectivePerson);		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
	
	public Dynamic taskTagDeleteDynamic(TaskTag taskTag, EffectivePerson effectivePerson) throws Exception {
		if ( taskTag == null) {
			throw new Exception("taskTag object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}		
		Dynamic dynamic = dynamicService.getTagDeleteDynamic( taskTag, effectivePerson);		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			dynamic = dynamicService.save( emc, dynamic, null );
		} catch (Exception e) {
			throw e;
		}
		return dynamic;
	}
}
