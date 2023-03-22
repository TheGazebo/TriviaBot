package com.coathar.trivia.commands;

import com.coathar.trivia.TriviaHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TriviaStart implements CommandExecutor, TabCompleter
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        TriviaHandler triviaHandler = TriviaHandler.getInstance();

        if(triviaHandler.isTriviaActive())
        {
            sender.sendMessage("Trivia is already in progress.");
        }
        else
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

        return true;
    }

    /**
     * The tab complete event
     * @param sender The person doing the tab completion.
     * @param command The command object being tab completed
     * @param alias The alias of the command being tab completed
     * @param args The arguments being tab completed
     * @return The list of valid completions
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        final List<String> completions = new ArrayList<>();

        if(args.length == 1)
        {
            final List<String> options = TriviaHandler.getInstance().getLabels();
            StringUtil.copyPartialMatches(args[0], options, completions);
            Collections.sort(completions);
        }

        return completions;
    }
}
