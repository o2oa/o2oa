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
class SingleAddTest3 {

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
		tickets.add(opt.get(), Arrays.asList("b${b1}", "c${c1}"), true, Tickets.MODE_SINGLE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("b,c", value);
		opt = tickets.findTicketWithLabel("c1");
		tickets.add(opt.get(), Arrays.asList("a${a2}"), false, Tickets.MODE_SINGLE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("a", value);
		opt = tickets.findTicketWithLabel("a2");
		tickets.completed(opt.get());
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("a", value);

	}

}
