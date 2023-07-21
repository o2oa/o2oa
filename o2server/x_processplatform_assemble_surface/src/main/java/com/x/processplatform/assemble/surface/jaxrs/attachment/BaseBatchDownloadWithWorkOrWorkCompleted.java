package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;
import com.x.general.core.entity.GeneralFile;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

class BaseBatchDownloadWithWorkOrWorkCompleted extends BaseAction {

	protected String adjustFileName(String fileName, String title) {
		if (StringUtils.isBlank(fileName)) {
			if (title.length() > 60) {
				title = title.substring(0, 60);
			}
			fileName = title + DateTools.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss) + ".zip";
		} else {
			String extension = FilenameUtils.getExtension(fileName);
			if (StringUtils.isEmpty(extension)) {
				fileName = fileName + ".zip";
			}
		}
		fileName = StringUtils.replaceEach(fileName, Business.FILENAME_SENSITIVES_KEY,
				Business.FILENAME_SENSITIVES_EMPTY);
		return fileName;
	}

	protected List<Attachment> listAttachment(Business business, String site, String job) throws Exception {
		List<Attachment> attachmentList;
		if (StringUtils.isBlank(site) || EMPTY_SYMBOL.equals(site)) {
			attachmentList = business.attachment().listWithJobObject(job);
		} else if (site.indexOf(SITE_SEPARATOR) == -1) {
			attachmentList = business.entityManagerContainer().listEqualAndEqual(Attachment.class,
					Attachment.job_FIELDNAME, job, Attachment.site_FIELDNAME, site);
		} else {
			attachmentList = business.entityManagerContainer().listEqualAndIn(Attachment.class,
					Attachment.job_FIELDNAME, job, Attachment.site_FIELDNAME,
					Arrays.asList(site.split(SITE_SEPARATOR)));
		}
		return attachmentList;
	}

	protected Pair<String, String> getTitleAndJob(EffectivePerson effectivePerson, Business business, String workId)
			throws Exception {
		Work work = business.entityManagerContainer().fetch(workId, Work.class);
		if (work != null) {
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowVisit().build();
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionWorkCompletedAccessDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
						work.getId());
			}
			return Pair.of(work.getTitle(), work.getJob());
		} else {
			WorkCompleted workCompleted = business.entityManagerContainer().fetch(workId, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityExist(workId);
			}
			Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
					.enableAllowVisit().build();
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionWorkCompletedAccessDenied(effectivePerson.getDistinguishedName(),
						workCompleted.getTitle(), workCompleted.getId());
			}
			return Pair.of(workCompleted.getTitle(), workCompleted.getJob());
		}
	}

	protected void assembleFile(Business business, Map<String, byte[]> map, String files) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		if (StringUtils.isNotEmpty(files)) {
			String[] flagList = files.split(FILE_SEPARATOR);
			for (String flag : flagList) {
				if (StringUtils.isNotBlank(flag)) {
					GeneralFile generalFile = emc.find(flag.trim(), GeneralFile.class);
					if (generalFile != null) {
						StorageMapping gfMapping = ThisApplication.context().storageMappings().get(GeneralFile.class,
								generalFile.getStorage());
						map.put(generalFile.getName(), generalFile.readContent(gfMapping));

						generalFile.deleteContent(gfMapping);
						emc.beginTransaction(GeneralFile.class);
						emc.delete(GeneralFile.class, generalFile.getId());
						emc.commit();
					}
				}
			}
		}
	}
}
