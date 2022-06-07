package com.x.base.core.container.checker;

import com.x.base.core.container.EntityManagerContainerBasic;

abstract class AbstractChecker {

	AbstractChecker(EntityManagerContainerBasic emc) {
		this.emc = emc;
	}

	protected EntityManagerContainerBasic emc;

}