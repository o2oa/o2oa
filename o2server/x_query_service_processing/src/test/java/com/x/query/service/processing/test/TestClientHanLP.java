package com.x.query.service.processing.test;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;
import com.hankcs.hanlp.mining.word.WordInfo;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.x.query.service.processing.helper.ExtractTextHelper;
import com.x.query.service.processing.helper.LanguageProcessingHelper;

public class TestClientHanLP {

	@Test
	public void test2() throws Exception {
		LanguageProcessingHelper lph = new LanguageProcessingHelper();
		File file = new File("d:/1.doc");
		String text = ExtractTextHelper.extract(FileUtils.readFileToByteArray(file), file.getName(), true, true, true,
				false);
		System.out.println(text);
		System.out.println("!!!!");
		System.out.println(HanLP.extractKeyword(text, 30));
		System.out.println(HanLP.extractWords(text, 30));
	}

	@Test
	public void test3() {
		System.out.println(CoreSynonymDictionary.similarity("西红柿", "番茄"));
	}

	@Test
	public void test4() {
		List<Term> termList = StandardTokenizer.segment("商品和服务");
		for (Term term : termList) {
			System.out.println(term.word);
			System.out.println(term.nature.toString());
		}
	}

	@Test
	public void test5() throws Exception {
		String str = FileUtils.readFileToString(new File("d:/1.txt"), "utf-8");
		List<String> list = HanLP.extractKeyword(str, 10);
		for (String s : list) {
			System.out.println(s);
		}
	}
	
	@Test
	public void test6() throws Exception {
		String str = FileUtils.readFileToString(new File("d:/1.txt"), "utf-8");
		List<WordInfo> list = HanLP.extractWords(str, 10);
		for (WordInfo s : list) {
			//System.out.println(XGsonBuilder.toJson(s));
			System.out.println(s);
		}
	}
	
	@Test
	public void test7() throws Exception {
		String str = FileUtils.readFileToString(new File("d:/1.txt"), "utf-8");
		List<String> list = HanLP.extractSummary(str, 10);
		for (String s : list) {
			System.out.println(s);
		}
	}

}
