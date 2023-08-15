package com.x.processplatform.core.express.ticket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public class Ticket {

	private String label;

	private String act;

	private boolean join;

	private boolean completed;

	private boolean valid;

	private boolean enable;

	private String target;

	private List<String> sibling;

	private List<String> fellow;

	private List<String> next;

	private String mode;

	public Ticket() {
		this.label = UUID.randomUUID().toString();
		this.act = "";
		this.completed = false;
		this.join = true;
		this.valid = true;
		this.enable = true;
		this.target = "";
		this.sibling = new ArrayList<>();
		this.fellow = new ArrayList<>();
		this.next = new ArrayList<>();
	}

	public Ticket(String target) {
		this();
		this.target = target;
	}

	public Ticket(String target, String label) {
		this();
		this.target = target;
		this.label = label;

	}

	public Ticket sibling(Collection<Ticket> sibling) {
		this.sibling = sibling.stream().map(Ticket::label).collect(Collectors.toList());
		this.sibling.remove(this.label);
		return this;
	}

	public Ticket fellow(Collection<Ticket> fellow) {
		this.fellow = fellow.stream().map(Ticket::label).collect(Collectors.toList());
		this.fellow.remove(this.label);
		return this;
	}

	public Ticket next(Collection<Ticket> next) {
		this.next = next.stream().map(Ticket::label).collect(Collectors.toList());
		this.next.remove(this.label);
		return this;
	}

	public Ticket next(Ticket... next) {
		this.next = Stream.of(next).map(Ticket::label).collect(Collectors.toList());
		this.next.remove(this.label);
		return this;
	}

	public Ticket clearSibling() {
		this.sibling().clear();
		return this;
	}

	public Ticket clearFellow() {
		this.fellow().clear();
		return this;
	}

	public Ticket clearNext() {
		this.next().clear();
		return this;
	}

	public String mode() {
		return mode;
	}

	public Ticket mode(String mode) {
		this.mode = mode;
		return this;
	}

	public boolean join() {
		return join;
	}

	public Ticket join(boolean join) {
		this.join = join;
		return this;
	}

	public boolean enable() {
		return enable;
	}

	public Ticket enable(boolean enable) {
		this.enable = enable;
		return this;
	}

	public boolean valid() {
		return valid;
	}

	public Ticket valid(boolean valid) {
		this.valid = valid;
		return this;
	}

	public boolean completed() {
		return completed;
	}

	public Ticket completed(boolean completed) {
		this.completed = completed;
		return this;
	}

	public String label() {
		return label;
	}

	public Ticket label(String label) {
		this.label = label;
		return this;
	}

	public String act() {
		return act;
	}

	public Ticket act(String act) {
		this.act = act;
		return this;
	}

	public String target() {
		return target;
	}

	public Ticket target(String target) {
		this.target = target;
		return this;
	}

	public List<String> sibling() {
		return sibling;
	}

	public List<String> next() {
		return next;
	}

	public List<String> fellow() {
		return fellow;
	}

	@Override
	public int hashCode() {
		return Objects.hash(label);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ticket other = (Ticket) obj;
		return StringUtils.equalsIgnoreCase(label, other.label);
	}

}
