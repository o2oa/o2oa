package com.x.processplatform.service.processing.processor.merge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class MergeProcessor extends AbstractMergeProcessor {

	private static Logger logger = LoggerFactory.getLogger(MergeProcessor.class);

	public MergeProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
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
		Work other = findWorkMergeTo(aeiObjects);

		/* 完全找不到合并的文档,唯一一份 */
		if (null != other) {
			aeiObjects.getUpdateWorks().add(other);
			aeiObjects.getDeleteWorks().add(aeiObjects.getWork());
			this.mergeTaskCompleted(aeiObjects, aeiObjects.getWork(), other);
			this.mergeRead(aeiObjects, aeiObjects.getWork(), other);
			this.mergeReadCompleted(aeiObjects, aeiObjects.getWork(), other);
			this.mergeReview(aeiObjects, aeiObjects.getWork(), other);
			this.mergeHint(aeiObjects, aeiObjects.getWork(), other);
			this.mergeAttachment(aeiObjects, aeiObjects.getWork(), other);
			this.mergeWorkLog(aeiObjects, aeiObjects.getWork(), other);
			aeiObjects.getWorkLogs().stream()
					.filter(p -> StringUtils.equals(p.getFromActivityToken(), aeiObjects.getWork().getActivityToken()))
					.forEach(obj -> {
						aeiObjects.getDeleteWorkLogs().add(obj);
					});
		} else {
			Work branch = this.findWorkBranch(aeiObjects);
			if (null != branch) {
				aeiObjects.getWork().setSplitting(true);
				aeiObjects.getWork().setSplitTokenList(ListUtils.longestCommonSubsequence(
						aeiObjects.getWork().getSplitTokenList(), branch.getSplitTokenList()));
				aeiObjects.getWork().setSplitToken(aeiObjects.getWork().getSplitTokenList()
						.get(aeiObjects.getWork().getSplitTokenList().size() - 1));
				aeiObjects.getWork().setSplitValue("");
				results.add(aeiObjects.getWork());
			} else {
				aeiObjects.getWork().setSplitting(false);
				aeiObjects.getWork().setSplitTokenList(new ArrayList<String>());
				aeiObjects.getWork().setSplitToken("");
				aeiObjects.getWork().setSplitValue("");
				results.add(aeiObjects.getWork());
			}
		}
		return results;
	}

	private Work findWorkMergeTo(AeiObjects aeiObjects) throws Exception {
		String join = StringUtils.join(aeiObjects.getWork().getSplitTokenList(), ",");
		/* 查找同级 */
		Work other = aeiObjects.getWorks().stream().filter(o -> {
			if (BooleanUtils.isTrue(o.getSplitting()) && (o != aeiObjects.getWork())) {
				if (StringUtils.equals(StringUtils.join(o.getSplitTokenList(), ","), join)) {
					return true;
				}
			}
			return false;
		}).sorted((o1, o2) -> {
			return o1.getCreateTime().compareTo(o2.getCreateTime());
		}).findFirst().orElse(null);

		/* 找不到同级那么开始早更深层次的文档 */
		if (null == other) {
			other = aeiObjects.getWorks().stream().filter(o -> {
				if (BooleanUtils.isTrue(o.getSplitting()) && (o != aeiObjects.getWork())) {
					if (StringUtils.startsWith(StringUtils.join(o.getSplitTokenList(), ","), join)) {
						return true;
					}
				}
				return false;
			}).sorted((o1, o2) -> {
				int compare = o2.getSplitTokenList().size() - o1.getSplitTokenList().size();
				if (compare == 0) {
					return o2.getCreateTime().compareTo(o1.getCreateTime());
				}
				return compare;
			}).findFirst().orElse(null);
		}
		return other;
	}

	private Work findWorkBranch(AeiObjects aeiObjects) throws Exception {
		Work branch = null;
		String join = StringUtils.join(aeiObjects.getWork().getSplitTokenList(), ",");
		while (StringUtils.indexOf(join, ",") > 0) {
			join = StringUtils.substringBeforeLast(join, ",");
			final String part = join;
			branch = aeiObjects.getWorks().stream().filter(o -> {
				if (BooleanUtils.isTrue(o.getSplitting()) && (o != aeiObjects.getWork())) {
					if (StringUtils.startsWithIgnoreCase(StringUtils.join(o.getSplitTokenList(), ","), part)) {
						return true;
					}
				}
				return false;
			}).findFirst().orElse(null);
			if (null != branch) {
				return branch;
			}
		}
		return null;
	}

	private void mergeTaskCompleted(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getTaskCompleteds().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
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
						// o.setArrivedActivityToken(oldest.getActivityToken());
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