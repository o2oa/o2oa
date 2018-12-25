package com.x.query.assemble.surface.jaxrs.entry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.assemble.surface.Business;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.segment.Word;

abstract class BaseAction extends StandardJaxrsAction {

	protected LinkedHashMap<String, Integer> search(Business business, String key) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
		List<String> keys = new ArrayList<>();
		for (IWord iWord : ThisApplication.analyzer.analyze(key).wordList) {
			/* 过滤掉太长的字符 */
			if (StringUtils.length(iWord.getValue()) < 31) {
				keys.add(iWord.getValue());
			}
		}
		if (!keys.isEmpty()) {
			List<Word> words = emc.listIn(Word.class, Word.value_FIELDNAME, keys);
			for (Word word : words) {
				Integer count = map.get(word.getEntry());
				if (null != count) {
					map.put(word.getEntry(), count + word.getCount());
				} else {
					map.put(word.getEntry(), word.getCount());
				}
			}
		}
		return map;
	}

}
