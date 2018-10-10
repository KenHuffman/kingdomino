package com.huffmancoding.kingdomino;

public class Location
{
    private final int row;
    private final int column;

    public Location(int r, int c)
    {
        row = r;
        column = c;
    }

    public int getRow()
    {
        return row;
    }

    public int getColumn()
    {
        return column;
    }

    public boolean isAdjacent(Location location2)
    {
        int distance = Math.abs(row - location2.row) +
            Math.abs(column - location2.column);
        return distance == 1;
    }
}
