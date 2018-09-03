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
import com.o2platform.website.entity.WebSiteVisitRecord;
import com.o2platform.website.service.WebSiteVisitRecordServiceI;


@Controller
@RequestMapping("visit")
public class WebSiteVisitRecordController extends BaseController {
    public static Logger logger = Logger.getLogger( WebSiteVisitRecordController.class );
    
    private String clazzName = new Object() {
    	public String getClassName() {
    		String clazzName = this.getClass().getName();
    		return clazzName.substring(0, clazzName.lastIndexOf('$'));
    	}
    }.getClassName();
    
    @Autowired
	private WebSiteVisitRecordServiceI webSiteVisitRecordService;	
	
	@RequestMapping( value = "index" )
	public Object index( HttpServletRequest req, HttpServletResponse resp ) {
		Map<String, Object> map = new HashMap<String, Object>();
		WebSiteVisitRecord entity = new WebSiteVisitRecord();
		entity.setPageName( "o2.html" );
		entity.setHostIp( IPHelper.getIpAddr( req ) );
		try{
			webSiteVisitRecordService.add( entity );
		}catch(Exception e){
			logger.error("保存访问日志信息表信息失败", e);
		}
		return map;
	}
	
	@RequestMapping( value = "download" )
	public Object download( HttpServletRequest req, HttpServletResponse resp ) {
		Map<String, Object> map = new HashMap<String, Object>();
		WebSiteVisitRecord entity = new WebSiteVisitRecord();
		entity.setPageName( "download.html" );
		entity.setHostIp( IPHelper.getIpAddr( req ) );
		try{
			webSiteVisitRecordService.add( entity );
		}catch(Exception e){
			logger.error("保存访问日志信息表信息失败", e);
		}
		return map;	
	}
	
	@RequestMapping( value = "feedback" )
	public Object feedback( HttpServletRequest req, HttpServletResponse resp ) {
		Map<String, Object> map = new HashMap<String, Object>();
		WebSiteVisitRecord entity = new WebSiteVisitRecord();
		entity.setPageName( "feedback.html" );
		entity.setHostIp( IPHelper.getIpAddr( req ) );
		try{
			webSiteVisitRecordService.add( entity );
		}catch(Exception e){
			logger.error("保存访问日志信息表信息失败", e);
		}
		return map;		
	}
	
	@RequestMapping( value = "trial" )
	public Object trial( HttpServletRequest req, HttpServletResponse resp ) {
		Map<String, Object> map = new HashMap<String, Object>();
		WebSiteVisitRecord entity = new WebSiteVisitRecord();
		entity.setPageName( "trial" );
		entity.setHostIp( IPHelper.getIpAddr( req ) );
		entity.setDescription( "http://demo.xplatform.tech" );
		try{
			webSiteVisitRecordService.add( entity );
		}catch(Exception e){
			logger.error("保存访问日志信息表信息失败", e);
		}
		return map;		
	}
	
	@RequestMapping( value = "forum" )
	public Object forum( HttpServletRequest req, HttpServletResponse resp ) {
		Map<String, Object> map = new HashMap<String, Object>();
		WebSiteVisitRecord entity = new WebSiteVisitRecord();
		entity.setPageName( "forum" );
		entity.setHostIp( IPHelper.getIpAddr( req ) );
		entity.setDescription( "http://www.o2server.io:9081/x_desktop/forum.html" );
		try{
			webSiteVisitRecordService.add( entity );
		}catch(Exception e){
			logger.error("保存访问日志信息表信息失败", e);
		}
		return map;		
	}
}
