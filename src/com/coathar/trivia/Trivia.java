package com.coathar.trivia;

import java.util.List;

public class Trivia implements Cloneable
{
    private final String m_Name;
    private final String m_Question;
    private final List<String> m_Answers;

    private boolean m_IsSolved;

    public Trivia(String name, String question, List<String> answers)
    {
        this.m_Name = name;
        this.m_Question = question;
        this.m_Answers = answers;

        this.m_IsSolved = false;

        if(question.isEmpty())
            throw new IllegalStateException("No question has been defined for the Trivia " + name + ".");
        else if(answers.size() == 0)
            throw new IllegalStateException("No answers have been defined for the Trivia " + name + ".");
    }

    public String getQuestion()
    {
        return this.m_Question;
    }

    public List<String> getAnswers()
    {
        return this.m_Answers;
    }

    public boolean isAnswer(String str, boolean requiresGlobal)
    {
        if(requiresGlobal && str.charAt(0) != '!')
            return false;

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
        return new Trivia(m_Name, m_Question, m_Answers);
    }
}
