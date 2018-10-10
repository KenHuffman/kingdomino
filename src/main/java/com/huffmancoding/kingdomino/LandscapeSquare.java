package com.huffmancoding.kingdomino;

import java.util.Optional;

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
    public Optional<Landscape> getLandscape()
    {
        return Optional.of(landscape);
    }

    public int getCrownCount()
    {
        return crowns;
    }
}
