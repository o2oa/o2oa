package com.x.processplatform.assemble.surface;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

public class JobControlBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobControlBuilder.class);

	private EffectivePerson effectivePerson;
	private Business business;
	private String jobOrWorkOrWorkCompleted;

	public JobControlBuilder(EffectivePerson effectivePerson, Business business, String jobOrWorkOrWorkCompleted) {
		this.effectivePerson = effectivePerson;
		this.business = business;
		this.jobOrWorkOrWorkCompleted = jobOrWorkOrWorkCompleted;
	}

	// 是否可以管理
	private boolean ifAllowManage = false;
	// 是否可以看到
	private boolean ifAllowVisit = false;
	// 是否可以保存
	private boolean ifAllowSave = false;

	public JobControlBuilder enableAllowManage() {
		this.ifAllowManage = true;
		return this;
	}

	public JobControlBuilder enableAllowVisit() {
		this.ifAllowVisit = true;
		return this;
	}

	public JobControlBuilder enableAllowSave() {
		this.ifAllowSave = true;
		return this;
	}

	public JobControlBuilder enableAll() {
		enableAllowManage();
		enableAllowVisit();
		enableAllowSave();
		return this;
	}

	public Control build() {
		try {
			Work work = business.entityManagerContainer().firstEqual(Work.class, Work.job_FIELDNAME,
					jobOrWorkOrWorkCompleted);
			if (null == work) {
				work = business.entityManagerContainer().find(jobOrWorkOrWorkCompleted, Work.class);
			}
			if (null != work) {
				return buildOfWork(work);
			} else {
				WorkCompleted workCompleted = business.entityManagerContainer().firstEqual(WorkCompleted.class,
						WorkCompleted.job_FIELDNAME, jobOrWorkOrWorkCompleted);
				if (null == workCompleted) {
					workCompleted = business.entityManagerContainer().find(jobOrWorkOrWorkCompleted,
							WorkCompleted.class);
				}
				if (null == workCompleted) {
					workCompleted = business.entityManagerContainer().firstEqual(WorkCompleted.class,
							WorkCompleted.work_FIELDNAME, jobOrWorkOrWorkCompleted);
				}
				if (null != workCompleted) {
					return buildOfWorkCompleted(workCompleted);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return new Control();
	}

	private Control buildOfWork(Work work) {
		WorkControlBuilder builder = new WorkControlBuilder(this.effectivePerson, this.business, work);
		if (ifAllowManage) {
			builder.enableAllowManage();
		}
		if (ifAllowVisit) {
			builder.enableAllowVisit();
		}
		if (ifAllowSave) {
			builder.enableAllowSave();
		}
		return builder.build();
	}

	/**
	 * 在workCompleted中allowSave == allowManage
	 *
	 * @param workCompleted
	 * @return
	 */
	private Control buildOfWorkCompleted(WorkCompleted workCompleted) {
		WorkCompletedControlBuilder builder = new WorkCompletedControlBuilder(this.effectivePerson, this.business,
				workCompleted);
		if (ifAllowManage) {
			builder.enableAllowManage();
		}
		if (ifAllowVisit) {
			builder.enableAllowVisit();
		}
		// 在workCompleted中allowSave == allowManage
		if (ifAllowSave) {
			builder.enableAllowManage();
		}
		Control o = builder.build();
		Control control = new Control();
		control.setWorkTitle(workCompleted.getTitle());
		control.setWorkJob(workCompleted.getJob());
		if (ifAllowManage) {
			control.setAllowManage(o.getAllowManage());
		}
		if (ifAllowVisit) {
			control.setAllowVisit(o.getAllowVisit());
		}
		if (ifAllowSave) {
			control.setAllowSave(o.getAllowManage());
		}
		return control;
	}
}
