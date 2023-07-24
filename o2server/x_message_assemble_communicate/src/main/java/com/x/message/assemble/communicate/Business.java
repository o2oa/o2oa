package com.x.message.assemble.communicate;


import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.instrument.Instrument;
import com.x.message.assemble.communicate.factory.IMConversationFactory;
import com.x.message.assemble.communicate.factory.MessageFactory;
import com.x.organization.core.express.Organization;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Instrument instrument;

	public Instrument instrument() throws Exception {
		if (null == this.instrument) {
			this.instrument = new Instrument();
		}
		return instrument;
	}

	private Organization organization;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}
	
	private MessageFactory message;

	public MessageFactory message() throws Exception {
		if (null == this.message) {
			this.message = new MessageFactory(this);
		}
		return message;
	}

	private IMConversationFactory imConversationFactory;

	public IMConversationFactory imConversationFactory() throws Exception {
		if (null == this.imConversationFactory) {
			this.imConversationFactory = new IMConversationFactory(this);
		}
		return imConversationFactory;
	}

}