package com.coathar.trivia.events;

import com.coathar.trivia.Trivia;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TriviaFireEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Trivia m_Trivia;
    private boolean m_IsCancelled;

    public TriviaFireEvent(Trivia trivia)
    {
        this.m_Trivia = trivia;
    }

    public Trivia getTrivia()
    {
        return this.m_Trivia;
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