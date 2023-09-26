package com.x.processplatform.assemble.surface.jaxrs.taskprocessmode;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.TaskProcessMode;
import com.x.processplatform.core.entity.content.TaskProcessModeItem;
import com.x.processplatform.core.entity.element.Process;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

class ActionList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionList.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(StringUtils.isBlank(wi.getProcess())){
			throw new ExceptionFieldEmpty(TaskProcessMode.process_FIELDNAME);
		}
		if(StringUtils.isBlank(wi.getActivity())){
			throw new ExceptionFieldEmpty(TaskProcessMode.activity_FIELDNAME);
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			ActionResult<List<Wo>> result = new ActionResult<>();

			Business business = new Business(emc);
			Process process = business.process().pick(wi.getProcess());
			if (null == process) {
				throw new ExceptionEntityNotExist(wi.getProcess(), Process.class.getSimpleName());
			}
			wi.setProcess(StringUtils.isBlank(process.getEdition()) ? wi.getProcess() : process.getEdition());
			List<TaskProcessMode> taskProcessModeList = business.taskProcessMode().listMode(effectivePerson.getUnique(), wi);
			SortTools.desc(taskProcessModeList, TaskProcessMode.hitCount_FIELDNAME, TaskProcessMode.updateTime_FIELDNAME);
			List<Wo> wos = new ArrayList<>();
			for(TaskProcessMode mode : taskProcessModeList){
				List<TaskProcessModeItem> itemList = mode.getTaskProcessModeItemList();
				SortTools.desc(itemList, TaskProcessModeItem.hitCount_FIELDNAME, TaskProcessModeItem.updateTime_FIELDNAME);
				if(itemList.size() > 3){
					itemList.subList(0, 3);
				}
				for(TaskProcessModeItem item : itemList){
					Wo wo = Wo.copier.copy(mode);
					wo.setRouteGroup(item.getRouteGroup());
					wo.setKeepTask(item.getKeepTask());
					wo.setOpinion(item.getOpinion());
					wo.setOrganizations(item.getOrganizations());
					wos.add(wo);
				}
			}

			result.setData(wos);
			return result;
		}
	}

	public static class Wi extends TaskProcessMode {

		private static final long serialVersionUID = -5033720364055042229L;

		static WrapCopier<Wi, TaskProcessMode> copier = WrapCopierFactory.wi(Wi.class, TaskProcessMode.class,
				ListTools.toList(TaskProcessMode.process_FIELDNAME, TaskProcessMode.activity_FIELDNAME,
						TaskProcessMode.activityAlias_FIELDNAME, TaskProcessMode.activityName_FIELDNAME), null);

	}

	public static class Wo extends TaskProcessMode {

		private static final long serialVersionUID = 1398834553299330481L;
		static WrapCopier<TaskProcessMode, Wo> copier = WrapCopierFactory.wo(TaskProcessMode.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisibleIncludeProperites, TaskProcessMode.taskProcessModeItemList_FIELDNAME));

	}
}
