package o2.collect.assemble.jaxrs.collect;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;

import o2.collect.assemble.jaxrs.collect.QueueAppLogReceive.WiAppLogReceive;
import o2.collect.core.entity.log.AppLog;

public class QueueAppLogReceive extends AbstractQueue<WiAppLogReceive> {

	private static Logger logger = LoggerFactory.getLogger(QueueAppLogReceive.class);

	protected void execute(WiAppLogReceive wi) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.info("from source ip:{}, appLog:{}.", wi.getAddress(), wi.getAppLog());
			emc.beginTransaction(AppLog.class);
			AppLog appLog = new AppLog();
			wi.getAppLog().copyTo(appLog, JpaObject.id_FIELDNAME);
			emc.persist(appLog, CheckPersistType.all);
			emc.commit();
		}
	}

	public static class WiAppLogReceive extends GsonPropertyObject {

		private String address;

		private AppLog appLog;

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public AppLog getAppLog() {
			return appLog;
		}

		public void setAppLog(AppLog appLog) {
			this.appLog = appLog;
		}

	}

}