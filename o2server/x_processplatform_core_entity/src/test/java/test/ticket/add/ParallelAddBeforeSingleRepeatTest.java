package test.ticket.add;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.x.processplatform.core.entity.ticket.Ticket;
import com.x.processplatform.core.entity.ticket.Tickets;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ParallelAddBeforeSingleRepeatTest {

	@Test
	@Order(1)
	void test01() {
		List<Ticket> p1 = Arrays.asList("A${LA}", "B${LB}", "C${LC}").stream().map(Ticket::new)
				.collect(Collectors.toList());
		List<String> p2 = Arrays.asList("B${LB2}", "C${LC2}", "D${LD2}", "E${LE2}");
		List<String> p3 = Arrays.asList("C${LC3}", "D${LD3}", "E${LE3}", "F${LF3}", "G${LG3}");
		Tickets tickets = Tickets.parallel(p1);
		String value = tickets.bubble().stream().<String>map(Ticket::distinguishedName)
				.collect(Collectors.joining(","));
		Assertions.assertEquals("A,B,C", value);
		Optional<Ticket> opt = tickets.findTicketWithLabel("LA");
		tickets.reset(opt.get(), p2);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("B,C,D,E", value);
		value = tickets.bubble().stream().<String>map(Ticket::label).collect(Collectors.joining(","));
		Assertions.assertEquals("LB,LC,LD2,LE2", value);
		opt = tickets.findTicketWithLabel("LB");
		tickets.add(opt.get(), p3, true, Tickets.MODE_SINGLE);
		value = tickets.bubble().stream().<String>map(Ticket::label).collect(Collectors.joining(","));
		Assertions.assertEquals("LC,LD2,LE2,LF3,LG3", value);
	}
}
