package com.coathar.trivia;

import com.coathar.trivia.events.CategoryEmptiedEvent;
import org.bukkit.Bukkit;

import java.util.*;

public class Category {

    private final String m_Name;
    private final boolean m_RefreshQuestions;
    private final List<Trivia> m_TemplateTrivia;
    private ArrayList<Trivia> m_RemainingTrivia;
    private Trivia m_LastTrivia;

    public Category(String name, boolean refreshQuestions, ArrayList<Trivia> triviaList)
    {
        this.m_Name = name;
        this.m_RefreshQuestions = refreshQuestions;
        this.m_TemplateTrivia = triviaList;

        this.refreshTrivia();
    }

    /**
     * @return The name of the question as used as a key in the configuration file.
     */
    public String getName()
    {
        return this.m_Name;
    }

    /**
     * Refreshes all trivia questions in this category.
     */
    public void refreshTrivia()
    {
        // Create a new list of trivia questions and add deep copies of the template trivia
        this.m_RemainingTrivia = new ArrayList<Trivia>(this.m_TemplateTrivia.size());

        for(Trivia trivia : this.m_TemplateTrivia)
            this.m_RemainingTrivia.add(trivia.clone());
    }

    /**
     * Selects a random trivia question from all remaining trivia questions. Will refresh and select a question if no remaining questions are available.
     * @return
     * @throws IllegalStateException
     */
    public Trivia pollQuestion() throws IllegalStateException
    {
        if(!m_RefreshQuestions && this.m_RemainingTrivia.isEmpty())
            throw new IllegalStateException("This category has already ran all questions and cannot be refreshed.");

        int index;

        if(m_RemainingTrivia.size() == 1)
        {
            index = 0;
        }
        else
        {
            Random random = new Random();
            index = random.nextInt(this.m_RemainingTrivia.size()); // [0, Size)
        }

        this.m_LastTrivia = this.m_RemainingTrivia.get(index).clone();
        this.m_RemainingTrivia.remove(index); // Remove to avoid repeating questions

        // No trivia is left, attempt to refresh if applicable
        if(this.m_RemainingTrivia.isEmpty())
        {
            // Questions should not be automatically refreshed, fire an event and check if it was cancelled.
            if(!m_RefreshQuestions)
            {
                CategoryEmptiedEvent event = new CategoryEmptiedEvent(this);
                Bukkit.getPluginManager().callEvent(event);

                if(event.isCancelled())
                {
                    this.refreshTrivia();
                }
            }
            else
            {
                this.refreshTrivia();
            }
        }

        return this.m_LastTrivia;
    }

    /**
     * @return Whether the category has any trivia remaining.
     */
    public boolean hasRemainingTrivia()
    {
        return !this.m_RemainingTrivia.isEmpty() || this.m_RefreshQuestions;
    }
}
