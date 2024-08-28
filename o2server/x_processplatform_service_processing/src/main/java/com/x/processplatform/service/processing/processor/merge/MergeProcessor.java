package com.x.processplatform.service.processing.processor.merge;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
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
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class MergeProcessor extends AbstractMergeProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(MergeProcessor.class);

	public MergeProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Merge merge) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.mergeArrive(aeiObjects.getWork().getActivityToken(), merge));
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Merge merge) throws Exception {
		// nothing
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Merge merge) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.mergeExecute(aeiObjects.getWork().getActivityToken(), merge));
		List<Work> results = new ArrayList<>();
		if (BooleanUtils.isNotTrue(aeiObjects.getWork().getSplitting())) {
			/* 如果不是一个拆分文档,直接通过 */
			results.add(aeiObjects.getWork());
			return results;
		}
		Optional<Work> other = findWorkSameLevelOrDeeper(aeiObjects);
		if (other.isPresent()) {
			aeiObjects.getUpdateWorks().add(other.get());
			aeiObjects.getDeleteWorks().add(aeiObjects.getWork());
			/* 应该废弃改变对work的指向 ? */
			this.mergeTaskCompleted(aeiObjects, aeiObjects.getWork(), other.get());
			this.mergeRead(aeiObjects, aeiObjects.getWork(), other.get());
			this.mergeReadCompleted(aeiObjects, aeiObjects.getWork(), other.get());
			this.mergeReview(aeiObjects, aeiObjects.getWork(), other.get());
			this.mergeAttachment(aeiObjects, aeiObjects.getWork(), other.get());
			this.mergeWorkLog(aeiObjects, aeiObjects.getWork(), other.get());
			aeiObjects.getWorkLogs().stream()
					.filter(p -> StringUtils.equals(p.getFromActivityToken(), aeiObjects.getWork().getActivityToken()))
					.forEach(obj -> aeiObjects.getDeleteWorkLogs().add(obj));
			// 本体被删除,如果另外合并对象处于merge环节要尝试流转
			if (Objects.equals(other.get().getActivityType(), ActivityType.merge)) {
				LOGGER.warn(
						"The work stays in merge is an illegal state, try to trigger the work(id:{}, job:{}) again.",
						other.get().getId(), other.get().getJob());
				results.add(other.get());
			}
		} else {
			Optional<List<String>> splitTokenList = this.findWorkShallower(aeiObjects);
			if (splitTokenList.isPresent()) {
				gotoShallower(aeiObjects, merge, splitTokenList.get());
			} else {
				// 只有一份也不能直接合并,因为可能设置了合并层数,返回到了处理人是拆分值的情况
				if ((null != merge.getMergeLayerThreshold()) && (merge.getMergeLayerThreshold() > 0)
						&& aeiObjects.getWork().getSplitTokenList().size() > merge.getMergeLayerThreshold()) {
					List<String> list = aeiObjects.getWork().getSplitTokenList().subList(0,
							aeiObjects.getWork().getSplitTokenList().size() - merge.getMergeLayerThreshold());
					gotoShallower(aeiObjects, merge, list);
				} else {
					// 唯一一份且合并层数大于等于当前的总层数,那么退出拆分状态.
					aeiObjects.getWork().setSplitting(false);
					aeiObjects.getWork().setSplitToken("");
					aeiObjects.getWork().setSplitTokenList(new ArrayList<>());
					aeiObjects.getWork().setSplitValue("");
					aeiObjects.getWork().setSplitValueList(new ArrayList<>());
					aeiObjects.getWork().setSplitTokenValueMap(new LinkedHashMap<>());
				}
			}
			// 不删除必然要继续流转,离开merge环节
			results.add(aeiObjects.getWork());
		}
		return results;
	}

	private void gotoShallower(AeiObjects aeiObjects, Merge merge, List<String> splitTokenList) {
		aeiObjects.getWork().setSplitting(true);
		int mergeLayerCount = aeiObjects.getWork().getSplitTokenList().size() - splitTokenList.size();
		if ((null != merge.getMergeLayerThreshold()) && (merge.getMergeLayerThreshold() > 0)) {
			mergeLayerCount = Math.min(mergeLayerCount, merge.getMergeLayerThreshold());
		}
		int threshold = aeiObjects.getWork().getSplitTokenList().size() - mergeLayerCount;
		// 回滚splitTokenList
		aeiObjects.getWork().setSplitTokenList(aeiObjects.getWork().getSplitTokenList().subList(0, threshold));
		// 回滚splitToken
		aeiObjects.getWork().setSplitToken(
				aeiObjects.getWork().getSplitTokenList().get(aeiObjects.getWork().getSplitTokenList().size() - 1));
		// 回滚splitValueList,如果是并行过来没有拆分值,通过存储值进行组装
		aeiObjects.getWork()
				.setSplitValueList(aeiObjects.getWork().getSplitTokenList().stream()
						.map(o -> aeiObjects.getWork().getSplitTokenValueMap().getOrDefault(o, null))
						.filter(Objects::nonNull).collect(Collectors.toList()));
		// 回滚splitValue
		if (ListTools.isNotEmpty(aeiObjects.getWork().getSplitValueList())) {
			aeiObjects.getWork().setSplitValue(
					aeiObjects.getWork().getSplitValueList().get(aeiObjects.getWork().getSplitValueList().size() - 1));
		} else {
			aeiObjects.getWork().setSplitValue("");
		}
	}

	/**
	 * 查找同级别或者拆分更深的文档
	 * 
	 * @param aeiObjects
	 * @return
	 * @throws Exception
	 */
	private Optional<Work> findWorkSameLevelOrDeeper(AeiObjects aeiObjects) throws Exception {
		String join = StringUtils.join(aeiObjects.getWork().getSplitTokenList(), ",");
		// 查找同级
		Optional<Work> other = aeiObjects.getWorks().stream()
				.filter(o -> BooleanUtils.isTrue(o.getSplitting()) && (o != aeiObjects.getWork())
						&& StringUtils.equals(StringUtils.join(o.getSplitTokenList(), ","), join))
				.sorted((o1, o2) -> o1.getCreateTime().compareTo(o2.getCreateTime())).findFirst();
		if (other.isPresent() && LOGGER.isDebugEnabled()) {
			LOGGER.debug("findWorkMergeTo work {} found same split level work {}.", aeiObjects.getWork()::getId,
					other.get()::getId);
		}
		// 找不到同级那么开始早更深层次的文档
		if (other.isEmpty()) {
			other = aeiObjects.getWorks().stream()
					.filter(o -> BooleanUtils.isTrue(o.getSplitting()) && (o != aeiObjects.getWork())
							&& StringUtils.startsWith(StringUtils.join(o.getSplitTokenList(), ","), join))
					.sorted((o1, o2) -> {
						int compare = o2.getSplitTokenList().size() - o1.getSplitTokenList().size();
						if (compare == 0) {
							return o2.getCreateTime().compareTo(o1.getCreateTime());
						}
						return compare;
					}).findFirst();
			if (other.isPresent() && LOGGER.isDebugEnabled()) {
				LOGGER.debug("findWorkMergeTo work {} found further split level work {}.", aeiObjects.getWork()::getId,
						other.get()::getId);
			}
		}
		return other;
	}

	/**
	 * 查找更浅层次的拆分文档
	 * 
	 * @param aeiObjects
	 * @return
	 * @throws Exception
	 */
	private Optional<List<String>> findWorkShallower(AeiObjects aeiObjects) throws Exception {
		List<String> list = aeiObjects.getWorks().stream()
				.filter(o -> BooleanUtils.isTrue(o.getSplitting()) && (o != aeiObjects.getWork()))
				.map(Work::getSplitTokenList).<List<String>>reduce(new ArrayList<>(), (a, b) -> {
					List<String> ac = ListUtils.longestCommonSubsequence(aeiObjects.getWork().getSplitTokenList(), a);
					List<String> bc = ListUtils.longestCommonSubsequence(aeiObjects.getWork().getSplitTokenList(), b);
					return (ac.size() > bc.size()) ? ac : bc;
				}, (x, y) -> x.size() >= y.size() ? x : y);
		return list.isEmpty() ? Optional.empty() : Optional.of(list);
	}

	private void mergeTaskCompleted(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getTaskCompleteds().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						aeiObjects.getUpdateTaskCompleteds().add(o);
					});
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void mergeRead(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getReads().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId())).forEach(o -> {
				o.setWork(oldest.getId());
				aeiObjects.getUpdateReads().add(o);
			});
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void mergeReadCompleted(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getReadCompleteds().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						aeiObjects.getUpdateReadCompleteds().add(o);
					});
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void mergeReview(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getReviews().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId())).forEach(o -> {
				o.setWork(oldest.getId());
				aeiObjects.getUpdateReviews().add(o);
			});
		} catch (Exception e) {
			LOGGER.error(e);
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
			LOGGER.error(e);
		}
	}

	private void mergeWorkLog(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getWorkLogs().stream()
					.filter(o -> StringUtils.equals(work.getActivityToken(), o.getArrivedActivityToken())
							&& StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						aeiObjects.getUpdateWorkLogs().add(o);
					});
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Merge merge, List<Work> works) throws Exception {
		// nothing
	}

	@Override
	protected Optional<Route> inquiring(AeiObjects aeiObjects, Merge merge) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.mergeInquire(aeiObjects.getWork().getActivityToken(), merge));
		return aeiObjects.getRoutes().stream().findFirst();
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Merge merge) throws Exception {
		// nothing
	}
}