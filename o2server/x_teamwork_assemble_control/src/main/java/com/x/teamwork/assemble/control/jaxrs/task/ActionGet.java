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

import net.sf.ehcache.Element;

public class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		Task task = null;
		TaskDetail taskDetail = null;
		List<ProjectExtFieldRele> extFieldReleList = null;
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
						wo.setMemoLob1( taskDetail.getMemoLob1() );
						wo.setMemoLob2( taskDetail.getMemoLob2() );
						wo.setMemoLob3( taskDetail.getMemoLob3() );
					}
					if( ListTools.isNotEmpty( extFieldReleList )) {
						wo.setExtFields( WoExtFieldRele.copier.copy( extFieldReleList ) );
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
		
		@FieldDescribe("备用LOB信息1(128K)")
		private String memoLob1;
		
		@FieldDescribe("备用LOB信息2(128K)")
		private String memoLob2;
		
		@FieldDescribe("备用LOB信息3(128K)")
		private String memoLob3;
		
		@FieldDescribe("所属项目的扩展列设定")
		private List<WoExtFieldRele> extFields;
		
		private Long rank;		
		
		public List<WoExtFieldRele> getExtFields() {
			return extFields;
		}

		public void setExtFields(List<WoExtFieldRele> extFields) {
			this.extFields = extFields;
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

		public String getMemoLob1() {
			return memoLob1;
		}

		public void setMemoLob1(String memoLob1) {
			this.memoLob1 = memoLob1;
		}

		public String getMemoLob2() {
			return memoLob2;
		}

		public void setMemoLob2(String memoLob2) {
			this.memoLob2 = memoLob2;
		}

		public String getMemoLob3() {
			return memoLob3;
		}

		public void setMemoLob3(String memoLob3) {
			this.memoLob3 = memoLob3;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo( Task.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

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