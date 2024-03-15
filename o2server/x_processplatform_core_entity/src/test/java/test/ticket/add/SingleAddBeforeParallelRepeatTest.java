package test.ticket.add;

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
class SingleAddBeforeParallelRepeatTest {

	@DisplayName("B前加签EFG,FEG串行处理")
	@Test
	@Order(1)
	void test01() {
		List<Ticket> p1 = Arrays.asList("A${LA}", "B${LB}", "C${LC}").stream().map(Ticket::new)
				.collect(Collectors.toList());
		List<String> p2 = Arrays.asList("A${LA2}", "C${LC2}");
		Tickets tickets = Tickets.single(p1);
		String value = tickets.bubble().stream().<String>map(Ticket::distinguishedName)
				.collect(Collectors.joining(","));
		Assertions.assertEquals("A,B,C", value);
		Optional<Ticket> opt = tickets.findTicketWithLabel("LB");
		tickets.add(opt.get(), p2, true, Tickets.MODE_PARALLEL);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("A,C", value);
		tickets.completed("LA2");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("C", value);
		tickets.completed("LC2");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("A,B,C", value);
		tickets.completed("LB");
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("", value);
	}

}
