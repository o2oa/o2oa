package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.ProjectGroupRele;
import com.x.teamwork.core.entity.Priority;
import com.x.teamwork.core.entity.ProjectGroup;


/**
 * 对项目组信息查询的服务
 * 
 * @author O2LEE
 */
public class PriorityQueryService {

	private PriorityService priorityService = new PriorityService();
	

	public List<ProjectGroup> list(List<String> groupIds) throws Exception {
		if ( ListTools.isEmpty( groupIds )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return priorityService.list( emc, groupIds );
		} catch (Exception e) {
			throw e;
		}
	}	
	
	/**
	 * 根据优先级的标识查询优先级信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Priority get( String id ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return priorityService.get(emc, id );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据优先级的名称查询优先级信息
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<Priority> getByName( String name ) throws Exception {
		if ( StringUtils.isEmpty( name )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return priorityService.getByName(emc, name );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据用户列示优先级信息列表
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public List<Priority> listPriority() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return priorityService.listPriority(emc);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据用户列示优先级信息列表
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public List<Priority> listPriorityByPerson( String person ) throws Exception {
		if (StringUtils.isEmpty(person)) {
			return new ArrayList<>();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return priorityService.listPriorityByPerson(emc, person);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据项目组ID，查询项目组内所有的项目ID列表
	 * @param emc
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public List<String> listProjectIdByGroup(String group ) throws Exception {
		if (StringUtils.isEmpty(group)) {
			return new ArrayList<>();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return priorityService.listProjectIdByGroup(emc, group);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listGroupIdByProject( String projectId ) throws Exception {
		List<String> result = new ArrayList<>();
		if (StringUtils.isEmpty( projectId )) {
			return result;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<ProjectGroupRele> reles = priorityService.listReleWithProject(emc, projectId);
			if( ListTools.isNotEmpty( reles )) {
				for( ProjectGroupRele rele : reles ) {
					if( !result.contains( rele.getGroupId() )) {
						result.add( rele.getGroupId() );
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * 判断是否存在分组和项目的关联
	 * @param groupId
	 * @param projectId
	 * @return
	 * @throws Exception 
	 */
	public boolean existsWithProjectAndGroup(String groupId, String projectId) throws Exception {
		if (StringUtils.isEmpty( groupId )) {
			return false;
		}
		if (StringUtils.isEmpty( projectId )) {
			return false;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<ProjectGroupRele> reles = priorityService.listReleWithProjectAndGroup(emc, projectId, groupId );
			if( ListTools.isNotEmpty( reles )) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

}
