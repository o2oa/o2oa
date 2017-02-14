package com.x.component.assemble.control.jaxrs.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.component.core.entity.Component;

@Wrap(Component.class)
public class WrapOutComponent extends Component {

	private static final long serialVersionUID = -4751880472257349437L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
