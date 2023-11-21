package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.Date;

interface Add {

	Collection<Ticket> afterParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	Collection<Ticket> afterQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	Collection<Ticket> afterSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	Collection<Ticket> beforeParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	Collection<Ticket> beforeQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

	Collection<Ticket> beforeSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

//	default void setLevel(Ticket ticket, Collection<Ticket> targets) {
//		targets.stream().forEach(o -> o.level(ticket.level()).parent(ticket.parent()));
//	}
//
//	default void setParentLevel(Ticket ticket, Collection<Ticket> targets) {
//		long level = (new Date()).getTime();
//		targets.stream().forEach(o -> o.level(level).parent(ticket.label()));
//	}

//	default void completedThenNotJoin(Tickets tickets, Ticket ticket) {
//		tickets.listSibling(ticket, false).stream().forEach(o -> o.enable(false));
//		
//	}

}
