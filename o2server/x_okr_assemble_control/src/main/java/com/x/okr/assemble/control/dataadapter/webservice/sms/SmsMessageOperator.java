package com.x.okr.assemble.control.dataadapter.webservice.sms;

import org.apache.commons.lang3.StringUtils;

import com.x.okr.assemble.control.dataadapter.webservice.WebservicesClient;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrUserManagerService;

public class SmsMessageOperator {
	
	public static  OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	
	public static OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	
	public static Boolean sendWithPersonName( String personName, String message ) {
		String unique = null;
		try {
			unique = okrUserManagerService.getUniqueWithPerson(personName);
			sendWithUnique(unique, message);
		} catch (Exception e1) {
			System.out.println(">>>>>>>>>>>根据员工姓名查询唯一编码发生异常！");
			e1.printStackTrace();
			return false;
		}
		return false;
	}
	
	public static Boolean sendWithUnique( String unique, String message ) {
		if( StringUtils.isNotEmpty( unique )) {
			String SMS_WSDL = null;
			WebservicesClient webservicesClient = new WebservicesClient();
			try {
				SMS_WSDL = okrConfigSystemService.getValueWithConfigCode("SMS_WSDL");
			} catch (Exception e) {
				System.out.println("获取SMS_WSDL参数发生异常");
				e.printStackTrace();
			}
			
			message = "执行力管控系统：" + message;
			
			if(StringUtils.isNotEmpty(SMS_WSDL) && !"NONE".equalsIgnoreCase( SMS_WSDL )) {
				try {
					Object result = webservicesClient.jaxws( SMS_WSDL, "SENDSMS", unique, message);
					System.out.println(">>>>>>>>>>>短信发送接口调用成功！result= " + result.toString());
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				System.out.println(">>>>>>>>>>>未发送短信！WSDL未定义！");
			}
		}else {
			System.out.println(">>>>>>>>>>>未发送短信！员工unique为空！");
		}
		return false;
	}
}
