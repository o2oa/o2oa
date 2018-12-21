package com.x.query.service.processing.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.quartz.Job;

import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.query.service.processing.ExtractTextTools;
import com.x.query.service.processing.ThisApplication;

public abstract class Crawl implements Job {

	private static Logger logger = LoggerFactory.getLogger(Crawl.class);

	public static String[] SKIP_START_WITH = new String[] { "~", "!", "#", "$", "%", "^", "&", "*", "(", ")", "<", ">",
			"[", "]", "{", "}", "\\", "?" };

	protected List<WrapWord> toWord(String content) {
		List<WrapWord> words = new ArrayList<>();
		if (StringUtils.isNotEmpty(content)) {
			List<WrapItem> items = new ArrayList<>();
			Sentence sen = ThisApplication.analyzer.analyze(content);
			for (IWord o : sen.wordList) {
				WrapItem w = new WrapItem();
				w.setLabel(o.getLabel());
				w.setValue(o.getValue());
				items.add(w);
			}
			/*
			 * b 区别词 c 连词 d 副词 e 叹词 f 方位词 h 前缀 k 后缀 o 拟声词 p 介词 q 量词 r 代词 u 组词 w 标点
			 */
			items = items.stream()
					.filter(o -> (StringUtils.length(o.getValue()) > 1)
							&& (!StringUtils.startsWithAny(o.getValue(), SKIP_START_WITH))
							&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "b"))
							&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "c"))
							&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "d"))
							&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "e"))
							&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "f"))
							&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "h"))
							&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "k"))
							&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "o"))
							&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "p"))
							&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "q"))
							&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "r"))
							&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "u"))
							&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "w")) && (!label_skip_m(o)))
					.collect(Collectors.toList());
			Map<WrapItem, Long> map = items.stream()
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			for (java.util.Map.Entry<WrapItem, Long> en : map.entrySet()) {
				WrapWord word = new WrapWord();
				word.setCount(en.getValue().intValue());
				word.setLabel(en.getKey().getLabel());
				word.setValue(en.getKey().getValue());
				words.add(word);
			}
		}
		return words;
	}

	private boolean label_skip_m(WrapItem item) {
		if (!StringUtils.startsWithIgnoreCase(item.getLabel(), "m")) {
			return false;
		} else {
			return NumberUtils.isParsable(item.getValue());
		}
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

	public static class WrapItem extends GsonPropertyObject {

		private String value;

		private String label;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WrapItem other = (WrapItem) obj;
			if (label == null) {
				if (other.label != null)
					return false;
			} else if (!label.equals(other.label))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

	}

	public static class WrapWord extends GsonPropertyObject {

		private String value;

		private String label;

		private Integer count;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

	}

}