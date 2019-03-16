package com.x.processplatform.core.entity.element.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;

public class WorkLogTree {

	private Node root;

	List<WorkLog> list;

	Nodes nodes = new Nodes();

	public WorkLogTree(List<WorkLog> list) throws Exception {
		this.list = new ArrayList<WorkLog>(list);
		List<String> froms = ListTools.extractProperty(list, WorkLog.fromActivityToken_FIELDNAME, String.class, true,
				true);
		List<String> arriveds = ListTools.extractProperty(list, WorkLog.arrivedActivityToken_FIELDNAME, String.class,
				true, true);
		List<String> values = ListUtils.subtract(froms, arriveds);
		List<WorkLog> begins = list.stream()
				.filter(o -> BooleanUtils.isTrue(o.getConnected()) && values.contains(o.getFromActivityToken()))
				.collect(Collectors.toList());
		if (begins.size() != 1) {
			throw new ExceptionBeginNotFound(begins.size());
		}
		root = new Node();
		root.workLog = begins.get(0);
		root.parent = null;
		this.nodes.add(root);
		this.sub(root);
	}

	private void sub(Node node) {
		node.children = new Nodes();
		List<WorkLog> os = list.stream()
				.filter(o -> StringUtils.equals(node.workLog.getArrivedActivityToken(), o.getFromActivityToken()))
				.collect(Collectors.toList());
		if (!os.isEmpty()) {
			for (WorkLog o : os) {
				list.remove(o);
				Node child = new Node();
				child.workLog = o;
				child.parent = node;
				node.children.add(child);
				this.nodes.add(child);
				sub(child);
			}
		}
	}

	public static class Nodes extends TreeList<Node> {

		public boolean onlyManual() {
			return true;
		}

		public boolean containsWorkLog(WorkLog workLog) {
			for (Node n : this) {
				if (Objects.equals(n.getWorkLog(), workLog)) {
					return true;
				}
			}
			return false;
		}

	}

	public static class Node {

		private WorkLog workLog;
		private Node parent;
		private Nodes children = new Nodes();

		public Node upTo(ActivityType activityType, ActivityType... pass) {
			Node p = this.parent;
			List<ActivityType> passActivityTypes = ListTools.toList(pass);
			while ((p != null) && (!Objects.equals(p.workLog.getArrivedActivityType(), activityType))
					&& ListTools.contains(passActivityTypes, p.workLog.getFromActivityType())) {
				p = p.parent;
			}
			return p;
		}

		public WorkLog getWorkLog() {
			return workLog;
		}

	}

	public Node root() {
		return root;
	}

	public List<WorkLog> children(WorkLog workLog) {
		List<WorkLog> os = new ArrayList<>();
		Node node = null;
		for (Node o : nodes) {
			if (Objects.equals(o.workLog, workLog)) {
				node = o;
				break;
			}
		}
		if (node != null) {
			for (Node o : this.down(node)) {
				os.add(o.workLog);
			}
		}
		return os;
	}

	public List<WorkLog> parents(WorkLog workLog) {
		List<WorkLog> os = new ArrayList<>();
		Node node = null;
		for (Node o : nodes) {
			if (Objects.equals(o.workLog, workLog)) {
				node = o;
				break;
			}
		}
		if (node != null) {
			for (Node o : this.up(node)) {
				os.add(o.workLog);
			}
		}
		return os;
	}

	public Nodes down(Node node) {
		Nodes nodes = new Nodes();
		for (Node o : node.children) {
			nodes.add(o);
		}
		for (Node o : node.children) {
			nodes.addAll(down(o));
		}
		return nodes;
	}

	public Nodes up(Node node) {
		Nodes nodes = new Nodes();
		if (null != node.parent) {
			nodes.add(node.parent);
			nodes.addAll(up(node.parent));
		}
		return nodes;
	}

	public Node find(WorkLog workLog) {
		Node node = null;
		for (Node o : nodes) {
			if (Objects.equals(o.workLog, workLog)) {
				node = o;
				break;
			}
		}
		return node;
	}

	public Nodes nodes() {
		return nodes;
	}

	public Nodes rootTo(Node n) {
		Nodes os = new Nodes();
		Nodes loop = new Nodes();
		loop.add(this.root());
		while (!loop.isEmpty()) {
			Nodes temps = new Nodes();
			for (Node o : loop) {
				if (!os.contains(o) && (n != o)) {
					os.add(o);
					temps.addAll(o.children);
				}
			}
			loop = temps;
		}
		os.add(n);
		return os;
	}

	public Node location(Work work) {
		Node node = null;
		for (Node o : nodes) {
			if (Objects.equals(work.getActivityToken(), o.workLog.getFromActivityToken())) {
				node = o;
				break;
			}
		}
		return node;

	}

}
