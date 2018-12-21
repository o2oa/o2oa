package com.x.collaboration.assemble.websocket.jaxrs.sms;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.collaboration.assemble.websocket.ThisApplication;
import com.x.collaboration.core.entity.SMSMessage;
import com.x.organization.core.express.Organization;

public class ActionSendSMS extends ActionBase {

    private static  Logger logger = LoggerFactory.getLogger(ActionSendSMS.class);

	private Organization org = new Organization( ThisApplication.context() );

    /**
     * 发送短信到
     * @param jsonElement
     * @return
     * @throws Exception
     */
	protected ActionResult<WrapOutBoolean> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
        ActionResult<WrapOutBoolean> result = new ActionResult<>();
        Wi wi = null;
        Boolean check = true;

        try {
            wi = this.convertToWrapIn( jsonElement, Wi.class );
        } catch (Exception e ) {
            check = false;
            Exception exception = new ExceptionSendSMS( e, wi.getPerson() );
            result.error( exception );
            logger.error( e, effectivePerson, request, null);
        }

        if( check ){
            //获取企业短信中心的配置，然后调用服务接入短信
            System.out.println("系统正在发送短信到企业短信中心，收信人：" + wi.getPerson() + ", 手机号：" + wi.getMobile() );
            result.setData( null );
        }

        return result;
	}

    public static class Wi extends SMSMessage {
    }
}