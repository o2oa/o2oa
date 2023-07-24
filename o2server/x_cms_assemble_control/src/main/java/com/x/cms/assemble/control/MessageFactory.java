package com.x.cms.assemble.control;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.cms.assemble.control.queue.QueueSendDocumentNotify.MessageWo;

/**
 * 消息发送
 * @author sword
 */
public class MessageFactory {

	private static Logger logger = LoggerFactory.getLogger( MessageFactory.class );

	public static void cms_publish( String person, MessageWo messageWo ) {
		String title = "新信息发布:" + messageWo.getTitle();
		logger.debug("cms send notification:[" + title + "] for target person：" + person);
		MessageConnector.send(MessageConnector.TYPE_CMS_PUBLISH, title, person, messageWo);
	}

	public static void cms_publish_creator(MessageWo messageWo) {
		String title = "新信息发布:" + messageWo.getTitle();
		logger.debug("cms send notification:[" + title + "] for target person：" + messageWo.getCreatorPerson());
		MessageConnector.send(MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR, title, messageWo.getCreatorPerson(), messageWo);
	}
}
