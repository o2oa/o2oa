package com.x.component.assemble.control.jaxrs.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.component.core.entity.Component;

@Wrap(Component.class)
public class WrapInComponent extends Component {

	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}
