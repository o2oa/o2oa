package com.x.teamwork.assemble.control.jaxrs.dynamic;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class ActionListNextWithTask extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListNextWithTask.class);

	protected ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer count, String taskId, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		Task task = taskQueryService.getFromCache(taskId);
		if(task == null){
			throw new TaskNotExistsException(taskId);
		}
		if(!this.isReader(taskId, effectivePerson, false)){
			throw new ExceptionAccessDenied(effectivePerson);
		}
		wrapIn.setTaskId(taskId);
		QueryFilter queryFilter = wrapIn.getQueryFilter();

		result.setData(wos);

		Long total = dynamicQueryService.countWithCondition(null, queryFilter );

		if( total > 0 ) {
			List<Dynamic> dynamicList = dynamicQueryService.listPagingWithCondition(null,
					wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter, page, count);
			String chatObjectType = "CHAT";
			for(Dynamic dynamic : dynamicList){
				Wo wo = Wo.copier.copy(dynamic);
				if( wo.getObjectType().equals( chatObjectType )) {
					wo.setDescription( chatQueryService.getContent( wo.getBundle() ));
				}
				wos.add(wo);
			}
		}

		result.setCount( total );
		result.setData( wos );
		return result;
	}

	public static class Wi extends WrapInTaskTag {
	}

	public static class Wo extends Dynamic {

		private static final long serialVersionUID = -5729493503619102062L;

		static WrapCopier<Dynamic, Wo> copier = WrapCopierFactory.wo( Dynamic.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}
