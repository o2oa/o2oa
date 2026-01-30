package com.x.teamwork.assemble.control.jaxrs.task;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.List;

/**
 * @author sword
 */
public class ActionExportWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExportWithFilter.class);

	protected ActionResult<Wo> execute( EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		logger.info("{}操作excel导出", effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Wi wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		String person = this.isSysManager(effectivePerson) ? "" : effectivePerson.getDistinguishedName();
		if(BooleanUtils.isTrue(wrapIn.getJustMy())){
			wrapIn.setExecutor(effectivePerson.getDistinguishedName());
			person = "";
		}
		QueryFilter queryFilter = wrapIn.getQueryFilter();
		List<Task> taskList = taskQueryService.listWithCondition(person,
				wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter);
		String excelName = wrapIn.getExcelName();
		if(StringUtils.isBlank(excelName)){
			if(StringUtils.isNotBlank(wrapIn.getProject())){
				Project project = projectQueryService.get(wrapIn.getProject());
				if(project!=null){
					excelName = project.getTitle();
				}
			}
		}
		String id = this.taskWriteToExcel(effectivePerson, taskList, excelName, wrapIn.getProject());
		wo.setId(id);
		result.setData(wo);
		return result;
	}

	public static class Wi extends WrapInQueryTask{

		@FieldDescribe("是否限制我负责的任务.")
		private Boolean justMy = false;

		@FieldDescribe("导出的excel名称.")
		private String excelName;

		public Boolean getJustMy() {
			return justMy;
		}

		public void setJustMy(Boolean justMy) {
			this.justMy = justMy;
		}

		public String getExcelName() {
			return excelName;
		}

		public void setExcelName(String excelName) {
			this.excelName = excelName;
		}
	}

	public static class Wo extends WoId {

	}

}
