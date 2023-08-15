package com.x.processplatform.core.express.ticket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class Tickets {

	public static final String MODE_PARALLEL = "parallel";
	public static final String MODE_QUEUE = "queue";
	public static final String MODE_SINGLE = "single";
	public static final String MODE_GRAB = "grab";

	protected Map<String, Ticket> context = new LinkedHashMap<>();

	private String mode;

	public String mode() {

		return this.mode;

	}

	public static Tickets single(Collection<Ticket> targets) {

		Tickets tickets = new Tickets();

		tickets.mode = MODE_GRAB;

		Tickets.interconnectedAsSibling(targets).stream().forEach(o -> {
			o.sibling(targets);
			o.mode(MODE_GRAB);
			tickets.context.put(o.label(), o);
		});

		return tickets;

	}

	public static Tickets grab(Collection<Ticket> targets) {

		Tickets tickets = new Tickets();

		tickets.mode = MODE_SINGLE;

		Tickets.interconnectedAsSibling(targets).stream().forEach(o -> {
			o.sibling(targets);
			o.mode(MODE_SINGLE);
			tickets.context.put(o.label(), o);
		});

		return tickets;

	}

	public static Tickets parallel(Collection<Ticket> targets) {

		Tickets tickets = new Tickets();

		tickets.mode = MODE_PARALLEL;

		Tickets.interconnectedAsFellow(targets).stream().forEach(o -> {
			o.fellow(targets);
			o.mode(MODE_PARALLEL);
			tickets.context.put(o.label(), o);
		});

		return tickets;

	}

	public static Tickets queue(Collection<Ticket> targets) {

		Tickets tickets = new Tickets();

		tickets.mode = MODE_QUEUE;

		Tickets.interconnectedAsNext(targets).stream().forEach(o -> {
			o.fellow(targets);
			o.mode(MODE_QUEUE);
			tickets.context.put(o.label(), o);
		});

		return tickets;
	}

	public Optional<Ticket> findTicketWithLabel(String label) {
		return Optional.ofNullable(this.context.get(label));
	}

	public List<Ticket> bubble() {
		List<Ticket> list = this.context.values().stream().filter(o -> (!o.completed()) && o.enable() && o.valid())
				.collect(Collectors.toList());
		List<String> next = list.stream().flatMap(o -> o.next().stream()).filter(StringUtils::isNotEmpty)
				.collect(Collectors.toList());
		return list.stream().filter(o -> !next.contains(o.label())).collect(Collectors.toList());
	}

	public Tickets completed(String label) {
		Optional<Ticket> opt = this.findTicketWithLabel(label);
		if (opt.isPresent()) {
			return completed(opt.get());
		}
		return this;
	}

	public Tickets completed(Ticket ticket) {
		this.listSibling(ticket, false).stream().forEach(o -> o.enable(false));
		ticket.enable(true).completed(true);
		return this;
	}

	public Tickets add(String label, Collection<String> targets, boolean before, String addMode) {
		Optional<Ticket> opt = this.findTicketWithLabel(label);
		if (opt.isPresent()) {
			return add(opt.get(), targets.stream().distinct().map(Ticket::new).collect(Collectors.toList()), before,
					addMode);
		}
		return this;
	}

	/**
	 * 加签
	 * 
	 * @param targets
	 * @return
	 */
	public Tickets add(Ticket ticket, Collection<Ticket> targets, boolean before, String addMode) {
		Add add = null;
		if (StringUtils.equalsIgnoreCase(ticket.mode(), MODE_PARALLEL)) {
			add = new ParallelAdd();
		} else if (StringUtils.equalsIgnoreCase(ticket.mode(), MODE_QUEUE)) {
			add = new QueueAdd();
		} else {
			add = new SingleAdd();
		}
		if (before) {
			switch (addMode) {
			case MODE_PARALLEL:
				add.beforeParallel(this, ticket, targets);
				break;
			case MODE_QUEUE:
				add.beforeQueue(this, ticket, targets);
				break;
			default:
				add.beforeSingle(this, ticket, targets);
				break;
			}
		} else {
			switch (addMode) {
			case MODE_PARALLEL:
				add.afterParallel(this, ticket, targets);
				break;
			case MODE_QUEUE:
				add.afterQueue(this, ticket, targets);
				break;
			default:
				add.afterSingle(this, ticket, targets);
				break;
			}
		}
		targets.stream().forEach(o -> this.context.put(o.label(), o));
		return this;
	}

	/**
	 * 列示sibling
	 * 
	 * @param ticket
	 * @param selfInclude 是否排除第一个参数对象ticket
	 * @return
	 */
	protected List<Ticket> listSibling(Ticket ticket, boolean selfInclude) {
		LinkedHashSet<Ticket> resultSet = new LinkedHashSet<>();
		if (selfInclude) {
			resultSet.add(ticket);
		}
		Queue<Ticket> queue = new LinkedList<>();
		queue.add(ticket);
		while (!queue.isEmpty()) {
			Ticket currentTicket = queue.poll();
			resultSet.add(currentTicket);
			currentTicket.sibling().stream().map(this.context::get).filter(Objects::nonNull).forEach(o -> {
				if (!resultSet.contains(o)) {
					queue.add(o);
				}
			});
		}
		if (!selfInclude) {
			resultSet.remove(ticket);
		}
		return new ArrayList<>(resultSet);
	}

	protected List<Ticket> listFellow(Ticket ticket) {
		return this.listSibling(ticket, false).stream().flatMap(o -> o.fellow().stream()).distinct().map(context::get)
				.filter(Objects::nonNull).flatMap(o -> o.sibling().stream()).distinct().map(context::get)
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	protected List<Ticket> listNext(Ticket ticket) {
		return this.listSibling(ticket, false).stream().flatMap(o -> o.next().stream()).distinct().map(context::get)
				.filter(Objects::nonNull).flatMap(o -> o.sibling().stream()).distinct().map(context::get)
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	public static List<Ticket> interconnectedAsSibling(Collection<Ticket> col) {
		if (!col.isEmpty()) {
			col.stream().forEach(o -> o.sibling(col));
			return col.stream().collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	public static List<Ticket> interconnectedAsFellow(Collection<Ticket> col) {
		if (!col.isEmpty()) {
			col.stream().forEach(o -> o.fellow(col));
			return col.stream().collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	public static List<Ticket> interconnectedAsNext(Collection<Ticket> col) {
		if (!col.isEmpty()) {
			Iterator<Ticket> iter = col.iterator();
			Ticket cur = iter.next();
			while (iter.hasNext()) {
				Ticket o = iter.next();
				cur.next(o);
				cur = o;
			}
			return col.stream().collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	public List<Ticket> listNextTo(Ticket ticket) {
		return this.context.values().stream().filter(o -> o.next().contains(ticket.label()))
				.collect(Collectors.toList());
	}

}