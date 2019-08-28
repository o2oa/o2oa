package com.x.query.service.processing.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.Deflater;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.util.Base64;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import com.x.base.core.project.tools.ByteTools;
import com.x.base.core.project.tools.DefaultCharset;

public class TestClient {

	@Test
	public void test2() throws Exception {
		PDFParser parser = null;
		PDDocument pdDoc = null;
		COSDocument cosDoc = null;
		PDFTextStripper pdfStripper;

		String parsedText;
		File file = new File("d:/3.pdf");
		parser = new PDFParser(new RandomAccessFile(file, "r"));
		parser.parse();
		cosDoc = parser.getDocument();
		pdfStripper = new PDFTextStripper();
		pdDoc = new PDDocument(cosDoc);
		pdfStripper.setStartPage(1);
		pdfStripper.setEndPage(pdDoc.getNumberOfPages());

		// reading text from page 1 to 10
		// if you want to get text from full pdf file use this code
		// pdfStripper.setEndPage(pdDoc.getNumberOfPages());

		System.out.println(pdfStripper.getText(pdDoc));
	}

	@Test
	public void test3() throws Exception {
		File file = new File("d:/test.nnet");
		System.out.println(Base64.encodeBase64String(FileUtils.readFileToByteArray(file)));
	}

	@Test
	public void zipByteArray() throws Exception {
		Deflater compressor = new Deflater();
		compressor.setLevel(Deflater.BEST_COMPRESSION);
		String str = FileUtils.readFileToString(new File("d:/XNNET"));
		byte[] input = Base64.decodeBase64(str);
		try (ByteArrayInputStream in = new ByteArrayInputStream(input)) {
			NeuralNetwork<MomentumBackpropagation> neuralNetwork = NeuralNetwork.load(in);
			// MlpNN mlpNN = MlpNN.fromNeuralNetwork(neuralNetwork);
			// String value = mlpNN.toString();
			// System.out.println(value.getBytes().length);
		}

	}

	@Test
	public void test5() throws Exception {
		String str = FileUtils.readFileToString(new File("d:/code.txt"), DefaultCharset.charset);
		byte[] bs = ByteTools.decompressBase64String(str);
		System.out.println(bs.length);
		String s = ByteTools.compressBase64String(bs);
		System.out.println(s.length());
	}
}
