package o2.collect.assemble.jaxrs.collect;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.ListTools;

import o2.base.core.project.config.Config;
import o2.collect.assemble.Business;
import o2.collect.assemble.jaxrs.collect.QueueUnexpectedErrorLogReceive.WiUnexpectedErrorLogReceive;
import o2.collect.core.entity.Unit;
import o2.collect.core.entity.log.UnexpectedErrorLog;

public class QueueUnexpectedErrorLogReceive extends AbstractQueue<WiUnexpectedErrorLogReceive> {

	private static Logger logger = LoggerFactory.getLogger(QueueUnexpectedErrorLogReceive.class);

	protected void execute(WiUnexpectedErrorLogReceive wi) throws Exception {
		String name = wi.getName();
		String password = wi.getPassword();
		String address = wi.getAddress();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String unitId = business.unit().getWithName(name, null);
			if (StringUtils.isEmpty(unitId)) {
				logger.info("can not find unit with name:{}.", name);
			} else {
				Unit unit = emc.find(unitId, Unit.class);
				if (!StringUtils.equals(Crypto.encrypt(password, Config.token().getKey()), unit.getPassword())) {
					logger.info("unit name:{} password not match.", name);
				} else {
					logger.info("unit name:{}, form source ip:{}. submit: {} unexpectedErrorLog.", name, address,
							ListTools.size(wi.getUnexpectedErrorLogList()));
					emc.beginTransaction(UnexpectedErrorLog.class);
					for (UnexpectedErrorLog o : ListTools.trim(wi.getUnexpectedErrorLogList(), true, false)) {
						o.setUnitName(unit.getName());
						o.setUnit(unit.getId());
						o.setAddress(address);
						try {
							emc.check(o, CheckPersistType.all);
							emc.persist(o);
						} catch (Exception e) {
							logger.error(e);
						}
					}
					emc.commit();
				}
			}
		}

	}

	public static class WiUnexpectedErrorLogReceive extends GsonPropertyObject {

		private String name;
		private String password;
		private String address;

		private List<UnexpectedErrorLog> unexpectedErrorLogList;

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

		public List<UnexpectedErrorLog> getUnexpectedErrorLogList() {
			return unexpectedErrorLogList;
		}

		public void setUnexpectedErrorLogList(List<UnexpectedErrorLog> unexpectedErrorLogList) {
			this.unexpectedErrorLogList = unexpectedErrorLogList;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

	}

}
