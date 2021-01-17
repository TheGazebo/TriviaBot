package com.coathar.trivia.commands;

import com.coathar.trivia.TriviaBot;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TriviaReload implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        TriviaBot.getInstance().reloadQuestions();

        sender.sendMessage(ChatColor.GREEN + "TriviaBot reloaded.");

        return true;
    }
}
