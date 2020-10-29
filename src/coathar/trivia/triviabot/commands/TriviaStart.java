package coathar.trivia.triviabot.commands;

import coathar.trivia.triviabot.TriviaHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TriviaStart implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        TriviaHandler triviaHandler = TriviaHandler.getInstance();

        if(triviaHandler.isTriviaActive())
        {
            sender.sendMessage("Trivia is already started!");
        }
        else
        {
            sender.sendMessage("Trivia Started");
            triviaHandler.triviaQuestion();
        }

        return true;
    }
}
