package com.x.general.assemble.control.jaxrs.invoice;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvoiceExists extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionInvoiceExists(String number) {
		super("该发票号码已存在: {}.", number);
	}
}
