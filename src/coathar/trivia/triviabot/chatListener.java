package coathar.trivia.triviabot;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class chatListener implements Listener {
	
	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event) {
		if(Main.triviaActive){
			if(event.getMessage().toLowerCase().equals(Main.answer)) {
				new BukkitRunnable() {
					@Override
					public void run() {
						Bukkit.broadcastMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "Trivia Bot" + ChatColor.GOLD + "] " + ChatColor.GREEN + event.getPlayer().getName() + ChatColor.DARK_GREEN + " wins! The correct answer was: " + ChatColor.GREEN + Main.answers.get(Main.r));
						Main.triviaActive = false;
						if (Main.triviaActive) {
							triviaQuestion();
						}
					}
				}.runTaskLater(Main.plugin, 20);
			}
		}
	}
}
