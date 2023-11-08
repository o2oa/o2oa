package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class ParallelReset implements Reset {

	@Override
	public List<Ticket> reset(Tickets tickets, Ticket ticket, Collection<String> targets) {
		List<Ticket> fellow = tickets.listFellow(ticket, false);
		List<Ticket> list = targets.stream()
				.map(o -> ticket.copy().distinguishedName(o).fromDistinguishedName("").act(Tickets.ACT_RESET))
				.collect(Collectors.toList());
		list.addAll(fellow);
		return Tickets.interconnectedAsFellow(list);
	}

}