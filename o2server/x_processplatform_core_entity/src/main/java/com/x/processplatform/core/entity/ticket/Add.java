package com.x.processplatform.core.entity.ticket;

import java.util.Collection;

interface Add {

	void afterParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void afterQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void afterSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void beforeParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void beforeQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void beforeSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

}
