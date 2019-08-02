package com.x.query.assemble.surface.jaxrs.segment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

abstract class BaseAction extends StandardJaxrsAction {
	protected List<String> keys(String key) {
		List<String> os = new ArrayList<>();
		for (Term term : HanLP.segment(key)) {
			/* 字段不要太长 */
			if (StringUtils.length(term.word) < 31) {
				os.add(term.word);
			}
		}
		return os;
	}
}
