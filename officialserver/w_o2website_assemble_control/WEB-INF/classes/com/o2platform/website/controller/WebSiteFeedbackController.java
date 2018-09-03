package com.o2platform.website.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.o2platform.common.ip.IPHelper;
import com.o2platform.website.entity.WebSiteFeedbackContent;
import com.o2platform.website.entity.WebSiteFeedbackRecord;
import com.o2platform.website.service.WebSiteFeedbackContentServiceI;
import com.o2platform.website.service.WebSiteFeedbackRecordServiceI;


@Controller
@RequestMapping("WebSiteFeedback")
public class WebSiteFeedbackController extends BaseController {
    public static Logger logger = Logger.getLogger( WebSiteFeedbackController.class );
    
    private String clazzName = new Object() {
    	public String getClassName() {
    		String clazzName = this.getClass().getName();
    		return clazzName.substring(0, clazzName.lastIndexOf('$'));
    	}
    }.getClassName();
    
    @Autowired
	private WebSiteFeedbackRecordServiceI webSiteFeedbackRecordService;
    
    @Autowired
	private WebSiteFeedbackContentServiceI webSiteFeedbackContentService;
	
	@RequestMapping( value = "submit" )
	public Object submit( String title, String content, String telephone, String email, HttpServletRequest req, HttpServletResponse resp ) {
		Map<String, Object> map = new HashMap<String, Object>();
		WebSiteFeedbackRecord feedbackRecord = new WebSiteFeedbackRecord();
		WebSiteFeedbackContent feedbackContent = new WebSiteFeedbackContent();
		feedbackRecord.setTitle( title );
		feedbackRecord.setTelephone( telephone );
		feedbackRecord.setEmail( email );
		feedbackRecord.setHostIp( IPHelper.getIpAddr( req ) );
		
		feedbackContent.setContent( content );
		
		try{
			webSiteFeedbackRecordService.add( feedbackRecord );
			webSiteFeedbackContentService.add( feedbackContent );
		}catch(Exception e){
			logger.error("保存用户反馈信息表信息失败", e);
		}
		return map;
	}
}
