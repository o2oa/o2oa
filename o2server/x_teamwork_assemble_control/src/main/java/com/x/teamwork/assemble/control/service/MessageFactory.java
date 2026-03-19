package com.x.teamwork.assemble.control.service;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.teamwork.core.entity.Chat;
import com.x.teamwork.core.entity.ProjectStatusEnum;
import com.x.teamwork.core.entity.Task;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author sword
 */
public class MessageFactory {

	public static void message_to_teamWorkCreate(Task  task) throws Exception {
		String title = "您有新工作任务";
		title = StringTools.utf8SubString( title, JpaObject.length_255B );
		MessageConnector.send( MessageConnector.TYPE_TEAMWORK_TASKCREATE, title, task.getExecutor(), task);
	}

	public static void message_to_teamWorkUpdate( Task oldTask, Task newTask ) throws Exception {
		Set<String> set = new HashSet<>(newTask.getParticipantList());
		set.add(oldTask.getExecutor());
		set.add(newTask.getExecutor());
		if(!oldTask.getExecutor().equals(newTask.getExecutor())){
			String title = "工作任务负责人变更为：" + OrganizationDefinition.name(newTask.getExecutor());
			for(String person : set){
				MessageConnector.send( MessageConnector.TYPE_TEAMWORK_TASKUPDATE, title, person, newTask );
			}
		}

		if(!oldTask.getWorkStatus().equals(newTask.getWorkStatus())){
			String title = "工作任务状态变更为:" + ProjectStatusEnum.getNameByValue(newTask.getWorkStatus());
			for(String person : set){
				MessageConnector.send( MessageConnector.TYPE_TEAMWORK_TASKUPDATE, title, person, newTask );
			}
		}
	}

	public static void message_to_teamWorkUpdateParticipants( Task  task, List<String> participants ) throws Exception {
		String title = "工作任务参与者变更:" + adjustTitle( task );
		title = StringTools.utf8SubString( title, JpaObject.length_255B );
		if( ListTools.isNotEmpty( participants )) {
			UserManagerService userManagerService = new UserManagerService();
			for( String participant  : participants ) {
				if( participant.indexOf( "@I" ) > 0 ) {
					participant = userManagerService.getPersonNameWithIdentity( participant );
				}
				MessageConnector.send( MessageConnector.TYPE_TEAMWORK_TASKUPDATE, title, participant,	task );
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
		String title = "工作任务交流:" + adjustTitle( chat );
		title = StringTools.utf8SubString( title, JpaObject.length_255B  );
		//查询该任务
		Task task = new TaskQueryService().get( chat.getTaskId() );
		if( task !=  null ) {
			Set<String> set = new HashSet<>(task.getParticipantList());
			set.add(task.getExecutor());
			for( String participant : set ) {
				MessageConnector.send( MessageConnector.TYPE_TEAMWORK_CHAT, title, participant,	chat );
			}
		}else {
			MessageConnector.send( MessageConnector.TYPE_TEAMWORK_CHAT, title, chat.getTarget(),	chat );
		}
	}

	private static String adjustTitle( Task o) {
		return o.getName();
	}

	private static String adjustTitle( Chat o) {
		String title = o.getContent();
		if (StringUtils.isBlank(title)) {
			title = "无标题 " + DateTools.format(o.getCreateTime());
		}
		return title;
	}

}
