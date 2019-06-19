package com.x.temwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Task;

public class ActionParticipantRemove extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionParticipantRemove.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,  String id,  JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Task task = null;
		Wi wi = null;
		Boolean check = true;
		String optType = "REMOVE_PARTICIPANTS";

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
			if( ListTools.isNotEmpty( wi.getParticipants() ) ) {
				try {					
					task = taskPersistService.removeParticipants( id, wi.getParticipantPersons(), wi.getParticipantIdentitys(), wi.getParticipantUnits(), wi.getParticipantGroups(), effectivePerson );		
					
					// 更新缓存
					ApplicationCache.notify( Task.class );					
					Wo wo = new Wo();
					wo.setId( task.getId() );			
					result.setData( wo );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskPersistException(e, "工作任务信息保存时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			//记录工作任务信息变化记录
			try {
				dynamicPersistService.save( task, optType, effectivePerson, jsonElement.toString() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

	public static class Wi {
		
		@FieldDescribe("参与者标识列表：可能是个人，可能是身份，也可能是组织和群组")
		private List<String> participants;

		public List<String> getParticipants() {
			return participants;
		}

		public void setParticipants(List<String> participants) {
			this.participants = participants;
		}
		
		public List<String> getParticipantPersons(){
			return getParticipantWithTag("@P");
		}
		
		public List<String> getParticipantIdentitys(){
			return getParticipantWithTag("@I");
		}
		
		public List<String> getParticipantGroups(){
			return getParticipantWithTag("@G");
		}
		
		public List<String> getParticipantUnits(){
			return getParticipantWithTag("@U");
		}
		
		private List<String> getParticipantWithTag( String tag ){
			List<String> result = new ArrayList<>();
			if( ListTools.isNotEmpty( participants )) {
				for( String flag : participants ) {
					if( flag.toUpperCase().endsWith( tag.toUpperCase() )  ) {
						result.add( flag );
					}
				}
			}
			return result;
		}		
	}

	public static class Wo extends WoId {
	}
	
}