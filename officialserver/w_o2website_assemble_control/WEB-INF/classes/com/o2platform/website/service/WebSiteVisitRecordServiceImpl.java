package com.o2platform.website.service;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.o2platform.website.dao.WebSiteVisitRecordDao;
import com.o2platform.website.entity.WebSiteVisitRecord;

@Service("webSiteVisitRecordService")
public class WebSiteVisitRecordServiceImpl implements WebSiteVisitRecordServiceI { 

	public static Logger logger = Logger.getLogger( WebSiteVisitRecordServiceImpl.class );
	
	@Autowired
	private WebSiteVisitRecordDao dao;

	@Override
	public boolean add( WebSiteVisitRecord entity ){
		try {
			dao.add( entity );
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
