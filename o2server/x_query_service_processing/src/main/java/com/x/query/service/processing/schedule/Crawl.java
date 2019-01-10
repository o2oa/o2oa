package com.x.query.service.processing.schedule;

import java.util.List;

import org.quartz.Job;

import com.x.base.core.entity.StorageObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.query.service.processing.ExtractTextTools;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.helper.LanguageProcessingHelper;
import com.x.query.service.processing.helper.LanguageProcessingHelper.Item;

public abstract class Crawl implements Job {

	private static Logger logger = LoggerFactory.getLogger(Crawl.class);

	public static String[] SKIP_START_WITH = new String[] { "~", "!", "#", "$", "%", "^", "&", "*", "(", ")", "<", ">",
			"[", "]", "{", "}", "\\", "?" };

	public static LanguageProcessingHelper languageProcessingHelper = new LanguageProcessingHelper();

	protected List<Item> toWord(String content) {
		return languageProcessingHelper.word(content);
	}

	protected String text(StorageObject storageObject) throws Exception {
		if (ExtractTextTools.support(storageObject.getName())) {
			try {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						storageObject.getStorage());
				if (null != mapping) {
					return ExtractTextTools.extract(storageObject.readContent(mapping), storageObject.getName(),
							Config.query().getExtractOffice(), Config.query().getExtractPdf(),
							Config.query().getExtractText(), Config.query().getExtractImage());
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
		return "";
	}

}