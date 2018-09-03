package com.o2platform.website.service;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.o2platform.website.dao.WebSiteDownloadRecordDao;
import com.o2platform.website.entity.WebSiteDownloadRecord;

@Service("webSiteDownloadRecordService")
public class WebSiteDownloadRecordServiceImpl implements WebSiteDownloadRecordServiceI { 

	public static Logger logger = Logger.getLogger(WebSiteDownloadRecordServiceImpl.class);
	
	@Autowired
	private WebSiteDownloadRecordDao dao;

	@Override
	public boolean add( WebSiteDownloadRecord entity ){
		try {
			dao.add( entity );
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
