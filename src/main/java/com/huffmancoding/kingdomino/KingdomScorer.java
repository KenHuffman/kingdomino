package com.huffmancoding.kingdomino;

import java.util.ArrayList;
import java.util.List;

/**
 * This class scores a kingdom by determining the adjacent landscapes that form
 * a contiguous property. Then totaling up the scores for each property.
 *
 * @author Ken Huffman
 */
public class KingdomScorer
{
    /** the kingdom to score. */
    private final Kingdom kingdom;

    /** the list of properties. */
    private final List<KingdomProperty> properties = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param k the kingdom to compute the scores for.
     */
    public KingdomScorer(Kingdom k)
    {
        kingdom = k;
    }

    /**
     * Get the current score for the kingdom.
     *
     * @return the sum of the scores of the contiguous properties found.
     */
    public int getScore()
    {
        int size = kingdom.getDimension();

        properties.clear();
        for (int row = 0; row < size; ++row)
        {
            for (int column = 0; column < size; ++column)
            {
                addSquareToProperty(new Location(row, column));
            }
        }

        int totalScore = 0;
        for (KingdomProperty property : properties)
        {
            totalScore += property.getScore();
        }

        return totalScore;
    }

    /**
     * Add a location to an existing property that matches it or create a new
     * property for the list that contains just that location.
     *
     * @param location the location to assign to a property
     */
    private void addSquareToProperty(Location location)
    {
        Square square = kingdom.getSquare(location);
        if (square == null || square.getLandscape() == null)
        {
            // empty squares and castles aren't part of properties
            return;
        }

        LandscapeSquare landscapeSquare = (LandscapeSquare)square;
        for (KingdomProperty property : properties)
        {
            if (property.addSquareIfPossible(landscapeSquare, location))
            {
                return;
            }
        }

        KingdomProperty property = new KingdomProperty(landscapeSquare, location);
        properties.add(property);
    }
}
