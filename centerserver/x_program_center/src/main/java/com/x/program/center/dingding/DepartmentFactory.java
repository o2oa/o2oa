package com.x.program.center.dingding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DepartmentFactory {

	private List<Department> departments = new ArrayList<>();

	public DepartmentFactory(List<Department> departments) {
		this.departments.addAll(departments);
	}

	public Department root() {
		return departments.stream().filter(o -> {
			return Objects.equals(o.getId(), 1L) ? true : false;
		}).findFirst().orElse(null);
	}

	public List<Department> listSub(Department department) {
		return departments.stream().filter(o -> {
			return Objects.equals(o.getParentid(), Objects.toString(department.getId())) ? true : false;
		}).collect(Collectors.toList());
	}

	public Department get(String id) {
		return departments.stream().filter(o -> {
			return Objects.equals(id, Objects.toString(o.getId())) ? true : false;
		}).findFirst().orElse(null);
	}

}
