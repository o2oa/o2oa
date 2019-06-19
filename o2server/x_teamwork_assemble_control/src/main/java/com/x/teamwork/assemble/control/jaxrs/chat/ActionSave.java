package com.x.teamwork.assemble.control.jaxrs.chat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Chat;
import com.x.teamwork.core.entity.Task;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Chat chat = null;
		Task task = null;
		Wi wi = null;
		Boolean check = true;
		String optType = "CREATE";

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new ChatPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {
			if( StringUtils.isEmpty( wi.getTaskId() )) {
				check = false;
				Exception exception = new TaskIDEmptyException();
				result.error(exception);
			}
		}		
		if (check) {
			if( StringUtils.isEmpty( wi.getContent() )) {
				check = false;
				Exception exception = new ChatContentEmptyException();
				result.error(exception);
			}
		}		
		if (check) {
			if( StringUtils.isEmpty( wi.getSender() )) {
				wi.setSender( effectivePerson.getDistinguishedName() );
			}
		}		
		if (check) {
			if(  wi.getContent().getBytes().length > 230 ) {
				wi.setIsLob( true );
			}
		}
		
		if (check) {
			try {
				task = taskQueryService.get( wi.getTaskId() );
				if ( task == null) {
					check = false;
					Exception exception = new TaskNotExistsException( wi.getTaskId() );
					result.error( exception );
				}else {
					wi.setProjectId( task.getProject() );
					wi.setProjectTitle( task.getProjectName() );
					wi.setTaskTitle( task.getName() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ChatPersistException(e, "根据ID查询工作任务信息对象时发生异常。ID:" + wi.getTaskId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {					
				chat = chatPersistService.create( wi, effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( Chat.class );
				Wo wo = new Wo();
				wo.setId( chat.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ChatPersistException(e, "工作交流信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}			
		}
		if (check) {
			try {					
				dynamicPersistService.save( chat, optType, effectivePerson, jsonElement.toString() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

	public static class Wi extends Chat {
		
		private static final long serialVersionUID = -6314932919066148113L;
		
		public static WrapCopier<Wi, Chat> copier = WrapCopierFactory.wi( Wi.class, Chat.class, null, null );

	}

	public static class Wo extends WoId {
	}
	
}