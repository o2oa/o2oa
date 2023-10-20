package test.ticket.add.level2;

import java.util.Arrays;
import java.util.List;
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
class SingleAddBeforeQueueAndBeforeQueueTest {

	@DisplayName("B前加签EFG,EFG串行处理,F前加签IJK,IJK串行处理,即单人-前加签串行-前加签串行")
	@Test
	@Order(1)
	void test01() {
		List<Ticket> p1 = Arrays.asList("A${LA}", "B${LB}", "C${LC}").stream().map(Ticket::new)
				.collect(Collectors.toList());
		List<String> p2 = Arrays.asList("E${LE}", "F${LF}", "G${LG}");
		List<String> p3 = Arrays.asList("I${LI}", "J${LJ}", "K${LK}");
		Tickets tickets = Tickets.single(p1);
		String value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("A,B,C", value);
		Optional<Ticket> opt = tickets.findTicketWithLabel("LB");
		tickets.add(opt.get(), p2, true, Tickets.MODE_QUEUE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("E", value);
		tickets.completed("LE");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("F", value);
		Optional<Ticket> opt1 = tickets.findTicketWithLabel("LF");
		tickets.add(opt1.get(), p3, true, Tickets.MODE_QUEUE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("I", value);
		tickets.completed("LI");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("J", value);
		tickets.completed("LJ");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("K", value);
		tickets.completed("LK");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("F", value);
		tickets.completed("LF");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("G", value);
		tickets.completed("LG");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("A,B,C", value);
		tickets.completed("LB");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("", value);

	}

}
