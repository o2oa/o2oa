package com.x.calendar.assemble.control.schedule;

import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.service.Calendar_EventCommentQueryService;
import com.x.calendar.core.entity.Calendar_EventComment;

/**
 * 定期检查是否存在没有被任何event或者event_master引用的EventComment记录，如果有，则需要进行删除
 * 
 * @author O2LEE
 *
 */
public class CheckEventComment extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(CheckEventComment.class);
	protected Calendar_EventCommentQueryService calendar_EventCommentQueryService = new Calendar_EventCommentQueryService();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		Date now = new Date();
		List<String> ids = calendar_EventCommentQueryService.listNeedCheckCommnetIds( now, 500 );
		Integer maxWhileTimes = 10;
		while( ListTools.isNotEmpty( ids )){
			if( --maxWhileTimes < 0 ){
				break;
			}
			ids.forEach( commentId->{
				try {
					if( !check( commentId, now )){
						removeEventComment( commentId );
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			ids = calendar_EventCommentQueryService.listNeedCheckCommnetIds( now, 500 );
		}
		logger.info("The trigger for calendar alarm execute completed." + new Date());
	}

	/**
	 * 根据commentId，删除指定的备注信息
	 * @param commentId
	 */
	private void removeEventComment(String commentId ) throws Exception {
		Boolean quote = false;
		if( calendar_EventCommentQueryService.countEventBundle( commentId ) > 0 ){
			quote = true;
		}
		if( calendar_EventCommentQueryService.countRepeatMasterBundle( commentId ) > 0 ){
			quote = true;
		}
		if( !quote ){
			//没有引用，直接
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Calendar_EventComment entity = emc.find( commentId, Calendar_EventComment.class );
				if( entity != null ){
					emc.beginTransaction( Calendar_EventComment.class );
					emc.remove( entity, CheckRemoveType.all );
					emc.commit();
				}
			}
		}
	}

	/**
	 * 检查该ID是否有事件引用到
	 * 
	 * @param commentId
	 * @return
	 * @throws Exception
	 */
	private boolean check( String commentId, Date now ) throws Exception {
		if( calendar_EventCommentQueryService.countEventBundle( commentId ) > 0 ){
			return true;
		}
		if( calendar_EventCommentQueryService.countRepeatMasterBundle( commentId ) > 0 ){
			return true;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Calendar_EventComment entity = emc.find( commentId, Calendar_EventComment.class );
			if( entity != null ){
				emc.beginTransaction( Calendar_EventComment.class );
				entity.setCheckTime( new Date() );
				emc.check( entity, CheckPersistType.all );
				emc.commit();
			}
		}
		return false;
	}

}
