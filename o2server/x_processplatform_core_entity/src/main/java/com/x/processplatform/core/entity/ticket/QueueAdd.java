package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class QueueAdd implements Add {

	@Override
	public void afterParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		setLayer(targets);
		List<Ticket> next = tickets.listNext(ticket);
		Tickets.interconnectedAsFellow(targets);
		targets.stream().forEach(o -> o.appendNext(next));
		completedThenNotJoin(tickets, ticket);
	}

	@Override
	public void afterQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		setLayer(targets);
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		List<Ticket> fellow = tickets.listFellow(ticket);
		List<Ticket> next = tickets.listNext(ticket);
		List<Ticket> list = Tickets.interconnectedAsNext(targets);
		Optional<Ticket> first = list.stream().findFirst();
		if (first.isPresent()) {
			first.get().appendSibling(sibling).appendFellow(fellow);
		}
		targets.stream().forEach(o -> o.appendNext(next));
		completedThenNotJoin(tickets, ticket);
	}

	@Override
	public void afterSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		setLayer(targets);
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		List<Ticket> fellow = tickets.listFellow(ticket);
		List<Ticket> next = tickets.listNext(ticket);
		sibling.addAll(targets);
		Tickets.interconnectedAsSibling(sibling);
		targets.stream().forEach(o -> o.appendFellow(fellow).appendNext(next));
		tickets.listNextTo(ticket).forEach(o -> o.appendNext(targets.stream().collect(Collectors.toList())));
		completedThenNotJoin(tickets, ticket);
	}

	@Override
	public void beforeParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		setLayer(targets);
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		List<Ticket> fellow = tickets.listFellow(ticket);
		List<Ticket> next = tickets.listNext(ticket);
		Tickets.interconnectedAsFellow(fellow);
		tickets.listNextTo(ticket).stream().forEach(o -> o.appendNext(targets.stream().collect(Collectors.toList())));
		targets.stream().forEach(o -> o.appendNext(sibling).appendNext(ticket).appendNext(next));
	}

	@Override
	public void beforeQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		setLayer(targets);
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		List<Ticket> fellow = tickets.listFellow(ticket);
		List<Ticket> next = tickets.listNext(ticket);
		List<Ticket> list = Tickets.interconnectedAsNext(targets);
		Optional<Ticket> first = list.stream().findFirst();
		if (first.isPresent()) {
			first.get().appendFellow(fellow);
			tickets.listNextTo(ticket).stream().forEach(o -> o.appendNext(first.get()));
		}
		list.stream().forEach(o -> o.appendNext(ticket).appendNext(next).appendNext(sibling));
	}

	@Override
	public void beforeSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		setLayer(targets);
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		List<Ticket> next = tickets.listNext(ticket);
		Tickets.interconnectedAsSibling(targets);
		tickets.listNextTo(ticket).stream().forEach(o -> o.appendNext(targets.stream().collect(Collectors.toList())));
		targets.stream().forEach(o -> o.appendNext(ticket).appendNext(next).appendNext(sibling));
	}
}
