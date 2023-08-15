package com.x.processplatform.core.express.ticket;

import java.util.Collection;
import java.util.List;

class QueueAdd implements Add {

	@Override
	public void afterParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		afterCommon(tickets, ticket);
		List<Ticket> list = Tickets.interconnectedAsFellow(targets);
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(list));
		List<Ticket> next = tickets.listNext(ticket);
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		list.stream().forEach(o -> o.next(next).sibling(sibling));
		ticket.clearSibling();
	}

	@Override
	public void afterQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		afterCommon(tickets, ticket);
		List<Ticket> list = Tickets.interconnectedAsNext(targets);
		List<Ticket> fellow = tickets.listFellow(ticket);
		fellow.add(list.get(0));
		Tickets.interconnectedAsFellow(fellow);
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		list.get(0).sibling(sibling);
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(list.get(0)));
		list.get(list.size() - 1).next(tickets.listNext(ticket));
		ticket.clearSibling();
	}

	@Override
	public void afterSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		afterCommon(tickets, ticket);
		List<Ticket> list = Tickets.interconnectedAsSibling(targets);
		List<Ticket> next = tickets.listNext(ticket);
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		list.stream().forEach(o -> o.next(next).sibling(sibling));
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(list));
		ticket.clearSibling();
	}

	private void afterCommon(Tickets tickets, Ticket ticket) {
		tickets.listSibling(ticket, true).stream().forEach(o -> o.enable(false).join(false));
	}

	@Override
	public void beforeParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		List<Ticket> list = Tickets.interconnectedAsFellow(targets);
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(list));
		List<Ticket> next = tickets.listNext(ticket);
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		list.stream().forEach(o -> o.next(next).sibling(sibling));
		ticket.clearSibling();
	}

	@Override
	public void beforeQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		List<Ticket> list = Tickets.interconnectedAsNext(targets);
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(list.get(0)));
		list.get(list.size() - 1).next(ticket);
		List<Ticket> sibling = tickets.listSibling(ticket, false);
		list.get(0).sibling(sibling);
		ticket.clearSibling();
	}

	@Override
	public void beforeSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		List<Ticket> list = Tickets.interconnectedAsSibling(targets);
		List<Ticket> nextTo = tickets.listNextTo(ticket);
		nextTo.stream().forEach(o -> o.next(list));
		list.get(list.size() - 1).next(ticket);
		List<Ticket> fellow = tickets.listFellow(ticket);
		list.stream().forEach(o -> o.fellow(fellow));
	}

}
