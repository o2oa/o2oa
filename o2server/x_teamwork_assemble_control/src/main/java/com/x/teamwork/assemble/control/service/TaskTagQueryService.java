package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskTag;
import com.x.teamwork.core.entity.TaskTagRele;


/**
 * 对动态信息查询的服务
 * 
 * @author O2LEE
 */
public class TaskTagQueryService {

	private TaskTagService taskTagService = new TaskTagService();
	
	/**
	 * 根据动态的标识查询动态信息
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public TaskTag get( String flag ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("flag is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskTagService.get(emc, flag );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据ID列表查询动态信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<TaskTag> list(List<String> ids) throws Exception {
		if (ListTools.isEmpty( ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.list( TaskTag.class,  ids );
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listTagIdsWithTask(EffectivePerson currentPerson, String taskId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskTagService.listTagIdsWithTask(emc, taskId, currentPerson.getDistinguishedName());
		} catch (Exception e) {
			throw e;
		}
	}

	public List<TaskTag> listTagsWithTask(EffectivePerson currentPerson, String taskId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> tagIds =  taskTagService.listTagIdsWithTask(emc, taskId, currentPerson.getDistinguishedName());
			if( ListTools.isNotEmpty( tagIds )) {
				return emc.list( TaskTag.class, tagIds );
			}else {
				return null;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> listTagNamesWithTask(EffectivePerson currentPerson, String taskId) throws Exception {
		List<String> tagNames = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> tagIds = taskTagService.listTagIdsWithTask(emc, taskId, currentPerson.getDistinguishedName());
			List<TaskTag> tags = null;
			
			if( ListTools.isNotEmpty( tagIds )) {
				tags = emc.list( TaskTag.class, tagIds );
			}
			if( ListTools.isNotEmpty( tags )) {
				for( TaskTag tag : tags ) {
					tagNames.add( tag.getTag() );
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return tagNames;
	}

	/**
	 * 根据项目和人员列示的项目标签信息
	 * @param effectivePerson
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public List<TaskTag> listWithProjectAndPerson( EffectivePerson effectivePerson, String project ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<TaskTag> tags = taskTagService.listWithProjectAndPerson( emc, project, effectivePerson.getDistinguishedName() );
			return tags;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<TaskTag> listWithTaskAndPerson(EffectivePerson effectivePerson, Task task) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<TaskTagRele> reles = taskTagService.listReleWithTaskAndPerson(emc, task.getId(), effectivePerson.getDistinguishedName());
			List<TaskTag> tags = new ArrayList<>();
			TaskTag tag = null;
			for( TaskTagRele rele : reles ) {
				tag = emc.find( rele.getTagId(), TaskTag.class );
				if( tag != null ) {
					tags.add( tag );
				}
			}
			return tags;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listTaskIdsWithTagContent(  String tagName, String project, String personName ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> tagIds =  taskTagService.listTagIdsWithContent( emc, tagName, project, personName );
			if( ListTools.isNotEmpty( tagIds )) {
				return taskTagService.listTaskIdsWithReleTagIds( emc, tagIds );
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	public List<String> listTaskIdsWithTagIds(  List<String> tagIds ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if( ListTools.isNotEmpty( tagIds )) {
				return taskTagService.listTaskIdsWithReleTagIds( emc, tagIds );
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
}
