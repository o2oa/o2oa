package com.x.mind.assemble.control.queue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.MessageFactory;
import com.x.mind.assemble.control.service.UserManagerService;
import com.x.mind.entity.MindBaseInfo;

/**
 * 脑图分享之后，向所有的被分享者推送消息通知
 */
public class QueueShareNotify extends AbstractQueue<MindBaseInfo> {
	
	private static  Logger logger = LoggerFactory.getLogger( QueueShareNotify.class );

	public void execute( MindBaseInfo mindBaseInfo) throws Exception {
		if( mindBaseInfo == null ) {
			logger.warn("can not send publish notify for mind, because mindBaseInfo is NULL!" );
			return;
		}
		logger.debug("system try to send notify for mind:" + mindBaseInfo.getName() );

		List<String> persons = caculatePerson( mindBaseInfo );
		MessageWo wo = composeMessageWo( mindBaseInfo );
		MessageFactory.notify_forMindShare(persons, wo);
		logger.debug("system send notify for new replyInfo completed!" );
	}

	/**
	 * 根据指定的推送配置计算需要接收通知的人员列表
	 * @param mindBaseInfo
	 * @return
	 */
	private List<String> caculatePerson( MindBaseInfo mindBaseInfo ) throws Exception {
		UserManagerService userManagerService = new UserManagerService();
		List<String> sendPersons = new ArrayList<>();
		String[] sendPersonTypeConfig = null;

		List<String> groups = mindBaseInfo.getShareGroupList();
		List<String> units = mindBaseInfo.getShareUnitList();
		List<String> persons = mindBaseInfo.getSharePersonList();

		//回复消息通知类别：一共3位，第1位是否通知分区管理员ForumManagerList，第2位是否通知版主ModeratorNames，第3位是否通知发贴人，0-不通知|1-通知
		if( ListTools.isNotEmpty( groups )) {
			List<String> pl_ = userManagerService.listPersonListWithGroups( groups );
			if( ListTools.isNotEmpty( pl_ )) {
				sendPersons.addAll( pl_ );
			}
		}
		if( ListTools.isNotEmpty( units )) {
			List<String> pl_ = userManagerService.listPersonListWithUnits( units );
			if( ListTools.isNotEmpty( pl_ )) {
				sendPersons.addAll( pl_ );
			}
		}
		if( ListTools.isNotEmpty( persons )) {
			sendPersons.addAll( persons );
		}
		HashSet<String> set = new HashSet<String>( sendPersons );
		sendPersons.clear();
		sendPersons.addAll(set);
		return sendPersons;
	}

	private MessageWo composeMessageWo( MindBaseInfo mindBaseInfo ) {
		MessageWo messageWo = null;
		if( mindBaseInfo != null ){
			messageWo = new MessageWo();
			messageWo.setId( mindBaseInfo.getFolderId() );
			messageWo.setTitle( mindBaseInfo.getName() );
			messageWo.setCreatePerson( mindBaseInfo.getCreator() );
			messageWo.setCreateTime( mindBaseInfo.getCreateTime() );
		}
		return messageWo;
	}
}
