package com.x.cms.assemble.control;

import com.x.base.core.project.message.MessageConnector;
import com.x.cms.assemble.control.queue.QueueSendDocumentNotify.MessageWo;

public class MessageFactory {

	public static void cms_publish(String person, MessageWo messageWo ) throws Exception {
		String title = "新信息发布:" + messageWo.getTitle();
		System.out.println("CMS推送消息:" +  title + ", 目标用户：" + person );
		MessageConnector.send(MessageConnector.TYPE_CMS_PUBLISH,  title, person, messageWo);
	}
}
