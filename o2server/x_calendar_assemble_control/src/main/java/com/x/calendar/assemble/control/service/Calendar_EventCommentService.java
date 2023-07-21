package com.x.calendar.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.common.date.DateOperation;
import com.x.calendar.core.entity.Calendar_EventComment;


/**
 * 日历记录信息服务类
 * @author O2LEE
 *
 */
public class Calendar_EventCommentService {
	
	private DateOperation dateOperation = new DateOperation();

	/**
	 * 获取指定的备注信息对象列表
	 * @return
	 * @throws Exception
	 */
	public List<Calendar_EventComment> list(EntityManagerContainer emc, List<String> ids ) throws Exception {
		Business business =  new Business( emc );
		return business.calendar_EventCommentFactory().list(ids);
	}

	public Long countEventBundle( EntityManagerContainer emc, String commentId ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ) {
			return 0L;
		}
		Business business =  new Business( emc );
		return business.calendar_EventCommentFactory().countEventBundle(commentId);
	}

	public Long countRepeatMasterBundle( EntityManagerContainer emc, String commentId ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ) {
			return 0L;
		}
		Business business =  new Business( emc );
		return business.calendar_EventCommentFactory().countRepeatMasterBundle(commentId);
	}

	/**
	 * 查询检查时间早于now的maxCount条记录的ID列表
	 * @param emc
	 * @param now
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listNeedCheckCommnetIds(EntityManagerContainer emc, Date now, Integer maxCount) throws Exception {
		if( now == null ) {
			now = new Date();
		}
		if( maxCount == null ) {
			maxCount = 1000;
		}
		Business business =  new Business( emc );
		return business.calendar_EventCommentFactory().listNeedCheckCommnetIds( now, maxCount );
	}
}
