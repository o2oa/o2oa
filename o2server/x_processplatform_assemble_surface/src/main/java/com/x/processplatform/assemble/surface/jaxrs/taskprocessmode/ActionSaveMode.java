package com.x.processplatform.assemble.surface.jaxrs.taskprocessmode;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.TaskProcessMode;
import com.x.processplatform.core.entity.content.TaskProcessModeItem;
import com.x.processplatform.core.entity.element.Process;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ActionSaveMode extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSaveMode.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(StringUtils.isBlank(wi.getProcess())){
			throw new ExceptionFieldEmpty(TaskProcessMode.process_FIELDNAME);
		}
		if(StringUtils.isBlank(wi.getActivity())){
			throw new ExceptionFieldEmpty(TaskProcessMode.activity_FIELDNAME);
		}
		if(StringUtils.isBlank(wi.getRouteId())){
			throw new ExceptionFieldEmpty(TaskProcessMode.routeId_FIELDNAME);
		}
		if(StringUtils.isBlank(wi.getAction())){
			throw new ExceptionFieldEmpty(TaskProcessMode.action_FIELDNAME);
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = business.process().pick(wi.getProcess());
			if (null == process) {
				throw new ExceptionEntityNotExist(wi.getProcess(), Process.class.getSimpleName());
			}
			wi.setProcess(StringUtils.isBlank(process.getEdition()) ? wi.getProcess() : process.getEdition());
			wi.setProcessName(process.getName());
			emc.beginTransaction(TaskProcessMode.class);
			TaskProcessModeItem item = new TaskProcessModeItem(wi.getRouteGroup(), wi.getKeepTask(), wi.getOpinion(), wi.getOrganizations());
			TaskProcessMode taskProcessMode = business.taskProcessMode().getMode(effectivePerson.getUnique(), wi);
			if(taskProcessMode != null){
				this.updateMode(taskProcessMode, item);
			}else{
				taskProcessMode = Wi.copier.copy(wi);
				taskProcessMode.setPerson(effectivePerson.getUnique());
				boolean flag = this.updateMode(taskProcessMode, item);
				if(flag) {
					emc.persist(taskProcessMode, CheckPersistType.all);
				}
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setId(taskProcessMode.getId());
			result.setData(wo);
		}

		return result;
	}

	private boolean updateMode(TaskProcessMode taskProcessMode, TaskProcessModeItem item) throws Exception{
		boolean flag = true;
		taskProcessMode.setHitCount(taskProcessMode.getHitCount() + 1);
		List<String> orgList = new ArrayList<>();
		if(item.getOrganizations()!=null){
			item.getOrganizations().values().stream().forEach(o -> orgList.addAll(o));
		}
		if(orgList.size() < TaskProcessMode.MAX_ORG_RECORD) {
			List<TaskProcessModeItem> list = new ArrayList<>(taskProcessMode.getProperties().getTaskProcessModeItemList());
			Optional<TaskProcessModeItem> optional = list.stream().filter(o -> o.getMd5Key().equals(item.getMd5Key())).findFirst();
			if (optional.isPresent()) {
				optional.get().addHitCount();
			} else {
				list.add(item);
				SortTools.desc(list, TaskProcessModeItem.hitCount_FIELDNAME, TaskProcessModeItem.updateTime_FIELDNAME);
				if(list.size() > TaskProcessMode.MAX_ITEM){
					list = list.subList(0, TaskProcessMode.MAX_ITEM);
				}
			}
			taskProcessMode.setTaskProcessModeItemList(list);
		}else{
			flag = false;
		}
		return flag;
	}

	public static class Wi extends TaskProcessMode {

		private static final long serialVersionUID = -5033720364055042229L;

		static WrapCopier<Wi, TaskProcessMode> copier = WrapCopierFactory.wi(Wi.class, TaskProcessMode.class, null, ListTools
				.toList(JpaObject.FieldsUnmodifyIncludePorperties, TaskProcessMode.hitCount_FIELDNAME, TaskProcessMode.person_FIELDNAME));

	}

	public static class Wo extends WoId {

	}

}
