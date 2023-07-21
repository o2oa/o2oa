package com.x.base.core.container;

import com.x.base.core.container.checker.StringValueListRemoveChecker;
import com.x.base.core.container.checker.StringValueRemoveChecker;

public class RemoveChecker {

	public RemoveChecker(EntityManagerContainerBasic emc) {
		this.stringValue = new StringValueRemoveChecker(emc);
		this.stringValueList = new StringValueListRemoveChecker(emc);
	}

	public StringValueRemoveChecker stringValue;
	public StringValueListRemoveChecker stringValueList;
}
