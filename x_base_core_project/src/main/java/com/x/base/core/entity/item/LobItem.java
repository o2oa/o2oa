package com.x.base.core.entity.item;

import com.x.base.core.entity.SliceJpaObject;

public abstract class LobItem extends SliceJpaObject {

	private static final long serialVersionUID = 2686455754221284260L;

	public abstract String getData();

	public abstract void setData(String data);

}
