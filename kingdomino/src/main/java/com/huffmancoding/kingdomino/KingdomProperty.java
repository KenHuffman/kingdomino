package com.huffmancoding.kingdomino;

import java.util.ArrayList;
import java.util.List;

public class KingdomProperty
{
    private final Landscape landscape;

    private final List<Location> locations = new ArrayList<>();

    private int crownCount = 0;

    public KingdomProperty(LandscapeSquare square, Location location)
    {
        landscape = square.getLandscape().get();
        addLocation(square, location);
    }

    private void addLocation(LandscapeSquare square, Location location)
    {
        locations.add(location);
        crownCount += square.getCrownCount();
    }

    public boolean addSquareIfPossible(LandscapeSquare square, Location location)
    {
        if (square.getLandscape().get() == landscape)
        {
            for (Location l : locations)
            {
                if (location.isAdjacent(l))
                {
                    addLocation(square, location);
                    return true;
                }
            }
        }

        return false;
    }

    public int getScore()
    {
        return locations.size() * crownCount;
    }
}
