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
		Map<String, TriviaType> triviaMap = new HashMap<>();

		// Load trivia by usage: dvz, april event, jeopardy
		for(String label : this.m_Config.getKeys(false))
		{
			Map<String, Category> categories = new HashMap<String, Category>();

			ConfigurationSection typeSection = this.m_Config.getConfigurationSection(label);
			ConfigurationSection categorySection = typeSection.getConfigurationSection("categories");

			String prefix = ChatColor.translateAlternateColorCodes('&',
					typeSection.getString("prefix", "&6[&eTrivia Bot&6]")); // The prefix to display before the question. Replaces [Trivia Bot].
			boolean showCategories = typeSection.getBoolean("show-categories", false); // Whether the categories will be displayed before each question.
			boolean requireGlobal = typeSection.getBoolean("require-global", false); // Whether the categories will be displayed before each question.
			boolean refreshQuestions = typeSection.getBoolean("refresh-questions", true); // Whether the categories will refresh these questions once all questions have been exhausted.

			// Load each category under the main label
			for(String categoryKey : categorySection.getKeys(false))
			{
				ConfigurationSection questionsSection = categorySection.getConfigurationSection(categoryKey);
				ArrayList<Trivia> trivia = new ArrayList<>();

				// Load each trivia question
				for(String triviaKey : questionsSection.getKeys(false))
				{
					ConfigurationSection triviaQuestion = questionsSection.getConfigurationSection(triviaKey);

					try
					{
						String question = triviaQuestion.getString("question");
						List<String> answers = triviaQuestion.getStringList("answers");

						trivia.add(new Trivia(triviaKey, question, answers));
					}
					catch(IllegalStateException e)
					{
						// Log this error and skip loading this question.
						this.m_Logger.log(Level.WARNING, e.getMessage());
					}
				}

				try
				{
					categories.put(categoryKey, new Category(categoryKey, refreshQuestions, trivia));
				}
				catch(IllegalStateException e)
				{
					// Log this error and skip loading this category.
					this.m_Logger.log(Level.WARNING, e.getMessage());
				}

			}

			try
			{
				TriviaType triviaType = new TriviaType(label, prefix, showCategories, requireGlobal, categories);
				triviaMap.put(label, triviaType);
			}
			catch(IllegalStateException e)
			{
				// Log this error and skip loading this type.
				this.m_Logger.log(Level.WARNING, e.getMessage());
			}
		}

		try
		{
			this.m_TriviaHandler.loadTrivia(triviaMap);
		}
		catch(IllegalStateException e)
		{
			this.m_Logger.log(Level.WARNING, "No trivia has been defined. The plugin has failed to load.");
		}
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
	 * @param category The category for the trivia question
	 */
	public void triviaQuestion(String label, String category)
	{
		this.m_TriviaHandler.triviaQuestion(label, category);
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
