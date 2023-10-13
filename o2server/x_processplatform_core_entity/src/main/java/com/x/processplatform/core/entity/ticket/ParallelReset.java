package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class ParallelReset implements Reset {

	@Override
	public List<Ticket> reset(Tickets tickets, Ticket ticket, Collection<String> targets) {
		List<Ticket> list = targets.stream().map(o -> ticket.copy().distinguishedName(o)).collect(Collectors.toList());
		list.addAll(tickets.listFellow(ticket));
		return Tickets.interconnectedAsFellow(list);
	}

}
