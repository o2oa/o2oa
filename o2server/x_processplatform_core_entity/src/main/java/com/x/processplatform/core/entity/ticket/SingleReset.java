package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class SingleReset implements Reset {

	@Override
	public List<Ticket> reset(Tickets tickets, Ticket ticket, Collection<String> targets) {
		// 过滤掉重复的值,避免同一个用户多次处理.
		List<String> exists = tickets.list(false, true, true).stream()
				.filter(o -> Objects.equals(ticket.level(), o.level())).map(Ticket::distinguishedName)
				.collect(Collectors.toList());
		List<Ticket> list = targets.stream().filter(o -> !exists.contains(o))
				.map(o -> ticket.copy().distinguishedName(o).fromDistinguishedName("")).collect(Collectors.toList());
		list.addAll(tickets.listSibling(ticket, false));
		return Tickets.interconnectedAsSibling(list);
	}

}
