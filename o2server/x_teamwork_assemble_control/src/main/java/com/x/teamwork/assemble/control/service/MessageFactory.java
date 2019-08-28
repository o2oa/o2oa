package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.teamwork.assemble.common.date.DateOperation;
import com.x.teamwork.core.entity.Chat;
import com.x.teamwork.core.entity.Task;

public class MessageFactory {	
	
	public static void message_to_teamWorkCreate( Task  task ) throws Exception {
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>message for teamwork create!" + task.getExecutor() );
		String title = "新工作任务:" + adjustTitle( task );
		title = StringTools.utf8SubString( title, JpaObject.length_255B );
		MessageConnector.send( MessageConnector.TYPE_TEAMWORK_TASKCREATE, title, task.getExecutor(),	task );
	}
	
	public static void message_to_teamWorkUpdate( Task  task ) throws Exception {
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>message for teamwork update!" + task.getExecutor() );
		String title = "工作任务信息变更:" + adjustTitle( task );
		title = StringTools.utf8SubString( title, JpaObject.length_255B );
		MessageConnector.send( MessageConnector.TYPE_TEAMWORK_TASKUPDATE, title, task.getExecutor(),	task );
	}
	
	public static void message_to_teamWorkUpdateParticipants( Task  task, List<String> participants ) throws Exception {
		String title = "工作任务参与者变更:" + adjustTitle( task );
		title = StringTools.utf8SubString( title, JpaObject.length_255B );		
		if( ListTools.isNotEmpty( participants )) {
			UserManagerService userManagerService = new UserManagerService();
			for( String participant  : participants ) {
				//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>message for teamwork update participants!" + participant );
				if( participant.indexOf( "@I" ) > 0 ) {
					participant = userManagerService.getPersonNameWithIdentity( participant );
				}
				MessageConnector.send( MessageConnector.TYPE_TEAMWORK_TASKUPDATE, title, participant,	task );
			}
		}
	}
	
	public static void message_to_teamWorkUpdateManagers( Task  task, List<String> managers ) throws Exception {
		String title = "工作任务管理者变更:" + adjustTitle( task );
		title = StringTools.utf8SubString( title, JpaObject.length_255B );
		
		if( ListTools.isNotEmpty( managers )) {
			UserManagerService userManagerService = new UserManagerService();
			for( String manager  : managers ) {
				if( manager.indexOf( "@I" ) > 0 ) {
					manager = userManagerService.getPersonNameWithIdentity( manager );
				}
				MessageConnector.send( MessageConnector.TYPE_TEAMWORK_TASKUPDATE, title, manager,	task );
			}
		}
	}
	
	public static void message_to_teamWorkOverTime( Task  task, Boolean overtime ) throws Exception {
		String title = null;
		if( overtime ) {
			title = "工作任务超时:" + adjustTitle( task );
		}else {
			title = "工作任务即将超时:" + adjustTitle( task );
		}
		title = StringTools.utf8SubString( title, JpaObject.length_255B  );
		MessageConnector.send( MessageConnector.TYPE_TEAMWORK_TASKOVERTIME, title, task.getExecutor(),	task );
	}
	
	public static void message_to_teamWorkDelete( Task  task ) throws Exception {
		String title = "工作任务删除:" + adjustTitle( task );
		title = StringTools.utf8SubString( title, JpaObject.length_255B  );
		MessageConnector.send( MessageConnector.TYPE_TEAMWORK_TASKDELETE, title, task.getExecutor(),	task );
	}
	
	public static void message_to_chat( Chat  chat ) throws Exception {
		String title = "工作交流:" + adjustTitle( chat );
		title = StringTools.utf8SubString( title, JpaObject.length_255B  );
		//查询该任务
		Task task = new TaskQueryService().get( chat.getTaskId() );
		if( task !=  null ) {
			if( ListTools.isNotEmpty( task.getParticipantList() )) {
				UserManagerService userManagerService = new UserManagerService();
				for( String participant : task.getParticipantList() ) {
					if( participant.indexOf( "@I" ) > 0 ) {
						participant = userManagerService.getPersonNameWithIdentity( participant );
					}
					MessageConnector.send( MessageConnector.TYPE_TEAMWORK_CHAT, title, participant,	chat );
				}
			}
		}else {
			MessageConnector.send( MessageConnector.TYPE_TEAMWORK_CHAT, title, chat.getTarget(),	chat );
		}		
	}

	private static String adjustTitle( Task o) {
		String title = "";
		if (StringUtils.isEmpty(o.getName())) {
			title = "无标题 " + DateTools.format(o.getCreateTime());
		} else {
			title = o.getName();
		}
		if( o.getStartTime() != null && o.getEndTime() != null ) {
			title += ", (" + DateOperation.getDate( o.getStartTime(), "yyyy-MM-dd") + " - " + DateOperation.getDate( o.getEndTime(), "yyyy-MM-dd") + ")";
		}
		return title;
	}
	
	private static String adjustTitle( Chat o) {
		String title = "";
		if (StringUtils.isEmpty(o.getContent() )) {
			title = "无标题 " + DateTools.format(o.getCreateTime());
		} else {
			title = o.getContent();
		}
		return title;
	}
}