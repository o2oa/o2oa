package com.x.processplatform.core.entity.ticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class Ticket implements Serializable {

	private static final long serialVersionUID = -9138959874056364353L;

	private static final Pattern DISTINGUISHEDNAME_LABEL_PATTERN = Pattern.compile("([^$]*)\\$\\{([^}]*)}$");

	// 标识
	private String label;
	// 操作
	private String act;
	// 是否完成
	private boolean completed;
	// 验证有效性
	private boolean valid;
	// 启用
	private boolean enable;
	// 目标
	private String distinguishedName;
	// 兄弟
	private List<String> sibling;
	// 伙伴
	private List<String> fellow;
	// 后续
	private List<String> next;
	// 模式
	private String mode;
	// 上级
	private String parent;
	// 授权标识
	private String fromDistinguishedName;

	public Ticket() {
		this.label = UUID.randomUUID().toString();
		this.act = "";
		this.completed = false;
		this.valid = true;
		this.enable = true;
		this.distinguishedName = "";
		this.sibling = new ArrayList<>();
		this.fellow = new ArrayList<>();
		this.next = new ArrayList<>();
		this.parent = "";
		this.fromDistinguishedName = "";
	}

	public Ticket(String distinguishedName) {
		this();
		this.distinguishedName(distinguishedName);
	}

	public Ticket(String distinguishedName, String label) {
		this();
		this.distinguishedName = distinguishedName;
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

	public Ticket appendSibling(Collection<Ticket> collection) {
		collection.stream().forEach(o -> {
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

	public Ticket appendFellow(Collection<Ticket> collection) {
		collection.stream().forEach(o -> {
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

	public Ticket appendNext(Collection<Ticket> collection) {
		collection.stream().forEach(o -> {
			if (!StringUtils.equals(this.label(), o.label())) {
				this.next().add(o.label());
			}
		});
		return this;
	}

	public String parent() {
		return parent;
	}

	public Ticket parent(String parent) {
		this.parent = parent;
		return this;
	}

	public String mode() {
		return mode;
	}

	public Ticket mode(String mode) {
		this.mode = mode;
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

	public String fromDistinguishedName() {
		return fromDistinguishedName;
	}

	public Ticket fromDistinguishedName(String fromDistinguishedName) {
		this.fromDistinguishedName = fromDistinguishedName;
		return this;
	}

	public String distinguishedName() {
		return distinguishedName;
	}

	public Ticket distinguishedName(String distinguishedName) {
		Matcher matcher = DISTINGUISHEDNAME_LABEL_PATTERN.matcher(distinguishedName);
		if (matcher.find()) {
			this.distinguishedName = matcher.group(1);
			this.label = matcher.group(2);
		} else {
			this.distinguishedName = distinguishedName;
		}
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

	public Ticket empower(String from, String to) {
		if (StringUtils.isNotEmpty(from) && StringUtils.isNotEmpty(to)
				&& StringUtils.isEmpty(this.fromDistinguishedName)
				&& StringUtils.equalsIgnoreCase(from, this.distinguishedName)) {
			this.fromDistinguishedName(from);
			this.distinguishedName(to);
		}

		return this;
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

	public Ticket copyFromSkipLabelDistinguishedName(Ticket ticket) {
		// 不需要复制label,distinguishedName
		this.act = ticket.act;
		this.completed = ticket.completed;
		this.valid = ticket.valid;
		this.enable = ticket.enable;
		this.sibling = new ArrayList<>(ticket.sibling);
		this.fellow = new ArrayList<>(ticket.fellow);
		this.next = new ArrayList<>(ticket.next);
		this.mode = ticket.mode;
		this.parent = ticket.parent;
		this.fromDistinguishedName = ticket.fromDistinguishedName;
		return this;
	}

}