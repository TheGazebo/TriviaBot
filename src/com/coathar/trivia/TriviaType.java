package com.coathar.trivia;

import java.util.*;

public class TriviaType {

    private final String m_Label;
    private final String  m_BotPrefix;
    private final boolean m_ShowCategories;
    private final boolean m_RequireGlobal;
    private final Map<String, Category> m_RemainingCategories;
    private final Map<String, Category> m_EmptiedCategories;
    private Category m_LastCategory;

    public TriviaType(String label, String prefix, boolean showCategories, boolean requireGlobal, Map<String, Category> categories)
    {
        this.m_Label = label;
        this.m_BotPrefix = prefix;
        this.m_ShowCategories = showCategories;
        this.m_RequireGlobal = requireGlobal;
        this.m_RemainingCategories = categories;
        this.m_EmptiedCategories = new HashMap<String, Category>();

        if(categories.size() == 0)
            throw new IllegalStateException("No categories have been defined for the TriviaType " + label + ".");
    }

    /**
     * @return Returns the high level label for this trivia type.
     */
    public String getLabel()
    {
        return this.m_Label;
    }

    /**
     * @return Returns the prefix the trivia bot should use for this trivia type.
     */
    public String getPrefix()
    {
        return this.m_BotPrefix;
    }

    /**
     * @return Whether the trivia bot should display the category when posting the question.
     */
    public boolean showCategory()
    {
        return this.m_ShowCategories;
    }

    /**
     * @return Whether the trivia bot should require global chat when answering trivia of this type.
     */
    public boolean requireGlobal()
    {
        return this.m_RequireGlobal;
    }

    /**
     * Selects a random category from all categories with remaining questions. Will refresh all categories for reuse if no categories have remaining questions.
     * @return A randomly selected category.
     */
    public Category pollCategory()
    {
        int index;

        if(this.m_RemainingCategories.size() == 1)
        {
            index = 0;
        }
        else
        {
            Random random = new Random();
            index = random.nextInt(this.m_RemainingCategories.size());
        }

        return pollCategory(index);
    }

    public Category pollCategory(String categoryKey)
    {
        if(this.m_RemainingCategories.containsKey(categoryKey))
            return this.m_RemainingCategories.get(categoryKey);
        else
            return pollCategory();
    }


    /**
     * Selects a category from the specified index. Will refresh all categories for reuse if no categories have remaining questions.
     * @return A category at the specified index or a random category from a refreshed listing if no categories have remaining questions.
     */
    public Category pollCategory(int index) throws IndexOutOfBoundsException
    {
        ArrayList<String> values = new ArrayList<>(this.m_RemainingCategories.keySet());

        try
        {
            String key = values.get(index);
            Category nextCategory = this.m_RemainingCategories.get(key);

            if(!nextCategory.hasRemainingTrivia())
            {
                m_EmptiedCategories.put(key, this.m_RemainingCategories.remove(key));
                return this.pollCategory();
            }

            this.m_LastCategory = nextCategory;
            return this.m_LastCategory;
        }
        // No categories presently available
        catch(IndexOutOfBoundsException e)
        {
            // Refresh all categories since none are available
            if(!this.m_EmptiedCategories.isEmpty())
            {
                for(Map.Entry<String, Category> pair : this.m_EmptiedCategories.entrySet())
                {
                    pair.getValue().refreshTrivia();
                    this.m_RemainingCategories.put(pair.getKey(), pair.getValue());
                }

                this.m_EmptiedCategories.clear();
                return this.pollCategory();
            }
            // No categories are remaining or in the emptied pool. None were defined to begin with. Should never be reached.
            else
            {
                throw new IndexOutOfBoundsException("The index (" + index + ") is out of bounds for the amount of categories (" + m_RemainingCategories.size() + ").");
            }
        }
    }

    public List<String> getCategoryKeys()
    {
        Set<String> keys = this.m_RemainingCategories.keySet();
        return Arrays.asList(keys.toArray(new String[keys.size()]));
    }
}
