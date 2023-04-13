package com.coathar.trivia;

public class Tuple<A,B>
{
    A m_First;
    B m_Second;

    public Tuple(A a, B b)
    {
        this.m_First = a;
        this.m_Second = b;
    }

    public A getFirstValue()
    {
        return m_First;
    }

    public B getSecondValue()
    {
        return m_Second;
    }

    public void setFirstValue(A a)
    {
        this.m_First = a;
    }

    public void setSecondValue(B b)
    {
        this.m_Second = b;
    }
}
