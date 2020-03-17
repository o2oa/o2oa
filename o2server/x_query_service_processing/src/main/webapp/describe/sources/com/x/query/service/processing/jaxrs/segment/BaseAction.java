package com.x.query.service.processing.jaxrs.segment;

import org.apache.commons.lang3.StringUtils;

import com.hankcs.hanlp.HanLP;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.query.core.entity.segment.Entry;
import com.x.query.core.entity.segment.Word;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.helper.LanguageProcessingHelper;

abstract class BaseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	private static LanguageProcessingHelper languageProcessingHelper = new LanguageProcessingHelper();

	protected final static int BATCHSIZE = 100;

	protected void setSummary(Entry entry, String body, String attachment) {
		String summary = StringUtils.join(HanLP.extractSummary(body + attachment, 10), ",");
		summary = StringUtils.deleteWhitespace(summary);
		entry.setSummary(StringTools.utf8SubString(summary, JpaObject.length_255B));
	}

	protected void titleToWord(Business business, String title, Entry entry) throws Exception {
		if (StringUtils.isNotEmpty(title)) {
			for (LanguageProcessingHelper.Item o : languageProcessingHelper.word(title)) {
				Word word = this.createWord(o, entry);
				if (null != word) {
					word.setTag(Word.TAG_TITLE);
					business.entityManagerContainer().persist(word, CheckPersistType.all);
				}
			}
		}
	}

	protected void bodyToWord(Business business, String body, Entry entry) throws Exception {
		if (StringUtils.isNotEmpty(body)) {
			for (LanguageProcessingHelper.Item o : languageProcessingHelper.word(body)) {
				Word word = this.createWord(o, entry);
				if (null != word) {
					word.setTag(Word.TAG_BODY);
					business.entityManagerContainer().persist(word, CheckPersistType.all);
				}
			}
		}
	}

	protected void attachmentToWord(Business business, String attachment, Entry entry) throws Exception {
		if (StringUtils.isNotEmpty(attachment)) {
			for (LanguageProcessingHelper.Item o : languageProcessingHelper.word(attachment)) {
				Word word = this.createWord(o, entry);
				if (null != word) {
					word.setTag(Word.TAG_ATTACHMENT);
					business.entityManagerContainer().persist(word, CheckPersistType.all);
				}
			}
		}
	}

	protected void keywordPhraseToWord(Business business, String title, String body, String attachment, Entry entry)
			throws Exception {
		String value = title + body + attachment;
		for (String str : HanLP.extractKeyword(value, 32)) {
			Word word = this.createWord(str, entry);
			if (null != word) {
				word.setTag(Word.TAG_KEYWORD);
				business.entityManagerContainer().persist(word, CheckPersistType.all);
			}
		}
		for (String str : HanLP.extractPhrase(value, 16)) {
			Word word = this.createWord(str, entry);
			if (null != word) {
				word.setTag(Word.TAG_PHRASE);
				business.entityManagerContainer().persist(word, CheckPersistType.all);
			}
		}
	}

	private Word createWord(String value, Entry entry) {
		if (StringUtils.length(value) < 31) {
			Word word = new Word(entry);
			word.setValue(StringUtils.lowerCase(value));
			word.setCount(1);
			return word;
		}
		return null;
	}

	private Word createWord(LanguageProcessingHelper.Item item, Entry entry) {
		if (StringUtils.length(item.getValue()) < 31) {
			/* 可能产生过长的字比如...................................... */
			Word word = new Word(entry);
			word.setValue(StringUtils.lowerCase(item.getValue()));
			word.setLabel(item.getLabel());
			word.setCount(item.getCount().intValue());
			return word;
		}
		return null;
	}

	protected String storageObjectToText(StorageObject storageObject) throws Exception {
		if ((null != storageObject.getLength()) && (storageObject.getLength() > 0)
				&& (storageObject.getLength() < Config.query().getCrawlWork().getMaxAttachmentSize())) {
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
			logger.print("忽略文件长度为0或者过大的附件:{}, size:{}, id:{}.", storageObject.getName(), storageObject.getLength(),
					storageObject.getId());
		}
		return "";
	}
}
