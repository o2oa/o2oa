package com.x.bbs.assemble.control;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.queue.MessageWo;

public class MessageFactory {

	private static Logger logger = LoggerFactory.getLogger( MessageFactory.class );

	/**
	 * 新的回复，通知相关管理员
	 * @param persons
	 * @param messageWo
	 * @throws Exception
	 */
	public static void notifyManager_forNewReply(List<String> persons, MessageWo messageWo ) throws Exception {
		if(ListTools.isNotEmpty( persons )){
			for( String person : persons ){
				notifyManager_forNewReply( person, messageWo );
			}
		}
	}

	/**
	 * 新的回复，通知主题创建者
	 * @param persons
	 * @param messageWo
	 * @throws Exception
	 */
	public static void notifySubjectCreator_forNewReply(List<String> persons, MessageWo messageWo ) throws Exception {
		if(ListTools.isNotEmpty( persons )){
			for( String person : persons ){
				notifySubjectCreator_forNewReply( person, messageWo );
			}

		}
	}

	/**
	 * 新的回复，通知相关管理员
	 * @param person
	 * @param messageWo
	 * @throws Exception
	 */
	public static void notifyManager_forNewReply( String person, MessageWo messageWo ) throws Exception {
		if(StringUtils.isNotEmpty( person ) ){
			String personName = StringUtils.isNotEmpty( messageWo.getCreatePerson() )?"":messageWo.getCreatePerson().split("@")[0];
			String title = personName + "发表了新的回复:" + messageWo.getTitle();
			logger.debug("bbs send notification:new reply for subject [" +  messageWo.getTitle() + "], target：" + person );
			MessageConnector.send( MessageConnector.TYPE_BBS_REPLYCREATE,  title, person, messageWo );
		}
	}

	/**
	 * 新的回复，通知主题创建者
	 * @param person
	 * @param messageWo
	 * @throws Exception
	 */
	public static void notifySubjectCreator_forNewReply( String person, MessageWo messageWo ) throws Exception {
		if(StringUtils.isNotEmpty( person ) ){
			String personName = StringUtils.isNotEmpty( messageWo.getCreatePerson() )?"":messageWo.getCreatePerson().split("@")[0];
			String title = personName + "回复了您发表的主题:" + messageWo.getTitle();
			logger.debug("bbs send notification:new reply for subject [" +  messageWo.getTitle() + "], target：" + person );
			MessageConnector.send( MessageConnector.TYPE_BBS_REPLYCREATE,  title, person, messageWo );
		}
	}

	/**
	 * 新的主题发表，通知相关管理员
	 * @param persons
	 * @param messageWo
	 * @throws Exception
	 */
	public static void notifyManager_forNewSubject(List<String> persons, MessageWo messageWo ) throws Exception {
		if(ListTools.isNotEmpty( persons )){
			for( String person : persons ){
				notifyManager_forNewSubject( person, messageWo );
			}
		}
	}

	/**
	 * 新的主题发表，通知相关管理员
	 * @param person
	 * @param messageWo
	 * @throws Exception
	 */
	public static void notifyManager_forNewSubject( String person, MessageWo messageWo ) throws Exception {
		if(StringUtils.isNotEmpty( person ) ){
			String personName = StringUtils.isNotEmpty( messageWo.getCreatePerson() )?"":messageWo.getCreatePerson().split("@")[0];
			String title = personName + "发表了新的主题:" + messageWo.getTitle();
			logger.debug("bbs send notification:new reply for subject [" +  messageWo.getTitle() + "], target：" + person );
			MessageConnector.send( MessageConnector.TYPE_BBS_SUBJECTCREATE,  title, person, messageWo );
		}
	}
}
