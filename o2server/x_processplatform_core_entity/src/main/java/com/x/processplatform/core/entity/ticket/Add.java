package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.Date;

interface Add {

	void afterParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void afterQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void afterSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void beforeParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void beforeQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void beforeSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	default void setLevel(Ticket ticket, Collection<Ticket> targets) {
		targets.stream().forEach(o -> o.level(ticket.level()).parent(ticket.parent()));
	}

	default void setParentLevel(Ticket ticket, Collection<Ticket> targets) {
		long level = (new Date()).getTime();
		targets.stream().forEach(o -> o.level(level).parent(ticket.label()));
	}

	default void completedThenNotJoin(Tickets tickets, Ticket ticket) {
		tickets.listSibling(ticket, true).stream().forEach(o -> o.completed(true));
	}

}
