package com.x.processplatform.core.entity.ticket;

import java.util.Collection;

interface Reset {

	Collection<Ticket> reset(Tickets tickets, Ticket ticket, Collection<Ticket> targets);

}