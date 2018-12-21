package com.x.base.core.container;

import com.x.base.core.container.checker.BooleanValueListPersistChecker;
import com.x.base.core.container.checker.BooleanValuePersistChecker;
import com.x.base.core.container.checker.ByteValueArrayPersistChecker;
import com.x.base.core.container.checker.DateValueListPersistChecker;
import com.x.base.core.container.checker.DateValuePersistChecker;
import com.x.base.core.container.checker.DoubleValueListPersistChecker;
import com.x.base.core.container.checker.DoubleValuePersistChecker;
import com.x.base.core.container.checker.FloatValueListPersistChecker;
import com.x.base.core.container.checker.FloatValuePersistChecker;
import com.x.base.core.container.checker.IntegerValueListPersistChecker;
import com.x.base.core.container.checker.IntegerValuePersistChecker;
import com.x.base.core.container.checker.LongValueListPersistChecker;
import com.x.base.core.container.checker.LongValuePersistChecker;
import com.x.base.core.container.checker.StringValueListPersistChecker;
import com.x.base.core.container.checker.StringValuePersistChecker;

public class PersistChecker {

	public PersistChecker(EntityManagerContainerBasic emc) {
		this.stringValue = new StringValuePersistChecker(emc);
		this.stringValueList = new StringValueListPersistChecker(emc);
		this.booleanValue = new BooleanValuePersistChecker(emc);
		this.booleanValueList = new BooleanValueListPersistChecker(emc);
		this.dateValue = new DateValuePersistChecker(emc);
		this.dateValueList = new DateValueListPersistChecker(emc);
		this.integerValue = new IntegerValuePersistChecker(emc);
		this.integerValueList = new IntegerValueListPersistChecker(emc);
		this.doubleValue = new DoubleValuePersistChecker(emc);
		this.doubleValueList = new DoubleValueListPersistChecker(emc);
		this.longValue = new LongValuePersistChecker(emc);
		this.longValueList = new LongValueListPersistChecker(emc);
		this.floatValue = new FloatValuePersistChecker(emc);
		this.floatValueList = new FloatValueListPersistChecker(emc);
		this.byteValueArray = new ByteValueArrayPersistChecker(emc);
	}

	public StringValuePersistChecker stringValue;
	public StringValueListPersistChecker stringValueList;
	public BooleanValuePersistChecker booleanValue;
	public BooleanValueListPersistChecker booleanValueList;
	public DateValuePersistChecker dateValue;
	public DateValueListPersistChecker dateValueList;
	public IntegerValuePersistChecker integerValue;
	public IntegerValueListPersistChecker integerValueList;
	public DoubleValuePersistChecker doubleValue;
	public DoubleValueListPersistChecker doubleValueList;
	public LongValuePersistChecker longValue;
	public LongValueListPersistChecker longValueList;
	public FloatValuePersistChecker floatValue;
	public FloatValueListPersistChecker floatValueList;
	public ByteValueArrayPersistChecker byteValueArray;

}
