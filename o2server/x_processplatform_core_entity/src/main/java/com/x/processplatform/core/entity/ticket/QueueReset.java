package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

class QueueReset implements Reset {

	@Override
	public Collection<Ticket> reset(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		Tickets.interconnectedAsNext(targets);
		List<Ticket> next = tickets.listNext(ticket);
		targets.stream().forEach(o -> o.appendNext(next));
		Optional<Ticket> opt = targets.stream().findFirst();
		if (opt.isPresent()) {
			tickets.listNextTo(ticket).stream().forEach(o -> o.next(opt.get()));
		}
		return targets;
	}

}