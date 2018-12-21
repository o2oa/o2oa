package com.x.processplatform.assemble.bam.jaxrs.state;

import com.x.processplatform.assemble.bam.stub.ApplicationStubs;
import com.x.processplatform.assemble.bam.stub.PersonStubs;
import com.x.processplatform.assemble.bam.stub.UnitStubs;

public class State {

	private ApplicationStubs applicationStubs = new ApplicationStubs();

	private UnitStubs unitStubs = new UnitStubs();

	private PersonStubs personStubs = new PersonStubs();

	private ActionSummary.Wo summary = new ActionSummary.Wo();

	private ActionRunning.Wo running = new ActionRunning.Wo();

	private ActionOrganization.Wo organization = new ActionOrganization.Wo();

	private ActionCategory.Wo category = new ActionCategory.Wo();

	public ApplicationStubs getApplicationStubs() {
		return applicationStubs;
	}

	public void setApplicationStubs(ApplicationStubs applicationStubs) {
		this.applicationStubs = applicationStubs;
	}

	public PersonStubs getPersonStubs() {
		return personStubs;
	}

	public void setPersonStubs(PersonStubs personStubs) {
		this.personStubs = personStubs;
	}

	public UnitStubs getUnitStubs() {
		return unitStubs;
	}

	public void setUnitStubs(UnitStubs unitStubs) {
		this.unitStubs = unitStubs;
	}

	public ActionSummary.Wo getSummary() {
		return summary;
	}

	public void setSummary(ActionSummary.Wo summary) {
		this.summary = summary;
	}

	public ActionRunning.Wo getRunning() {
		return running;
	}

	public void setRunning(ActionRunning.Wo running) {
		this.running = running;
	}

	public ActionOrganization.Wo getOrganization() {
		return organization;
	}

	public void setOrganization(ActionOrganization.Wo organization) {
		this.organization = organization;
	}

	public ActionCategory.Wo getCategory() {
		return category;
	}

	public void setCategory(ActionCategory.Wo category) {
		this.category = category;
	}

}
