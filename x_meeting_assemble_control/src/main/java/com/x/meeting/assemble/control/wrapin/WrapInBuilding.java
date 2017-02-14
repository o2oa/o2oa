package com.x.meeting.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.meeting.core.entity.Building;

@Wrap(Building.class)
public class WrapInBuilding extends Building {

	private static final long serialVersionUID = -4665940290226161172L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}
