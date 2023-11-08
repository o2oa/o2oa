package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class SingleReset implements Reset {

	@Override
	public List<Ticket> reset(Tickets tickets, Ticket ticket, Collection<String> targets) {
		List<Ticket> list = targets.stream()
				.map(o -> ticket.copy().distinguishedName(o).fromDistinguishedName("").act(Tickets.ACT_RESET))
				.collect(Collectors.toList());
		list.addAll(tickets.listSibling(ticket, false));
		return Tickets.interconnectedAsSibling(list);
	}

}
