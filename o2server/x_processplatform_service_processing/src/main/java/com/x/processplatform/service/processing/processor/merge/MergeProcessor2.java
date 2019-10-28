package com.x.processplatform.service.processing.processor.merge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class MergeProcessor2 extends AbstractMergeProcessor {

	private static Logger logger = LoggerFactory.getLogger(MergeProcessor2.class);

	public MergeProcessor2(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Merge merge) throws Exception {
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Merge merge) throws Exception {
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Merge merge) throws Exception {
		List<Work> results = new ArrayList<>();
		if (BooleanUtils.isNotTrue(aeiObjects.getWork().getSplitting())) {
			/* 如果不是一个拆分文档,直接通过 */
			results.add(aeiObjects.getWork());
			return results;
		}
		String splitToken = null;
		List<Work> waitMerges = new ArrayList<>();
		for (String str : aeiObjects.getWork().getSplitTokenList()) {
			if (StringUtils.isNotEmpty(str)) {
				waitMerges = aeiObjects.getWorks().stream()
						.filter(o -> ListTools.contains(o.getSplitTokenList(), str)
								&& (!Objects.equals(ActivityType.end, o.getActivityType())))
						.collect(Collectors.toList());
				if (!waitMerges.isEmpty()) {
					if (waitMerges.stream()
							.allMatch(o -> StringUtils.equals(o.getActivity(), aeiObjects.getWork().getActivity()))) {
						splitToken = str;
						break;
					}
				}
			}
		}
		/* 没有找到需要合并的work,直接返回 */
		if (StringUtils.isEmpty(splitToken)) {
			return results;
		}
		/* 已经找到了需要合并的work,找到最老的作为基准work */
		Work oldest = waitMerges.stream()
				.sorted(Comparator.comparing(Work::getCreateTime, Comparator.nullsLast(Date::compareTo))).findFirst()
				.get();
		aeiObjects.getUpdateWorks().add(oldest);
		waitMerges.stream().filter(o -> (!Objects.equals(o, oldest))).forEach(o -> {
			/* 排除oldest文档不进行删除 */
			aeiObjects.getDeleteWorks().add(o);
			/* 是不可能有待办的 */
			/* 将已办归并到最早work */
			this.mergeTaskCompleted(aeiObjects, o, oldest);
			/* 将待阅归并到最早work */
			this.mergeRead(aeiObjects, o, oldest);
			/* 将已阅归并到最早work */
			this.mergeReadCompleted(aeiObjects, o, oldest);
			/* 将参阅归并到最早work */
			this.mergeReview(aeiObjects, o, oldest);
			/* 将提示归并到最早work */
			this.mergeHint(aeiObjects, o, oldest);
			/* 将附件归并到当前work */
			this.mergeAttachment(aeiObjects, o, oldest);
			/* 将工作日志归并到当前work */
			this.mergeWorkLog(aeiObjects, o, oldest);
			/* 将要合并的工作上当前为连接的workLog删除 */
			try {
				aeiObjects.getWorkLogs().stream()
						.filter(p -> StringUtils.equals(p.getFromActivityToken(), o.getActivityToken()))
						.forEach(obj -> {
							aeiObjects.getDeleteWorkLogs().add(obj);
						});
			} catch (Exception e) {
				logger.error(e);
			}
		});
		List<String> splitTokens = new ArrayList<>();
		for (String str : oldest.getSplitTokenList()) {
			if (StringUtils.equals(str, splitToken)) {
				break;
			}
			splitTokens.add(str);
		}
		oldest.setSplitTokenList(splitTokens);
		if (splitTokens.isEmpty()) {
			oldest.setSplitValue("");
			oldest.setSplitting(false);
			oldest.setSplitToken("");
		} else {
			oldest.setSplitting(true);
			oldest.setSplitToken(splitTokens.get(splitTokens.size() - 1));
		}
		results.add(oldest);
		return results;
	}

	private void mergeTaskCompleted(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getTaskCompleteds().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						// o.setActivityToken(oldest.getActivityToken());
						aeiObjects.getUpdateTaskCompleteds().add(o);
					});
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void mergeRead(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getReads().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId())).forEach(o -> {
				o.setWork(oldest.getId());
				// o.setActivityToken(oldest.getActivityToken());
				aeiObjects.getUpdateReads().add(o);
			});
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void mergeReadCompleted(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getReadCompleteds().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						// o.setActivityToken(oldest.getActivityToken());
						aeiObjects.getUpdateReadCompleteds().add(o);
					});
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void mergeReview(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getReviews().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId())).forEach(o -> {
				o.setWork(oldest.getId());
				aeiObjects.getUpdateReviews().add(o);
			});
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void mergeHint(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getHints().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId())).forEach(o -> {
				o.setWork(oldest.getId());
				aeiObjects.getUpdateHints().add(o);
			});
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void mergeAttachment(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getAttachments().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						aeiObjects.getUpdateAttachments().add(o);
					});
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void mergeWorkLog(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getWorkLogs().stream()
					.filter(o -> StringUtils.equals(work.getActivityToken(), o.getArrivedActivityToken())
							&& StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						o.setArrivedActivityToken(oldest.getActivityToken());
						aeiObjects.getUpdateWorkLogs().add(o);
					});
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Merge merge) throws Exception {
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Merge merge) throws Exception {
		List<Route> results = new ArrayList<>();
		results.add(aeiObjects.getRoutes().get(0));
		return results;
	}

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Merge merge = (Merge) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, merge);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects) throws Exception {
		Merge merge = (Merge) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, merge);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Merge merge = (Merge) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, merge);
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Merge merge) throws Exception {
	}
}