package com.x.ai.assemble.control;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x.ai.assemble.control.quartz.CmsDocumentIndexTask;
import com.x.ai.assemble.control.quartz.InitConfigTask;
import com.x.ai.assemble.control.queue.QueueDocumentIndex;
import com.x.ai.core.entity.AiModel;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import java.io.File;
import java.net.URL;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 * 应用初始化及销毁业务处理
 * @author sword
 */
public class ThisApplication {
	private static final Logger logger = LoggerFactory.getLogger(ThisApplication.class);

	private static final Gson gson = XGsonBuilder.instance();

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static final QueueDocumentIndex queueDocumentIndex = new QueueDocumentIndex();

	public static void init() throws Exception {
		context().startQueue(queueDocumentIndex);
		context.schedule(CmsDocumentIndexTask.class, "0 0 1,12 * * ?");
		context.schedule(InitConfigTask.class, "0 0/5 * * * ?");
		initModel();
	}

	private static void initModel(){
		try {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Long count = emc.count(AiModel.class);
				if (count == 0) {
					URL jsonUrl = Thread.currentThread().getContextClassLoader().getResource("InitModel.json");
					if(jsonUrl != null){
						emc.beginTransaction(AiModel.class);
						File file = new File(jsonUrl.toURI());
						String json = FileUtils.readFileToString(file, DefaultCharset.charset);
						List<AiModel> mcpList = gson.fromJson(json, new TypeToken<List<AiModel>>(){}.getType());
						for (AiModel mcp : mcpList) {
							emc.persist(mcp);
						}
						emc.commit();
					}
				}
			}
		} catch (Exception e) {
			logger.warn("初始化模型数据时发生异常:" + e.getMessage());
		}
	}

	public static void destroy() {
	}
}
