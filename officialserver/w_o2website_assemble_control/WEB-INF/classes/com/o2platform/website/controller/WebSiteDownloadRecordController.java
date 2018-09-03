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
import com.o2platform.website.entity.WebSiteDownloadRecord;
import com.o2platform.website.service.WebSiteDownloadRecordServiceI;


@Controller
@RequestMapping("webSiteDownloadRecord")
public class WebSiteDownloadRecordController extends BaseController {
    public static Logger logger = Logger.getLogger( WebSiteDownloadRecordController.class );
    
    private String clazzName = new Object() {
    	public String getClassName() {
    		String clazzName = this.getClass().getName();
    		return clazzName.substring(0, clazzName.lastIndexOf('$'));
    	}
    }.getClassName();
    
    @Autowired
	private WebSiteDownloadRecordServiceI webSiteDownloadRecordService;	
	
	@RequestMapping( value = "download" )
	public Object download( String fileName, HttpServletRequest req, HttpServletResponse resp ) {
		Map<String, Object> map = new HashMap<String, Object>();
		WebSiteDownloadRecord entity = new WebSiteDownloadRecord();
		entity.setFileName( "fileName" );
		entity.setHostIp( IPHelper.getIpAddr( req ) );
		try{
			webSiteDownloadRecordService.add( entity );
		}catch(Exception e){
			logger.error("保存文件下载日志信息表信息失败", e);
		}
		return map;
	}
}
