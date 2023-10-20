package test.ticket.add;

import java.util.Arrays;
import java.util.List;
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
		Ticket b = new Ticket("b", "b1");
		Ticket c = new Ticket("c", "c1");
		tickets.add(a, Arrays.asList(b, c), false, Tickets.MODE_SINGLE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("b,c", value);
		Ticket d = new Ticket("d", "d1");
		Ticket e = new Ticket("e", "e1");
		tickets.add(c, Arrays.asList(d, e), true, Tickets.MODE_SINGLE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("d,e", value);
		tickets.completed(d);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("b,c", value);
		Ticket c2 = new Ticket("c", "c2");
		Ticket d2 = new Ticket("d", "d2");
		Ticket f = new Ticket("f", "f1");
		tickets.add(b, Arrays.asList(c2, d2, f), false, Tickets.MODE_SINGLE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("d,e", value);
	}

}
