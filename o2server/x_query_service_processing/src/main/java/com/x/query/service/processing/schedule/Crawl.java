package com.x.query.service.processing.schedule;

import java.util.List;

import com.x.base.core.entity.StorageObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.helper.LanguageProcessingHelper;
import com.x.query.service.processing.helper.LanguageProcessingHelper.Item;

public abstract class Crawl extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(Crawl.class);

	public static String[] SKIP_START_WITH = new String[] { "~", "!", "#", "$", "%", "^", "&", "*", "(", ")", "<", ">",
			"[", "]", "{", "}", "\\", "?" };

	public static final long MAX_ATTACHMENT_BYTE_LENGTH = 10 * 1024 * 1024;

	public static LanguageProcessingHelper languageProcessingHelper = new LanguageProcessingHelper();

	protected List<Item> toWord(String content) {
		return languageProcessingHelper.word(content);
	}

	protected String text(StorageObject storageObject) throws Exception {
		if ((null != storageObject.getLength()) && (storageObject.getLength() > 0)
				&& (storageObject.getLength() < MAX_ATTACHMENT_BYTE_LENGTH)) {
			if (ExtractTextTools.support(storageObject.getName())) {
				try {
					StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
							storageObject.getStorage());
					if (null != mapping) {
						/* 忽略设置强制不索引图片 */
						return ExtractTextTools.extract(storageObject.readContent(mapping), storageObject.getName(),
								Config.query().getExtractOffice(), Config.query().getExtractPdf(),
								Config.query().getExtractText(), false);
					} else {
						logger.print(
								"storageMapping is null can not extract storageObject text, storageObject:{}, name:{}.",
								storageObject.getId(), storageObject.getName());
					}
				} catch (Exception e) {
					logger.print("error extract attachment text, storageObject:{}, name:{}.", storageObject.getId(),
							storageObject.getName());
				}
			}
		} else {
			logger.print("忽略过大的附件:{}, size:{}, id:{}.", storageObject.getName(), storageObject.getLength(),
					storageObject.getId());
		}
		return "";
	}

}