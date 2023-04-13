package com.coathar.trivia.events;

import com.coathar.trivia.Category;
import com.coathar.trivia.Trivia;
import com.coathar.trivia.TriviaType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TriviaFireEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private TriviaType m_Type;
    private Category m_Category;
    private Trivia m_Question;
    private boolean m_IsCancelled;

    public TriviaFireEvent(TriviaType type, Category category, Trivia question)
    {
        this.m_Type = type;
        this.m_Category = category;
        this.m_Question = question;
    }

    public TriviaType getTrivia()
    {
        return this.m_Type;
    }

    public Category getCategory()
    {
        return this.m_Category;
    }

    public Trivia getQuestion()
    {
        return this.m_Question;
    }

    public boolean isCancelled()
    {
        return this.m_IsCancelled;
    }

    public void setCancelled(boolean cancelled)
    {
        this.m_IsCancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}