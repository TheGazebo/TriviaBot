package com.coathar.trivia;

import java.util.List;

public class Trivia implements Cloneable
{
    private final String m_Label;
    private final String m_Category;
    private final String m_Question;
    private final List<String> m_Answers;

    private boolean m_IsSolved;

    public Trivia(String label, String category, String question, List<String> answers)
    {
        this.m_Label = label;
        this.m_Category = category;
        this.m_Question = question;
        this.m_Answers = answers;

        this.m_IsSolved = false;
    }

    public String getLabel()
    {
        return this.m_Label;
    }

    public String getCategory()
    {
        return this.m_Category;
    }

    public String getQuestion()
    {
        return this.m_Question;
    }

    public List<String> getAnswers()
    {
        return this.m_Answers;
    }

    public boolean isAnswer(String str)
    {
        // Loop over answers and compare
        for(String answer : this.m_Answers)
        {
            if(answer.charAt(0) == '!' && answer.length() > 1)
                answer = answer.substring(1);

            if(answer.equalsIgnoreCase(str))
                return true;
        }

        return false;
    }

    public void flagSolved()
    {
        this.m_IsSolved = true;
    }

    public boolean isSolved()
    {
        return m_IsSolved;
    }

    @Override
    public Trivia clone()
    {
        return new Trivia(m_Label, m_Category, m_Question, m_Answers);
    }
}
