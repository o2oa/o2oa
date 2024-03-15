package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.io.File;
import java.io.OutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.x.processplatform.assemble.surface.*;
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
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

class BaseBatchDownloadWithWorkOrWorkCompleted extends BaseAction {

	protected String adjustFileName(String fileName, String title) {
		if (StringUtils.isBlank(fileName)) {
			if(StringUtils.isNotBlank(title)) {
				if (title.length() > 60) {
					title = title.substring(0, 60);
				}
				fileName = title + DateTools.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss) + ".zip";
			}else{
				fileName = DateTools.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss) + ".zip";
			}
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

	protected List<Pair<String, String>> getTitleAndJob(EffectivePerson effectivePerson, Business business, String workIds) {
		List<Pair<String, String>> list = new ArrayList<>();
		Set<String> workIdSet = new HashSet<>(Arrays.asList(workIds.split(SITE_SEPARATOR)));
		for(String workId : workIdSet) {
			Control control = new JobControlBuilder(effectivePerson, business, workId).enableAllowVisit().build();
			if (BooleanUtils.isTrue(control.getAllowVisit())) {
				list.add(Pair.of(StringUtils.isBlank(control.getWorkTitle()) ? "无标题" : control.getWorkTitle(), control.getWorkJob()));
			}
		}
		return list;
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

	/**
	 * 下载附件并打包为zip
	 *
	 * @param readableMap
	 * @param os
	 * @throws Exception
	 */
	protected void downToZip(final Map<String, List<Attachment>> readableMap, OutputStream os, Map<String, byte[]> otherAttMap)
			throws Exception {
		Map<String, Attachment> filePathMap = new HashMap<>();
		List<String> emptyFolderList = new ArrayList<>();
		/* 生成zip压缩文件内的目录结构 */
		if (readableMap != null) {
			for(String key : readableMap.keySet()) {
				String encodeKey = StringUtils.replaceEach(key, Business.FILENAME_SENSITIVES_KEY, Business.FILENAME_SENSITIVES_EMPTY);
				for (Attachment att : readableMap.get(key)) {
					String name = StringUtils.replaceEach(att.getName(), Business.FILENAME_SENSITIVES_KEY, Business.FILENAME_SENSITIVES_EMPTY);
					name = encodeKey + File.separator + name;
					if (filePathMap.containsKey(name)) {
						name = encodeKey + File.separator + att.getSite() + "-" + name;
					}
					filePathMap.put(name, att);
				}
			}
		}
		try (ZipOutputStream zos = new ZipOutputStream(os)) {
			for (Map.Entry<String, Attachment> entry : filePathMap.entrySet()) {
				zos.putNextEntry(new ZipEntry(entry.getKey()));
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						entry.getValue().getStorage());
				entry.getValue().readContent(mapping, zos);
			}

			if (otherAttMap != null) {
				for (Map.Entry<String, byte[]> entry : otherAttMap.entrySet()) {
					zos.putNextEntry(new ZipEntry(StringUtils.replaceEach(entry.getKey(), Business.FILENAME_SENSITIVES_KEY,
							Business.FILENAME_SENSITIVES_EMPTY)));
					zos.write(entry.getValue());
				}
			}

			// 往zip里添加空文件夹
			for (String emptyFolder : emptyFolderList) {
				zos.putNextEntry(new ZipEntry(emptyFolder));
			}
		}
	}
}
