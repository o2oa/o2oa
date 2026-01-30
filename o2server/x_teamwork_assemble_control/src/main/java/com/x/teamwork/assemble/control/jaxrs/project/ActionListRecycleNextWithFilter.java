package com.x.teamwork.assemble.control.jaxrs.project;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
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
import com.x.teamwork.core.entity.ProjectStatusEnum;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.InTerm;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sword
 */
public class ActionListRecycleNextWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListRecycleNextWithFilter.class);

	protected ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer pageNum, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		wrapIn.setExecutor(effectivePerson.getDistinguishedName());
		wrapIn.setStatusList(ListTools.toList(ProjectStatusEnum.CANCELED.getValue()));
		QueryFilter  queryFilter = wrapIn.getQueryFilter(effectivePerson);

		Long total = projectQueryService.countWithCondition(null, queryFilter);
		if(total > 0){
			List<Project> projectList = projectQueryService.listPagingWithCondition(null,
					wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter, pageNum, count);

			for( Project project : projectList ) {
				Wo wo = Wo.copier.copy(project);
				if( wo.getStarPersonList().contains( effectivePerson.getDistinguishedName() )) {
					wo.setStar( true );
				}

				WrapOutControl control = new WrapOutControl();
				if(this.canEdit(project, effectivePerson)) {
					control.setDelete( true );
					control.setEdit( true );
					control.setSortable( true );
				}
				if(ProjectStatusEnum.isEndStatus(project.getWorkStatus())){
					control.setTaskCreate(false);
				}
				control.setFounder( control.getEdit() ? true : this.isManager(project.getId(), effectivePerson) );
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

		private static final long serialVersionUID = -3566153918359309288L;
		static WrapCopier<Project, Wo> copier = WrapCopierFactory.wo( Project.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}
