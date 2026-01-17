package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Command {
    public abstract ObjectNode execute();
}
