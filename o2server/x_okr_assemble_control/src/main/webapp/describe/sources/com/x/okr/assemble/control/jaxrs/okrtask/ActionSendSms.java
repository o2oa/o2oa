package com.x.okr.assemble.control.jaxrs.okrtask;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.dataadapter.webservice.sms.SmsMessageOperator;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkBaseInfoProcess;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkNotExists;
import com.x.okr.entity.OkrWorkBaseInfo;

/**
 * 向指定的人员发送指定的短消息内容
 * @author O2LEE
 *
 */
public class ActionSendSms extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSendSms.class);

	protected ActionResult<WrapOutBoolean> execute(HttpServletRequest request, EffectivePerson effectivePerson, String workId, JsonElement jsonElement ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		WrapOutBoolean wrapOutBoolean = new WrapOutBoolean();
		Wi wrapIn = null;
		Boolean check = true;

		if( workId == null || workId.isEmpty() ){
			check = false;
			Exception exception = new ExceptionWorkIdEmpty();
			result.error( exception );
		}
		
		if( check ){
			try {
				wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWrapInConvert(e, jsonElement);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		
		if( check ){
			try{
				okrWorkBaseInfo = okrWorkBaseInfoService.get( workId );
				if( okrWorkBaseInfo == null ){
					check = false;
					Exception exception = new ExceptionWorkNotExists( workId );
					result.error( exception );
				}
			}catch(Exception e){
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "查询指定ID的具体工作信息时发生异常。ID：" + workId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			String person = wrapIn.getPerson();
			String identity = wrapIn.getIdentity();
			String message = wrapIn.getMessage();
			if( StringUtils.isNotEmpty( person )) {
				SmsMessageOperator.sendWithPersonName(person, message);
				wrapOutBoolean.setValue( true );
				result.setData( wrapOutBoolean );
			}
			if( StringUtils.isNotEmpty( identity )) {
				person = userManagerService.getPersonNameByIdentity(identity);
				if( StringUtils.isNotEmpty( person )) {
					SmsMessageOperator.sendWithPersonName( person, message );
					wrapOutBoolean.setValue( true );
					result.setData( wrapOutBoolean );
				}
			}
		}
		return result;
	}
	
	public static class Wi {

		@FieldDescribe("需要发送短信的人员姓名.")
		private String person = null;
		
		@FieldDescribe("需要发送短信的人员身份.")
		private String identity = null;

		@FieldDescribe("短信内容.")
		private String message = null;

		public String getPerson() {
			return person;
		}

		public String getMessage() {
			return message;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
	}
}