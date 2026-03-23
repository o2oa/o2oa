package com.x.teamwork.assemble.control.jaxrs.projectTemplate;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.ProjectTemplate;
import com.x.teamwork.core.entity.TaskListTemplate;


public class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		ProjectTemplate projectTemplate = null;
		Boolean check = true;
		List<TaskListTemplate> taskListTemplate = null;
		WrapOutControl control = null;

		if ( StringUtils.isEmpty( id ) ) {
			check = false;
			Exception exception = new ProjectTemplateFlagForQueryEmptyException();
			result.error( exception );
		}

		/*String cacheKey = ApplicationCache.concreteCacheKey( id ,effectivePerson);
		Element element = projectTemplateCache.get( cacheKey );

		if ((null != element) && (null != element.getObjectValue())) {
			wo = (Wo) element.getObjectValue();
			result.setData( wo );
		} else {*/
			if( Boolean.TRUE.equals( check ) ){
				try {
					projectTemplate = projectTemplateQueryService.get( id );
					if ( projectTemplate == null) {
						check = false;
						Exception exception = new ProjectTemplateNotExistsException( id );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ProjectTemplateQueryException(e, "根据指定flag查询项目模板信息对象时发生异常。id:" + id );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if( Boolean.TRUE.equals( check ) ){
				try {
					wo = Wo.copier.copy( projectTemplate );
					taskListTemplate = projectTemplateQueryService.getTaskListTemplateWithTemplateId(wo.getId());
					if( ListTools.isNotEmpty( taskListTemplate )) {
						wo.setTaskListTemplate( WoTaskListTemplate.copier.copy( taskListTemplate ));
					}
					
					//计算权限
					if( Boolean.TRUE.equals( check ) ){
						Business business = null;
						try (EntityManagerContainer bc = EntityManagerContainerFactory.instance().create()) {
							business = new Business(bc);
						}
						try {
							control = new WrapOutControl();
							if( business.isManager(effectivePerson) 
									|| effectivePerson.getDistinguishedName().equalsIgnoreCase( projectTemplate.getOwner() )){
								control.setDelete( true );
								control.setEdit( true );
								control.setSortable( true );								
							}else{
								control.setDelete( false );
								control.setEdit( false );
								control.setSortable( false );								
							}
							if(effectivePerson.getDistinguishedName().equalsIgnoreCase( projectTemplate.getOwner() )){
								control.setFounder( true );
							}else{
								control.setFounder( false );
							}
							wo.setControl(control);
						} catch (Exception e) {
							check = false;
							Exception exception = new ProjectTemplateQueryException(e, "根据指定flag查询工作任务权限信息时发生异常。id:" + id);
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
					
					//projectTemplateCache.put(new Element(cacheKey, wo));
					result.setData(wo);
				} catch (Exception e) {
					Exception exception = new ProjectTemplateQueryException(e, "将查询出来的项目模板信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		//}
		return result;
	}

	public static class Wo extends ProjectTemplate {
		@FieldDescribe("项目模板权限")
		private WrapOutControl control = null;	
		
		@FieldDescribe("模板对应的泳道信息")
		private List<WoTaskListTemplate> taskListTemplate = null;
		
		public WrapOutControl getControl() {
			return control;
		}

		public void setControl(WrapOutControl control) {
			this.control = control;
		}
		
		public List<WoTaskListTemplate> getTaskListTemplate() {
			return taskListTemplate;
		}

		public void setTaskListTemplate(List<WoTaskListTemplate> taskListTemplate) {
			this.taskListTemplate = taskListTemplate;
		}
		
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<ProjectTemplate, Wo> copier = WrapCopierFactory.wo( ProjectTemplate.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}
	public static class WoTaskListTemplate extends TaskListTemplate {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskListTemplate, WoTaskListTemplate> copier = WrapCopierFactory.wo( TaskListTemplate.class, WoTaskListTemplate.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}
}