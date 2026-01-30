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
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

import java.util.ArrayList;
import java.util.List;

public class ActionListNextWithProject extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListNextWithProject.class);

	protected ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer count, String projectId, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		Project project = projectQueryService.getFromCache(projectId);
		if(project == null){
			throw new ProjectNotExistsException(projectId);
		}
		if(!this.isReader(projectId, effectivePerson, true)){
			throw new ExceptionAccessDenied(effectivePerson);
		}
		wrapIn.setBundle(projectId);
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

		private static final long serialVersionUID = 3252968472650815682L;
		static WrapCopier<Dynamic, Wo> copier = WrapCopierFactory.wo( Dynamic.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}
