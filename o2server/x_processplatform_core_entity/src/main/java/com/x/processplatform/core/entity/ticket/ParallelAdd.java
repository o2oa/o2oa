package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.x.base.core.project.gson.XGsonBuilder;

class ParallelAdd implements Add {

	@Override
	public Collection<Ticket> afterParallel(Tickets tickets, Ticket ticket, Collection<Ticket> collection) {
		// List<Ticket> targets = tickets.trimWithBubble(collection);
		if (!collection.isEmpty()) {
			List<Ticket> next = tickets.listNext(ticket);
			Tickets.interconnectedAsFellow(collection);
			collection.stream().forEach(o -> o.appendNext(next));
			tickets.completed(ticket);
		}
		return collection;
	}

	@Override
	public Collection<Ticket> afterQueue(Tickets tickets, Ticket ticket, Collection<Ticket> collection) {
		List<Ticket> targets = tickets.trimWithBubble(collection);
		if (!targets.isEmpty()) {
			List<Ticket> sibling = tickets.listSibling(ticket, false);
			List<Ticket> fellow = tickets.listFellow(ticket, false);
			List<Ticket> next = tickets.listNext(ticket);
			List<Ticket> list = Tickets.interconnectedAsNext(targets);
			Optional<Ticket> first = list.stream().findFirst();
			if (first.isPresent()) {
				first.get().appendSibling(sibling).appendFellow(fellow);
			}
			targets.stream().forEach(o -> o.appendNext(next));
			tickets.completed(ticket);
			// completedThenNotJoin(tickets, ticket);
		}
		return targets;
	}

	@Override
	public Collection<Ticket> afterSingle(Tickets tickets, Ticket ticket, Collection<Ticket> collection) {
		List<Ticket> targets = tickets.trimWithBubble(collection);
		if (!targets.isEmpty()) {
			List<Ticket> sibling = tickets.listSibling(ticket, false);
			List<Ticket> fellow = tickets.listFellow(ticket, true);
			List<Ticket> next = tickets.listNext(ticket);
			sibling.addAll(targets);
			Tickets.interconnectedAsSibling(sibling);
			fellow.addAll(targets);
			Tickets.interconnectedAsFellow(fellow);
			targets.stream().forEach(o -> o.appendNext(next));
			tickets.listNextTo(ticket).forEach(o -> o.appendNext(targets.stream().collect(Collectors.toList())));
			tickets.completed(ticket);
			// completedThenNotJoin(tickets, ticket);
		}
		return targets;
	}

	@Override
	public Collection<Ticket> beforeParallel(Tickets tickets, Ticket ticket, Collection<Ticket> collection) {
		List<Ticket> targets = tickets.trimWithBubble(collection);
		if (!targets.isEmpty()) {
			List<Ticket> sibling = tickets.listSibling(ticket, false);
			List<Ticket> fellow = tickets.listFellow(ticket, false);
			List<Ticket> next = tickets.listNext(ticket);
			Tickets.interconnectedAsFellow(fellow);
			tickets.listNextTo(ticket).stream()
					.forEach(o -> o.appendNext(targets.stream().collect(Collectors.toList())));
			targets.stream().forEach(o -> o.appendNext(sibling).appendNext(ticket).appendNext(next));
		}
		return targets;
	}

	@Override
	public Collection<Ticket> beforeQueue(Tickets tickets, Ticket ticket, Collection<Ticket> collection) {
		List<Ticket> targets = tickets.trimWithBubble(collection);
		if (!targets.isEmpty()) {
			List<Ticket> sibling = tickets.listSibling(ticket, false);
			List<Ticket> fellow = tickets.listFellow(ticket, false);
			List<Ticket> next = tickets.listNext(ticket);
			List<Ticket> list = Tickets.interconnectedAsNext(targets);
			Optional<Ticket> first = list.stream().findFirst();
			if (first.isPresent()) {
				first.get().appendFellow(fellow);
				tickets.listNextTo(ticket).stream().forEach(o -> o.appendNext(first.get()));
			}
			list.stream().forEach(o -> o.appendNext(ticket).appendNext(next).appendNext(sibling));
		}
		return targets;
	}

	@Override
	public Collection<Ticket> beforeSingle(Tickets tickets, Ticket ticket, Collection<Ticket> collection) {
		List<Ticket> targets = tickets.trimWithBubble(collection);
		if (!targets.isEmpty()) {
			List<Ticket> sibling = tickets.listSibling(ticket, false);
			List<Ticket> fellow = tickets.listFellow(ticket, true);
			List<Ticket> next = tickets.listNext(ticket);
			Tickets.interconnectedAsSibling(targets);
			fellow.stream().forEach(o -> o.appendFellow(targets));
			targets.stream().forEach(o -> o.appendFellow(ticket));
			tickets.listNextTo(ticket).stream()
					.forEach(o -> o.appendNext(targets.stream().collect(Collectors.toList())));
			targets.stream().forEach(o -> o.appendNext(ticket).appendNext(next).appendNext(sibling));
		}
		return targets;
	}

}
