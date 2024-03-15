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
class SingleAddTest {

	@DisplayName("single3层加签中含办理.")
	@Test
	@Order(1)
	void test01() {
		Ticket a = new Ticket("a", "a1");
		Tickets tickets = Tickets.single(a);
		String value = tickets.bubble().stream().<String>map(Ticket::distinguishedName)
				.collect(Collectors.joining(","));
		Assertions.assertEquals("a", value);
		Optional<Ticket> opt = tickets.findTicketWithLabel("a1");
		tickets.add(opt.get(), Arrays.asList("b${b1}", "c${c1}"), false, Tickets.MODE_SINGLE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("b,c", value);
		opt = tickets.findTicketWithLabel("c1");
		tickets.add(opt.get(), Arrays.asList("d${d1}", "e${e1}"), true, Tickets.MODE_SINGLE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("d,e", value);
		opt = tickets.findTicketWithLabel("d1");
		tickets.completed(opt.get());
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("b,c", value);
		opt = tickets.findTicketWithLabel("b1");
		tickets.add(opt.get(), Arrays.asList("c${c2}", "d${d2}", "f${f1}"), false, Tickets.MODE_SINGLE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("c,d,f", value);
	}

}
