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
			System.out.println("system query employee unique with person name  got an exception!");
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
				System.out.println("okr system get parameter named 'SMS_WSDL' got an exception!");
				e.printStackTrace();
			}
			
			message = "O2OA_OKR message: " + message;
			
			if(StringUtils.isNotEmpty(SMS_WSDL) && !"NONE".equalsIgnoreCase( SMS_WSDL )) {
				try {
					Object result = webservicesClient.jaxws( SMS_WSDL, "SENDSMS", unique, message);
					System.out.println("Message send successful! result:" + result.toString());
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				System.out.println("Message can not send, because sms wsdl not defind!");
			}
		}else {
			System.out.println("Message can not send, because employee unique is empty!");
		}
		return false;
	}
}
