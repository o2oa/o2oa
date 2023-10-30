package com.x.processplatform.core.entity.ticket;

import java.util.Collection;
import java.util.List;

interface Reset {

	List<Ticket> reset(Tickets tickets, Ticket ticket, Collection<String> targets);

}