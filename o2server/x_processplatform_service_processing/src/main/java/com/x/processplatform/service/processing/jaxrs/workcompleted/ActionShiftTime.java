package com.x.processplatform.service.processing.jaxrs.workcompleted;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Data.DataWork;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.express.service.processing.jaxrs.workcompleted.ActionShiftTimeWi;
import com.x.processplatform.core.express.service.processing.jaxrs.workcompleted.ActionShiftTimeWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.query.core.entity.Item;

public class ActionShiftTime extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionShiftTime.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		Wi wi = null;

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
			WorkCompleted workCompleted = emc.fetch(wi.getId(), WorkCompleted.class,
					ListTools.toList(WorkCompleted.job_FIELDNAME));
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(wi.getId(), WorkCompleted.class);
			}
			executorSeed = workCompleted.getJob();
		}

		return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(new CallableAction(wi)).get(300,
				TimeUnit.SECONDS);
	}

	public static class Wi extends ActionShiftTimeWi {

		private static final long serialVersionUID = -7222129694132489744L;

	}

	public static class Wo extends ActionShiftTimeWo {

		private static final long serialVersionUID = 8166148918001178788L;
	}

	public class CallableAction implements Callable<ActionResult<Wo>> {

		private Wi wi;

		CallableAction(Wi wi) {
			this.wi = wi;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			WorkCompleted workCompleted = null;
			Integer adjustMinutes = wi.getAdjustMinutes();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

				workCompleted = emc.find(wi.getId(), WorkCompleted.class);

				Business business = new Business(emc);
				if ((null != workCompleted) && (null != adjustMinutes) && (adjustMinutes != 0)) {
					if (BooleanUtils.isTrue(wi.getWorkCompletedEnable())) {
						adjustWorkCompleted(business, workCompleted, adjustMinutes);
					}
					if (BooleanUtils.isTrue(wi.getRecordEnable())) {
						adjustRecord(business, workCompleted, adjustMinutes);
					}
					if (BooleanUtils.isTrue(wi.getTaskCompletedEnable())) {
						adjustTaskCompleted(business, workCompleted, adjustMinutes);
					}
					if (BooleanUtils.isTrue(wi.getReadEnable())) {
						adjustRead(business, workCompleted, adjustMinutes);
					}
					if (BooleanUtils.isTrue(wi.getReadCompletedEnable())) {
						adjustReadCompleted(business, workCompleted, adjustMinutes);
					}
					if (BooleanUtils.isTrue(wi.getReviewEnable())) {
						adjustReview(business, workCompleted, adjustMinutes);
					}
					if (BooleanUtils.isTrue(wi.getAttachmentEnable())) {
						adjustAttachment(business, workCompleted, adjustMinutes);
					}
					if (BooleanUtils.isTrue(wi.getDataEnable()) && BooleanUtils.isNotTrue(workCompleted.getMerged())) {
						adjustData(business, workCompleted, adjustMinutes);
					}
					emc.commit();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new ExceptionShiftTime(e, wi.getId());
			}
			Wo wo = new Wo();
			wo.setValue(true);
			ActionResult<Wo> result = new ActionResult<>();
			result.setData(wo);
			return result;
		}

		private void adjustWorkCompleted(Business business, WorkCompleted workCompleted, Integer adjustMinutes)
				throws Exception {
			business.entityManagerContainer().beginTransaction(WorkCompleted.class);
			adjust(workCompleted.getCreateTime(), adjustMinutes).ifPresent(workCompleted::setCreateTime);
			adjust(workCompleted.getStartTime(), adjustMinutes).ifPresent(workCompleted::setStartTime);
			adjust(workCompleted.getCompletedTime(), adjustMinutes).ifPresent(workCompleted::setCompletedTime);
			adjust(workCompleted.getExpireTime(), adjustMinutes).ifPresent(workCompleted::setExpireTime);
		}

		private void adjustRecord(Business business, WorkCompleted workCompleted, Integer adjustMinutes)
				throws Exception {
			List<Record> list = business.entityManagerContainer().listEqual(Record.class, Record.job_FIELDNAME,
					workCompleted.getJob());
			business.entityManagerContainer().beginTransaction(Record.class);
			for (Record rec : list) {
				adjust(rec.getCreateTime(), adjustMinutes).ifPresent(rec::setCreateTime);
				adjust(rec.getRecordTime(), adjustMinutes).ifPresent(rec::setRecordTime);
				adjust(rec.getStartTime(), adjustMinutes).ifPresent(rec::setStartTime);
			}
		}

		private void adjustTaskCompleted(Business business, WorkCompleted workCompleted, Integer adjustMinutes)
				throws Exception {
			List<TaskCompleted> list = business.entityManagerContainer().listEqual(TaskCompleted.class,
					TaskCompleted.job_FIELDNAME, workCompleted.getJob());
			business.entityManagerContainer().beginTransaction(TaskCompleted.class);
			for (TaskCompleted o : list) {
				adjust(o.getCreateTime(), adjustMinutes).ifPresent(o::setCreateTime);
				adjust(o.getCompletedTime(), adjustMinutes).ifPresent(o::setCompletedTime);
				adjust(o.getStartTime(), adjustMinutes).ifPresent(o::setStartTime);
				adjust(o.getExpireTime(), adjustMinutes).ifPresent(o::setExpireTime);
				adjust(o.getPressTime(), adjustMinutes).ifPresent(o::setPressTime);
				adjust(o.getRetractTime(), adjustMinutes).ifPresent(o::setRetractTime);
				adjust(o.getViewTime(), adjustMinutes).ifPresent(o::setViewTime);
			}
		}

		private void adjustRead(Business business, WorkCompleted workCompleted, Integer adjustMinutes)
				throws Exception {
			List<Read> list = business.entityManagerContainer().listEqual(Read.class, Read.job_FIELDNAME,
					workCompleted.getJob());
			business.entityManagerContainer().beginTransaction(Read.class);
			for (Read o : list) {
				adjust(o.getCreateTime(), adjustMinutes).ifPresent(o::setCreateTime);
				adjust(o.getStartTime(), adjustMinutes).ifPresent(o::setStartTime);
				adjust(o.getViewTime(), adjustMinutes).ifPresent(o::setViewTime);
			}
		}

		private void adjustReadCompleted(Business business, WorkCompleted workCompleted, Integer adjustMinutes)
				throws Exception {
			List<ReadCompleted> list = business.entityManagerContainer().listEqual(ReadCompleted.class,
					ReadCompleted.job_FIELDNAME, workCompleted.getJob());
			business.entityManagerContainer().beginTransaction(ReadCompleted.class);
			for (ReadCompleted o : list) {
				adjust(o.getCreateTime(), adjustMinutes).ifPresent(o::setCreateTime);
				adjust(o.getCompletedTime(), adjustMinutes).ifPresent(o::setCompletedTime);
				adjust(o.getStartTime(), adjustMinutes).ifPresent(o::setStartTime);
			}
		}

		private void adjustReview(Business business, WorkCompleted workCompleted, Integer adjustMinutes)
				throws Exception {
			List<Review> list = business.entityManagerContainer().listEqual(Review.class, Review.job_FIELDNAME,
					workCompleted.getJob());
			business.entityManagerContainer().beginTransaction(Review.class);
			for (Review o : list) {
				adjust(o.getCreateTime(), adjustMinutes).ifPresent(o::setCreateTime);
				adjust(o.getCompletedTime(), adjustMinutes).ifPresent(o::setCompletedTime);
				adjust(o.getStartTime(), adjustMinutes).ifPresent(o::setStartTime);
			}
		}

		private void adjustAttachment(Business business, WorkCompleted workCompleted, Integer adjustMinutes)
				throws Exception {
			List<Attachment> list = business.entityManagerContainer().listEqual(Attachment.class,
					Attachment.job_FIELDNAME, workCompleted.getJob());
			business.entityManagerContainer().beginTransaction(Attachment.class);
			for (Attachment o : list) {
				adjust(o.getCreateTime(), adjustMinutes).ifPresent(o::setCreateTime);
				adjust(o.getWorkCreateTime(), adjustMinutes).ifPresent(o::setWorkCreateTime);
				adjust(o.getLastUpdateTime(), adjustMinutes).ifPresent(o::setLastUpdateTime);
			}
		}

		private void adjustData(Business business, WorkCompleted workCompleted, Integer adjustMinutes)
				throws Exception {
			List<Item> exists = business.entityManagerContainer().listEqualAndEqual(Item.class,
					DataItem.bundle_FIELDNAME, workCompleted.getJob(), DataItem.itemCategory_FIELDNAME,
					ItemCategory.pp);
			DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
			JsonElement jsonElement = converter.assemble(exists);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			DataWork dataWork = DataWork.workCompletedCopier.copy(workCompleted);
			dataWork.setWorkId(workCompleted.getId());
			dataWork.setCompleted(true);
			jsonObject.add(Data.WORK_PROPERTY, gson.toJsonTree(dataWork));
			List<Item> currents = converter.disassemble(jsonObject);
			List<Item> removes = converter.subtract(exists, currents);
			List<Item> adds = converter.subtract(currents, exists);
			if ((!removes.isEmpty()) || (!adds.isEmpty())) {
				business.entityManagerContainer().beginTransaction(Item.class);
				for (Item _o : removes) {
					business.entityManagerContainer().remove(_o);
				}
				for (Item _o : adds) {
					_o.setDistributeFactor(workCompleted.getDistributeFactor());
					_o.setBundle(workCompleted.getJob());
					_o.setItemCategory(ItemCategory.pp);
					business.entityManagerContainer().persist(_o);
				}
			}
		}
	}

	private Optional<Date> adjust(Date date, Integer adjustMinutes) {
		if (null == date) {
			return Optional.empty();
		}
		return Optional.ofNullable(DateUtils.addMinutes(date, adjustMinutes));
	}

}
