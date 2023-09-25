package com.x.processplatform.core.express.ticket;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class ParallelAdd implements Add {

	@Override
	public void afterParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		List<Ticket> fellow = tickets.listFellow(ticket);
		List<Ticket> next = tickets.listNext(ticket);
		fellow.addAll(targets);
		Tickets.interconnectedAsNext(fellow);
		fellow.stream().forEach(o -> o.appendNext(next).appendSibling(sibling));
		completedThenNotJoin(ticket);
	}

	@Override
	public void afterQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		List<Ticket> fellow = tickets.listFellow(ticket);
		List<Ticket> next = tickets.listNext(ticket);
		fellow.addAll(targets);
		Tickets.interconnectedAsNext(fellow);
		Optional<Ticket> first = targets.stream().findFirst();
		if (first.isPresent()) {
			first.get().appendSibling(sibling).appendFellow(fellow);
		}
		targets.stream().forEach(o -> o.appendNext(next));
		completedThenNotJoin(ticket);
	}

	@Override
	public void afterSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		List<Ticket> fellow = tickets.listFellow(ticket);
		List<Ticket> next = tickets.listNext(ticket);
		targets.stream().forEach(o -> o.appendSibling(sibling).appendFellow(fellow).appendNext(next));
		tickets.listNextTo(ticket).forEach(o -> o.appendNext(targets.stream().collect(Collectors.toList())));
		completedThenNotJoin(ticket);
	}

	private void completedThenNotJoin(Ticket ticket) {
		ticket.join(false).completed(true);
	}

	@Override
	public void beforeParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		List<Ticket> fellow = tickets.listFellow(ticket);
		List<Ticket> next = tickets.listNext(ticket);
		Tickets.interconnectedAsFellow(fellow);
		tickets.listNextTo(ticket).stream().forEach(o -> o.appendNext(targets.stream().collect(Collectors.toList())));
		targets.stream().forEach(o -> o.appendSibling(sibling).appendNext(ticket).appendNext(next));
	}

	@Override
	public void beforeQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		List<Ticket> fellow = tickets.listFellow(ticket);
		List<Ticket> next = tickets.listNext(ticket);
		List<Ticket> list = Tickets.interconnectedAsNext(targets);
		Optional<Ticket> first = list.stream().findFirst();
		if (first.isPresent()) {
			first.get().appendSibling(sibling).appendFellow(fellow);
			tickets.listNextTo(ticket).stream().forEach(o -> o.appendNext(first.get()));
		}
		list.stream().forEach(o -> o.appendNext(next));
	}

	@Override
	public void beforeSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		targets.stream().forEach(o -> o.mode(Tickets.MODE_SINGLE));
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		sibling.addAll(targets);
		Tickets.interconnectedAsSibling(sibling);
		List<Ticket> fellow = tickets.listFellow(ticket);
		fellow.addAll(targets);
		Tickets.interconnectedAsFellow(fellow);
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(targets));
		targets.stream().forEach(o -> o.next(ticket));
		ticket.clearSibling().clearFellow();
	}

}
