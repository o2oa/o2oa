package com.x.query.service.processing.test;

import java.io.IOException;
import java.util.Map;

import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;

/**
 * 演示词向量的训练与应用
 *
 * @author hankcs
 */
public class DemoWord2Vec {
	// private static final String TRAIN_FILE_NAME =
	// TestUtility.ensureTestData("搜狗文本分类语料库已分词.txt",
	// "http://hanlp.linrunsoft.com/release/corpus/sogou-mini-segmented.zip");
	private static String MODEL_FILE_NAME = "";

	public static void main(String[] args) throws Exception {
		MODEL_FILE_NAME = "d:/o2server/commons/hanlp/data/hanlp-wiki-vec-zh.txt";
		WordVectorModel wordVectorModel = trainOrLoadModel();
		printNearest("中国", wordVectorModel);
		printNearest("美丽", wordVectorModel);
		printNearest("购买", wordVectorModel);

		// 文档向量
		DocVectorModel docVectorModel = new DocVectorModel(wordVectorModel);
		String[] documents = new String[] { "山东苹果丰收", "农民在江苏种水稻", "奥运会女排夺冠", "世界锦标赛胜出", "中国足球失败", };

		System.out.println(docVectorModel.similarity(documents[0], documents[1]));
		System.out.println(docVectorModel.similarity(documents[0], documents[4]));

		for (int i = 0; i < documents.length; i++) {
			docVectorModel.addDocument(i, documents[i]);
		}

		printNearestDocument("体育", documents, docVectorModel);
		printNearestDocument("农业", documents, docVectorModel);
		printNearestDocument("我要看比赛", documents, docVectorModel);
		printNearestDocument("要不做饭吧", documents, docVectorModel);

		System.out.println(wordVectorModel.similarity("中国", "美国"));
		System.out.println(wordVectorModel.similarity("中国", "台湾"));
		System.out.println(wordVectorModel.similarity("中国", "专制"));
		System.out.println(wordVectorModel.similarity("美国", "民主"));
	}

	static void printNearest(String word, WordVectorModel model) {
		System.out.printf(
				"\n                                                Word     Cosine\n------------------------------------------------------------------------\n");
		for (Map.Entry<String, Float> entry : model.nearest(word)) {
			System.out.printf("%50s\t\t%f\n", entry.getKey(), entry.getValue());
		}
	}

	static void printNearestDocument(String document, String[] documents, DocVectorModel model) {
		printHeader(document);
		for (Map.Entry<Integer, Float> entry : model.nearest(document)) {
			System.out.printf("%50s\t\t%f\n", documents[entry.getKey()], entry.getValue());
		}
	}

	private static void printHeader(String query) {
		System.out.printf(
				"\n%50s          Cosine\n------------------------------------------------------------------------\n",
				query);
	}

	static WordVectorModel trainOrLoadModel() throws IOException {
		// if (!IOUtil.isFileExisted(MODEL_FILE_NAME)) {
		// if (!IOUtil.isFileExisted(TRAIN_FILE_NAME)) {
		// System.err.println("语料不存在，请阅读文档了解语料获取与格式：https://github.com/hankcs/HanLP/wiki/word2vec");
		// System.exit(1);
		// }
		// Word2VecTrainer trainerBuilder = new Word2VecTrainer();
		// return trainerBuilder.train(TRAIN_FILE_NAME, MODEL_FILE_NAME);
		// }

		return loadModel();
	}

	static WordVectorModel loadModel() throws IOException {
		return new WordVectorModel(MODEL_FILE_NAME);
	}
}