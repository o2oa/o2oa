package com.x.processplatform.core.entity.element.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.WorkLog;

public class WorkLogTree {

	private Node root;

	List<WorkLog> list;

	List<Node> nodes = new TreeList<>();

	public WorkLogTree(List<WorkLog> list) throws Exception {
		this.list = new ArrayList<WorkLog>(list);
		List<String> froms = ListTools.extractProperty(list, WorkLog.fromActivityToken_FIELDNAME, String.class, true,
				true);
		List<String> arriveds = ListTools.extractProperty(list, WorkLog.arrivedActivityToken_FIELDNAME, String.class,
				true, true);
		List<String> values = ListUtils.subtract(froms, arriveds);
		List<WorkLog> begins = list.stream().filter(o -> values.contains(o.getFromActivityToken()))
				.collect(Collectors.toList());
		if (begins.size() != 1) {
			throw new Exception();
		}
		root = new Node();
		root.workLog = begins.get(0);
		root.parent = null;
		this.nodes.add(root);
		this.sub(root);
	}

	private void sub(Node node) {
		node.children = new ArrayList<>();
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

	public static class Node {
		private WorkLog workLog;
		private Node parent;
		private List<Node> children;
	}

	public WorkLog root() {
		return root.workLog;
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

	private List<Node> down(Node node) {
		List<Node> os = new ArrayList<>();
		for (Node o : node.children) {
			os.add(o);
		}
		for (Node o : node.children) {
			os.addAll(down(o));
		}
		return os;
	}

	private List<Node> up(Node node) {
		List<Node> os = new ArrayList<>();
		if (null != node.parent) {
			os.add(node.parent);
			os.addAll(up(node.parent));
		}
		return os;
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

	public List<Node> nodes() {
		return nodes;
	}

}
