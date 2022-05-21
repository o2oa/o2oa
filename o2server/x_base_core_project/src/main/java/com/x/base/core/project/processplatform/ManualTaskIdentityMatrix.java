package com.x.base.core.project.processplatform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class ManualTaskIdentityMatrix extends GsonPropertyObject {

	private static final long serialVersionUID = 5107066526414421883L;

	private Matrix matrix = new Matrix();

	public static final String ADD_POSITION_BEFORE = "before";
	public static final String ADD_POSITION_AFTER = "after";
	public static final String ADD_POSITION_TOP = "top";
	public static final String ADD_POSITION_BOTTOM = "bottom";
	public static final String ADD_POSITION_EXTEND = "extend";

	public static ManualTaskIdentityMatrix concreteSingleRow(List<String> list) {
		ManualTaskIdentityMatrix manualTaskIdentityMatrix = new ManualTaskIdentityMatrix();
		Row row = new Row();
		for (String str : list) {
			if (StringUtils.isNotBlank(str)) {
				row.add(str);
			}
		}
		if (!row.isEmpty()) {
			manualTaskIdentityMatrix.matrix.add(row);
		}
		return manualTaskIdentityMatrix;
	}

	public static ManualTaskIdentityMatrix concreteMultiRow(List<String> list) {
		ManualTaskIdentityMatrix manualTaskIdentityMatrix = new ManualTaskIdentityMatrix();
		for (String str : list) {
			if (StringUtils.isNotBlank(str)) {
				Row row = new Row();
				row.add(str);
				manualTaskIdentityMatrix.matrix.add(row);
			}
		}
		return manualTaskIdentityMatrix;
	}

	public ManualTaskIdentityMatrix add(String identity, String position, List<String> list) {
		List<String> identities = ListTools.trim(list, true, true);
		if (ListTools.isEmpty(identities)) {
			return this;
		}
		if (StringUtils.equals(ADD_POSITION_TOP, position)) {
			Row row = new Row();
			row.addAll(ListTools.trim(list, true, true));
			matrix.add(0, row);
		} else if (StringUtils.equals(ADD_POSITION_BOTTOM, position)) {
			Row row = new Row();
			row.addAll(ListTools.trim(list, true, true));
			matrix.add(row);
		} else {
			int rowpos = 0;
			int colpos = -1;
			for (Row row : matrix) {
				colpos = row.indexOf(identity);
				if (colpos > -1) {
					break;
				} else {
					rowpos++;
				}
			}
			if (colpos > -1) {
				addBeforeAfterExtend(position, identities, rowpos, colpos);
			}
		}
		compact();
		return this;
	}

	private void addBeforeAfterExtend(String position, List<String> identities, int rowpos, int colpos) {
		if (StringUtils.equals(ADD_POSITION_BEFORE, position)) {
			Row row = new Row();
			row.addAll(identities);
			matrix.add(rowpos, row);
		} else if (StringUtils.equals(ADD_POSITION_AFTER, position)) {
			Row row = new Row();
			row.addAll(identities);
			matrix.add(rowpos + 1, row);
		} else {
			matrix.get(rowpos).addAll(colpos + 1, identities);
		}
	}

	public ManualTaskIdentityMatrix reset(String identity, List<String> addBeforeIdentities,
			List<String> extendIdentities, List<String> addAfterIdentities, boolean remove) {
		int rowpos = 0;
		int colpos = -1;
		for (Row row : matrix) {
			colpos = row.indexOf(identity);
			if (colpos > -1) {
				break;
			} else {
				rowpos++;
			}
		}
		if (colpos > -1) {
			resetUpdate(rowpos, colpos, addBeforeIdentities, extendIdentities, addAfterIdentities, remove);
		}
		return this;
	}

	private void resetUpdate(int rowpos, int colpos, List<String> addBeforeIdentities, List<String> extendIdentities,
			List<String> addAfterIdentities, boolean remove) {
		if ((null != addBeforeIdentities) && (!addBeforeIdentities.isEmpty())) {
			for (String str : addBeforeIdentities) {
				Row row = new Row();
				row.add(str);
				matrix.add(rowpos++, row);
			}
		}
		if ((null != extendIdentities) && (!extendIdentities.isEmpty())) {
			matrix.get(rowpos).addAll(colpos + 1, extendIdentities);
		}
		if (remove) {
			matrix.get(rowpos).remove();
		}
		if ((null != addAfterIdentities) && (!addAfterIdentities.isEmpty())) {
			for (String str : addAfterIdentities) {
				Row row = new Row();
				row.add(str);
				matrix.add(++rowpos, row);
			}
			compact();
		}
	}

	public void clear() {
		this.matrix.clear();
	}

	public boolean isEmpty() {
		compact();
		return matrix.isEmpty();
	}

	public boolean contains(String identity) {
		for (Row row : matrix) {
			if (row.contains(identity)) {
				return true;
			}
		}
		return false;
	}

	public ManualTaskIdentityMatrix remove(String identity) {
		matrix.stream().forEach(row -> row.remove(identity));
		compact();
		return this;
	}

	public ManualTaskIdentityMatrix remove(Collection<String> identities) {
		matrix.stream().forEach(row -> row.removeAll(identities));
		compact();
		return this;
	}

	/**
	 * 工作处理完成,如果在行中有用户直接删除行
	 * 
	 * @param identity
	 * @return
	 */
	public List<String> completed(String identity) {
		List<String> list = new ArrayList<>();
		for (Row row : matrix) {
			if (row.contains(identity)) {
				list.addAll(row);
				row.clear();
				break;
			}
		}
		compact();
		return ListTools.trim(list, true, true);
	}

	/**
	 * 工作处理完成,如果在行中有用户直接删除行
	 * 
	 * @param identities
	 * @return
	 */
	public List<String> completed(List<String> identities) {
		Set<Row> rows = new HashSet<>();
		for (String identity : identities) {
			for (Row row : matrix) {
				if (row.contains(identity)) {
					rows.add(row);
					break;
				}
			}
		}
		List<String> list = new ArrayList<>();
		Iterator<Row> rowIterator = matrix.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (rows.contains(row)) {
				rowIterator.remove();
				list.addAll(row);
			}
		}
		compact();
		return ListTools.trim(list, true, true);
	}

	public ManualTaskIdentityMatrix replace(String source, String target) {
		matrix.stream().forEach(row -> row.replaceAll(s -> StringUtils.equalsIgnoreCase(s, source) ? target : s));
		compact();
		return this;
	}

	public List<String> read() {
		return isEmpty() ? new ArrayList<>() : new ArrayList<>(matrix.get(0));
	}

	public List<String> flat() {
		List<String> list = new ArrayList<>();
		this.matrix.stream().forEach(row -> row.stream().forEach(list::add));
		return list;
	}

	public ManualTaskIdentityMatrix reduce(String identity) {
		matrix.stream().forEach(row -> {
			if (row.contains(identity)) {
				row.clear();
				row.add(identity);
			}
		});
		compact();
		return this;
	}

	private void compact() {
		Iterator<Row> rowIterator = matrix.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Iterator<String> cellIterator = row.iterator();
			while (cellIterator.hasNext()) {
				if (StringUtils.isEmpty(cellIterator.next())) {
					cellIterator.remove();
				}
			}
			List<String> trim = ListTools.trim(row, true, true);
			row.clear();
			row.addAll(trim);
			if (row.isEmpty()) {
				rowIterator.remove();
			}
		}
	}

	public static class Matrix extends LinkedList<Row> {

		private static final long serialVersionUID = -53740621980996248L;

	}

	public static class Row extends LinkedList<String> {

		private static final long serialVersionUID = 4774108881630629L;

	}

	public static ManualTaskIdentityMatrix fromJson(String json) {
		ManualTaskIdentityMatrix o = new ManualTaskIdentityMatrix();
		o.matrix = (new Gson()).fromJson(json, Matrix.class);
		return o;
	}

	public String toJson() {
		return (new Gson()).toJson(this.matrix);
	}

	public static void main(String[] args) {
		String json = "[['A','B','C','D'],['E','F'],['G'],['H'],['I','J'],['K','L','M']]";
		ManualTaskIdentityMatrix matrix = ManualTaskIdentityMatrix.fromJson(json);
		matrix.reduce("C");
		matrix.replace("C", "E");
		System.out.println(matrix.toJson());
	}

}