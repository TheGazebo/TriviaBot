package coathar.trivia.triviabot;


import java.util.List;
import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
public class Main extends JavaPlugin {
	
	FileConfiguration config = getConfig();
	File configFile;
	public static List<String> questions;
	public static List<String> answers;
	public static String answer;
	Random random = new Random();
	public static Main plugin;
	private Logger log = Logger.getLogger("Minecraft");
	public static boolean triviaActive = false;
	public static int r = 0;
	
	@Override
	public void onEnable() {
		plugin = this;
		Bukkit.getPluginManager().registerEvents(new chatListener(), this);
		if (!(new File(getDataFolder(), "config.yml").exists())) {
			List<String> defaultQuestions = Arrays.asList("These are where you locate questions","They correspond with the answers below");
			config.set("Questions", defaultQuestions);
			List<String> defaultAnswers = Arrays.asList("These are where you locate answers","They correspond with the answers above");
			config.set("Answers", defaultAnswers);
			saveConfig();
		}
		log.info("TriviaBot is now enabled!");
		questions = config.getStringList("Questions");
		answers = config.getStringList("Answers");
	}
	@Override
	public void onDisable() {
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label,
			String[] args) {
		if (command.getName().equalsIgnoreCase("triviastart")) {
			if(triviaActive) {
				sender.sendMessage("Trivia is already started!");
			} else {
				sender.sendMessage("Trivia Started");
				triviaQuestion();
			}
			return true;
		}
		if (command.getName().equalsIgnoreCase("triviareload")) {
			sender.sendMessage("Trivia Config Reloaded");
			triviaActive = false;
			this.reloadConfig();
			config = getConfig();
			questions = config.getStringList("Questions");
			answers = config.getStringList("Answers");
			return true;
		}
		return false;
	}
	public void triviaQuestion() {
		triviaActive = true;
		r = random.nextInt(questions.size());
		Bukkit.broadcastMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "Trivia Bot" + ChatColor.GOLD + "] " + ChatColor.DARK_AQUA + "Question: " + ChatColor.AQUA + questions.get(r));
		answer = answers.get(r).toLowerCase();
	}
	
}
