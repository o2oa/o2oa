package com.x.query.assemble.surface.jaxrs.entry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.segment.Entry;
import com.x.query.core.entity.segment.Word;

class ActionSearchAccessible extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String key) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			List<String> keys = new ArrayList<>();
			for (IWord iWord : ThisApplication.analyzer.analyze(key).wordList) {
				/* 过滤掉太长的字符 */
				if (StringUtils.length(iWord.getValue()) < 31) {
					keys.add(iWord.getValue());
				}
			}
			if (!keys.isEmpty()) {
				List<Word> words = emc.listIn(Word.class, Word.value_FIELDNAME, keys);
				LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
				for (Word word : words) {
					Integer count = map.get(word.getEntry());
					if (null != count) {
						map.put(word.getEntry(), count + word.getCount());
					} else {
						map.put(word.getEntry(), word.getCount());
					}
				}

				List<String> ids = new ArrayList<>();
				map.entrySet().stream().sorted(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed())
						.limit(200).forEach(o -> {
							ids.add(o.getKey());
						});
				List<Entry> os = emc.list(Entry.class, ids);
				wos = Wo.copier.copy(os);
				wos = wos.stream().sorted(
						Comparator.comparing(Wo::getLastUpdateTime, Comparator.nullsFirst(Date::compareTo).reversed()))
						.collect(Collectors.toList());
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Entry {

		private static final long serialVersionUID = -8067704098385000667L;

		static WrapCopier<Entry, Wo> copier = WrapCopierFactory.wo(Entry.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}
}