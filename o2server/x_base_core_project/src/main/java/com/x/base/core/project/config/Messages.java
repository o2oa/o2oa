package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import com.x.base.core.project.message.MessageConnector;

public class Messages extends ConcurrentSkipListMap<String, Message> {

	private static final long serialVersionUID = 1336172131736006743L;

	public static final Boolean DEFAULT_WEBSOCKETENABLE = true;

	public Messages() throws Exception {
		super();
	}

	public static Messages defaultInstance() throws Exception {
		Messages o = new Messages();

		/* 文件通知 */
		o.put(MessageConnector.TYPE_ATTACHMENT_SHARE,
				new Message(MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS,
						MessageConnector.CONSUME_DINGDING, MessageConnector.CONSUME_ZHENGWUDINGDING,
						MessageConnector.CONSUME_QIYEWEIXIN));

		o.put(MessageConnector.TYPE_ATTACHMENT_EDITOR,
				new Message(MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS,
						MessageConnector.CONSUME_DINGDING, MessageConnector.CONSUME_ZHENGWUDINGDING,
						MessageConnector.CONSUME_QIYEWEIXIN));

		o.put(MessageConnector.TYPE_ATTACHMENT_SHARECANCEL,
				new Message(MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS,
						MessageConnector.CONSUME_DINGDING, MessageConnector.CONSUME_ZHENGWUDINGDING,
						MessageConnector.CONSUME_QIYEWEIXIN));

		o.put(MessageConnector.TYPE_ATTACHMENT_EDITORCANCEL,
				new Message(MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS,
						MessageConnector.CONSUME_DINGDING, MessageConnector.CONSUME_ZHENGWUDINGDING,
						MessageConnector.CONSUME_QIYEWEIXIN));

		o.put(MessageConnector.TYPE_ATTACHMENT_EDITORMODIFY,
				new Message(MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS,
						MessageConnector.CONSUME_DINGDING, MessageConnector.CONSUME_ZHENGWUDINGDING,
						MessageConnector.CONSUME_QIYEWEIXIN));
		/* 文件通知结束 */

		/* 会议通知 */
		o.put(MessageConnector.TYPE_MEETING_INVITE,
				new Message(MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS,
						MessageConnector.CONSUME_DINGDING, MessageConnector.CONSUME_ZHENGWUDINGDING,
						MessageConnector.CONSUME_QIYEWEIXIN));

		o.put(MessageConnector.TYPE_MEETING_DELETE,
				new Message(MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS,
						MessageConnector.CONSUME_DINGDING, MessageConnector.CONSUME_ZHENGWUDINGDING,
						MessageConnector.CONSUME_QIYEWEIXIN));
		/* 会议通知结束 */

		/* 待办已办通知 */
		o.put(MessageConnector.TYPE_TASK_CREATE,
				new Message(MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS,
						MessageConnector.CONSUME_DINGDING, MessageConnector.CONSUME_ZHENGWUDINGDING,
						MessageConnector.CONSUME_QIYEWEIXIN));
		/* 待办提醒通知 */
		o.put(MessageConnector.TYPE_TASK_PRESS,
				new Message(MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS,
						MessageConnector.CONSUME_DINGDING, MessageConnector.CONSUME_ZHENGWUDINGDING,
						MessageConnector.CONSUME_QIYEWEIXIN));

		o.put(MessageConnector.TYPE_TASK_DELETE, new Message());

		o.put(MessageConnector.TYPE_TASKCOMPLETED_CREATE, new Message());

		o.put(MessageConnector.TYPE_TASKCOMPLETED_DELETE, new Message());
		/* 待办已办通知结束 */

		/* 待阅已阅通知 */
		o.put(MessageConnector.TYPE_READ_CREATE,
				new Message(MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS,
						MessageConnector.CONSUME_DINGDING, MessageConnector.CONSUME_ZHENGWUDINGDING,
						MessageConnector.CONSUME_QIYEWEIXIN));

		o.put(MessageConnector.TYPE_READ_DELETE, new Message());

		o.put(MessageConnector.TYPE_READCOMPLETED_CREATE, new Message());

		o.put(MessageConnector.TYPE_READCOMPLETED_DELETE, new Message());
		/* 待阅已阅通知结束 */

		/* 日程管理消息通知 */
		o.put(MessageConnector.TYPE_CALENDAR_ALARM,
				new Message(MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS,
						MessageConnector.CONSUME_DINGDING, MessageConnector.CONSUME_ZHENGWUDINGDING,
						MessageConnector.CONSUME_QIYEWEIXIN));

		/* 文档发布消息通知 */
//		o.put(MessageConnector.TYPE_CMS_PUBLISH,
//				new Message(MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS ));

		/* 社区新贴发布消息通知 */
		o.put(MessageConnector.TYPE_BBS_SUBJECTCREATE,
				new Message( MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS ));

		/* 社区新回复发布消息通知 */
		o.put(MessageConnector.TYPE_BBS_REPLYCREATE,
				new Message( MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS ));
		/* 脑图分享消息通知 */
		o.put(MessageConnector.TYPE_MIND_FILESHARE,
				new Message( MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS ));

		/* 脑图发送消息通知 */
		o.put(MessageConnector.TYPE_MIND_FILESEND,
				new Message( MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS ));

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