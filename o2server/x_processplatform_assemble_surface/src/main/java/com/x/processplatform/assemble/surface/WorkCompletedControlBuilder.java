package com.x.processplatform.assemble.surface;

import java.util.Arrays;
import java.util.function.Consumer;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.WorkCompleted;

public class WorkCompletedControlBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkCompletedControlBuilder.class);

	private EffectivePerson effectivePerson;
	private Business business;
	private WorkCompleted workCompleted;

	public WorkCompletedControlBuilder(EffectivePerson effectivePerson, Business business,
			WorkCompleted workCompleted) {
		this.effectivePerson = effectivePerson;
		this.business = business;
		this.workCompleted = workCompleted;
	}

	// 是否可以管理
	private boolean ifAllowManage = false;
	// 是否可以看到
	private boolean ifAllowVisit = false;
	// 是否可以处理待阅
	private boolean ifAllowReadProcessing = false;
	// 是否可以回滚
	private boolean ifAllowRollback = false;

	public WorkCompletedControlBuilder enableAllowManage() {
		this.ifAllowManage = true;
		return this;
	}

	public WorkCompletedControlBuilder enableAllowVisit() {
		this.ifAllowVisit = true;
		return this;
	}

	public WorkCompletedControlBuilder enableAllowReadProcessing() {
		this.ifAllowReadProcessing = true;
		return this;
	}

	public WorkCompletedControlBuilder enableAllowRollback() {
		this.ifAllowRollback = true;
		return this;
	}

	public WorkCompletedControlBuilder enableAll() {
		enableAllowManage();
		enableAllowVisit();
		enableAllowReadProcessing();
		enableAllowRollback();
		return this;
	}

	private Boolean canManage;

	private boolean canManage() throws Exception {
		if (null == canManage) {
			this.canManage = business.ifPersonCanManageApplicationOrProcess(effectivePerson,
					workCompleted.getApplication(), workCompleted.getProcess())
					|| this.business.ifPersonHasPermissionWriteReviewWithJob(this.effectivePerson,
							this.workCompleted.getJob());
		}
		return this.canManage;
	}

	private Boolean readable;

	private boolean readable() throws Exception {
		if (null == readable) {
			this.readable = ((!BooleanUtils.isTrue(Config.ternaryManagement().getSecurityClearanceEnable()))
					|| business.ifPersonHasSufficientSecurityClearance(effectivePerson.getDistinguishedName(),
							workCompleted.getObjectSecurityClearance()))
					&& (business.ifPersonHasTaskReadTaskCompletedReadCompletedReviewWithJob(
							effectivePerson.getDistinguishedName(), workCompleted.getJob())
							|| business.ifJobHasBeenCorrelation(effectivePerson.getDistinguishedName(),
									workCompleted.getJob()));
		}
		return this.readable;
	}

	private Boolean hasReadWithJob;

	private boolean hasReadWithJob() throws Exception {
		if (null == hasReadWithJob) {
			this.hasReadWithJob = business.ifPersonHasReadWithJob(effectivePerson.getDistinguishedName(),
					workCompleted.getJob());
		}
		return this.hasReadWithJob;
	}

	public Control build() {
		Control control = new Control();
		if (null == workCompleted) {
			return control;
		}
		control.setWorkTitle(workCompleted.getTitle());
		control.setWorkJob(workCompleted.getJob());
		Arrays.<Pair<Boolean, Consumer<Control>>>asList(Pair.of(ifAllowManage, this::computeAllowManage),
				Pair.of(ifAllowVisit, this::computeAllowVisit),
				Pair.of(ifAllowReadProcessing, this::computeAllowReadProcessing),
				Pair.of(ifAllowRollback, this::computeAllowRollback)).stream().filter(Pair::first)
				.forEach(o -> o.second().accept(control));
		return control;
	}

	private void computeAllowManage(Control control) {
		try {
			control.setAllowManage(canManage());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowVisit(Control control) {
		try {
			control.setAllowVisit(canManage() || readable());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowReadProcessing(Control control) {
		try {
			control.setAllowReadProcessing(hasReadWithJob());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowRollback(Control control) {
		try {
			control.setAllowRollback(canManage() || BooleanUtils.isTrue(workCompleted.getAllowRollback()));
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}
}
