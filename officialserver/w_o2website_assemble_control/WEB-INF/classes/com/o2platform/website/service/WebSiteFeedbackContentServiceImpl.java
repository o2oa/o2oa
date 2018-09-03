package com.o2platform.website.service;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.o2platform.website.dao.WebSiteFeedbackContentDao;
import com.o2platform.website.entity.WebSiteFeedbackContent;

/**
 * SYS_CONFIG[系统配置信息表]表操作Serfice实现类

 * 类   名：Sys_configServiceImpl<br/>
 * 表   名：SYS_CONFIGSERVICEIMPL<br/>
 * 注   释：<br/>
 * 作   者：GREENLEAF<br/>
 * 单   位：浙江兰德纵横网络技术有限公司<br/>
 * 日   期：2014-09-03 20:36:57
**/
@Service( "sys_configService" )
public class WebSiteFeedbackContentServiceImpl implements WebSiteFeedbackContentServiceI { 

	public static Logger logger = Logger.getLogger(WebSiteFeedbackContentServiceImpl.class);

	@Autowired
	private WebSiteFeedbackContentDao dao;
	
	/**
	 * 新增数据方法实现
	**/
	@Override
	public boolean add( WebSiteFeedbackContent entity ){
		try {
			dao.add( entity );
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
