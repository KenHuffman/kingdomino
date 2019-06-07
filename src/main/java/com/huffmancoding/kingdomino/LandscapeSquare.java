package com.huffmancoding.kingdomino;

public class LandscapeSquare implements Square
{
    private final Landscape landscape;
    private final int crowns;

    public LandscapeSquare(Landscape l, int c)
    {
        landscape = l;
        crowns = c;
    }

    @Override
    public Landscape getLandscape()
    {
        return landscape;
    }

    public int getCrowns()
    {
        return crowns;
    }
}
