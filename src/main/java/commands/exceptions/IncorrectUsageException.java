package commands.exceptions;

import commands.PluginCommand;

public class IncorrectUsageException extends MalformedCommandException {

    public IncorrectUsageException(PluginCommand command) {
        super(command.getUsage());
    }

}
