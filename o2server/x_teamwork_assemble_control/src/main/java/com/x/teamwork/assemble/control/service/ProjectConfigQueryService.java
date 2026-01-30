package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.ProjectGroupRele;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;
import com.x.teamwork.core.entity.tools.filter.term.InTerm;
import com.x.teamwork.core.entity.Priority;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectConfig;
import com.x.teamwork.core.entity.ProjectGroup;


/**
 * 对项目组信息查询的服务
 * 
 * @author O2LEE
 */
public class ProjectConfigQueryService {

	private ProjectConfigService projectConfigService = new ProjectConfigService();
	

	public List<ProjectGroup> list(List<String> groupIds) throws Exception {
		if ( ListTools.isEmpty( groupIds )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectConfigService.list( emc, groupIds );
		} catch (Exception e) {
			throw e;
		}
	}	
	
	/**
	 * 根据项目组的标识查询项目组信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProjectConfig get( String id ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectConfigService.get(emc, id );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据project获取项目配置信息列表
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public List<ProjectConfig> getProjectConfigByProject( String project ) throws Exception {
		if (StringUtils.isEmpty(project)) {
			return new ArrayList<>();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectConfigService.getProjectConfigByProject(emc, project);
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
			return projectConfigService.listPriority(emc);
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
			return projectConfigService.listPriorityByPerson(emc, person);
		} catch (Exception e) {
			throw e;
		}
	}

	
	/**
	 * 根据条件查询项目配置ID列表，最大查询2000条,查询未删除
	 * @param effectivePerson
	 * @param i
	 * @param queryFilter
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllProjectConfigIds(EffectivePerson effectivePerson, int maxCount, QueryFilter queryFilter) throws Exception {
		String personName = effectivePerson.getDistinguishedName();
		if( maxCount ==  0) {
			maxCount = 1000;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectConfigService.listAllProjectConfigIds( emc, maxCount, personName, queryFilter );
		} catch (Exception e) {
			throw e;
		}
	}	
	
	/**
	 * 根据项目ID列表查询项目信息列表，根据上一条的sequnce查询指定数量的信息
	 * @param pageSize
	 * @param lastId
	 * @param orderField
	 * @param orderType
	 * @param projectIds
	 * @return
	 * @throws Exception
	 */
	public List<ProjectConfig> listWithProjectConfigIdFilter( Integer pageSize, String lastId, String orderField, String orderType, List<String> projectConfigIds ) throws Exception {
		ProjectConfig projectConfig = null;
		if( pageSize == 0 ) { pageSize = 20; }
		if( StringUtils.isEmpty( orderField ) ) { 
			orderField = "createTime";
		}
		if( StringUtils.isEmpty( orderType ) ) { 
			orderType = "desc";
		}
		QueryFilter queryFilter = new QueryFilter();
		queryFilter.addInTerm( new InTerm("id", new ArrayList<>(projectConfigIds) ));
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if( lastId != null ) {
				projectConfig = emc.find( lastId, ProjectConfig.class );
			}
			if( projectConfig != null ) {
				return projectConfigService.listWithFilter(emc, pageSize, projectConfig.getSequence(), orderField, orderType, null, null, null, null, queryFilter );
			}else {
				return projectConfigService.listWithFilter(emc, pageSize, null, orderField, orderType, null, null, null, null, queryFilter );
			}	
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
			return projectConfigService.listProjectIdByGroup(emc, group);
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
			List<ProjectGroupRele> reles = projectConfigService.listReleWithProject(emc, projectId);
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
			List<ProjectGroupRele> reles = projectConfigService.listReleWithProjectAndGroup(emc, projectId, groupId );
			if( ListTools.isNotEmpty( reles )) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

}
