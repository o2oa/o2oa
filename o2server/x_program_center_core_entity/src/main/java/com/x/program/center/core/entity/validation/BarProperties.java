package com.x.program.center.core.entity.validation;

import com.x.base.core.entity.JsonProperties;

public class BarProperties extends JsonProperties {

	private static final long serialVersionUID = 3619957195494464187L;

	private String name;
	private Integer conut;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getConut() {
		return conut;
	}

	public void setConut(Integer conut) {
		this.conut = conut;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conut == null) ? 0 : conut.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BarProperties other = (BarProperties) obj;
		if (conut == null) {
			if (other.conut != null)
				return false;
		} else if (!conut.equals(other.conut))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}