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

        // If trivia wasn't yet enabled and the loop is now active feed a trivia question to the players.
        if(triviaHandler.isTriviaActive() && loopStatus)
        {
            triviaHandler.triviaQuestion();
        }

        if(loopStatus)
        {
            sender.sendMessage(ChatColor.GREEN + "Trivia Loop enabled.");
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Trivia Loop disabled.");
        }

        return true;
    }
}
