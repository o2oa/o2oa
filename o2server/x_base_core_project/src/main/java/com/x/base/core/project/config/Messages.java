package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.ProcessPlatform.Press;
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

	@FieldDescribe("清理设置.")
	private Clean clean;

	public Clean clean() {
		return this.clean == null ? new Clean() : this.clean;
	}

	public static class Clean extends ConfigObject {

		public static Clean defaultInstance() {
			Clean o = new Clean();
			return o;
		}

		public final static Boolean DEFAULT_ENABLE = true;

		public final static Integer DEFAULT_KEEP = 7;

		public final static String DEFAULT_CRON = "30 30 6 * * ?";

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("消息保留天数")
		private Integer keep = DEFAULT_KEEP;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public void setCron(String cron) {
			this.cron = cron;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}
	}
}