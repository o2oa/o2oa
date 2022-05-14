package com.x.base.core.project.processplatform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class ManualTaskIdentityMatrix extends GsonPropertyObject {

	private static final long serialVersionUID = 5107066526414421883L;

	private Matrix matrix = new Matrix();

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

	public ManualTaskIdentityMatrix extend(String identity, boolean replace, List<String> list) {
		for (Row row : matrix) {
			int idx = row.indexOf(identity);
			if (idx > -1) {
				row.addAll(idx + 1, list);
				if (replace) {
					row.remove(idx);
				}
				break;
			}
		}
		return this;
	}

	public ManualTaskIdentityMatrix extend(String identity, boolean replace, String... arr) {
		return this.extend(identity, replace, Arrays.asList(arr));
	}

	public ManualTaskIdentityMatrix add(String identity, boolean after, boolean replace, List<String> list) {
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
		if (replace && (colpos > -1)) {
			matrix.get(rowpos).remove(colpos);
		}
		if (after) {
			rowpos++;
		}
		for (String str : list) {
			Row row = new Row();
			row.add(str);
			matrix.add(rowpos++, row);
		}
		compact();
		return this;
	}

	public ManualTaskIdentityMatrix add(String identity, boolean after, boolean replace, String... arr) {
		return this.add(identity, after, replace, Arrays.asList(arr));
	}

	public void clear() {
		this.matrix.clear();
	}

	public boolean isEmpty() {
		compact();
		return matrix.isEmpty();
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
		matrix.stream().forEach(row -> {
			if (row.contains(identity)) {
				list.addAll(row);
				row.clear();
			}
		});
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
		List<String> list = new ArrayList<>();
		matrix.stream().forEach(row -> {
			if (!ListUtils.intersection(identities, row).isEmpty()) {
				list.addAll(row);
				row.clear();
			}
		});
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