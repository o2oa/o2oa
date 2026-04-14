package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.teamwork.assemble.control.Business;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.assemble.control.service.MessageFactory;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskView;

/**
 * 工作任务参与者变更
 * @author sword
 */
public class ActionParticipantUpdate extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionParticipantUpdate.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,  String id,  JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn( jsonElement, Wi.class );
		Wo wo = new Wo();
		Task task = taskQueryService.get( id );
		if( task == null ) {
			throw new TaskNotExistsException( id );
		}

		if(!this.isManager(task.getId(), effectivePerson)){
			throw new TaskPersistException("权限不足!");
		}

		this.saveParticipant(wi, id, effectivePerson, wo);
		result.setData( wo );
		return result;
	}

	private void saveParticipant(Wi wi, String id, EffectivePerson effectivePerson, Wo wo) throws Exception{
		final List<String> newParticipants = new ArrayList<>();
		wi.getParticipantList().stream().forEach(participant -> {
			try {
				if (OrganizationDefinition.isIdentityDistinguishedName(participant)) {
					participant = userManagerService.getPersonNameWithIdentity(participant);
				}
				if(OrganizationDefinition.isPersonDistinguishedName(participant) && !newParticipants.contains(participant)){
					newParticipants.add(participant);
				}
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
		});
		Task task;
		boolean savePermission = false;
		List<String> oldParticipants;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			task = emc.find(id, Task.class);
			oldParticipants = task.getParticipantList();
			boolean flag = oldParticipants.size() == newParticipants.size() && oldParticipants.containsAll(newParticipants);
			if(!flag){
				emc.beginTransaction(Task.class);
				task.setParticipantList(newParticipants);
				savePermission = taskPersistService.savePermission(business,task, null, newParticipants);
				emc.commit();
			}
		}
		if(savePermission){
			this.dealParticipantChange(task, oldParticipants, effectivePerson, wo);
			CacheManager.notify( Task.class );
		}
	}

	private void dealParticipantChange(Task task, List<String> oldParticipants, EffectivePerson effectivePerson, Wo wo){
		List<String> newParticipants = task.getParticipantList();
		List<String> addParticipants = new ArrayList<>(newParticipants);
		List<String> removeParticipants = new ArrayList<>(oldParticipants);
		addParticipants.removeAll(oldParticipants);
		removeParticipants.removeAll(newParticipants);

		try {
			new BatchOperationPersistService().addOperation(
					BatchOperationProcessService.OPT_OBJ_TASK,
					BatchOperationProcessService.OPT_TYPE_PERMISSION,  task.getId(),  task.getId(), "变更工作任务参与者，刷新文档权限：ID=" +   task.getId() );
		} catch (Exception e) {
			logger.error(e);
		}

		//记录工作任务信息变化记录
		try {
			List<Dynamic>  dynamics = dynamicPersistService.taskParticipantsUpdateDynamic(task, addParticipants, removeParticipants, effectivePerson );
			if( dynamics == null ) {
				dynamics = new ArrayList<>();
			}
			wo.setDynamics( WoDynamic.copier.copy( dynamics ));
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static class Wi {

		@FieldDescribe("参与者标识列表：可能是个人，可能是身份，也可能是组织和群组")
		private List<String> participantList;

		public List<String> getParticipantList() {
			return participantList == null ? Collections.emptyList() : participantList;
		}

		public void setParticipantList(List<String> participantList) {
			this.participantList = participantList;
		}
	}

	public static class Wo extends WoId {

		@FieldDescribe("操作引起的动态内容")
		List<WoDynamic> dynamics = new ArrayList<>();

		public List<WoDynamic> getDynamics() {
			return dynamics;
		}

		public void setDynamics(List<WoDynamic> dynamics) {
			this.dynamics = dynamics;
		}
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
