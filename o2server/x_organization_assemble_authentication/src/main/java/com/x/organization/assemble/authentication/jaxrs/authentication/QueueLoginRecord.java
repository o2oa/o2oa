package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.Date;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.jaxrs.authentication.QueueLoginRecord.LoginRecord;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.enums.PersonStatusEnum;

public class QueueLoginRecord extends AbstractQueue<LoginRecord> {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueueLoginRecord.class);

	protected void execute(LoginRecord wrapIn) throws Exception {
		String name = wrapIn.getName();
		String address = wrapIn.getAddress();
		String client = wrapIn.getClient();
		Date date = wrapIn.getDate();
		LOGGER.debug("record login name:{}, date:{}, address:{}, client:{}.", name, date, address, client);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String id = business.person().getWithCredential(name);
			Person person = emc.find(id, Person.class);
			if (null != person) {
				emc.beginTransaction(Person.class);
				person.setLastLoginTime(date);
				person.setLastLoginAddress(address);
				person.setLastLoginClient(client);
				if(PersonStatusEnum.LOCK.getValue().equals(person.getStatus()) &&
						person.getLockExpireTime().getTime() < System.currentTimeMillis()){
					person.setStatus(PersonStatusEnum.NORMAL.getValue());
					person.setLockExpireTime(null);
					person.setStatusDes("");
				}
				emc.commit();
			}
		}
	}

	public static class LoginRecord extends GsonPropertyObject {

		private static final long serialVersionUID = 2815453423099696409L;

		private String name;

		private String address;

		private Date date;

		private String client;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public String getClient() {
			return client;
		}

		public void setClient(String client) {
			this.client = client;
		}

	}

}
