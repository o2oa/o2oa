package com.x.common.core.container.checker;

import com.x.common.core.container.EntityManagerContainerBasic;

public abstract class AbstractChecker {

	protected AbstractChecker(EntityManagerContainerBasic emc) {
		this.emc = emc;
	}

	protected EntityManagerContainerBasic emc;

}
