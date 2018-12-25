package com.x.query.service.processing.test;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.hankcs.hanlp.HanLP;
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
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println(HanLP.extractKeyword(text, 30));
		System.out.println(HanLP.extractWords(text, 30));
	}
}
