package com.x.processplatform.core.entity.ticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class Ticket implements Serializable {

	private static final long serialVersionUID = -9138959874056364353L;

	// 标识
	private String label;
	// 操作
	private String act;
	// 是否参与待办
	private boolean join;
	// 是否完成
	private boolean completed;
	// 验证有效性
	private boolean valid;
	// 启用
	private boolean enable;
	// 目标
	private String target;
	// 兄弟
	private List<String> sibling;
	// 伙伴
	private List<String> fellow;
	// 后续
	private List<String> next;
	// 模式
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

	public Ticket sibling(Ticket... sibling) {
		return sibling(Arrays.asList(sibling));
	}

	public Ticket sibling(Collection<Ticket> sibling) {
		this.sibling = sibling.stream().map(Ticket::label).distinct().collect(Collectors.toList());
		this.sibling.remove(this.label);
		return this;
	}

	public Ticket fellow(Ticket... fellow) {
		return fellow(Arrays.asList(fellow));
	}

	public Ticket fellow(Collection<Ticket> fellow) {
		this.fellow = fellow.stream().map(Ticket::label).distinct().collect(Collectors.toList());
		this.fellow.remove(this.label);
		return this;
	}

	public Ticket next(Ticket... next) {
		return next(Arrays.asList(next));
	}

	public Ticket next(Collection<Ticket> next) {
		this.next = next.stream().map(Ticket::label).distinct().collect(Collectors.toList());
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

	public Ticket appendSibling(Ticket... tickets) {
		return appendSibling(Arrays.asList(tickets));
	}

	public Ticket appendFellow(Ticket... tickets) {
		return appendFellow(Arrays.asList(tickets));
	}

	public Ticket appendNext(Ticket... tickets) {
		return appendNext(Arrays.asList(tickets));
	}

	public Ticket appendSibling(List<Ticket> list) {
		list.stream().forEach(o -> {
			if (!StringUtils.equals(this.label(), o.label())) {
				if (!this.sibling().contains(o.label())) {
					this.sibling().add(o.label());
				}
				if (!o.sibling().contains(this.label())) {
					o.sibling().add(this.label());
				}
			}
		});
		return this;
	}

	public Ticket appendFellow(List<Ticket> list) {
		list.stream().forEach(o -> {
			if (!StringUtils.equals(this.label(), o.label())) {
				if (!this.fellow().contains(o.label())) {
					this.fellow().add(o.label());
				}
				if (!o.fellow().contains(this.label())) {
					o.fellow().add(this.label());
				}
			}
		});
		return this;
	}

	public Ticket appendNext(List<Ticket> list) {
		list.stream().forEach(o -> {
			if (!StringUtils.equals(this.label(), o.label())) {
				this.next().add(o.label());
			}
		});
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