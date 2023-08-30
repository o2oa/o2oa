package com.x.processplatform.core.express.ticket;

import java.util.Collection;
import java.util.List;

class SingleAdd implements Add {

	@Override
	public void afterParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		afterCommon(tickets, ticket);
		ticket.next(targets);
		List<Ticket> fellow = tickets.listFellow(ticket);
		fellow.addAll(targets);
		Tickets.interconnectedAsFellow(fellow);
		List<Ticket> next = tickets.listNext(ticket);
		Tickets.interconnectedAsFellow(targets).stream().forEach(o -> o.fellow(targets).next(next));
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(targets));
		ticket.clearSibling();
	}

	@Override
	public void afterQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		afterCommon(tickets, ticket);
		List<Ticket> list = Tickets.interconnectedAsNext(targets);
		ticket.next(list.get(0));
		List<Ticket> next = tickets.listNext(ticket);
		list.get(list.size() - 1).next(next);
		List<Ticket> fellow = tickets.listFellow(ticket);
		fellow.add(list.get(0));
		Tickets.interconnectedAsFellow(fellow);
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(list.get(0)));
		ticket.clearSibling();
	}

	@Override
	public void afterSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		afterCommon(tickets, ticket);
		ticket.next(targets);
		List<Ticket> fellow = tickets.listFellow(ticket);
		fellow.addAll(targets);
		Tickets.interconnectedAsFellow(fellow);
		List<Ticket> next = tickets.listNext(ticket);
		Tickets.interconnectedAsSibling(targets).stream().forEach(o -> o.next(next));
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(targets));
		ticket.clearSibling().clearFellow();
	}

	private void afterCommon(Tickets tickets, Ticket ticket) {
		tickets.listSibling(ticket, true).stream().forEach(o -> o.enable(false).join(false));
	}

	@Override
	public void beforeParallel(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		beforeCommon(tickets, ticket);
		List<Ticket> fellow = tickets.listFellow(ticket);
		fellow.addAll(targets);
		Tickets.interconnectedAsFellow(fellow);
		List<Ticket> sibling = tickets.listSibling(ticket, true);
		targets.stream().forEach(o -> o.next(sibling));
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(targets));
	}

	@Override
	public void beforeQueue(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		beforeCommon(tickets, ticket);
		List<Ticket> list = Tickets.interconnectedAsNext(targets);
		List<Ticket> fellow = tickets.listFellow(ticket);
		fellow.add(list.get(0));
		Tickets.interconnectedAsFellow(fellow);
		list.get(list.size() - 1).next(ticket);
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(list.get(0)));
	}

	@Override
	public void beforeSingle(Tickets tickets, Ticket ticket, Collection<Ticket> targets) {
		beforeCommon(tickets, ticket);
		List<Ticket> fellow = tickets.listFellow(ticket);
		fellow.addAll(targets);
		Tickets.interconnectedAsFellow(fellow);
		List<Ticket> sibling = tickets.listSibling(ticket, true);
		Tickets.interconnectedAsSibling(targets).stream().forEach(o -> o.next(sibling));
		tickets.listNextTo(ticket).stream().forEach(o -> o.next(targets));
	}

	private void beforeCommon(Tickets tickets, Ticket ticket) {
		tickets.listSibling(ticket, false).stream().forEach(o -> o.enable(false).join(false));
	}

}
