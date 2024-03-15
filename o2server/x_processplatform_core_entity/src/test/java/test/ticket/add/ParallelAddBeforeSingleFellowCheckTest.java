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
class ParallelAddBeforeSingleFellowCheckTest {

	/**
	 * A和B并行,B前加签单人CD,那么CD和AB分别作为fellow,而CD之间不应该是fellow
	 */
	@Test
	@Order(1)
	void test01() {
		List<Ticket> p1 = Arrays.asList("A${LA}", "B${LB}").stream().map(o -> new Ticket(o))
				.collect(Collectors.toList());
		List<String> p2 = Arrays.asList("C${LC}", "D${LD}");
		Tickets tickets = Tickets.parallel(p1);
		String value = tickets.bubble().stream().<String>map(Ticket::distinguishedName)
				.collect(Collectors.joining(","));
		Assertions.assertEquals("A,B", value);
		Optional<Ticket> opt = tickets.findTicketWithLabel("LB");
		tickets.add(opt.get(), p2, true, Tickets.MODE_SINGLE);
		value = tickets.bubble().stream().<String>map(Ticket::distinguishedName).collect(Collectors.joining(","));
		Assertions.assertEquals("A,C,D", value);
		opt = tickets.findTicketWithLabel("LC");
		value = opt.get().fellow().stream().collect(Collectors.joining(","));
		System.out.println(value);
		Assertions.assertEquals("LA,LB", value);
	}
}
