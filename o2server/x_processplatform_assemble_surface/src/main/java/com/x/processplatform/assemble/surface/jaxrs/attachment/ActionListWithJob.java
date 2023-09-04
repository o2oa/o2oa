package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListWithJob extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithJob.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String job) throws Exception {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> job);

		ActionResult<List<Wo>> result = new ActionResult<>();
		CompletableFuture<List<Wo>> listFuture = listFuture(effectivePerson, job);
		CompletableFuture<Boolean> checkControlFuture = checkJobControlFuture(effectivePerson, job);
		result.setData(listFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
		if (BooleanUtils
				.isFalse(checkControlFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS))) {
			throw new ExceptionAccessDenied(effectivePerson, job);
		}
		return result;

	}

	private CompletableFuture<List<Wo>> listFuture(EffectivePerson effectivePerson, String flag) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> wos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
				List<String> units = business.organization().unit().listWithPerson(effectivePerson);
				final String job = flag;
				for (Attachment attachment : business.entityManagerContainer().listEqual(Attachment.class,
						Attachment.job_FIELDNAME, job)) {
					Wo wo = Wo.copier.copy(attachment);
					boolean canControl = this.control(attachment, effectivePerson, identities, units, business);
					boolean canEdit = this.edit(attachment, effectivePerson, identities, units, business);
					boolean canRead = this.read(attachment, effectivePerson, identities, units, business);
					if (canRead) {
						wo.getControl().setAllowRead(true);
						wo.getControl().setAllowEdit(canEdit);
						wo.getControl().setAllowControl(canControl);
						wos.add(wo);
					}
				}
				wos = wos.stream()
						.sorted(Comparator.comparing(Wo::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
								.thenComparing(
										Comparator.comparing(Wo::getCreateTime, Comparator.nullsLast(Date::compareTo))))
						.collect(Collectors.toList());
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return wos;
		}, ThisApplication.forkJoinPool());
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionListWithJob$Wo")
	public static class Wo extends Attachment {

		private static final long serialVersionUID = -7666329770246726197L;

		static WrapCopier<Attachment, Wo> copier = WrapCopierFactory.wo(Attachment.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private WoControl control = new WoControl();

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
			this.control = control;
		}

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionListWithJob$WoControl")
	public static class WoControl extends GsonPropertyObject {
		private static final long serialVersionUID = -7283783148043076205L;
		@FieldDescribe("可读.")
		@Schema(description = "可读.")
		private Boolean allowRead = false;
		@FieldDescribe("可写.")
		@Schema(description = "可写.")
		private Boolean allowEdit = false;
		@FieldDescribe("可控制.")
		@Schema(description = "可控制.")
		private Boolean allowControl = false;

		public Boolean getAllowRead() {
			return allowRead;
		}

		public void setAllowRead(Boolean allowRead) {
			this.allowRead = allowRead;
		}

		public Boolean getAllowEdit() {
			return allowEdit;
		}

		public void setAllowEdit(Boolean allowEdit) {
			this.allowEdit = allowEdit;
		}

		public Boolean getAllowControl() {
			return allowControl;
		}

		public void setAllowControl(Boolean allowControl) {
			this.allowControl = allowControl;
		}

	}

}
