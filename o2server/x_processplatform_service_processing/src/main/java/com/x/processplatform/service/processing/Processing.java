package com.x.processplatform.service.processing;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;

public class Processing extends BaseProcessing {

	private EntityManagerContainer entityManagerContainer;

	private ProcessingAttributes processingAttributes;

	@Override
	public EntityManagerContainer entityManagerContainer() {
		return this.entityManagerContainer;
	}

	public Processing(ProcessingAttributes processingAttributes) throws Exception {
		if (null == processingAttributes) {
			this.processingAttributes = new ProcessingAttributes();
		} else {
			this.processingAttributes = processingAttributes;
		}
		if (this.processingAttributes.getLoop() > 64) {
			throw new IllegalStateException("processing too many.");
		}
		this.entityManagerContainer = EntityManagerContainerFactory.instance().create();
	}

	public void processing(String workId) throws Exception {
		this.processing(workId, new ProcessingConfigurator());
	}

	public void processing(String workId, ProcessingConfigurator processingConfigurator) throws Exception {
		try {
			Work work = null;
			work = this.entityManagerContainer().fetch(workId, Work.class,
					ListTools.toList(Work.workStatus_FIELDNAME, Work.job_FIELDNAME));
			if (null != work) {
				switch (work.getWorkStatus()) {
				case start:
					workId = this.begin().arrive(workId, processingConfigurator, processingAttributes);
					break;
				case hanging:
					workId = null;
					break;
				default:
					break;
				}
			}
			if (StringUtils.isEmpty(workId)) {
				return;
			}
			// workStatus is processing
			List<String> nextLoops = SetUniqueList.setUniqueList(new ArrayList<String>());
			// 强制从arrived开始
			if (BooleanUtils.isTrue(processingAttributes.ifForceJoinAtArrive())) {
				workId = this.arrive(workId, processingConfigurator, processingAttributes);
			}
			if (BooleanUtils.isFalse(processingConfigurator.getJoinAtExecute())) {
				workId = this.arrive(workId, processingConfigurator, processingAttributes);
			}
			if (StringUtils.isEmpty(workId)) {
				return;
			}
			List<String> executed = null;
			if (BooleanUtils.isFalse(processingAttributes.ifForceJoinAtInquire())) {
				executed = this.execute(workId, processingConfigurator, processingAttributes);
			} else {
				// 强制从inquire开始
				executed = new ArrayList<>();
				executed.add(workId);
			}
			for (String str : executed) {
				if (StringUtils.isNotEmpty(str)) {
					List<String> inquired = inquire(str, processingConfigurator, processingAttributes);
					for (String o : inquired) {
						nextLoops.add(arrive(o, processingConfigurator, processingAttributes));
					}
				}
			}
			processingAttributes.increaseLoop();
			for (String str : nextLoops) {
				if (StringUtils.isNotEmpty(str) && BooleanUtils.isTrue(processingConfigurator.getContinueLoop())) {
					// clone processingAttributes 对象
					new Processing(processingAttributes.copyInstancePointToSingletonSignalStack()).processing(str);
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException("processing fialure.", e);
		} finally {
			this.entityManagerContainer().close();
		}
	}

	private String arrive(String workId, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes processingAttributes) throws Exception {
		try {
			Work work = null;
			work = this.entityManagerContainer().fetch(workId, Work.class,
					ListTools.toList(Work.destinationActivityType_FIELDNAME));
			if (null == work) {
				return null;
			}
			String id = null;
			switch (work.getDestinationActivityType()) {
			case agent:
				id = this.agent().arrive(workId, processingConfigurator, processingAttributes);
				break;
			case begin:
				id = this.begin().arrive(workId, processingConfigurator, processingAttributes);
				break;
			case cancel:
				id = this.cancel().arrive(workId, processingConfigurator, processingAttributes);
				break;
			case choice:
				id = this.choice().arrive(workId, processingConfigurator, processingAttributes);
				break;
			case delay:
				id = this.delay().arrive(workId, processingConfigurator, processingAttributes);
				break;
			case embed:
				id = this.embed().arrive(workId, processingConfigurator, processingAttributes);
				break;
			case end:
				id = this.end().arrive(workId, processingConfigurator, processingAttributes);
				break;
			case invoke:
				id = this.invoke().arrive(workId, processingConfigurator, processingAttributes);
				break;
			case manual:
				id = this.manual().arrive(workId, processingConfigurator, processingAttributes);
				break;
			case merge:
				id = this.merge().arrive(workId, processingConfigurator, processingAttributes);
				break;
			case parallel:
				id = this.parallel().arrive(workId, processingConfigurator, processingAttributes);
				break;
			case publish:
				id = this.publish().arrive(workId, processingConfigurator, processingAttributes);
				break;
			case service:
				id = this.service().arrive(workId, processingConfigurator, processingAttributes);
				break;
			case split:
				id = this.split().arrive(workId, processingConfigurator, processingAttributes);
				break;
			default:
				break;
			}
			/** 在内层的方法中进行提交,这里不需要再次进行提交,在内层提交是因为比如发送待办等要在提交后运行 */
			return id;
		} catch (Exception e) {
			throw new IllegalStateException("processing arrive failure.", e);
		}
	}

	private List<String> execute(String workId, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes processingAttributes) throws Exception {
		List<String> executed = new ArrayList<>();
		try {
			Work work = null;
			work = this.entityManagerContainer().fetch(workId, Work.class,
					ListTools.toList(Work.activityType_FIELDNAME));
			if (null == work) {
				return executed;
			}
			switch (work.getActivityType()) {
			case agent:
				executed.addAll(this.agent().execute(workId, processingConfigurator, processingAttributes));
				break;
			case begin:
				executed.addAll(this.begin().execute(workId, processingConfigurator, processingAttributes));
				break;
			case cancel:
				this.cancel().execute(workId, processingConfigurator, processingAttributes);
				break;
			case choice:
				executed.addAll(this.choice().execute(workId, processingConfigurator, processingAttributes));
				break;
			case delay:
				executed.addAll(this.delay().execute(workId, processingConfigurator, processingAttributes));
				break;
			case embed:
				executed.addAll(this.embed().execute(workId, processingConfigurator, processingAttributes));
				break;
			case end:
				this.end().execute(workId, processingConfigurator, processingAttributes);
				break;
			case invoke:
				executed.addAll(this.invoke().execute(workId, processingConfigurator, processingAttributes));
				break;
			case manual:
				executed.addAll(this.manual().execute(workId, processingConfigurator, processingAttributes));
				break;
			case merge:
				executed.addAll(this.merge().execute(workId, processingConfigurator, processingAttributes));
				break;
			case parallel:
				executed.addAll(this.parallel().execute(workId, processingConfigurator, processingAttributes));
				break;
			case publish:
				executed.addAll(this.publish().execute(workId, processingConfigurator, processingAttributes));
				break;
			case service:
				executed.addAll(this.service().execute(workId, processingConfigurator, processingAttributes));
				break;
			case split:
				executed.addAll(this.split().execute(workId, processingConfigurator, processingAttributes));
				break;
			default:
				break;
			}
			// 在内层的方法中进行提交,这里不需要再次进行提交,在内层提交是因为比如发送待办等要在提交后运行
			return executed;
		} catch (Exception e) {
			throw new IllegalStateException("processing execute failure.", e);
		}
	}

	private List<String> inquire(String workId, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes processingAttributes) throws Exception {
		try {
			List<String> inquired = new ArrayList<>();
			Work work = null;
			work = this.entityManagerContainer().fetch(workId, Work.class,
					ListTools.toList(Work.activityType_FIELDNAME));
			if (null == work) {
				return inquired;
			}
			switch (work.getActivityType()) {
			case agent:
				inquired.addAll(this.agent().inquire(workId, processingConfigurator, processingAttributes));
				break;
			case begin:
				inquired.addAll(this.begin().inquire(workId, processingConfigurator, processingAttributes));
				break;
			case cancel:
				break;
			case choice:
				inquired.addAll(this.choice().inquire(workId, processingConfigurator, processingAttributes));
				break;
			case delay:
				inquired.addAll(this.delay().inquire(workId, processingConfigurator, processingAttributes));
				break;
			case embed:
				inquired.addAll(this.embed().inquire(workId, processingConfigurator, processingAttributes));
				break;
			case end:
				break;
			case invoke:
				inquired.addAll(this.invoke().inquire(workId, processingConfigurator, processingAttributes));
				break;
			case manual:
				inquired.addAll(this.manual().inquire(workId, processingConfigurator, processingAttributes));
				break;
			case merge:
				inquired.addAll(this.merge().inquire(workId, processingConfigurator, processingAttributes));
				break;
			case parallel:
				inquired.addAll(this.parallel().inquire(workId, processingConfigurator, processingAttributes));
				break;
			case publish:
				inquired.addAll(this.publish().inquire(workId, processingConfigurator, processingAttributes));
				break;
			case service:
				inquired.addAll(this.service().inquire(workId, processingConfigurator, processingAttributes));
				break;
			case split:
				inquired.addAll(this.split().inquire(workId, processingConfigurator, processingAttributes));
				break;
			default:
				break;
			}
			// 在内层的方法中进行提交,这里不需要再次进行提交,在内层提交是因为比如发送待办等要在提交后运行
			return inquired;
		} catch (Exception e) {
			throw new IllegalStateException("processing inquire failure.", e);
		}
	}
}
