package com.x.processplatform.assemble.surface.factory.content;

import java.util.List;

import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

public class JobFactory extends AbstractFactory {

	public JobFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public boolean hasMultiRelative(String job) throws Exception {
		Business business = this.business();
		return (business.work().countWithJob(job) + business.workCompleted().countWithJob(job)) > 1 ? true : false;
	}

	public String findWithWorkOrWorkCompleted(String workOrWorkCompleted) throws Exception {
		Work work = this.entityManagerContainer().fetch(workOrWorkCompleted, Work.class,
				ListTools.toList(Work.job_FIELDNAME));
		if (null != work) {
			return work.getJob();
		}
		WorkCompleted workCompleted = this.entityManagerContainer().fetch(workOrWorkCompleted, WorkCompleted.class,
				ListTools.toList(WorkCompleted.job_FIELDNAME));
		if (null != workCompleted) {
			return workCompleted.getJob();
		}
		List<WorkCompleted> os = this.entityManagerContainer().fetchEqual(WorkCompleted.class,
				ListTools.toList(Work.job_FIELDNAME), WorkCompleted.work_FIELDNAME, workOrWorkCompleted);
		if (os.size() == 1) {
			return os.get(0).getJob();
		}
		return null;
	}

	public boolean jobExist(String value) throws Exception {
		if (this.entityManagerContainer().countEqual(Work.class, Work.job_FIELDNAME, value) > 0) {
			return true;
		} else if (this.entityManagerContainer().countEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME,
				value) > 0) {
			return true;
		} else {
			return false;
		}
	}
}
