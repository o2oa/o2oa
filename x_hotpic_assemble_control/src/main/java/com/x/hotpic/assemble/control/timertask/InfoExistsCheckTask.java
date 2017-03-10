package com.x.hotpic.assemble.control.timertask;

import java.util.List;
import java.util.TimerTask;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.hotpic.assemble.control.jaxrs.hotpic.WrapOutHotPictureInfo;
import com.x.hotpic.assemble.control.service.BbsInfoChecker;
import com.x.hotpic.assemble.control.service.CmsInfoChecker;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.assemble.control.service.inf.InfoCheckerInf;
import com.x.hotpic.entity.HotPictureInfo;

/**
 * 对所有的信息对象进行检查 ，看看这些信息对象是否存在，如果不存在则进行删除
 * 
 * @author liyi_
 *
 */
public class InfoExistsCheckTask extends TimerTask {

	private Logger logger = LoggerFactory.getLogger(InfoExistsCheckTask.class);
	private HotPictureInfoServiceAdv hotPictureInfoServiceAdv = new HotPictureInfoServiceAdv();
	private BeanCopyTools<HotPictureInfo, WrapOutHotPictureInfo> wrapout_copier = BeanCopyToolsBuilder
			.create(HotPictureInfo.class, WrapOutHotPictureInfo.class, null, WrapOutHotPictureInfo.Excludes);

	/**
	 * 1、先查询出所有的信息列表，按照排序号和更新时间倒排序 2、删除50个以外的信息对象
	 * 3、检查至多50个信息对象，查询每一个对象信息是否仍然存在，如果不存在，则进行删除
	 */
	public void run() {
		List<HotPictureInfo> allHotPictureInfoList = null;
		List<WrapOutHotPictureInfo> allWrapOutallHotPictureInfoList = null;
		// 1、先查询出所有的信息列表，按照排序号和更新时间倒排序
		try {
			allHotPictureInfoList = hotPictureInfoServiceAdv.listAll();
		} catch (Exception e) {
			logger.warn("system list all hot picture info got an exception.");
			logger.error(e);
		}

		if (allHotPictureInfoList != null && !allHotPictureInfoList.isEmpty()) {
			try {
				allWrapOutallHotPictureInfoList = wrapout_copier.copy(allHotPictureInfoList);
			} catch (Exception e) {
				logger.warn("system copy hot picture info list to wrap out list got an exception.");
				logger.error(e);
			}
		}

		if (allWrapOutallHotPictureInfoList != null && !allWrapOutallHotPictureInfoList.isEmpty()) {
			try {
				SortTools.desc(allWrapOutallHotPictureInfoList, "updateTime");
			} catch (Exception e) {
				logger.warn("system sort picture info list got an exception.");
				logger.error(e);
			}
		}

		if (allWrapOutallHotPictureInfoList != null && !allWrapOutallHotPictureInfoList.isEmpty()) {
			for (int i = 0; i < allWrapOutallHotPictureInfoList.size(); i++) {
				if (i < 20) {
					// 检查信息是否存在
					try {
						checkInfoExists(allWrapOutallHotPictureInfoList.get(i));
					} catch (Exception e) {
						logger.warn("system check picture info exists from db got an exception.");
						logger.error(e);
					}
				} else {
					// 删除，超过20条了，无意义
					try {
						hotPictureInfoServiceAdv.delete(allWrapOutallHotPictureInfoList.get(i).getId());
					} catch (Exception e) {
						logger.warn("system delete not exists picture info got an exception.");
						logger.error(e);
					}
				}
			}
		}
		logger.info("[InfoExistsCheckTask]hotpicture info exists timertask excute completed.");
	}

	/**
	 * 检查信息是否存在，如果不存在，则进行数据删除
	 * 
	 * @param wrapOutHotPictureInfo
	 * @throws Exception
	 */
	private void checkInfoExists(WrapOutHotPictureInfo wrapOutHotPictureInfo) throws Exception {
		InfoCheckerInf infoCheckerInf = null;
		Boolean exists = false;
		if ("CMS".equals(wrapOutHotPictureInfo.getApplication())) {
			infoCheckerInf = new CmsInfoChecker();
		} else if ("BBS".equals(wrapOutHotPictureInfo.getApplication())) {
			infoCheckerInf = new BbsInfoChecker();
		} else {
			logger.info("[InfoExistsCheckTask]hot picture application is not in CMS or BBS.title:"
					+ wrapOutHotPictureInfo.getTitle());
		}
		if (infoCheckerInf != null) {
			exists = infoCheckerInf.check(wrapOutHotPictureInfo.getInfoId());
		}
		if (!exists) {
			hotPictureInfoServiceAdv.delete(wrapOutHotPictureInfo.getId());
			logger.info("[InfoExistsCheckTask]hot picture has deleted.title:" + wrapOutHotPictureInfo.getTitle());
		} else {
			// logger.info("[InfoExistsCheckTask]hot picture exists.title:" +
			// wrapOutHotPictureInfo.getTitle() );
		}
	}
}