package test.ticket;

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

import com.x.processplatform.core.express.ticket.Ticket;
import com.x.processplatform.core.express.ticket.Tickets;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ParallelAddAfterParallelAndBeforeQueueTest {

	private static final List<Ticket> p1 = Arrays.asList(new Ticket("A", "LA"), new Ticket("B", "LB"),
			new Ticket("C", "LC"));
	private static final List<Ticket> p2 = Arrays.asList(new Ticket("E", "LE"), new Ticket("F", "LF"),
			new Ticket("G", "LG"));
	private static final List<Ticket> p3 = Arrays.asList(new Ticket("I", "LI"), new Ticket("J", "LJ"),
			new Ticket("K", "LK"));

	@DisplayName("B后加签EFG,EFG并行处理，F前加签IJK，IJK串行处理,即并行-后加签并行-前加签串行")
	@Test
	@Order(1)
	void test01() {
		Tickets tickets = Tickets.parallel(p1);
		String value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		Assertions.assertEquals("A,B,C", value);
		Optional<Ticket> opt = tickets.findTicketWithLabel("LB");
		tickets.add(opt.get(), p2, false, Tickets.MODE_PARALLEL);
		value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		Assertions.assertEquals("A,C,E,F,G", value);
		Optional<Ticket> opt1 = tickets.findTicketWithLabel("LF");
		tickets.add(opt1.get(), p3, true, Tickets.MODE_QUEUE);
		value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		Assertions.assertEquals("A,C,E,G,I", value);
		tickets.completed("LE");
		value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		Assertions.assertEquals("A,C,G,I", value);
		tickets.completed("LI");
		value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		Assertions.assertEquals("A,C,G,J", value);
		tickets.completed("LC");
		value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		Assertions.assertEquals("A,G,J", value);
		tickets.completed("LJ");
		value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		Assertions.assertEquals("A,G,K", value);

		// tickets.completed("LA");
		// value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		// Assertions.assertEquals("G,K", value);
		// tickets.completed("LG");
		// value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		// Assertions.assertEquals("K", value);
		// tickets.completed("LK");
		// value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		// Assertions.assertEquals("F", value);
		// tickets.completed("LF");
		// value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		// Assertions.assertEquals("", value);

		tickets.completed("LK");
		value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		Assertions.assertEquals("A,F,G", value);
		tickets.completed("LA");
		value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		Assertions.assertEquals("F,G", value);
		tickets.completed("LG");
		value = tickets.bubble().stream().<String>map(Ticket::target).sorted().collect(Collectors.joining(","));
		Assertions.assertEquals("F", value);
		tickets.completed("LF");
		value = tickets.bubble().stream().<String>map(Ticket::target).collect(Collectors.joining(","));
		Assertions.assertEquals("", value);
	}

}
