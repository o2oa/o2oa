package com.x.program.center;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.program.center.CodeTransferQueue.Message;
import com.x.program.center.core.entity.Code;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class CodeTransferQueue extends AbstractQueue<Message> {

	private static Logger logger = LoggerFactory.getLogger(CodeTransferQueue.class);

	private static final Integer TRANSFER_INTERVAL = 30;

	private static final Integer MAX_INTERVAL_COUNT = 5;

	/** 进行缓存主要目的是判断上次发送时间,不能在短时间内重复发送 */
	private static Ehcache cache = ApplicationCache.instance().getCache(Code.class, 1000, TRANSFER_INTERVAL,
			TRANSFER_INTERVAL);

	private static final String collect_code_transfer_address = "http://collect.xplatform.tech:20080/o2_collect_assemble/jaxrs/code/transfer";

	public void execute(Message message) {
		try {
			if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
				logger.warn("短信无法发送,系统没有启用O2云服务.");
				return;
			}
			Integer count = this.intervalCount(message);
			if (count > MAX_INTERVAL_COUNT) {
				logger.warn("短信发送请求被忽略.手机号: {}, 在{}秒内重复发送{}次.", message.getMobile(), TRANSFER_INTERVAL, count);
				return;
			}
			message.setName(Config.collect().getName());
			message.setPassword(Config.collect().getPassword());
			ActionResponse resp = ConnectionAction.put(collect_code_transfer_address, null, message);
			Wo wo = resp.getData(Wo.class);
			if (BooleanUtils.isNotTrue(wo.getValue())) {
				throw new Exception("transfer code message error:" + resp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class Wo extends WrapBoolean {

	}

	private Integer intervalCount(Message message) {
		String cacheKey = ApplicationCache.concreteCacheKey(message.getMobile());
		Element element = cache.get(cacheKey);
		Integer count = 1;
		if ((null != element) && (null != element.getObjectValue())) {
			count = (Integer) element.getObjectValue();
			count = count + 1;
		}
		cache.put(new Element(cacheKey, count));
		return count;
	}

	public class Message extends GsonPropertyObject {

		private String mobile;

		private String answer;

		private String name;

		private String password;

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getAnswer() {
			return answer;
		}

		public void setAnswer(String answer) {
			this.answer = answer;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

}
