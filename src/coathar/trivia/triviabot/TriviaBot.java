package coathar.trivia.triviabot;

import java.util.List;
import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import coathar.trivia.triviabot.commands.TriviaReload;
import coathar.trivia.triviabot.commands.TriviaStart;
import coathar.trivia.triviabot.commands.TriviaToggleLoop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class TriviaBot extends JavaPlugin {

	private static volatile TriviaBot m_UniqueInstance;

	private FileConfiguration m_Config;
	private Logger 			  m_Logger;
	private TriviaHandler     m_TriviaHandler;
	
	@Override
	public void onEnable()
	{
		this.m_UniqueInstance = this;

		this.getDataFolder().mkdir();
		this.m_Config = this.getConfig();
		this.m_Logger = this.getLogger();

		if(!(new File(getDataFolder(), "config.yml").exists()))
		{
			List<String> defaultQuestions = Arrays.asList("These are where you locate questions","They correspond with the answers below");
			this.m_Config.set("Questions", defaultQuestions);

			List<String> defaultAnswers = Arrays.asList("These are where you locate answers","They correspond with the questions above");
			this.m_Config.set("Answers", defaultAnswers);

			saveConfig();

			this.m_Logger.warning("No configuration file was found. Dummy questions were loaded. Reload after filling in questions.");
		}

		List<String> questions = this.m_Config.getStringList("Questions");
		List<String> answers   = this.m_Config.getStringList("Answers");

		this.m_TriviaHandler = TriviaHandler.getInstance();
		this.m_TriviaHandler.loadTrivia(questions, answers);

		this.registerCommands();

		this.m_Logger.info("TriviaBot is now enabled!");
	}

	/**
	 * Reloads the trivia questions from the configuration.
	 */
	@Override
	public void reloadConfig()
	{
		this.reloadConfig();
		this.m_Config = this.getConfig();

		List<String> questions = this.m_Config.getStringList("Questions");
		List<String> answers   = this.m_Config.getStringList("Answers");

		this.m_TriviaHandler.loadTrivia(questions, answers);
	}

	/**
	 * Broadcasts out a question and sets the handler to await an answer
	 */
	public void triviaQuestion()
	{
		this.m_TriviaHandler.triviaQuestion();
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
