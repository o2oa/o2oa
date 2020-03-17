package com.x.teamwork.assemble.control.jaxrs.chat;

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
import com.x.teamwork.assemble.control.service.MessageFactory;
import com.x.teamwork.core.entity.Chat;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.Task;

public class ActionCreate extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Chat chat = null;
		Task task = null;
		Wi wi = null;
		Wo wo = new Wo();
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
			chat = Wi.copier.copy( wi );
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
				chat.setSender( effectivePerson.getDistinguishedName() );
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
					chat.setProjectId( task.getProject() );
					chat.setProjectTitle( task.getProjectName() );
					chat.setTaskTitle( task.getName() );
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
				String lobContent = wi.getContent();
				if( wi.getContent().length() > 70 ) {
					chat.setIsLob( true );
					chat.setContent( wi.getContent().substring(0, 70) + "...");
				}
				
				chat = chatPersistService.create( effectivePerson, chat, lobContent );
				
				// 更新缓存
				ApplicationCache.notify( Chat.class );
				
				wo.setId( chat.getId() );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new ChatPersistException(e, "工作评论信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}			
		}
		
		if (check) {
			try {
				MessageFactory.message_to_chat( chat );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				Dynamic dynamic = dynamicPersistService.chatPublishDynamic( chat, effectivePerson,  jsonElement.toString() );
				if( dynamic != null ) {
					List<WoDynamic> dynamics = new ArrayList<>();
					dynamics.add( WoDynamic.copier.copy( dynamic ) );
					if( wo != null ) {
						wo.setDynamics(dynamics);
					}
				}
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData( wo );
		return result;
	}	

	public static class Wi {
		
		public static WrapCopier<Wi, Chat> copier = WrapCopierFactory.wi( Wi.class, Chat.class, null, null );

		@FieldDescribe("所属工作ID，<font style='color:red'>必填</font>")
		private String taskId = null;
		
		@FieldDescribe("交流内容，<font style='color:red'>必填</font>" )
		private String content = null;
		
		@FieldDescribe("发送者，非必填")
		private String sender = null;

		@FieldDescribe("目标者，非必填")
		private String target = null;		

		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public String getSender() {
			return sender;
		}

		public void setSender(String sender) {
			this.sender = sender;
		}

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
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