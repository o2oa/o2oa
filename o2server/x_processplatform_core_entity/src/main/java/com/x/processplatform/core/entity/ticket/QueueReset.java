package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

class QueueReset implements Reset {

	@Override
	public List<Ticket> reset(Tickets tickets, Ticket ticket, Collection<String> targets) {
		List<Ticket> next = tickets.listNext(ticket);
		List<String> exists = tickets.list(false, true, true).stream()
				.filter(o -> StringUtils.equals(ticket.layer(), o.layer())).map(Ticket::distinguishedName)
				.collect(Collectors.toList());
		List<Ticket> list = Tickets.interconnectedAsNext(targets.stream().filter(o -> !exists.contains(o))
				.map(o -> ticket.copy().distinguishedName(o).fromDistinguishedName("")).collect(Collectors.toList()));
		list.stream().forEach(o -> o.appendNext(next));
		Optional<Ticket> opt = list.stream().findFirst();
		if (opt.isPresent()) {
			tickets.listNextTo(ticket).stream().forEach(o -> o.next(opt.get()));
		}
		return list;
	}

}