package com.o2platform.website.service;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.o2platform.website.dao.WebSiteFeedbackRecordDao;
import com.o2platform.website.entity.WebSiteFeedbackRecord;

@Service("webSiteFeedbackRecordService")
public class WebSiteFeedbackRecordServiceImpl implements WebSiteFeedbackRecordServiceI { 

	public static Logger logger = Logger.getLogger(WebSiteFeedbackRecordServiceImpl.class);
	
	@Autowired
	private WebSiteFeedbackRecordDao dao;

	@Override
	public boolean add( WebSiteFeedbackRecord entity ){
		try {
			dao.add( entity );
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
