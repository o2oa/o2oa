package com.x.processplatform.core.entity.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ManualTaskIdentityMatrix extends GsonPropertyObject {

	private static final long serialVersionUID = 5107066526414421883L;

	private List<List<String>> matrix = new ArrayList<>();

	public static ManualTaskIdentityMatrix concteteAsColumn(List<String> list) {
		ManualTaskIdentityMatrix manualTaskIdentityMatrix = new ManualTaskIdentityMatrix();
		for (String str : list) {
			if (StringUtils.isNotBlank(str)) {
				List<String> row = new ArrayList<>();
				row.add(str);
				manualTaskIdentityMatrix.matrix.add(row);
			}
		}
		return manualTaskIdentityMatrix;
	}

	public static ManualTaskIdentityMatrix concteteAsRow(List<String> list) {
		ManualTaskIdentityMatrix manualTaskIdentityMatrix = new ManualTaskIdentityMatrix();
		List<String> row = new ArrayList<>();
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

	public void clear() {
		this.matrix.clear();
	}

	public boolean isEmpty() {
		compact();
		return matrix.isEmpty();
	}

	public boolean remove(String identity) {
		boolean tag = false;
		for (List<String> list : matrix) {
			tag = list.remove(identity) || tag;
		}
		compact();
		return tag;
	}

	public ManualTaskIdentityMatrix replace(String source, String target) {
		for (List<String> list : this.matrix) {
			list.replaceAll(s -> StringUtils.equalsIgnoreCase(s, source) ? target : s);
		}
		compact();
		return this;
	}

	private void compact() {
		for (List<String> row : matrix) {
			Iterator<String> cell = row.iterator();
			while (cell.hasNext()) {
				String value = cell.next();
				if (StringUtils.isEmpty(value)) {
					cell.remove();
				}
			}
		}
		Iterator<List<String>> row = matrix.iterator();
		while (row.hasNext()) {
			List<String> value = row.next();
			if (null == value || value.isEmpty()) {
				row.remove();
			}
		}
	}

}