package coathar.trivia.triviabot.commands;

import coathar.trivia.triviabot.TriviaBot;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TriviaReload implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        TriviaBot.getInstance().reloadConfig();

        sender.sendMessage(ChatColor.GREEN + "TriviaBot reloaded.");

        return true;
    }
}
