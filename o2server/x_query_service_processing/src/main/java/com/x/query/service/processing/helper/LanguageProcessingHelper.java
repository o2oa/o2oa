package com.x.query.service.processing.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class LanguageProcessingHelper {

	private static Logger logger = LoggerFactory.getLogger(LanguageProcessingHelper.class);

	public static String[] SKIP_START_WITH = new String[] { "~", "!", "#", "$", "%", "^", "&", "*", "(", ")", "<", ">",
			"[", "]", "{", "}", "\\", "?" };

	public static String[] SKIP_END_WITH = new String[] { "~", "!", "#", "$", "%", "^", "&", "*", "(", ")", "<", ">",
			"[", "]", "{", "}", "\\", "?" };

	public List<Item> word(String content) {
		List<Item> items = new ArrayList<>();
		if (StringUtils.isNotBlank(content)) {
			for (Term t : HanLP.segment(content)) {
				Item item = new Item();
				item.setLabel(t.nature.toString());
				/* 去掉中文空格和空格 */
				item.setValue(StringUtils.trimToEmpty(StringUtils.replace(t.word, "　", " ")));
				if (!skip(item)) {
					items.add(item);
				}
			}
		}
		/*
		 * b 区别词 c 连词 d 副词 e 叹词 f 方位词 h 前缀 k 后缀 o 拟声词 p 介词 q 量词 r 代词 u 组词 w 标点
		 */
		items = items.stream()
//				.filter(o -> (StringUtils.length(o.getValue()) > 1)
//						&& (!StringUtils.startsWithAny(o.getValue(), SKIP_START_WITH))
//						&& (!StringUtils.endsWithAny(o.getValue(), SKIP_END_WITH))
//						&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "b"))
//						&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "c"))
//						&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "d"))
//						&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "e"))
//						&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "f"))
//						&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "h"))
//						&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "k"))
//						&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "o"))
//						&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "p"))
//						&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "q"))
//						&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "r"))
//						&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "u"))
//						&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "w")) && (!label_skip_m(o)))
				.collect(Collectors.toList());
		Map<Item, Long> map = items.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		List<Item> list = new ArrayList<>();
		map.entrySet().stream().sorted(Map.Entry.<Item, Long>comparingByValue().reversed()).forEach(o -> {
			Item t = o.getKey();
			t.setCount(o.getValue());
			list.add(t);
		});
		return list;
	}

	private boolean skip(Item o) {
		if ((StringUtils.length(o.getValue()) > 1) && (!StringUtils.startsWithAny(o.getValue(), SKIP_START_WITH))
				&& (!StringUtils.endsWithAny(o.getValue(), SKIP_END_WITH))
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
				&& (!StringUtils.startsWithIgnoreCase(o.getLabel(), "w")) && (!label_skip_m(o))) {
			return false;
		}
		return true;
	}

	private boolean label_skip_m(Item item) {
		if (!StringUtils.startsWithIgnoreCase(item.getLabel(), "m")) {
			return false;
		} else {
			return NumberUtils.isParsable(item.getValue());
		}
	}

	public static class Item extends GsonPropertyObject {

		private String value;

		private String label;

		private Long count;

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

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
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
			Item other = (Item) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
	}
}