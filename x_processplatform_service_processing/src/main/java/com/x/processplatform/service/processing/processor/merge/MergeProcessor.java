package com.x.processplatform.service.processing.processor.merge;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;
import com.x.processplatform.service.processing.processor.AbstractProcessor;

public class MergeProcessor extends AbstractProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(MergeProcessor.class);

	public MergeProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(ProcessingConfigurator configurator,ProcessingAttributes attributes, Work work,
			Data data, Activity activity) throws Exception {
		return work;
	}

	@Override
	protected List<Work> executeProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity) throws Exception {
		List<Work> results = new ArrayList<>();
		List<Work> branchs = this.entityManagerContainer().fetchAttribute(
				this.listOtherSplitting(this.business(), work.getSplitToken(), work.getId()), Work.class, "activity",
				"splitToken");
		/* 判断其他工作是否已经到达此合并节点 */
		for (Work o : branchs) {
			if ((!StringUtils.equals(o.getSplitToken(), work.getSplitToken()))
					|| (!StringUtils.equals(o.getActivity(), work.getActivity()))) {
				return results;
			}
		}
		/* 所有工作均已经到达此节点,准备开始合并,将所有的TaskCompleted链接到合并后的工作 */
		this.entityManagerContainer().beginTransaction(TaskCompleted.class);
		this.entityManagerContainer().beginTransaction(Read.class);
		this.entityManagerContainer().beginTransaction(ReadCompleted.class);
		this.entityManagerContainer().beginTransaction(Review.class);
		this.entityManagerContainer().beginTransaction(WorkLog.class);
		List<String> attachments = SetUniqueList.setUniqueList(work.getAttachmentList());
		for (Work branch : branchs) {
			/* 重新加载完整对象 */
			branch = this.entityManagerContainer().find(branch.getId(), Work.class);
			for (TaskCompleted o : this.entityManagerContainer().list(TaskCompleted.class,
					this.business().taskCompleted().listWithWork(branch.getId()))) {
				o.setWork(work.getId());
			}
			for (Read o : this.entityManagerContainer().list(Read.class,
					this.business().read().listWithWork(branch.getId()))) {
				o.setWork(work.getId());
			}
			for (ReadCompleted o : this.entityManagerContainer().list(ReadCompleted.class,
					this.business().readCompleted().listWithWork(branch.getId()))) {
				o.setWork(work.getId());
			}
			for (Review o : this.entityManagerContainer().list(Review.class,
					this.business().review().listWithWork(branch.getId()))) {
				o.setWork(work.getId());
			}
			for (WorkLog o : this.entityManagerContainer().list(WorkLog.class,
					this.business().workLog().listWithWork(branch.getId()))) {
				o.setWork(work.getId());
			}
			if (branch.getAttachmentList().isEmpty()) {
				attachments.addAll(branch.getAttachmentList());
			}
			/* 删除多余的WorkLog */
			this.entityManagerContainer().delete(WorkLog.class,
					this.business().workLog().getWithFromActivityTokenWithNotConnected(branch.getActivityToken()));
			this.entityManagerContainer().remove(branch, CheckRemoveType.all);
		}
		work.getSplitTokenList().remove(work.getSplitToken());
		if (work.getSplitTokenList().isEmpty()) {
			work.setSplitValue("");
			work.setSplitting(false);
			work.setSplitToken("");
		} else {
			work.setSplitToken(work.getSplitTokenList().get(work.getSplitTokenList().size() - 1));
		}
		results.add(work);
		return results;
	}

	@Override
	protected List<Route> inquireProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity, List<Route> routes) throws Exception {
		List<Route> results = new ArrayList<>();
		results.add(routes.get(0));
		return results;
	}

	private List<String> listOtherSplitting(Business business, String splitToken, String currentWork) throws Exception {
		List<String> list = new ArrayList<>();
		for (String str : business.work().listContainSplitToken(splitToken)) {
			if (!StringUtils.equals(currentWork, str)) {
				list.add(str);
			}
		}
		return list;
	}
}