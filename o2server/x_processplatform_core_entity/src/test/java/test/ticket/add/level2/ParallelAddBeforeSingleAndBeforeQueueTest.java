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
class ParallelAddBeforeSingleAndBeforeQueueTest {

	@DisplayName("B前加签EFG,EFG单人处理,E前加签IJK,IJK串行处理,即并行-前加签单人-前加签串行")
	@Test
	@Order(1)
	void test01() {
		List<Ticket> p1 = Arrays.asList("A${LA}", "B${LB}", "C${LC}").stream().map(Ticket::new)
				.collect(Collectors.toList());
		List<String> p2 = Arrays.asList("E${LE}", "F${LF}", "G${LG}");
		List<String> p3 = Arrays.asList("I${LI}", "J${LJ}", "K${LK}");
		Tickets tickets = Tickets.parallel(p1);
		String value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("A,B,C", value);
		Optional<Ticket> opt = tickets.findTicketWithLabel("LB");
		tickets.add(opt.get(), p2, true, Tickets.MODE_SINGLE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("A,C,E,F,G", value);
		Optional<Ticket> opt1 = tickets.findTicketWithLabel("LE");
		tickets.add(opt1.get(), p3, true, Tickets.MODE_QUEUE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("A,C,I", value);
		tickets.completed("LA");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("C,I", value);
		tickets.completed("LI");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("C,J", value);
		tickets.completed("LC");
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
		Assertions.assertEquals("E,F,G", value);
		tickets.completed("LE");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("B", value);
		tickets.completed("LB");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).sorted()
				.collect(Collectors.joining(","));
		Assertions.assertEquals("", value);
	}

}
