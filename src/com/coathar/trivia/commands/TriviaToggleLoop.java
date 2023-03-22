package com.coathar.trivia.commands;

import com.coathar.trivia.TriviaHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TriviaToggleLoop implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        TriviaHandler triviaHandler = TriviaHandler.getInstance();

        boolean loopStatus = triviaHandler.toggleTriviaLooped();

        if(loopStatus)
        {
            sender.sendMessage(ChatColor.GREEN + "Trivia Loop enabled.");

            // If trivia wasn't yet enabled and the loop is now active feed a trivia question to the players.
            if(!triviaHandler.isTriviaActive())
            {
                try
                {
                    String category = args.length > 0 ? args[0] : "";

                    triviaHandler.triviaQuestion(category);
                }
                catch(Exception e)
                {
                    sender.sendMessage(ChatColor.RED + e.getMessage());
                }
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Trivia Loop disabled.");
        }

        return true;
    }
}
