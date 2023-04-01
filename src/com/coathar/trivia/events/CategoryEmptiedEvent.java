package com.coathar.trivia.events;

import com.coathar.trivia.Category;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CategoryEmptiedEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Category m_Category;
    private boolean m_IsCancelled;

    public CategoryEmptiedEvent(Category category)
    {
        this.m_Category = category;
    }

    public Category getCategory()
    {
        return this.m_Category;
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
