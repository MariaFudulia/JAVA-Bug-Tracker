package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Command {
    /**
     *
     * @return output
     */
    public abstract ObjectNode execute();
}
