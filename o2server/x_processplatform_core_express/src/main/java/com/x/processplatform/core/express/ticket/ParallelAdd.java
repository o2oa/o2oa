package com.x.processplatform.core.express.ticket;

import java.util.Collection;
import java.util.List;

class ParallelAdd implements Add {

	@Override
	public void afterParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		afterCommon(ticket);
		List<Ticket> fellow = tickets.listFellow(ticket);
		fellow.addAll(targets);
		Tickets.interconnectedAsFellow(fellow);
		List<Ticket> next = tickets.listNext(ticket);
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		fellow.stream().forEach(o -> o.next(next).sibling(sibling));
		ticket.clearSibling();
	}

	@Override
	public void afterQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		afterCommon(ticket);
		List<Ticket> list = Tickets.interconnectedAsNext(targets);
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		list.get(0).sibling(sibling);
		List<Ticket> fellow = tickets.listFellow(ticket);
		fellow.add(list.get(0));
		Tickets.interconnectedAsFellow(fellow);
		List<Ticket> next = tickets.listNext(ticket);
		list.get(list.size() - 1).next(next);
		ticket.clearSibling();
	}

	@Override
	public void afterSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		afterCommon(ticket);
		List<Ticket> next = tickets.listNext(ticket);
		Tickets.interconnectedAsSibling(targets);
		targets.stream().forEach(o -> o.next(next));
		tickets.listNextTo(ticket).forEach(o -> o.next(targets));
		ticket.clearSibling().clearFellow();
	}

	private void afterCommon(Ticket ticket) {
		ticket.join(false).completed(true);
	}

	@Override
	public void beforeParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		List<Ticket> fellow = tickets.listFellow(ticket);
		fellow.addAll(targets);
		Tickets.interconnectedAsFellow(fellow);
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(targets));
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		targets.stream().forEach(o -> o.sibling(sibling).next(ticket).clearFellow().clearSibling());
	}

	@Override
	public void beforeQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		List<Ticket> list = Tickets.interconnectedAsNext(targets);
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		List<Ticket> fellow = tickets.listFellow(ticket);
		list.get(0).sibling(sibling).fellow(fellow);
		list.get(list.size() - 1).next(ticket);
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(list.get(0)));
		targets.stream().forEach(o -> o.clearSibling().clearFellow());
	}

	@Override
	public void beforeSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
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
