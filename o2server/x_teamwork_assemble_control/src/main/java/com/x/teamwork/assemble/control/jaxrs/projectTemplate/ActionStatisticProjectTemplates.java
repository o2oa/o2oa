package com.x.teamwork.assemble.control.jaxrs.projectTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.ProjectTemplate;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

public class ActionStatisticProjectTemplates extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionStatisticProjectTemplates.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		List<String> projectTemplateIds = null;
		List<ProjectTemplate> projectTemplateList = null;
		List<String>  projectGroupList = null;
		List<WoGroup> woGroupList = new ArrayList<>();
		Business business = null;
		Boolean check = true;
		
		Integer allCount = 0;
		Integer myCount = 0;

		Cache.CacheKey cacheKey = new Cache.CacheKey( "ActionStatisticProjectTemplates", effectivePerson.getDistinguishedName() );
		Optional<?> optional = CacheManager.get(projectTemplateCache, cacheKey);

		if (optional.isPresent()) {
			wo = (Wo) optional.get();
			result.setData( wo );
		} else {
			if( Boolean.TRUE.equals( check ) ){
				try {
					//查询所有模板
					projectTemplateIds = projectTemplateQueryService.listAllProjectTemplateIds( effectivePerson, 2000,  new QueryFilter() );
					if( ListTools.isNotEmpty( projectTemplateIds )) {
						projectTemplateList = projectTemplateQueryService.list( projectTemplateIds );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ProjectTemplateQueryException( e, "查询所有项目模板信息列表时发生异常。" );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if( Boolean.TRUE.equals( check ) ){
				try {
					//查询我所有的项目组列表
					//projectGroupList = projectGroupQueryService.listGroupByPerson( effectivePerson.getDistinguishedName() );
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						business = new Business(emc);
						projectGroupList = business.projectTemplateFactory().fetchAllGroupType(effectivePerson.getDistinguishedName());
					}
					
				} catch (Exception e) {
					check = false;
					Exception exception = new ProjectTemplateQueryException( e, "查询用户所有项目模板信息列表时发生异常。" );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}	
			
			if( Boolean.TRUE.equals( check ) ){
				if( ListTools.isNotEmpty( projectTemplateList )) {
					for(  ProjectTemplate projectTemplate : projectTemplateList ) {
							allCount++;						
							if( projectTemplate.getOwner().equalsIgnoreCase(effectivePerson.getDistinguishedName()  )) {
								myCount++;
							}
					}
				}
			}
			
			if(Boolean.TRUE.equals( check )){
				if( ListTools.isNotEmpty( projectGroupList )) {
					for(  String projectGroup : projectGroupList ) {						
						List<ProjectTemplate> templateLists = business.projectTemplateFactory().ListProjectTemplateWithType(projectGroup);
						if(ListTools.isNotEmpty(templateLists)){
							List<WoTemplate> woTemplate = new ArrayList<>();
							WoGroup woGroup = new WoGroup();
							String type = "";
							woTemplate = WoTemplate.copier.copy( templateLists );
							for(ProjectTemplate templateList : templateLists){
								woGroup.addProjectTemplateTypeCount(1);
								type = templateList.getType();								
							}
							woGroup.setProjecTemplatetTypeName(type);
							woGroup.setWoTemplate(woTemplate);
							woGroupList.add(woGroup);
						}
					}
				}
			}
			
			if( Boolean.TRUE.equals( check ) ){
				try {
					wo = new Wo();
					wo.setAllCount( allCount );
					wo.setMyCount(myCount);				
					//SortTools.asc( woGroupList, "projectTemplateTypeCount");
					wo.setGroups( woGroupList );
					CacheManager.put( projectTemplateCache,cacheKey, wo);
					result.setData(wo);
				} catch (Exception e) {
					Exception exception = new ProjectTemplateQueryException(e, "将查询出来的应用项目模板信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo{

		@FieldDescribe("所有项目数量")
		private Integer allCount = 0;
		
		@FieldDescribe("所有项目数量")
		private Integer myCount = 0;
		
		@FieldDescribe("所有分组信息")
		private List<WoGroup> groups = null;
		
		public List<WoGroup> getGroups() {
			return groups;
		}

		public void setGroups(List<WoGroup> groups) {
			this.groups = groups;
		}

		public Integer getAllCount() {
			return allCount;
		}

		public void setAllCount(Integer allCount) {
			this.allCount = allCount;
		}

		public Integer getMyCount() {
			return myCount;
		}

		public void setMyCount(Integer myCount) {
			this.myCount = myCount;
		}

	}
	
	public static class WoTemplate extends ProjectTemplate{
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<ProjectTemplate, WoTemplate> copier = WrapCopierFactory.wo( ProjectTemplate.class, WoTemplate.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoGroup{
		
		@FieldDescribe("分组项目数量")
		private Integer projectTemplateTypeCount = 0;
		
		@FieldDescribe("分组名称")
		private String projecTemplatetTypeName;
		
		public String getProjecTemplatetName() {
			return projecTemplatetTypeName;
		}
		
		public void setProjecTemplatetTypeName(String projecTemplatetTypeName) {
			this.projecTemplatetTypeName = projecTemplatetTypeName;
		}
		
		public Integer getProjectTemplateTypeCount() {
			return projectTemplateTypeCount;
		}

		public void setProjectTemplateTypeCount(Integer projectTemplateTypeCount) {
			this.projectTemplateTypeCount = projectTemplateTypeCount;
		}

		public void addProjectTemplateTypeCount( Integer count ) {
			if( this.projectTemplateTypeCount == null ) {
				this.projectTemplateTypeCount =0;
			}
			this.projectTemplateTypeCount += count;
	   }
		
		@FieldDescribe("所有分组信息")
		private List<WoTemplate> woTemplate = null;
		
		public List<WoTemplate> getWoTemplate() {
			return woTemplate;
		}

		public void setWoTemplate(List<WoTemplate> woTemplate) {
			this.woTemplate = woTemplate;
		}
	}
}