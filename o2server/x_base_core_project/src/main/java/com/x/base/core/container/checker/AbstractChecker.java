package com.x.base.core.container.checker;

import com.x.base.core.container.EntityManagerContainerBasic;

public abstract class AbstractChecker {

	protected AbstractChecker(EntityManagerContainerBasic emc) {
		this.emc = emc;
	}

	protected EntityManagerContainerBasic emc;

}
