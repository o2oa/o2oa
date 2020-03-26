package com.x.calendar.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.calendar.core.entity.Calendar_EventComment;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 日程日历备注信息服务类
 * @author O2LEE
 *
 */
public class Calendar_EventCommentQueryService {
	private Calendar_EventCommentService calendar_EventCommentService = new Calendar_EventCommentService();

	public List<Calendar_EventComment> list(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventCommentService.list(emc, ids);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据ID获取指定日历记录备注信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar_EventComment get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, Calendar_EventComment.class);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 查询在事件信息中，commentId被引用的次数
	 * @param commentId
	 * @return
	 * @throws Exception
	 */
	public Long countEventBundle( String commentId ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ) {
			return 0L;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventCommentService.countEventBundle(emc, commentId);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 查询在事件循环主体信息中，commentId被引用的次数
	 * @param commentId
	 * @return
	 * @throws Exception
	 */
	public Long countRepeatMasterBundle( String commentId ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ) {
			return 0L;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventCommentService.countRepeatMasterBundle(emc, commentId);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 查询更新时间早于传入时间的前N条信息ID
	 * @param now
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listNeedCheckCommnetIds( Date now, Integer maxCount ) throws Exception {
		if( now == null ) {
			now = new Date();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventCommentService.listNeedCheckCommnetIds( emc, now, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
