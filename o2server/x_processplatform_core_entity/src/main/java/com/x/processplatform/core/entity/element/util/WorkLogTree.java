package com.x.processplatform.core.entity.element.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;

public class WorkLogTree {

	private Node root;

	public static List<String> RELY_WORKLOG_ITEMS = ListTools.toList(WorkLog.fromActivityToken_FIELDNAME,
			WorkLog.arrivedActivityToken_FIELDNAME, WorkLog.fromActivityType_FIELDNAME,
			WorkLog.arrivedActivityType_FIELDNAME, WorkLog.connected_FIELDNAME);

	Nodes nodes = new Nodes();

	public WorkLogTree(List<WorkLog> list) throws Exception {

		for (WorkLog o : list) {
			this.nodes().add(new Node(o));
		}

		List<String> froms = ListTools.extractProperty(list, WorkLog.fromActivityToken_FIELDNAME, String.class, true,
				true);
		List<String> arriveds = ListTools.extractProperty(list, WorkLog.arrivedActivityToken_FIELDNAME, String.class,
				true, true);
		List<String> values = ListUtils.subtract(froms, arriveds);
		WorkLog begin = list.stream()
				.filter(o -> BooleanUtils.isTrue(o.getConnected()) && values.contains(o.getFromActivityToken()))
				.findFirst().orElse(null); 
		if (null == begin) {
			throw new ExceptionBeginNotFound();
		}
		root = this.find(begin);
//		for (Node o : nodes) {
//			this.associate();
//		}
		this.associate();
	}

	private void associate() {
		for (Node node : nodes) {
			this.nodes.stream().filter(
					o -> StringUtils.equals(node.workLog.getFromActivityToken(), o.workLog.getArrivedActivityToken()))
					.forEach(o -> {
						node.parents.add(o);
					});
			this.nodes.stream().filter(
					o -> StringUtils.equals(node.workLog.getArrivedActivityToken(), o.workLog.getFromActivityToken()))
					.forEach(o -> {
						node.children.add(o);
					});
		}

	}

	public static class Nodes extends ListOrderedSet<Node> {

		private static final long serialVersionUID = 4612771613796262398L;

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

		public boolean containsWorkLogWithActivityToken(String activityToken) {
			for (Node n : this) {
				if (Objects.equals(n.getWorkLog().getArrivedActivityToken(), activityToken)) {
					return true;
				}
			}
			return false;
		}

		public Date latestArrivedTime() {
			Date date = null;
			if (!this.isEmpty()) {
				for (Node n : this) {
					if (null != n.getWorkLog().getArrivedTime()) {
						if (null == date) {
							date = n.getWorkLog().getArrivedTime();
						} else {
							date = n.getWorkLog().getArrivedTime().after(date) ? n.getWorkLog().getArrivedTime() : date;
						}
					}
				}
			}
			return date;
		}
	}

	public static class Node {

		public Node(WorkLog workLog) {
			this.workLog = workLog;
		}

		private WorkLog workLog;
		private Nodes parents = new Nodes();
		private Nodes children = new Nodes();

		public Nodes upTo(ActivityType activityType, ActivityType... pass) {
			return this.upTo(activityType, ListTools.toList(pass));
		}

		public Nodes upTo(ActivityType activityType, List<ActivityType> pass) {
			Nodes result = new Nodes();
			this.upTo(activityType, pass, result);
			return result;
		}

		private void upTo(ActivityType activityType, List<ActivityType> pass, Nodes result) {
			for (Node o : this.parents) {
				if (Objects.equals(o.workLog.getFromActivityType(), activityType)) {
					result.add(o);
				} else {
					if (ListTools.contains(pass, o.workLog.getFromActivityType())) {
						o.upTo(activityType, pass, result);
					}
				}
			}
		}

		public Nodes parents() {
			return parents;
		}

		public Nodes children() {
			return children;
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
		Nodes result = new Nodes();
		this.up(node, result);
		return result;
	}

	private void up(Node node, Nodes result) {
		for (Node o : node.parents) {
			result.add(o);
			up(o, result);
		}
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
