package com.x.teamwork.assemble.control.jaxrs.taskview;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.TaskView;

public class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String taskViewId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		TaskView taskView = null;
		Boolean check = true;
		Wo wo = new Wo();
		
		if ( StringUtils.isEmpty( taskViewId ) ) {
			check = false;
			Exception exception = new TaskViewIdForQueryEmptyException();
			result.error( exception );
		}

		if (check) {
			try {
				taskView = taskViewQueryService.get(taskViewId);
				if ( taskView == null) {
					check = false;
					Exception exception = new TaskViewNotExistsException(taskViewId);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskViewQueryException(e, "根据指定ID查询工作任务视图信息对象时发生异常。id:" + taskViewId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				taskViewPersistService.delete( taskViewId, effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( TaskView.class );
				
				wo.setId( taskView.getId() );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskViewQueryException(e, "根据指定flag删除工作任务标签信息对象时发生异常。taskViewId:" + taskViewId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData( wo );
		return result;
	}

	public static class Wo extends WoId {
	}
	
	public static class WoDynamic extends Dynamic{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Dynamic, WoDynamic> copier = WrapCopierFactory.wo( Dynamic.class, WoDynamic.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}		
	}
}