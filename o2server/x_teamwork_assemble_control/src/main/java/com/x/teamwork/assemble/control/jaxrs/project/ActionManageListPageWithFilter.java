package com.x.teamwork.assemble.control.jaxrs.project;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sword
 */
public class ActionManageListPageWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionManageListPageWithFilter.class);

	protected ActionResult<List<Wo>> execute( EffectivePerson effectivePerson, Integer pageNum, Integer count, JsonElement jsonElement ) throws Exception {
		logger.debug("effectivePerson:{}", effectivePerson::getDistinguishedName);
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wrapIn = this.convertToWrapIn(jsonElement, Wi.class);

		QueryFilter  queryFilter = wrapIn.getQueryFilter();
		String person = effectivePerson.getDistinguishedName();
		Business business = new Business(null);
		if(business.isManager(effectivePerson)){
			person = null;
		}
		Long total = projectQueryService.countWithCondition(person, queryFilter);
		if(total > 0){
			List<Project> projectList = projectQueryService.listPagingWithCondition(person,
					wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter, pageNum, count);

			for( Project project : projectList ) {
				Wo wo = Wo.copier.copy(project);

				WrapOutControl control = new WrapOutControl();
				control.setDelete( true );
				control.setEdit( true );
				control.setSortable( true );
				if(effectivePerson.getDistinguishedName().equalsIgnoreCase( project.getCreatorPerson())){
					control.setFounder( true );
				}else{
					control.setFounder( false );
				}
				wo.setControl(control);
				wos.add( wo );
			}
		}
		result.setCount( total );
		result.setData( wos );

		return result;
	}

	public static class Wi extends WrapInQueryProject{
	}

	public static class Wo extends WrapOutProject {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<Project, Wo> copier = WrapCopierFactory.wo( Project.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}
