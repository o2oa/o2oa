package com.x.teamview.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectDetail;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

/**
 * 对项目信息查询的服务
 * 
 * @author O2LEE
 */
public class ProjectQueryService {

	private ProjectService projectService = new ProjectService();
	private UserManagerService userManagerService = new UserManagerService();
	
	/**
	 * 根据项目的标识查询项目信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Project get( String id ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectService.get(emc, id );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public ProjectDetail getDetail(String id) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectService.getDetail(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String getDescription(String id) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ProjectDetail detail = projectService.getDetail(emc, id );
			if( detail != null ) {
				return detail.getDescription();
			}else {
				return "";
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据ID列表查询项目信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<Project> list(List<String> ids) throws Exception {
		if (ListTools.isEmpty( ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.list( Project.class,  ids );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据过滤条件查询符合要求的项目信息数量
	 * @param effectivePerson
	 * @param group  项目分组
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public Long countWithFilter( EffectivePerson effectivePerson, QueryFilter queryFilter ) throws Exception {
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<String> identityNames = null;
		String personName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			personName = effectivePerson.getDistinguishedName();
			unitNames = userManagerService.listUnitNamesWithPerson( personName );
			groupNames = userManagerService.listGroupNamesByPerson(personName);
			identityNames = userManagerService.listIdentitiesWithPerson( personName );
			return projectService.countWithFilter( emc, personName, identityNames, unitNames, groupNames, queryFilter );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据过滤条件查询符合要求的项目信息列表
	 * @param effectivePerson
	 * @param pageSize
	 * @param pageNum
	 * @param orderField
	 * @param orderType
	 * @param group  项目分组
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public List<Project> listWithFilter( EffectivePerson effectivePerson, Integer pageSize, Integer pageNum, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<String> identityNames = null;
		List<Project> projectList = null;
		List<Project> result = new ArrayList<>();
		String personName = null;
		Integer maxCount = 20;
		Integer startNumber = 0;		
		
		if( pageNum == 0 ) { pageNum = 1; }
		if( pageSize == 0 ) { pageSize = 20; }
		maxCount = pageSize * pageNum;
		startNumber = pageSize * ( pageNum -1 );
		
		if( StringUtils.isEmpty( orderField ) ) { 
			orderField = "createTime";
		}
		if( StringUtils.isEmpty( orderType ) ) { 
			orderType = "desc";
		}
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			personName = effectivePerson.getDistinguishedName();
			unitNames = userManagerService.listUnitNamesWithPerson( personName );
			groupNames = userManagerService.listGroupNamesByPerson( personName );
			identityNames = userManagerService.listIdentitiesWithPerson( personName );
			projectList = projectService.listWithFilter(emc, maxCount, orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter );
			
			if( ListTools.isNotEmpty( projectList )) {
				for( int i = 0; i<projectList.size(); i++ ) {
					if( i >= startNumber ) {
						result.add( projectList.get( i ));
					}
				}
			}			
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	/**
	 * 根据条件查询符合条件的项目信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param effectivePerson
	 * @param pageSize
	 * @param lastId
	 * @param orderField
	 * @param orderType
	 * @param group  项目分组
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public List<Project> listWithFilter( EffectivePerson effectivePerson, Integer pageSize, String lastId, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<Project> projectList = null;
		List<String> identityNames = null;
		String personName = null;
		Integer maxCount = 20;
		Project project = null;
		
		if( pageSize == 0 ) { pageSize = 20; }
		
		if( StringUtils.isEmpty( orderField ) ) { 
			orderField = "createTime";
		}
		if( StringUtils.isEmpty( orderType ) ) { 
			orderType = "desc";
		}
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			personName = effectivePerson.getDistinguishedName();
			unitNames = userManagerService.listUnitNamesWithPerson( personName );
			groupNames = userManagerService.listGroupNamesByPerson(personName);
			identityNames = userManagerService.listIdentitiesWithPerson(personName);
			if( lastId != null ) {
				project = emc.find( lastId, Project.class );
			}
			if( project != null ) {
				projectList = projectService.listWithFilter(emc, maxCount, project.getSequence(), orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter );
			}else {
				projectList = projectService.listWithFilter(emc, maxCount, null, orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter );
			}	
		} catch (Exception e) {
			throw e;
		}
		return projectList;
	}
	
	/**
	 * 判断用户是否为指定项目的管理员
	 * @param projectId
	 * @param distinguishedName
	 * @return
	 * @throws Exception 
	 */
	public Boolean isProjectManager(String projectId, String distinguishedName) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Project project = emc.find( projectId, Project.class );
			if( ListTools.isNotEmpty( project.getManageablePersonList() )){
				if( distinguishedName.equalsIgnoreCase( project.getCreatorPerson() )) {
					return true;
				}
				if( distinguishedName.equalsIgnoreCase( project.getExecutor() )) {
					return true;
				}
				if( project.getManageablePersonList().contains( distinguishedName )) {
					return true;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	/**
	 * 判断用户是项目参与者
	 * @param projectId
	 * @param distinguishedName
	 * @return
	 * @throws Exception 
	 */
	public Boolean isProjectParticipant( String projectId, String personName ) throws Exception {		
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<String> identityNames = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			unitNames = userManagerService.listUnitNamesWithPerson( personName );
			groupNames = userManagerService.listGroupNamesByPerson(personName);
			identityNames = userManagerService.listIdentitiesWithPerson(personName);
			
			Project project = emc.find( projectId, Project.class );

			if( project.getParticipantPersonList().contains( personName )) {
				return true;
			}
			project.getParticipantIdentityList().retainAll( identityNames );
			if( ListTools.isNotEmpty( project.getParticipantIdentityList() )) {
				return true;
			}
			project.getParticipantUnitList().retainAll( unitNames );
			if( ListTools.isNotEmpty( project.getParticipantUnitList() )) {
				return true;
			}
			project.getParticipantGroupList().retainAll( groupNames );
			if( ListTools.isNotEmpty( project.getParticipantGroupList() )) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}
	
	/**
	 * 判断用户是否拥有指定项目的访问权限
	 * @param appId
	 * @param distinguishedName
	 * @return
	 * @throws Exception 
	 */
	public Boolean isProjectViewer(String appId, String personName ) throws Exception {
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<String> identityNames = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			unitNames = userManagerService.listUnitNamesWithPerson( personName );
			groupNames = userManagerService.listGroupNamesByPerson(personName);
			identityNames = userManagerService.listIdentitiesWithPerson(personName);
			
			Project project = emc.find( appId, Project.class );
			if( personName.equalsIgnoreCase( project.getCreatorPerson() )) {
				return true;
			}
			if( personName.equalsIgnoreCase( project.getExecutor() )) {
				return true;
			}	
			if( project.getManageablePersonList().contains( personName )) {
				return true;
			}				
			if( project.getParticipantPersonList().contains( personName )) {
				return true;
			}
			project.getParticipantIdentityList().retainAll( identityNames );
			if( ListTools.isNotEmpty( project.getParticipantIdentityList() )) {
				return true;
			}
			project.getParticipantUnitList().retainAll( unitNames );
			if( ListTools.isNotEmpty( project.getParticipantUnitList() )) {
				return true;
			}
			project.getParticipantGroupList().retainAll( groupNames );
			if( ListTools.isNotEmpty( project.getParticipantGroupList() )) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	
}
