package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
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

public class ActionParticipantUpdate extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionParticipantUpdate.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,  String id,  JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Task task = null;
		Wi wi = null;
		Wo wo = new Wo();
		Boolean check = true;
		List<String> old_participants = null;
		List<String> new_participants = null;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if ( StringUtils.isEmpty( id ) ) {
			check = false;
			Exception exception = new TaskFlagForQueryEmptyException();
			result.error( exception );
		}
		
		if (check) {	
			task = taskQueryService.get( id );
			if( task == null ) {
				check = false;
				Exception exception = new TaskNotExistsException( id );
				result.error(exception);
			}
		}
		
		if (check) {
			old_participants = task.getParticipantList();
			new_participants = wi.getParticipantList();
			if( ListTools.isNotEmpty( wi.getParticipantList() ) ) {
				try {					
					task = taskPersistService.addParticipants( id, new_participants );
					// 更新缓存
					ApplicationCache.notify( Task.class );					
					ApplicationCache.notify( TaskView.class );
					
					wo.setId( task.getId() );			
					
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskPersistException(e, "工作任务信息保存时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		if (check) {
			List<String> addParticipants = new ArrayList<>();
			List<String> removeParticipants = new ArrayList<>();
			if( old_participants == null ) { old_participants = new ArrayList<>(); }
			if( new_participants == null ) { old_participants = new ArrayList<>(); }			
			for( String participant : old_participants ) {
				if( !new_participants.contains( participant )) {
					removeParticipants.add( participant );
				}
			}
			for( String participant : new_participants ) {
				if( !old_participants.contains( participant )) {
					addParticipants.add( participant );
				}
			}
			
			if (check) {
				try {					
					new BatchOperationPersistService().addOperation( 
							BatchOperationProcessService.OPT_OBJ_TASK, 
							BatchOperationProcessService.OPT_TYPE_PERMISSION,  task.getId(),  task.getId(), "变更工作任务参与者，刷新文档权限：ID=" +   task.getId() );
				} catch (Exception e) {
					logger.error(e, effectivePerson, request, null);
				}	
			}
			
			if (check) {
				try {
					MessageFactory.message_to_teamWorkUpdateParticipants( task, addParticipants );
				} catch (Exception e) {
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			//记录工作任务信息变化记录
			try {
				List<Dynamic>  dynamics = dynamicPersistService.taskParticipantsUpdateDynamic(task, addParticipants, removeParticipants, effectivePerson );
				if( dynamics == null ) {
					dynamics = new ArrayList<>();
				}
				wo.setDynamics( WoDynamic.copier.copy( dynamics ));
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData( wo );
		return result;
	}	

	public static class Wi {
		
		@FieldDescribe("参与者标识列表：可能是个人，可能是身份，也可能是组织和群组")
		private List<String> participantList;

		public List<String> getParticipantList() {
			return participantList;
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