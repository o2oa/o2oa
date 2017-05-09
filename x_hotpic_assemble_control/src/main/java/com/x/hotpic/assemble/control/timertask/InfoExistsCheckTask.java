package com.x.hotpic.assemble.control.timertask;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.clock.ClockScheduleTask;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;

/**
 * 对所有的信息对象进行检查 ，看看这些信息对象是否存在，如果不存在则进行删除
 * 
 * @author liyi_
 *
 */
public class InfoExistsCheckTask extends ClockScheduleTask {

	private Logger logger = LoggerFactory.getLogger(InfoExistsCheckTask.class);
	private HotPictureInfoServiceAdv hotPictureInfoServiceAdv = new HotPictureInfoServiceAdv();
	
	public InfoExistsCheckTask(Context context) {
		super(context);
	}
	
	/**
	 * 1、先查询出所有的信息列表，按照排序号和更新时间倒排序 2、删除50个以外的信息对象
	 * 3、检查至多50个信息对象，查询每一个对象信息是否仍然存在，如果不存在，则进行删除
	 */
	public void execute() {
		hotPictureInfoServiceAdv.documentExistsCheck();
		logger.info("Timertask Hotpicture InfoExistsCheckTask excute completed.");
	}
}