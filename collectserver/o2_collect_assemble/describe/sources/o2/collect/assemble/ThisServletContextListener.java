package o2.collect.assemble;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.openjpa.enhance.PCRegistry;

import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.DataMapping;
import com.x.base.core.project.config.DataMappings;

import o2.base.core.project.config.Config;
import o2.collect.assemble.jaxrs.collect.QueueAppLogReceive;
import o2.collect.assemble.jaxrs.collect.QueuePromptErrorLogReceive;
import o2.collect.assemble.jaxrs.collect.QueuePushMessageTransfer;
import o2.collect.assemble.jaxrs.collect.QueueTransmitReceive;
import o2.collect.assemble.jaxrs.collect.QueueUnexpectedErrorLogReceive;
import o2.collect.assemble.jaxrs.collect.QueueWarnLogReceive;
import o2.collect.assemble.sms.SmsSender;
import o2.collect.assemble.task.CleanDevice;
import o2.collect.assemble.task.CleanExpiredCode;
import o2.collect.assemble.task.CleanLog;
import o2.collect.core.entity.Account;
import o2.collect.core.entity.Code;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.Module;
import o2.collect.core.entity.Unit;
import o2.collect.core.entity.log.AppLog;
import o2.collect.core.entity.log.PromptErrorLog;
import o2.collect.core.entity.log.UnexpectedErrorLog;
import o2.collect.core.entity.log.WarnLog;

@WebListener
public class ThisServletContextListener implements ServletContextListener {

	private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public static QueueUnexpectedErrorLogReceive queueUnexpectedErrorLogReceive = null;

	public static QueuePromptErrorLogReceive queuePromptErrorLogReceive = null;

	public static QueueWarnLogReceive queueWarnLogReceive = null;

	public static QueueAppLogReceive queueAppLogReceive = null;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			String webApplicationDirectory = sce.getServletContext().getRealPath("");
			DataMappings dataMappings = new DataMappings();
			this.addDataMaping(dataMappings, Account.class,
					"jdbc:h2:tcp://127.0.0.1:" + Config.dataServer().getTcpPort() + "/o2", "sa",
					Config.token().getPassword(), 1);
			this.addDataMaping(dataMappings, Code.class,
					"jdbc:h2:tcp://127.0.0.1:" + Config.dataServer().getTcpPort() + "/o2", "sa",
					Config.token().getPassword(), 1);
			this.addDataMaping(dataMappings, Device.class,
					"jdbc:h2:tcp://127.0.0.1:" + Config.dataServer().getTcpPort() + "/o2", "sa",
					Config.token().getPassword(), 1);
			this.addDataMaping(dataMappings, Unit.class,
					"jdbc:h2:tcp://127.0.0.1:" + Config.dataServer().getTcpPort() + "/o2", "sa",
					Config.token().getPassword(), 1);
			this.addDataMaping(dataMappings, UnexpectedErrorLog.class,
					"jdbc:h2:tcp://127.0.0.1:" + Config.dataServer().getTcpPort() + "/o2", "sa",
					Config.token().getPassword(), 1);
			this.addDataMaping(dataMappings, PromptErrorLog.class,
					"jdbc:h2:tcp://127.0.0.1:" + Config.dataServer().getTcpPort() + "/o2", "sa",
					Config.token().getPassword(), 1);
			this.addDataMaping(dataMappings, WarnLog.class,
					"jdbc:h2:tcp://127.0.0.1:" + Config.dataServer().getTcpPort() + "/o2", "sa",
					Config.token().getPassword(), 1);
			this.addDataMaping(dataMappings, AppLog.class,
					"jdbc:h2:tcp://127.0.0.1:" + Config.dataServer().getTcpPort() + "/o2", "sa",
					Config.token().getPassword(), 1);
			this.addDataMaping(dataMappings, Module.class,
					"jdbc:h2:tcp://127.0.0.1:" + Config.dataServer().getTcpPort() + "/o2", "sa",
					Config.token().getPassword(), 1);
			EntityManagerContainerFactory.init(webApplicationDirectory, dataMappings);
			SmsSender.start();
			QueueTransmitReceive.start();
			QueuePushMessageTransfer.start();
			queueUnexpectedErrorLogReceive = new QueueUnexpectedErrorLogReceive();
			queueUnexpectedErrorLogReceive.start();
			queuePromptErrorLogReceive = new QueuePromptErrorLogReceive();
			queuePromptErrorLogReceive.start();
			queueWarnLogReceive = new QueueWarnLogReceive();
			queueWarnLogReceive.start();
			queueAppLogReceive = new QueueAppLogReceive();
			queueAppLogReceive.start();
			this.scheduler.scheduleWithFixedDelay(new CleanExpiredCode(), 15, 60 * 5, TimeUnit.SECONDS);
			this.scheduler.scheduleWithFixedDelay(new CleanDevice(), 10, 60 * 60, TimeUnit.SECONDS);
			this.scheduler.scheduleWithFixedDelay(new CleanLog(), 30, 60 * 60 * 4, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			SmsSender.stop();
			QueueTransmitReceive.stop();
			QueuePushMessageTransfer.stop();
			this.scheduler.shutdownNow();
			/** 关闭缓存 */
			ApplicationCache.shutdown();
			EntityManagerContainerFactory.close();
			PCRegistry.deRegister(sce.getClass().getClassLoader());
			if (null != queueUnexpectedErrorLogReceive) {
				queueUnexpectedErrorLogReceive.stop();
			}
			if (null != queuePromptErrorLogReceive) {
				queuePromptErrorLogReceive.stop();
			}
			if (null != queueWarnLogReceive) {
				queueWarnLogReceive.stop();
			}
			if (null != queueAppLogReceive) {
				queueAppLogReceive.stop();
			}
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private <T extends SliceJpaObject> void addDataMaping(DataMappings dataMappings, Class<T> clz, String url,
			String username, String password, Integer order) throws Exception {
		CopyOnWriteArrayList<DataMapping> list = dataMappings.get(clz.getName());
		if (null == list) {
			list = new CopyOnWriteArrayList<DataMapping>();
			dataMappings.put(clz.getName(), list);
		}
		DataMapping dataMapping = new DataMapping();
		dataMapping.setUrl(url);
		dataMapping.setUsername(username);
		dataMapping.setPassword(password);
		list.add(dataMapping);
	}

}
