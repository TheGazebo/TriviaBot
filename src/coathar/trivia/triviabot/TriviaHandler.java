package coathar.trivia.triviabot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.Random;

public class TriviaHandler implements Listener
{
	private static volatile TriviaHandler m_UniqueInstance;

	private Random  m_RandomGeneration;

	private List<String> m_Questions;
	private List<String> m_Answers;

	private boolean m_IsTriviaActive;
	private boolean m_IsTriviaLooped;
	private int     m_QuestionId;

	public TriviaHandler()
	{
		this.m_RandomGeneration = new Random();
	}

	/**
	 * Broadcasts out a question and sets the handler to await an answer
	 */
	public void triviaQuestion()
	{
		this.m_IsTriviaActive     = true;
		this.m_QuestionId         = this.m_RandomGeneration.nextInt(this.m_Questions.size());

		Bukkit.broadcastMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "Trivia Bot" + ChatColor.GOLD + "] " + ChatColor.DARK_AQUA + "Question: " + ChatColor.AQUA + this.m_Questions.get(this.m_QuestionId));
	}

	/**
	 * Routine called when a player answers a question correctly.
	 * @param player The player who correctly answered a question.
	 */
	private void answerQuestion(Player player)
	{
		Bukkit.broadcastMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "Trivia Bot" + ChatColor.GOLD + "] " + ChatColor.GREEN + player.getName() +
				ChatColor.DARK_GREEN + " wins! The correct answer was: " + ChatColor.GREEN + this.m_Answers.get(this.m_QuestionId));

		if(this.m_IsTriviaLooped)
			this.triviaQuestion();
	}
	
	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event)
	{
		Player player  = event.getPlayer();
		String message = event.getMessage();

		if(event.isAsynchronous() && this.m_IsTriviaActive && message.equalsIgnoreCase(this.m_Answers.get(this.m_QuestionId)))
		{
			this.m_IsTriviaActive = false;

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
	 * Loads the questions and answers to the handler. Resets handler state so players can't answer questions with wrong answers.
	 * @param questions The list of questions
	 * @param answers   The list of answers
	 */
	void loadTrivia(List<String> questions, List<String> answers)
	{
		this.m_Questions = questions;
		this.m_Answers   = answers;

		// Reload safety
		if(this.m_IsTriviaActive)
			Bukkit.broadcastMessage(ChatColor.RED + "Questions skipped due to reload...");

		this.m_IsTriviaActive = false;
		this.m_IsTriviaLooped = false;

		if(this.m_QuestionId > this.m_Questions.size())
			this.m_QuestionId = 0;
	}

	/**
	 * Toggles the trivia loop.
	 * @return Whether or not the trivia is set to loop.
	 */
	public boolean toggleTriviaLooped()
	{
		this.m_IsTriviaLooped = !this.m_IsTriviaLooped;
		return this.m_IsTriviaLooped;
	}

	/**
	 * @return Whether or not there is an active trivia question.
	 */
	public boolean isTriviaActive() { return this.m_IsTriviaActive; }

	/**
	 * @return Whether or not the trivia is set to loop.
	 */
	public boolean isTriviaLooped() { return this.m_IsTriviaLooped; }

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
