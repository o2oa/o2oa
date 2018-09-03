package com.x.server.console.test;

import org.junit.Test;

import de.vandermeer.asciitable.AT_Row;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

public class AciiTableTestClient {

	@Test
	public void test() {
		// tag::example[]
		AsciiTable at = new AsciiTable();
		at.addRule();
		at.addRow("row 1 col 1", "row 1 col 2");
		at.addRule();
		AT_Row row = at.addRow("row 2 col 1", "row 2 col 2");
		at.addRule();

		row.setPaddingTopChar('v');
		row.setPaddingBottomChar('^');
		row.setPaddingLeftChar('>');
		row.setPaddingRightChar('<');
		row.setTextAlignment(TextAlignment.CENTER);
		row.setPadding(1);
		System.out.println(at.render(33));
		// end::example[]

	}
}
