package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.general.core.entity.GeneralFile;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.StreamingOutput;
import java.util.*;

class ActionBatchDownload extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBatchDownload.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String docId, String site, String fileName, String flag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);

			Document document = emc.find(docId, Document.class);
			if (null == document) {
				throw new ExceptionDocumentNotExists(docId);
			}
			if (!business.isDocumentReader(effectivePerson, document)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<FileInfo> readableAttachmentList = this.listAttachment(business, site, docId);
			fileName = adjustFileName(fileName, document.getTitle());
			Map<String, byte[]> map = new HashMap<>();
			this.assembleFile(business, map, flag);

			StreamingOutput streamingOutput = output -> {
				try {
					business.downToZip(readableAttachmentList, output, map);
					output.flush();
				} catch (Exception e) {
					LOGGER.warn("batchDown error：{}", e.getMessage());
				}
			};

			LOGGER.info("{} do batchDown to:{}, from doc:{}.", effectivePerson.getDistinguishedName(), fileName, docId);
			String fastETag = StringTools.uniqueToken();
			Wo wo = new Wo(streamingOutput, this.contentType(true, fileName),
					this.contentDisposition(true, fileName),
					null, fastETag);
			result.setData(wo);
			return result;
		}
	}

	private List<FileInfo> listAttachment(Business business, String site, String docId) throws Exception {
		List<FileInfo> attachmentList;
		if (StringUtils.isBlank(site) || EMPTY_SYMBOL.equals(site)) {
			attachmentList = business.entityManagerContainer().listEqual(FileInfo.class,
					FileInfo.documentId_FIELDNAME, docId);
		} else if (site.indexOf(SITE_SEPARATOR) == -1) {
			attachmentList = business.entityManagerContainer().listEqualAndEqual(FileInfo.class,
					FileInfo.documentId_FIELDNAME, docId, FileInfo.site_FIELDNAME, site);
		} else {
			attachmentList = business.entityManagerContainer().listEqualAndIn(FileInfo.class,
					FileInfo.documentId_FIELDNAME, docId, FileInfo.site_FIELDNAME,
					Arrays.asList(site.split(SITE_SEPARATOR)));
		}
		return attachmentList;
	}

	private void assembleFile(Business business, Map<String, byte[]> map, String files) throws Exception {
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

	private String adjustFileName(String fileName, String title) {
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
		fileName = StringUtils.replaceEach(fileName, Business.FILENAME_SENSITIVES_KEY, Business.FILENAME_SENSITIVES_EMPTY);
		return fileName;
	}

	@Schema(name = "com.x.cms.assemble.control.jaxrs.fileinfo.ActionBatchDownload$Wo")
	public static class Wo extends WoFile {

		private static final long serialVersionUID = -4350231304623811352L;

		public Wo(StreamingOutput streamingOutput, String contentType, String contentDisposition, Long contentLength, String fastETag) {
			super(streamingOutput, contentType, contentDisposition, contentLength, fastETag);
		}

	}

}
