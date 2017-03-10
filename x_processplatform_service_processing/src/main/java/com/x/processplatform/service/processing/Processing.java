package com.x.processplatform.service.processing;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;

public class Processing extends BaseProcessing {

	private EntityManagerContainer entityManagerContainer;

	private int loop = 0;

	protected ProcessingAttributes attributes;

	public EntityManagerContainer entityManagerContainer() {
		return this.entityManagerContainer;
	}

	public Processing(ProcessingAttributes attributes) throws Exception {
		this.attributes = attributes;
		this.entityManagerContainer = EntityManagerContainerFactory.instance().create();
	}

	public Processing(Integer loop, ProcessingAttributes attributes, EntityManagerContainer entityManagerContainer)
			throws Exception {
		this.loop = ++loop;
		this.attributes = attributes;
		this.entityManagerContainer = entityManagerContainer;
		if (this.loop > 32) {
			throw new Exception("processing too many.");
		}
	}

	public void processing(String workId) throws Exception {
		this.processing(workId, new ProcessingConfigurator());
	}

	public void processing(String workId, ProcessingConfigurator processingConfigurator) throws Exception {
		try {
			Work work = null;
			work = this.entityManagerContainer().fetchAttribute(workId, Work.class, "workStatus");
			if (null != work) {
				switch (work.getWorkStatus()) {
				case start:
					workId = this.begin().arrive(workId, processingConfigurator, attributes);
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
			/* workStatus is processing */
			List<String> nextLoops = SetUniqueList.setUniqueList(new ArrayList<String>());
			/* 强制从arrived开始 */
			if (!processingConfigurator.getJoinAtExecute()) {
				workId = this.arrive(workId, processingConfigurator);
			}
			if (StringUtils.isEmpty(workId)) {
				return;
			}
			List<String> executed = this.execute(workId, processingConfigurator);
			for (String str : executed) {
				if (StringUtils.isNotEmpty(str)) {
					List<String> inquired = inquire(str, processingConfigurator);
					System.out.println("inquire destination size:" + inquired.size());
					for (String o : inquired) {
						nextLoops.add(arrive(o, processingConfigurator));
					}
				}
			}
			for (String str : nextLoops) {
				if (StringUtils.isNotEmpty(str)) {
					if (processingConfigurator.getContinueLoop()) {
						new Processing(this.loop, attributes, this.entityManagerContainer()).processing(str);
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("processing fialure.", e);
		} finally {
			this.entityManagerContainer().close();
		}
	}

	private String arrive(String workId, ProcessingConfigurator processingConfigurator) throws Exception {
		try {
			Work work = null;
			work = this.entityManagerContainer().fetchAttribute(workId, Work.class, "destinationActivityType");
			if (null == work) {
				return null;
			}
			String id = null;
			switch (work.getDestinationActivityType()) {
			case agent:
				// return this.agent().arrive(workId, processingConfigurator,
				// attributes);
				id = this.agent().arrive(workId, processingConfigurator, attributes);
				break;
			case begin:
				// return this.begin().arrive(workId, processingConfigurator,
				// attributes);
				id = this.begin().arrive(workId, processingConfigurator, attributes);
				break;
			case cancel:
				// return this.cancel().arrive(workId, processingConfigurator,
				// attributes);
				id = this.cancel().arrive(workId, processingConfigurator, attributes);
				break;
			case condition:
				// return this.condition().arrive(workId,
				// processingConfigurator, attributes);
				id = this.condition().arrive(workId, processingConfigurator, attributes);
				break;
			case choice:
				// return this.choice().arrive(workId, processingConfigurator,
				// attributes);
				id = this.choice().arrive(workId, processingConfigurator, attributes);
				break;
			case delay:
				// return this.delay().arrive(workId, processingConfigurator,
				// attributes);
				id = this.delay().arrive(workId, processingConfigurator, attributes);
				break;
			case embed:
				// return this.embed().arrive(workId, processingConfigurator,
				// attributes);
				id = this.embed().arrive(workId, processingConfigurator, attributes);
				break;
			case end:
				// return this.end().arrive(workId, processingConfigurator,
				// attributes);
				id = this.end().arrive(workId, processingConfigurator, attributes);
				break;
			case invoke:
				// return this.invoke().arrive(workId, processingConfigurator,
				// attributes);
				id = this.invoke().arrive(workId, processingConfigurator, attributes);
				break;
			case manual:
				// return this.manual().arrive(workId, processingConfigurator,
				// attributes);
				id = this.manual().arrive(workId, processingConfigurator, attributes);
				break;
			case merge:
				// return this.merge().arrive(workId, processingConfigurator,
				// attributes);
				id = this.merge().arrive(workId, processingConfigurator, attributes);
				break;
			case message:
				// return this.message().arrive(workId, processingConfigurator,
				// attributes);
				id = this.message().arrive(workId, processingConfigurator, attributes);
			case parallel:
				// return this.parallel().arrive(workId, processingConfigurator,
				// attributes);
				id = this.parallel().arrive(workId, processingConfigurator, attributes);
			case service:
				// return this.service().arrive(workId, processingConfigurator,
				// attributes);
				id = this.service().arrive(workId, processingConfigurator, attributes);
			case split:
				// return this.split().arrive(workId, processingConfigurator,
				// attributes);
				id = this.split().arrive(workId, processingConfigurator, attributes);
			default:
				break;
			}
			this.entityManagerContainer.commit();
			return id;
		} catch (Exception e) {
			throw new Exception("reach failure.", e);
		}
	}

	private List<String> execute(String workId, ProcessingConfigurator processingConfigurator) throws Exception {
		List<String> executed = new ArrayList<>();
		try {
			Work work = null;
			work = this.entityManagerContainer().fetchAttribute(workId, Work.class, "activityType");
			if (null == work) {
				return executed;
			}
			switch (work.getActivityType()) {
			case agent:
				executed.addAll(this.agent().execute(workId, processingConfigurator, attributes));
				break;
			case begin:
				executed.addAll(this.begin().execute(workId, processingConfigurator, attributes));
				break;
			case cancel:
				this.cancel().execute(workId, processingConfigurator, attributes);
				break;
			case choice:
				executed.addAll(this.choice().execute(workId, processingConfigurator, attributes));
				break;
			case condition:
				executed.addAll(this.condition().execute(workId, processingConfigurator, attributes));
				break;
			case delay:
				executed.addAll(this.delay().execute(workId, processingConfigurator, attributes));
				break;
			case embed:
				executed.addAll(this.embed().execute(workId, processingConfigurator, attributes));
				break;
			case end:
				this.end().execute(workId, processingConfigurator, attributes);
				break;
			case invoke:
				executed.addAll(this.invoke().execute(workId, processingConfigurator, attributes));
				break;
			case manual:
				executed.addAll(this.manual().execute(workId, processingConfigurator, attributes));
				// this.entityManagerContainer.commit();
				break;
			case merge:
				executed.addAll(this.merge().execute(workId, processingConfigurator, attributes));
				break;
			case message:
				executed.addAll(this.message().execute(workId, processingConfigurator, attributes));
				break;
			case parallel:
				executed.addAll(this.parallel().execute(workId, processingConfigurator, attributes));
				break;
			case service:
				executed.addAll(this.service().execute(workId, processingConfigurator, attributes));
				break;
			case split:
				executed.addAll(this.split().execute(workId, processingConfigurator, attributes));
				break;
			default:
				break;
			}
			this.entityManagerContainer.commit();
			return executed;
		} catch (Exception e) {
			throw new Exception("processing inquery failure.", e);
		}
	}

	private List<String> inquire(String workId, ProcessingConfigurator processingConfigurator) throws Exception {
		try {
			List<String> inquired = new ArrayList<>();
			Work work = null;
			work = this.entityManagerContainer().fetchAttribute(workId, Work.class, "activityType");
			if (null == work) {
				return inquired;
			}
			switch (work.getActivityType()) {
			case agent:
				inquired.addAll(this.agent().inquire(workId, processingConfigurator, attributes));
				break;
			case begin:
				inquired.addAll(this.begin().inquire(workId, processingConfigurator, attributes));
				break;
			case cancel:
				break;
			case choice:
				inquired.addAll(this.choice().inquire(workId, processingConfigurator, attributes));
				break;
			case condition:
				inquired.addAll(this.condition().inquire(workId, processingConfigurator, attributes));
				break;
			case delay:
				inquired.addAll(this.delay().inquire(workId, processingConfigurator, attributes));
				break;
			case embed:
				inquired.addAll(this.embed().inquire(workId, processingConfigurator, attributes));
				break;
			case end:
				break;
			case invoke:
				inquired.addAll(this.invoke().inquire(workId, processingConfigurator, attributes));
				break;
			case manual:
				inquired.addAll(this.manual().inquire(workId, processingConfigurator, attributes));
				break;
			case merge:
				inquired.addAll(this.merge().inquire(workId, processingConfigurator, attributes));
				break;
			case message:
				inquired.addAll(this.message().inquire(workId, processingConfigurator, attributes));
				break;
			case parallel:
				inquired.addAll(this.parallel().inquire(workId, processingConfigurator, attributes));
				break;
			case service:
				inquired.addAll(this.service().inquire(workId, processingConfigurator, attributes));
				break;
			case split:
				inquired.addAll(this.split().inquire(workId, processingConfigurator, attributes));
				break;
			default:
				break;
			}
			this.entityManagerContainer.commit();
			return inquired;
		} catch (Exception e) {
			throw new Exception("processing inquery failure.", e);
		}
	}
}