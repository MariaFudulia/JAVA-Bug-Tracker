package workflow;

import com.fasterxml.jackson.databind.node.ObjectNode;
import context.AppContext;
import tickets.Ticket;

public interface Workflow {
    boolean canReportTicket();
}
