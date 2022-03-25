package com.x.mind.assemble.control;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.queue.MessageWo;

public class MessageFactory {

	private static Logger logger = LoggerFactory.getLogger( MessageFactory.class );

	/**
     * 文件发送通知
	 * @param persons
     * @param messageWo
     * @throws Exception
	 */
	public static void notify_forMindSend(List<String> persons, MessageWo messageWo ) throws Exception {
		if(ListTools.isNotEmpty( persons )){
			for( String person : persons ){
				notify_forMindSend( person, messageWo );
			}
		}
	}

	/**
     * 文件分享通知
	 * @param persons
     * @param messageWo
     * @throws Exception
	 */
	public static void notify_forMindShare(List<String> persons, MessageWo messageWo ) throws Exception {
		if(ListTools.isNotEmpty( persons )){
			for( String person : persons ){
				notify_forMindShare( person, messageWo );
			}

		}
	}

	/**
     * 文件发送通知
	 * @param person
     * @param messageWo
     * @throws Exception
	 */
	public static void notify_forMindSend( String person, MessageWo messageWo ) throws Exception {
		if(StringUtils.isNotEmpty( person ) ){
			String personName = StringUtils.isNotEmpty( messageWo.getCreatePerson() )?"":messageWo.getCreatePerson().split("@")[0];
			String title = personName + "向您发送了脑图文件:" + messageWo.getTitle();
			logger.debug("mind send notification for mind send [" +  messageWo.getTitle() + "], target：" + person );
			MessageConnector.send( MessageConnector.TYPE_MIND_FILESEND,  title, person, messageWo );
		}
	}

	/**
	 * 文件分享通知
	 * @param person
	 * @param messageWo
	 * @throws Exception
	 */
	public static void notify_forMindShare( String person, MessageWo messageWo ) throws Exception {
		if(StringUtils.isNotEmpty( person ) ){
			String personName = StringUtils.isNotEmpty( messageWo.getCreatePerson() )?"":messageWo.getCreatePerson().split("@")[0];
			String title = personName + "向您分享了脑图文件:" + messageWo.getTitle();
			logger.debug("mind send notification for mind share [" +  messageWo.getTitle() + "], target：" + person );
			MessageConnector.send( MessageConnector.TYPE_MIND_FILESHARE,  title, person, messageWo );
		}
	}
}
