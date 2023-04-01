package com.coathar.trivia;

import com.coathar.trivia.events.TriviaFireEvent;
import com.coathar.trivia.events.TriviaSolvedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

import java.util.*;
import java.util.logging.Level;

public class TriviaHandler implements Listener
{
	private static volatile TriviaHandler m_UniqueInstance;

	private Map<String, TriviaType> m_AvailableTrivia;

	private TriviaType m_LastTriviaType;
	private Category m_LastCategory;
	private Trivia m_LastQuestion;

	private boolean m_IsTriviaLooped;

	public TriviaHandler()
	{
		Bukkit.getPluginManager().registerEvents(this, TriviaBot.getInstance());
	}


	/**
	 * Broadcasts a question and sets the handler to await an answer.
	 * Will select from the first defined grouping of trivia questions if no trivia has previously been posted.
	 * Will select using the previous trivia question's grouping if trivia has been posted.
	 */
	public void triviaQuestion()
	{
		if(this.m_LastTriviaType != null)
		{
			this.triviaQuestion(this.m_LastTriviaType.getLabel(), "");
		}
		else
		{
			this.triviaQuestion(this.getTriviaTypeKeys().get(0), "");
		}
	}

	public void triviaQuestion(String typeKey)
	{
		this.triviaQuestion(typeKey, "");
	}

	/**
	 * Broadcasts a question and sets the handler to await an answer.
	 * @param typeKey The type of trivia question to select.
	 * @param categoryKey The category to use.
	 */
	public void triviaQuestion(String typeKey, String categoryKey)
	{
		// Use the wrapper function if the provided key is empty.
		if(typeKey.isEmpty())
		{
			this.triviaQuestion();
			return;
		}

		try
		{
			// Select the appropriate type and then a random trivia question within it
			TriviaType triviaType = this.m_AvailableTrivia.get(typeKey);
			this.m_LastCategory = categoryKey.isEmpty() ? triviaType.pollCategory() : triviaType.pollCategory(categoryKey);
			this.m_LastQuestion = this.m_LastCategory.pollQuestion();

			TriviaFireEvent event = new TriviaFireEvent(triviaType);
			Bukkit.getPluginManager().callEvent(event);

			if(!event.isCancelled())
			{
				this.m_LastTriviaType = triviaType;

				if(triviaType.showCategory())
					Bukkit.broadcastMessage(triviaType.getPrefix() + " " + ChatColor.DARK_AQUA + "The category is: " + ChatColor.AQUA + this.m_LastCategory.getName());

				Bukkit.broadcastMessage(triviaType.getPrefix() + " " + ChatColor.DARK_AQUA + "Question: " + ChatColor.AQUA + this.m_LastQuestion.getQuestion());
			}
		}
		catch(IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException e)
		{
			TriviaBot.getInstance().getLogger().log(Level.WARNING, e.getMessage());
			throw e; // Throw for user feedback for commands.
		}
		catch(NullPointerException e)
		{
			String warningMessage = "The trivia type " + typeKey + " is not defined.";

			TriviaBot.getInstance().getLogger().log(Level.WARNING, warningMessage);
			throw new NullPointerException(warningMessage); // Throw for user feedback for commands.
		}
	}

	/**
	 * Routine called when a player answers a question correctly.
	 * @param player The player who correctly answered a question.
	 */
	private void answerQuestion(Player player)
	{
		TriviaSolvedEvent event = new TriviaSolvedEvent(player, this.m_LastTriviaType);
		Bukkit.getPluginManager().callEvent(event);

		String answer = this.m_LastQuestion.getAnswers().get(0);

		Bukkit.broadcastMessage(this.m_LastTriviaType.getPrefix() + " " + ChatColor.GREEN + player.getName() +
				ChatColor.DARK_GREEN + " wins! The correct answer was: " + ChatColor.GREEN + answer);

		if(this.m_IsTriviaLooped)
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					TriviaHandler.this.triviaQuestion();
				}
			}.runTaskLater(TriviaBot.getInstance(), 40);
	}

	public void cancelTrivia()
	{
		if(this.isTriviaActive())
		{
			this.m_LastQuestion.flagSolved();

			Bukkit.broadcastMessage(this.m_LastTriviaType.getPrefix() + " " + ChatColor.RED +
					"The current trivia question has been cancelled. The correct answer was: " + this.m_LastQuestion.getAnswers().get(0));
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player  = event.getPlayer();
		String message = event.getMessage();

		if(event.isAsynchronous() && !this.m_LastQuestion.isSolved() && this.m_LastQuestion.isAnswer(message, this.m_LastTriviaType.requireGlobal()))
		{
			this.m_LastQuestion.flagSolved();

			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					TriviaHandler.this.answerQuestion(player);
				}
			}.runTaskLater(TriviaBot.getInstance(), 20);
		}
	}

	/**
	 * Loads the questions and answers to the handler.
	 * @param trivia The list of trivia questions to load into the handler.
	 */
	void loadTrivia(Map<String, TriviaType> trivia)
	{
		this.m_AvailableTrivia = trivia;

		TriviaBot.getInstance().getLogger().log(Level.INFO, "Loaded " + this.m_AvailableTrivia.size() + " trivia types.");
	}

	/**
	 * Toggles the trivia loop.
	 * @return Whether the trivia is set to loop.
	 */
	public boolean toggleTriviaLooped()
	{
		this.m_IsTriviaLooped = !this.m_IsTriviaLooped;
		return this.m_IsTriviaLooped;
	}

	/**
	 * @return Whether there is an active trivia question.
	 */
	public boolean isTriviaActive()
	{
		return this.m_LastTriviaType != null && !this.m_LastQuestion.isSolved();
	}

	/**
	 * @return Whether the trivia is set to loop.
	 */
	public boolean isTriviaLooped()
	{
		return this.m_IsTriviaLooped;
	}

	public List<String> getTriviaTypeKeys()
	{
		Set<String> keys = this.m_AvailableTrivia.keySet();
		return Arrays.asList(keys.toArray(new String[keys.size()]));
	}

	public TriviaType getTriviaType(String key)
	{
		return key.isEmpty() ? this.m_AvailableTrivia.get(this.getTriviaTypeKeys().get(0)) : this.m_AvailableTrivia.get(key);
	}

	public static TriviaHandler getInstance()
	{
		if(m_UniqueInstance == null)
		{
			synchronized(TriviaHandler.class)
			{
				if(m_UniqueInstance == null)
					m_UniqueInstance = new TriviaHandler();
			}
		}
		return m_UniqueInstance;
	}
}
