package x.collaboration.service.message;

import com.google.gson.JsonElement;
import com.x.base.core.project.Context;
import com.x.base.core.project.x_collaboration_assemble_websocket;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;

public class SmsQueue extends AbstractQueue<JsonElement> {

	private Context context;

	SmsQueue(Context context) {
		this.context = context;
	}

	private static Logger logger = LoggerFactory.getLogger(SmsQueue.class);

    /**
     * 调用服务将短信信息通过x_collaboration_assemble_websocket发送到真实的企业短信提醒中心
     * 企业内部的短信提醒中心一般都是Web服务接口：目前有Domino的短信中心和Java的短信提醒中心，配置需要读取sms.json
     *
     * @param jsonElement
     * @throws Exception
     */
	@Override
	protected void execute( JsonElement jsonElement ) throws Exception {
		logger.debug("send message:{}.", jsonElement);
		context.applications().postQuery(x_collaboration_assemble_websocket.class, "sms", jsonElement);

	}
}