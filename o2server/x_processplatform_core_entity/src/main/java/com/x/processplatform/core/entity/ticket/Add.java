package com.x.processplatform.core.entity.ticket;

import java.util.Collection;

import com.x.base.core.project.tools.StringTools;

interface Add {

	void afterParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void afterQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void afterSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void beforeParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void beforeQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	void beforeSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	default void setLayer(Collection<Ticket> targets) {
		String layer = StringTools.uniqueToken();
		targets.stream().forEach(o -> o.layer(layer));
	}

	default void setLayer(Ticket ticket, Collection<Ticket> targets) {
		targets.stream().forEach(o -> o.layer(ticket.layer()));
	}

	default void completedThenNotJoin(Tickets tickets, Ticket ticket) {
		tickets.listSibling(ticket, true).stream().forEach(o -> o.completed(true));
	}

}
