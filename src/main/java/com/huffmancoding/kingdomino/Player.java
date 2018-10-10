package com.huffmancoding.kingdomino;

public class Player implements Comparable<Player>
{
    private final String name;
    private final String color;

    public Player(String n, String c)
    {
        name = n;
        color = c;
    }

    public String getName()
    {
        return name;
    }

    public String getColorName()
    {
        return color;
    }

    @Override
    public boolean equals(Object player)
    {
        if (player == this)
        {
            return true;
        }

        if (player instanceof Player)
        {
            return name.equals(((Player)player).name);
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public int compareTo(Player p)
    {
        return name.compareTo(p.name);
    }
}
