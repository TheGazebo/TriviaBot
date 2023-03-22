package com.coathar.trivia.events;

import com.coathar.trivia.Trivia;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class TriviaSolvedEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private Trivia m_Trivia;

    public TriviaSolvedEvent(Player player, Trivia trivia)
    {
        super(player);

        this.m_Trivia = trivia;
    }

    public Trivia getTrivia()
    {
        return this.m_Trivia;
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