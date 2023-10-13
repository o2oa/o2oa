package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class QueueReset implements Reset {

	@Override
	public List<Ticket> reset(Tickets tickets, Ticket ticket, Collection<String> targets) {
		List<Ticket> list = Tickets.interconnectedAsNext(
				targets.stream().map(o -> ticket.copy().distinguishedName(o)).collect(Collectors.toList()));
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(list.get(0)));
		return list;
	}

}