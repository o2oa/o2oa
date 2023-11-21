package com.x.processplatform.core.entity.ticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.XGsonBuilder;

public class Tickets implements Serializable {

	private static final long serialVersionUID = -6987455914475062814L;

	public static final String MODE_PARALLEL = "parallel";
	public static final String MODE_QUEUE = "queue";
	public static final String MODE_SINGLE = "single";
	public static final String ACT_RESET = "reset";
	public static final String ACT_ADD = "add";
	public static final String ACT_CREATE = "create";

	private Map<String, Ticket> context = new LinkedHashMap<>();

	private String mode;

	public String mode() {
		return this.mode;
	}

	public static Tickets single(Ticket... targets) {
		return single(Arrays.asList(targets));
	}

	public static Tickets single(Collection<Ticket> targets) {
		Tickets tickets = new Tickets();
		tickets.mode = MODE_SINGLE;
		Tickets.interconnectedAsSibling(targets).stream().forEach(o -> {
			o.sibling(targets);
			o.mode(MODE_SINGLE).act(ACT_CREATE);
			tickets.context.put(o.label(), o);
		});
		return tickets;
	}

	public static Tickets parallel(Ticket... targets) {
		return parallel(Arrays.asList(targets));
	}

	public static Tickets parallel(Collection<Ticket> targets) {
		Tickets tickets = new Tickets();
		tickets.mode = MODE_PARALLEL;
		Tickets.interconnectedAsFellow(targets).stream().forEach(o -> {
			o.fellow(targets);
			o.mode(MODE_PARALLEL).act(ACT_CREATE);
			tickets.context.put(o.label(), o);
		});
		return tickets;
	}

	public static Tickets queue(Ticket... targets) {
		return queue(Arrays.asList(targets));
	}

	public static Tickets queue(Collection<Ticket> targets) {
		Tickets tickets = new Tickets();
		tickets.mode = MODE_QUEUE;
		Tickets.interconnectedAsNext(targets).stream().forEach(o -> {
			o.mode(MODE_QUEUE).act(ACT_CREATE);
			tickets.context.put(o.label(), o);
		});
		return tickets;
	}

	public Optional<Ticket> findTicketWithLabel(String label) {
		return Optional.ofNullable(this.context.get(label));
	}

	public List<Ticket> list(Boolean ifCompleted, Boolean ifEnable, Boolean ifValid) {
		Predicate<Ticket> predicate = Objects::nonNull;
		if (null != ifCompleted) {
			predicate = predicate.and(o -> o.completed() == ifCompleted);
		}
		if (null != ifEnable) {
			predicate = predicate.and(o -> o.enable() == ifEnable);
		}
		if (null != ifValid) {
			predicate = predicate.and(o -> o.valid() == ifValid);
		}
		return this.context.values().stream().filter(predicate).collect(Collectors.toList());
	}

	public List<Ticket> bubble() {
		List<Ticket> list = list(false, true, true);
		List<String> next = list.stream().flatMap(o -> o.next().stream()).filter(StringUtils::isNotEmpty)
				.collect(Collectors.toList());
		return list.stream().filter(o -> !next.contains(o.label())).collect(Collectors.toList());
	}

