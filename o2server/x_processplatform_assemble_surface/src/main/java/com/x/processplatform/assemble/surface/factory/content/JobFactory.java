package com.x.processplatform.assemble.surface.factory.content;

import java.util.List;

import com.x.base.core.entity.JpaObject;
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
		String job = findWithWork(workOrWorkCompleted);
		if (null != job) {
			return job;
		}
		job = findWithWorkCompleted(workOrWorkCompleted);
		if (null != job) {
			return job;
		}
		return null;
	}

	public String findWithWork(String flag) throws Exception {
		Work work = this.entityManagerContainer().fetch(flag, Work.class, ListTools.toList(Work.job_FIELDNAME));
		if (null != work) {
			return work.getJob();
		}
		return null;
	}

	public String findWithWorkCompleted(String flag) throws Exception {
		WorkCompleted workCompleted = this.entityManagerContainer().fetch(flag, WorkCompleted.class,
				ListTools.toList(WorkCompleted.job_FIELDNAME));
		if (null != workCompleted) {
			return workCompleted.getJob();
		}
		List<WorkCompleted> os = this.entityManagerContainer().fetchEqual(WorkCompleted.class,
				ListTools.toList(WorkCompleted.job_FIELDNAME), WorkCompleted.work_FIELDNAME, flag);
		if (ListTools.isNotEmpty(os)) {
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

	public String findWorkOrWorkCompleted(String job) throws Exception {
		String id = "";
		List<Work> ws = this.entityManagerContainer().fetchEqualAscPaging(Work.class,
				ListTools.toList(Work.id_FIELDNAME), Work.job_FIELDNAME, job, 1, 1, JpaObject.sequence_FIELDNAME);
		if (ListTools.isNotEmpty(ws)) {
			id = ws.get(0).getId();
		}else {
			List<WorkCompleted> wcs = this.entityManagerContainer().fetchEqualAscPaging(WorkCompleted.class,
					ListTools.toList(WorkCompleted.id_FIELDNAME), WorkCompleted.job_FIELDNAME, job, 1, 1, JpaObject.sequence_FIELDNAME);
			if (ListTools.isNotEmpty(wcs)) {
				id = wcs.get(0).getId();
			}
		}
		return id;
	}
}
