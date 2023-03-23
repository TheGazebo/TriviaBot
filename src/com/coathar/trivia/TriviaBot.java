package com.coathar.trivia;

import java.util.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.coathar.trivia.commands.TriviaReload;
import com.coathar.trivia.commands.TriviaStart;
import com.coathar.trivia.commands.TriviaToggleLoop;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class TriviaBot extends JavaPlugin {

	private static volatile TriviaBot m_UniqueInstance;

	private FileConfiguration m_Config;
	private Logger m_Logger;
	private TriviaHandler m_TriviaHandler;

	@Override
	public void onEnable()
	{
		this.m_UniqueInstance = this;

		this.getDataFolder().mkdir();
		this.m_Config = this.getConfig();
		this.m_Logger = this.getLogger();

		// If the configuration file does not exist set up a default question to show the user how to format the file.
		if(!(new File(getDataFolder(), "config.yml").exists()))
		{
			this.m_Config.createSection("default");
			ConfigurationSection questionSection = this.m_Config.getConfigurationSection("defaultLabel").createSection("defaultQuestion");

			questionSection.set("question", "This is a default question.");

			List<String> defaultAnswers = Arrays.asList("This is a default answer", "This is also a default answer");
			questionSection.set("answers", defaultAnswers);

			saveConfig();

			this.m_Logger.warning("No configuration file was found. Dummy questions were loaded. Reload after filling in questions.");
		}

		this.m_TriviaHandler = TriviaHandler.getInstance();
		this.loadTriviaFromConfig();

		this.registerCommands();

		this.m_Logger.info("TriviaBot is now enabled!");
	}

	/**
	 * Loads all trivia from the configuration file.
	 */
	private void loadTriviaFromConfig()
	{
		Map<String, String> prefixMap = new HashMap<>();
		Map<String, List<Trivia>> triviaMap = new HashMap<>();

		for(String label : this.m_Config.getKeys(false))
		{
			List<Trivia> trivia = new ArrayList<>();
			ConfigurationSection typeSection = this.m_Config.getConfigurationSection(label);
			ConfigurationSection questionsSection = typeSection.getConfigurationSection("trivia-questions");

			prefixMap.put(label, ChatColor.translateAlternateColorCodes('&', typeSection.getString("prefix")));

			for(String triviaKey : questionsSection.getKeys(false))
			{
				ConfigurationSection triviaQuestion = questionsSection.getConfigurationSection(triviaKey);

				try
				{
					String category = triviaQuestion.getString("category", "");
					String question = triviaQuestion.getString("question");
					List<String> answers = triviaQuestion.getStringList("answers");

					// Avoid empty trivia questions
					if(question.isEmpty() || answers.size() == 0)
						continue;

					trivia.add(new Trivia(label, category, question, answers));
				}
				catch(NullPointerException e)
				{
					m_Logger.log(Level.WARNING, "Failed to load trivia question of type " + label + " with key " + triviaKey + ".");
				}
			}

			triviaMap.put(label, trivia);
		}

		this.m_TriviaHandler.loadTrivia(triviaMap, prefixMap);
	}

	/**
	 * Reloads the trivia questions from the configuration.
	 */
	public void reloadQuestions()
	{
		// Reload the config first
		this.reloadConfig();
		this.m_Config = this.getConfig();
		this.loadTriviaFromConfig();
	}

	/**
	 * Broadcasts a question and sets the handler to await an answer.
	 * @param label The type of the trivia question.
	 */
	public void triviaQuestion(String label)
	{
		this.m_TriviaHandler.triviaQuestion(label);
	}

	/**
	 * Broadcasts a question and sets the handler to await an answer.
	 */
	public void triviaQuestion()
	{
		this.m_TriviaHandler.triviaQuestion();
	}

	/**
	 * Cancels the active trivia question if there is one.
	 */
	public void cancelTrivia()
	{
		this.m_TriviaHandler.cancelTrivia();
	}

	@Override
	public void onDisable()
	{

	}

	/**
	 * Registers all commands
	 */
	private void registerCommands()
	{
		this.getCommand("triviastart").setExecutor(new TriviaStart());
		this.getCommand("triviatoggleloop").setExecutor(new TriviaToggleLoop());
		this.getCommand("triviareload").setExecutor(new TriviaReload());
	}

	public static TriviaBot getInstance()
	{
		return m_UniqueInstance;
	}

}
