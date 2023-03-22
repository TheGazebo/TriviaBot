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

import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class TriviaHandler implements Listener
{
	private static volatile TriviaHandler m_UniqueInstance;

	private Random  m_RandomGeneration;

	private List<Trivia> m_TriviaQuestions;

	private Trivia m_CurrentTrivia;
	private boolean m_IsTriviaLooped;

	public TriviaHandler()
	{
		this.m_RandomGeneration = new Random();
		Bukkit.getPluginManager().registerEvents(this, TriviaBot.getInstance());
	}

	/**
	 * Broadcasts a question and sets the handler to await an answer
	 */
	public void triviaQuestion()
	{
		int index = this.m_RandomGeneration.nextInt(this.m_TriviaQuestions.size());
		Trivia nextTrivia = this.m_TriviaQuestions.get(index).clone(); // Make sure to clone in order to avoid flagging the questions in the list.

		TriviaFireEvent event = new TriviaFireEvent(nextTrivia);
		Bukkit.getPluginManager().callEvent(event);

		if(!event.isCancelled())
		{
			this.m_CurrentTrivia = nextTrivia;
			Bukkit.broadcastMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "Trivia Bot" + ChatColor.GOLD + "] " + ChatColor.DARK_AQUA + "Question: " + ChatColor.AQUA + this.m_CurrentTrivia.getQuestion());
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

		Bukkit.broadcastMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "Trivia Bot" + ChatColor.GOLD + "] " + ChatColor.GREEN + player.getName() +
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
	void loadTrivia(List<Trivia> trivia)
	{
		this.m_TriviaQuestions = trivia;

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
		return !this.m_CurrentTrivia.isSolved();
	}

	/**
	 * @return Whether the trivia is set to loop.
	 */
	public boolean isTriviaLooped()
	{
		return this.m_IsTriviaLooped;
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
