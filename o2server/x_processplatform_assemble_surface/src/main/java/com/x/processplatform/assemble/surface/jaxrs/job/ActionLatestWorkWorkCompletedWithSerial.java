package com.x.processplatform.assemble.surface.jaxrs.job;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionLatestWorkWorkCompletedWithSerial extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionLatestWorkWorkCompletedWithSerial.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String serial) throws Exception {

		LOGGER.debug("execute:{}, serial:{}.", effectivePerson::getDistinguishedName, () -> serial);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			List<Pair> pairs = new ArrayList<>();
			List<Work> works = this.listWork(business, serial);
			works.stream().forEach(o -> {
				Pair p = new Pair();
				p.setCreateTime(o.getCreateTime());
				p.setJob(o.getJob());
				pairs.add(p);
			});
			List<WorkCompleted> workCompleteds = this.listWorkCompleted(business, serial);
			workCompleteds.stream().forEach(o -> {
				Pair p = new Pair();
				p.setCreateTime(o.getCreateTime());
				p.setJob(o.getJob());
				pairs.add(p);
			});
			Pair o = pairs.stream().sorted(Comparator.comparing(Pair::getCreateTime).reversed()).findFirst()
					.orElse(null);
			if (null != o) {
				wo.setValue(o.getJob());
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Pair {

		private Date createTime;

		private String job;

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public String getJob() {
			return job;
		}

		public void setJob(String job) {
			this.job = job;
		}

	}

	private List<Work> listWork(Business business, String serial) throws Exception {
		return business.entityManagerContainer().fetchEqual(Work.class,
				ListTools.toList(JpaObject.createTime_FIELDNAME, Work.serial_FIELDNAME, Work.job_FIELDNAME),
				Work.serial_FIELDNAME, serial);
	}

	private List<WorkCompleted> listWorkCompleted(Business business, String serial) throws Exception {
		return business.entityManagerContainer()
				.fetchEqual(
						WorkCompleted.class, ListTools.toList(JpaObject.createTime_FIELDNAME,
								WorkCompleted.serial_FIELDNAME, WorkCompleted.job_FIELDNAME),
						WorkCompleted.serial_FIELDNAME, serial);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.form.ActionLatestWorkWorkCompletedWithSerial$Wo")
	public class Wo extends WrapString {

		private static final long serialVersionUID = -8665525410401933889L;

	}
}