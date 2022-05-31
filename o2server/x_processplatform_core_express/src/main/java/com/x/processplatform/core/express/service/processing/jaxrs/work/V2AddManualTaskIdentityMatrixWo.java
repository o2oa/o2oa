package com.x.processplatform.core.express.service.processing.jaxrs.work;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;

public class V2AddManualTaskIdentityMatrixWo extends GsonPropertyObject {

	private static final long serialVersionUID = -8631082471633729236L;

	private ManualTaskIdentityMatrix manualTaskIdentityMatrix;

	public ManualTaskIdentityMatrix getManualTaskIdentityMatrix() {
		return manualTaskIdentityMatrix;
	}

	public void setManualTaskIdentityMatrix(ManualTaskIdentityMatrix manualTaskIdentityMatrix) {
		this.manualTaskIdentityMatrix = manualTaskIdentityMatrix;
	}

}