package com.x.query.service.processing;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.service.processing.jaxrs.neural.mlp.GenerateQueue;
import com.x.query.service.processing.jaxrs.neural.mlp.LearnQueue;
import com.x.query.service.processing.schedule.CrawlCms;
import com.x.query.service.processing.schedule.CrawlWork;
import com.x.query.service.processing.schedule.CrawlWorkCompleted;

public class ThisApplication {

	protected static Context context;

	public static CRFLexicalAnalyzer analyzer;

	public static GenerateQueue generateQueue = new GenerateQueue();

	public static LearnQueue learnQueue = new LearnQueue();

	public static final Set<String> learning_stop_tag = Collections.synchronizedSet(new HashSet<String>());

	public static final Set<String> generating_stop_tag = Collections.synchronizedSet(new HashSet<String>());

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			LoggerFactory.setLevel(Config.logLevel().x_query_service_processing());
			setupHanLP(context);
			analyzer = new CRFLexicalAnalyzer();
			analyzer.enableOffset(false);
			analyzer.enableAllNamedEntityRecognize(true);
			analyzer.enableNumberQuantifierRecognize(true);
			analyzer.enableNameRecognize(true);
			analyzer.enableOrganizationRecognize(true);
			analyzer.enablePlaceRecognize(true);
			generateQueue.start();
			learnQueue.start();
			if (BooleanUtils.isTrue(Config.query().getCrawlWork().getEnable())) {
				context.scheduleLocal(CrawlWork.class, Config.query().getCrawlWork().getCron());
			}
			if (BooleanUtils.isTrue(Config.query().getCrawlWork().getEnable())) {
				context.scheduleLocal(CrawlWorkCompleted.class, Config.query().getCrawlWorkCompleted().getCron());
			}
			if (BooleanUtils.isTrue(Config.query().getCrawlCms().getEnable())) {
				context.scheduleLocal(CrawlCms.class, Config.query().getCrawlCms().getCron());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			learnQueue.stop();
			generateQueue.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setupHanLP(Context context) throws Exception {
		File hamLPProperties = new File(context.path(), "/WEB-INF/classes/hanlp.properties");
		/*
		 * 本配置文件中的路径的根目录，根目录+其他路径=完整路径（支持相对路径，请参考：https://github.com/hankcs/HanLP/pull/
		 * 254） #Windows用户请注意，路径分隔符统一使用
		 */
		String data = "root=" + StringUtils.replace(Config.base(), "\\", "/")
				+ "/commons/hanlp/\nHanLP.Config.ShowTermNature = true\nHanLP.Config.Normalization = true";
		FileUtils.writeStringToFile(hamLPProperties, data, "utf-8", false);
	}

}
