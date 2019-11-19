package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.WorkCompletedControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Attachment_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ActionBatchDownloadWithWorkOrWorkCompleted extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionBatchDownloadWithWorkOrWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, String site) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String title = "";
			String job = "";
			Work work = emc.find(workId, Work.class);
			if(work == null){
				WorkCompleted workCompleted = emc.find(workId, WorkCompleted.class);
				if (null == workCompleted) {
					throw new Exception("workId: "+workId+" not exist in work or workCompleted");
				}
				if(!business.readable(effectivePerson, workCompleted)){
					throw new ExceptionWorkCompletedAccessDenied(effectivePerson.getDistinguishedName(),
							workCompleted.getTitle(), workCompleted.getId());
				}
				title = workCompleted.getTitle();
				job = workCompleted.getJob();
			}else{
				if(!business.readable(effectivePerson, work)){
					throw new ExceptionAccessDenied(effectivePerson, work);
				}
				title = work.getTitle();
				job = work.getJob();
			}
			List<Attachment> attachmentList;
			if (StringUtils.isBlank(site) || EMPTY_SYMBOL.equals(site)) {
				attachmentList = business.attachment().listWithJobObject(job);
			}else{
				attachmentList = emc.listEqualAndEqual(Attachment.class, Attachment.job_FIELDNAME, job, Attachment.site_FIELDNAME, site);
			}
			String zipName = title + DateTools.format(new Date(),DateTools.formatCompact_yyyyMMddHHmmss) + ".zip";
			logger.info("batchDown to {}，att size {}, from work {}",zipName, attachmentList.size(), workId);
			try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				business.downToZip(attachmentList, os);
				byte[] bs = os.toByteArray();
				Wo wo = new Wo(bs, this.contentType(false, zipName),
						this.contentDisposition(false, zipName));
				result.setData(wo);
			}

			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

	public static class WoControl extends WorkCompletedControl {
	}

}
