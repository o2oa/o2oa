package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.List;

class ParallelReset implements Reset {

	@Override
	public Collection<Ticket> reset(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		List<Ticket> fellow = tickets.listFellow(ticket, false);
		fellow.addAll(targets);
		Tickets.interconnectedAsFellow(fellow);
		return targets;
	}

}