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

	private Random  m_RandomGeneration;

	private Map<String, List<Trivia>> m_TriviaQuestions;
	private Map<String, String> m_Prefixes;

	private Trivia m_CurrentTrivia;
	private boolean m_IsTriviaLooped;

	public TriviaHandler()
	{
		this.m_RandomGeneration = new Random();
		Bukkit.getPluginManager().registerEvents(this, TriviaBot.getInstance());
	}


	/**
	 * Broadcasts a question and sets the handler to await an answer.
	 * Will select from the first defined grouping of trivia questions if no trivia has previously been posted.
	 * Will select using the previous trivia question's grouping if trivia has been posted.
	 */
	public void triviaQuestion()
	{
		if(this.m_CurrentTrivia != null)
		{
			this.triviaQuestion(this.m_CurrentTrivia.getLabel());
		}
		else
		{
			try
			{
				this.triviaQuestion(this.getLabels().get(0));
			}
			catch(IndexOutOfBoundsException e)
			{
				String warningMessage = "No trivia categories found. No trivia has been defined when attempting to start trivia.";

				TriviaBot.getInstance().getLogger().log(Level.WARNING, warningMessage);
				throw new IndexOutOfBoundsException(warningMessage); // Throw for user feedback for commands.
			}
		}
	}

	/**
	 * Broadcasts a question and sets the handler to await an answer.
	 * @param label The type of trivia question to select.
	 */
	public void triviaQuestion(String label)
	{
		// Use the wrapper function if the provided key is empty.
		if(label.isEmpty())
		{
			this.triviaQuestion();
			return;
		}

		try
		{
			// Select the appropriate type and then a random trivia question within it
			List<Trivia> questionsToUse = this.m_TriviaQuestions.get(label);
			int index = this.m_RandomGeneration.nextInt(questionsToUse.size());
			Trivia nextTrivia = questionsToUse.get(index).clone(); // Make sure to clone in order to avoid flagging the questions in the list.

			TriviaFireEvent event = new TriviaFireEvent(nextTrivia);
			Bukkit.getPluginManager().callEvent(event);

			if(!event.isCancelled())
			{
				this.m_CurrentTrivia = nextTrivia;
				String prefix = this.m_Prefixes.get(this.m_CurrentTrivia.getLabel());

				if(!this.m_CurrentTrivia.getCategory().isEmpty())
					Bukkit.broadcastMessage(prefix + " " + ChatColor.DARK_AQUA + "The category is: " + ChatColor.AQUA + this.m_CurrentTrivia.getQuestion());

				Bukkit.broadcastMessage(prefix + " " + ChatColor.DARK_AQUA + "Question: " + ChatColor.AQUA + this.m_CurrentTrivia.getQuestion());
			}
		}
		catch(IllegalArgumentException e)
		{
			String warningMessage = "The trivia type " + label + " was defined, but no trivia was defined for it.";

			TriviaBot.getInstance().getLogger().log(Level.WARNING, warningMessage);
			throw new IllegalArgumentException(warningMessage, e); // Throw for user feedback for commands.
		}
		catch(NullPointerException e)
		{
			String warningMessage = "The trivia type " + label + " is not defined.";

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
		TriviaSolvedEvent event = new TriviaSolvedEvent(player, this.m_CurrentTrivia);
		Bukkit.getPluginManager().callEvent(event);

		String prefix = this.m_Prefixes.get(this.m_CurrentTrivia.getLabel());
		Bukkit.broadcastMessage(prefix + " " + ChatColor.GREEN + player.getName() +
				ChatColor.DARK_GREEN + " wins! The correct answer was: " + ChatColor.GREEN + this.m_CurrentTrivia.getAnswers().get(0));

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
			this.m_CurrentTrivia.flagSolved();
			String prefix = this.m_Prefixes.get(this.m_CurrentTrivia.getLabel());
			Bukkit.broadcastMessage(prefix + " " + ChatColor.RED + "The current trivia question has been cancelled. The correct answer was: " + this.m_CurrentTrivia.getAnswers().get(0));
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player  = event.getPlayer();
		String message = event.getMessage();

		if(event.isAsynchronous() && !this.m_CurrentTrivia.isSolved() && this.m_CurrentTrivia.isAnswer(message))
		{
			this.m_CurrentTrivia.flagSolved();

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
	void loadTrivia(Map<String, List<Trivia>> trivia, Map<String, String> prefixes)
	{
		this.m_TriviaQuestions = trivia;
		this.m_Prefixes = prefixes;

		TriviaBot.getInstance().getLogger().log(Level.INFO, "Loaded " + this.m_TriviaQuestions.size() + " trivia questions.");
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
		return this.m_CurrentTrivia != null && !this.m_CurrentTrivia.isSolved();
	}

	/**
	 * @return Whether the trivia is set to loop.
	 */
	public boolean isTriviaLooped()
	{
		return this.m_IsTriviaLooped;
	}

	public List<String> getLabels()
	{
		Set<String> keys = this.m_TriviaQuestions.keySet();
		return Arrays.asList(keys.toArray(new String[keys.size()]));
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
