package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.x.base.core.project.organization.Person;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.PersonStub;
import com.x.processplatform.assemble.bam.stub.PersonStubs;

public class TimerPersonStubs extends ActionBase {

	public void execute(Business business) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		List<Person> os = business.organization().person().listLoginAfterObject(cal.getTime());
		PersonStubs stubs = new PersonStubs();
		List<PersonStub> list = new ArrayList<>();
		for (Person o : os) {
			PersonStub stub = new PersonStub();
			stub.setName(o.getName());
			stub.setValue(o.getDistinguishedName());
			list.add(stub);
		}
		stubs.addAll(list);
		ThisApplication.state.setPersonStubs(stubs);
	}

}