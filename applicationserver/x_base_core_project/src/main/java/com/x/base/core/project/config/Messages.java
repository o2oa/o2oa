package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import com.x.base.core.project.message.MessageConnector;

public class Messages extends ConcurrentSkipListMap<String, Message> {

	private static final long serialVersionUID = 1336172131736006743L;

	public Messages() throws Exception {
		super();
	}

	public static Messages defaultInstance() throws Exception {
		Messages o = new Messages();

		/* 文件通知 */
		o.put(MessageConnector.TYPE_ATTACHMENT_SHARE,
				new Message(MessageConnector.CONSUME_IM, MessageConnector.CONSUME_PMS));

		o.put(MessageConnector.TYPE_ATTACHMENT_EDITOR,
				new Message(MessageConnector.CONSUME_IM, MessageConnector.CONSUME_PMS));

		o.put(MessageConnector.TYPE_ATTACHMENT_SHARECANCEL,
				new Message(MessageConnector.CONSUME_IM, MessageConnector.CONSUME_PMS));

		o.put(MessageConnector.TYPE_ATTACHMENT_EDITORCANCEL,
				new Message(MessageConnector.CONSUME_IM, MessageConnector.CONSUME_PMS));

		o.put(MessageConnector.TYPE_ATTACHMENT_EDITORMODIFY,
				new Message(MessageConnector.CONSUME_IM, MessageConnector.CONSUME_PMS));
		/* 文件通知结束 */

		/* 会议通知 */
		o.put(MessageConnector.TYPE_MEETING_INVITE,
				new Message(MessageConnector.CONSUME_IM, MessageConnector.CONSUME_PMS));

		o.put(MessageConnector.TYPE_MEETING_DELETE,
				new Message(MessageConnector.CONSUME_IM, MessageConnector.CONSUME_PMS));
		/* 会议通知结束 */

		/* 待办已办通知 */
		o.put(MessageConnector.TYPE_TASK_CREATE,
				new Message(MessageConnector.CONSUME_IM, MessageConnector.CONSUME_PMS));

		o.put(MessageConnector.TYPE_TASK_DELETE, new Message());

		o.put(MessageConnector.TYPE_TASKCOMPLETED_CREATE, new Message());

		o.put(MessageConnector.TYPE_TASKCOMPLETED_DELETE, new Message());
		/* 待办已办通知结束 */

		/* 待阅已阅通知 */
		o.put(MessageConnector.TYPE_READ_CREATE,
				new Message(MessageConnector.CONSUME_IM, MessageConnector.CONSUME_PMS));

		o.put(MessageConnector.TYPE_READ_DELETE, new Message());

		o.put(MessageConnector.TYPE_READCOMPLETED_CREATE, new Message());

		o.put(MessageConnector.TYPE_READCOMPLETED_DELETE, new Message());
		/* 待阅已阅通知结束 */

		return o;
	}

	public List<String> getConsumers(String type) {
		Message o = this.get(type);
		if (o != null) {
			/* 这里必须复制内容,在消息处理中会对列表进行删除操作 */
			List<String> list = new ArrayList<>();
			list.addAll(o.getConsumers());
			return list;
		}
		return new ArrayList<String>();
	}
}