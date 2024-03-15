package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class SingleReset implements Reset {

	@Override
	public Collection<Ticket> reset(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		sibling.addAll(targets);
		Tickets.interconnectedAsSibling(sibling);
		return targets;
	}

}
