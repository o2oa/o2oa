package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.ProjectExtFieldRele;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;
import com.x.teamwork.core.entity.TaskExtField;
import com.x.teamwork.core.entity.TaskTag;

import net.sf.ehcache.Element;

public class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		Task task = null;
		TaskDetail taskDetail = null;
		TaskExtField taskExtField = null;
		List<ProjectExtFieldRele> extFieldReleList = null;
		List<TaskTag> tags = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( flag ) ) {
			check = false;
			Exception exception = new TaskFlagForQueryEmptyException();
			result.error( exception );
		}

		String cacheKey = ApplicationCache.concreteCacheKey( flag );
		Element element = taskCache.get( cacheKey );

		if ((null != element) && (null != element.getObjectValue())) {
			wo = (Wo) element.getObjectValue();
			result.setData( wo );
		} else {
			if (check) {
				try {
					task = taskQueryService.get( flag );
					if ( task == null) {
						check = false;
						Exception exception = new TaskNotExistsException(flag);
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException(e, "根据指定flag查询工作任务信息对象时发生异常。flag:" + flag);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				try {
					taskDetail = taskQueryService.getDetail( flag );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException(e, "根据指定flag查询工作任务信息对象时发生异常。flag:" + flag);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				try {
					taskExtField = taskQueryService.getExtField( flag );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException(e, "根据指定flag查询工作任务扩展属性信息对象时发生异常。flag:" + flag);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				try {
					extFieldReleList = projectExtFieldReleQueryService.listReleWithProject( task.getProject() );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException(e, "根据指定projectId查询项目扩展列配置信息对象时发生异常。projectId:" + task.getProject());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				try {
					wo = Wo.copier.copy( task );
					if ( wo != null && taskDetail != null) {
						wo.setDetail( taskDetail.getDetail() );
						wo.setDescription( taskDetail.getDescription() );
					}
					if( ListTools.isNotEmpty( extFieldReleList )) {
						wo.setExtFieldConfigs( WoExtFieldRele.copier.copy( extFieldReleList ) );
					}
					if( taskExtField != null ) {
						wo.setExtField( WoTaskExtField.copier.copy( taskExtField ));
					}
					tags = taskTagQueryService.listWithTaskAndPerson(effectivePerson, task);
					if( ListTools.isNotEmpty( tags )) {
						wo.setTags( WoTaskTag.copier.copy( tags ));
					}
					taskCache.put(new Element( cacheKey, wo ));
					result.setData(wo);
				} catch (Exception e) {
					Exception exception = new TaskQueryException(e, "将查询出来的工作任务信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends Task {
		
		@FieldDescribe("工作内容(128K)")
		private String detail;

		@FieldDescribe("说明详细信息(10M)")
		private String description;
		
		@FieldDescribe("扩展属性信息(对象)")
		private WoTaskExtField extField;
		
		@FieldDescribe("任务标签(列表)")
		private List<WoTaskTag> tags = null;
		
		@FieldDescribe("所属项目的扩展列设定(配置列表)")
		private List<WoExtFieldRele> extFieldConfigs;
		
		private Long rank;

		public WoTaskExtField getExtField() {
			return extField;
		}

		public void setExtField(WoTaskExtField extField) {
			this.extField = extField;
		}

		public List<WoExtFieldRele> getExtFieldConfigs() {
			return extFieldConfigs;
		}

		public void setExtFieldConfigs(List<WoExtFieldRele> extFieldConfigs) {
			this.extFieldConfigs = extFieldConfigs;
		}

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
		public List<WoTaskTag> getTags() {
			return tags;
		}

		public void setTags(List<WoTaskTag> tags) {
			this.tags = tags;
		}


		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo( Task.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
	
	public static class WoTaskExtField extends TaskExtField {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskExtField, WoTaskExtField> copier = WrapCopierFactory.wo( TaskExtField.class, WoTaskExtField.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}

	public static class WoTaskTag extends TaskTag {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskTag, WoTaskTag> copier = WrapCopierFactory.wo( TaskTag.class, WoTaskTag.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}
	
	public static class WoExtFieldRele{
	
		@FieldDescribe("备用列名称")
		private String extFieldName;
		
		@FieldDescribe("显示属性名称")
		private String displayName;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<ProjectExtFieldRele, WoExtFieldRele> copier = WrapCopierFactory.wo( ProjectExtFieldRele.class, WoExtFieldRele.class, null, ListTools.toList(JpaObject.FieldsInvisible));

		public String getExtFieldName() {
			return extFieldName;
		}

		public void setExtFieldName(String extFieldName) {
			this.extFieldName = extFieldName;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
	}
}