	public Tickets completed(String label) {
		Optional<Ticket> opt = this.bubble().stream().filter(o -> StringUtils.equalsIgnoreCase(label, o.label()))
				.findFirst();
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

	/**
	 * 加签,对targets进行在相同level已有的进行去重.
	 * 
	 * @param label
	 * @param targets
	 * @param before
	 * @param addMode
	 * @return
	 */
	public boolean add(String label, Collection<String> targets, boolean before, String addMode) {
		Optional<Ticket> opt = this.findTicketWithLabel(label);
		if (opt.isPresent()) {
			return add(opt.get(), targets, before, addMode);
		}
		return false;
	}

	public boolean add(Ticket ticket, Collection<String> targets, boolean before, String addMode) {
		return addExec(ticket, targets.stream().map(Ticket::new).collect(Collectors.toList()), before, addMode);
	}

	private boolean addExec(Ticket ticket, Collection<Ticket> targets, boolean before, String addMode) {
		if (targets.isEmpty()) {
			return false;
		}
		targets.stream().forEach(o -> o.act(Tickets.ACT_ADD));
		Add add = null;
		if (StringUtils.equalsIgnoreCase(ticket.mode(), MODE_PARALLEL)) {
			add = new ParallelAdd();
		} else if (StringUtils.equalsIgnoreCase(ticket.mode(), MODE_QUEUE)) {
			add = new QueueAdd();
		} else {
			add = new SingleAdd();
		}
		switch (addMode) {
		case MODE_PARALLEL:
			targets.stream().forEach(o -> o.mode(MODE_PARALLEL));
			if (before) {
				targets = add.beforeParallel(this, ticket, targets);
			} else {
				targets = add.afterParallel(this, ticket, targets);
			}
			break;
		case MODE_QUEUE:
			targets.stream().forEach(o -> o.mode(MODE_QUEUE));
			if (before) {
				targets = add.beforeQueue(this, ticket, targets);
			} else {
				targets = add.afterQueue(this, ticket, targets);
			}
			break;
		default:
			targets.stream().forEach(o -> o.mode(MODE_SINGLE));
			if (before) {
				targets = add.beforeSingle(this, ticket, targets);
			} else {
				targets = add.afterSingle(this, ticket, targets);
			}
			break;
		}
		targets.stream().forEach(o -> {
			o.parent(ticket.label());
			this.context.put(o.label(), o);
		});
		return (!targets.isEmpty());
	}

	/**
	 * 重置处理人,,对targets进行在相同level已有的进行去重.
	 * 
	 * @param ticket
	 * @param targets
	 * @return
	 */
	public boolean reset(String label, Collection<String> targets) {
		Optional<Ticket> opt = this.findTicketWithLabel(label);
		if (opt.isPresent()) {
			return reset(opt.get(), targets);
		}
		return false;
	}

	public boolean reset(Ticket ticket, Collection<String> targets) {
		if (targets.isEmpty()) {
			return false;
		}
		Reset reset = null;
		if (StringUtils.equalsIgnoreCase(ticket.mode(), MODE_PARALLEL)) {
			reset = new ParallelReset();
		} else if (StringUtils.equalsIgnoreCase(ticket.mode(), MODE_QUEUE)) {
			reset = new QueueReset();
		} else {
			reset = new SingleReset();
		}
		Collection<Ticket> list = targets.stream().map(Ticket::new).collect(Collectors.toList());
		list = trimWithBubble(list);
		if (list.isEmpty()) {
			return false;
		}
		list.stream().forEach(o -> {
			o.copyFromSkipLabelDistinguishedName(ticket);
			o.act(Tickets.ACT_RESET);
		});
		list = reset.reset(this, ticket, list);
		list.stream().forEach(o -> this.context.put(o.label(), o));
		ticket.completed(true).enable(false);
		return true;
	}

	protected List<Ticket> trimWithBubble(Collection<Ticket> targets) {
		List<String> exists = this.bubble().stream().map(Ticket::distinguishedName).collect(Collectors.toList());
		return targets.stream().filter(o -> (!exists.contains(o.distinguishedName()))).collect(Collectors.toList());
	}

	/**
	 * 根据distinguishedName禁用ticket
	 * 
	 * @param list
	 * @return
	 */
	public Tickets disableDistinguishedName(List<String> list) {
		this.context.entrySet().stream().forEach(o -> {
			if (list.contains(o.getValue().distinguishedName())) {
				o.getValue().enable(false);
			}
		});
		return this;
	}

	public Tickets disableDistinguishedName(String... distinguishedNames) {
		return disableDistinguishedName(Arrays.asList(distinguishedNames));
	}

	/**
	 * 列示sibling
	 * 
	 * @param ticket
	 * @param selfInclude 是否包含第一个参数对象ticket,false则排除.
	 * @return
	 */
	protected List<Ticket> listSibling(Ticket ticket, boolean selfInclude) {
		LinkedHashSet<Ticket> resultSet = new LinkedHashSet<>();
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

	protected List<Ticket> listFellow(Ticket ticket, boolean selfInclude) {
		List<Ticket> list = this.listSibling(ticket, true).stream().flatMap(o -> o.fellow().stream()).distinct()
				.map(context::get).filter(Objects::nonNull).flatMap(o -> this.listSibling(o, true).stream())
				.filter(Objects::nonNull).distinct().collect(Collectors.toList());
		if (selfInclude) {
			if (!list.contains(ticket)) {
				list.add(ticket);
			}
		} else {
			list.remove(ticket);
		}
		return list;
	}

	protected List<Ticket> listNext(Ticket ticket) {
		return this.listSibling(ticket, true).stream().flatMap(o -> o.next().stream()).distinct().map(context::get)
				.filter(Objects::nonNull).flatMap(o -> this.listSibling(o, true).stream()).filter(Objects::nonNull)
				.distinct().collect(Collectors.toList());
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

	public boolean isEmpty() {
		return (this.context == null) || this.context.isEmpty();
	}

	@Override
	public String toString() {
		return XGsonBuilder.toJson(this);
	}

}