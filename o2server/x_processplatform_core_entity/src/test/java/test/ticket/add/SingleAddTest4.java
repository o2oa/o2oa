package test.ticket.add;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.x.processplatform.core.entity.ticket.Ticket;
import com.x.processplatform.core.entity.ticket.Tickets;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SingleAddTest4 {

	@DisplayName("single3层加签中含办理.")
	@Test
	@Order(1)
	void test01() {
		Ticket a = new Ticket("a", "a1");
		Ticket b = new Ticket("b", "b1");
		Ticket c = new Ticket("c", "c1");
		Ticket d = new Ticket("d", "d1");
		Ticket e = new Ticket("e", "e1");
		Ticket f = new Ticket("f", "f1");
		Tickets tickets = Tickets.parallel(b, c);
		String value = tickets.bubble().stream().<String>map(Ticket::distinguishedName)
				.collect(Collectors.joining(","));
		Assertions.assertEquals("b,c", value);
		Optional<Ticket> opt = tickets.findTicketWithLabel("b1");
		tickets.reset("b1", Arrays.asList("d${d1}", "e${e1}"));
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("c,d,e", value);
		opt = tickets.findTicketWithLabel("d1");
		tickets.add(opt.get(), Arrays.asList("c${c2}", "f${f1}"), false, Tickets.MODE_SINGLE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("c,e,f", value);
	}

}
