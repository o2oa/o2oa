package o2.collect.assemble.jaxrs.collect;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Account;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.Unit;

public class QueueTransmitReceive {

	private static QueueTransmitReceive INSTANCE;
	private LinkedBlockingQueue<WiTransmitReceive> queue;

	private QueueTransmitReceive() {
		this.queue = new LinkedBlockingQueue<WiTransmitReceive>();
		ExecuteThread executeThread = new ExecuteThread();
		executeThread.start();
	}

	public static void send(WiTransmitReceive o) throws Exception {
		INSTANCE.queue.put(o);
	}

	public static void start() {
		if (INSTANCE == null) {
			synchronized (QueueTransmitReceive.class) {
				if (INSTANCE == null) {
					INSTANCE = new QueueTransmitReceive();
				}
			}
		}
	}

	public static void stop() {
		try {
			if (INSTANCE != null) {
				INSTANCE.queue.put(INSTANCE.new StopExecuteThreadSignal());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ExecuteThread extends Thread {
		public void run() {
			while (true) {
				try {
					WiTransmitReceive o = queue.take();
					if (o instanceof StopExecuteThreadSignal) {
						break;
					}
					execute(o);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class StopExecuteThreadSignal extends WiTransmitReceive {

	}

	private void execute(WiTransmitReceive wi) {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			emc.beginTransaction(Account.class);
			emc.beginTransaction(Unit.class);
			String unitId = business.unit().getWithName(wi.getName(), null);
			Unit unit = emc.find(unitId, Unit.class, ExceptionWhen.not_found);
			unit.setCenterHost(wi.getCenterProxyHost());
			unit.setCenterPort(wi.getCenterProxyPort());
			unit.setHttpProtocol(wi.getHttpProtocol());
			List<String> accountIds = business.account().listWithUnit(unit.getId());
			List<Account> accounts = emc.list(Account.class, accountIds);
			for (Account o : accounts) {
				if (wi.getMobileList().contains(o.getName())) {
					wi.getMobileList().remove(o.getName());
				} else {
					List<String> deviceIds = business.device().listWithAccount(o.getId());
					for (Device device : emc.list(Device.class, deviceIds)) {
						emc.remove(device);
					}
					emc.remove(o);
				}
			}
			for (String str : ListTools.add(wi.getMobileList(), true, true, new String[] {})) {
				Account account = new Account();
				account.setName(str);
				account.setUnit(unit.getId());
				emc.persist(account);
			}
			emc.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class WiTransmitReceive extends GsonPropertyObject {
		private String name;

		private String password;

		private List<String> mobileList;

		private String centerProxyHost;

		private Integer centerProxyPort;

		private String httpProtocol;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public List<String> getMobileList() {
			return mobileList;
		}

		public void setMobileList(List<String> mobileList) {
			this.mobileList = mobileList;
		}

		public String getCenterProxyHost() {
			return centerProxyHost;
		}

		public void setCenterProxyHost(String centerProxyHost) {
			this.centerProxyHost = centerProxyHost;
		}

		public Integer getCenterProxyPort() {
			return centerProxyPort;
		}

		public void setCenterProxyPort(Integer centerProxyPort) {
			this.centerProxyPort = centerProxyPort;
		}

		public String getHttpProtocol() {
			return httpProtocol;
		}

		public void setHttpProtocol(String httpProtocol) {
			this.httpProtocol = httpProtocol;
		}

	}
}
