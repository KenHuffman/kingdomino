package com.huffmancoding.kingdomino;

public class Tile implements Comparable<Tile>
{
    private final LandscapeSquare[] squares;

    private final int rank;

    private Player owner = null;

    public Tile(LandscapeSquare s1, LandscapeSquare s2, int r)
    {
        squares = new LandscapeSquare[] { s1, s2 };
        rank = r;
    }

    @Override
    public int compareTo(Tile t)
    {
        return rank - t.rank;
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof Tile && rank == ((Tile)o).rank;
    }

    @Override
    public int hashCode()
    {
        return rank;
    }

    public LandscapeSquare getSquare(int spot)
    {
        if (spot < 0 || spot > 1)
        {
            throw new IllegalArgumentException("Tiles only have two spots: 0 or 1");
        }

        return squares[spot];
    }

    public int getRank()
    {
        return rank;
    }

    public LandscapeSquare[] getSquares()
    {
        return squares;
    }

    public void setOwner(Player player)
    {
        owner = player;
    }

    public Player getOwner()
    {
        return owner;
    }
}
