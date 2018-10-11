package com.huffmancoding.kingdomino;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Kingdom
{
    private final Player player;
    private final Square[][] squares;

    public Kingdom(int size, Player p)
    {
        player = p;

        squares = new Square[size][];
        for (int row = 0; row < size; ++row)
        {
            squares[row] = new Square[size];
        }

        // put a castle in the middle of the grid
        squares[size / 2][size / 2] = new CastleSquare();
    }

    public int getDimension()
    {
        return squares.length;
    }

    public Player getPlayer()
    {
        return player;
    }

    public Square[][] getAllSquares()
    {
        return squares;
    }

    public int getScore()
    {
        List<KingdomProperty> properties = new ArrayList<>();
        for (int row = 0; row < squares.length; ++row)
        {
            for (int column = 0; column < squares[row].length; ++column)
            {
                addSquareToProperty(properties, new Location(row, column));
            }
        }

        int totalScore = 0;
        for (KingdomProperty property : properties)
        {
            totalScore += property.getScore();
        }

        return totalScore;
    }

    private void addSquareToProperty(List<KingdomProperty> properties, Location location)
    {
        Square square = getSquare(location);
        if (square == null || ! square.getLandscape().isPresent())
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

    private boolean isAdjacentMatch(Location location, LandscapeSquare square)
    {
        if (getSquare(location) != null)
        {
            // already occupied
            return false;
        }

        // check north, south, west, and east for a matching square
        Landscape landscape = square.getLandscape().get();
        int row = location.getRow();
        int column = location.getColumn();
        return (row > 0 && isMatchingLandscape(landscape, getSquare(new Location(row-1, column)))) ||
               (row < squares.length && isMatchingLandscape(landscape, getSquare(new Location(row+1, column)))) ||
               (column > 0 && isMatchingLandscape(landscape, getSquare(new Location(row, column-1)))) ||
               (column < squares[row].length && isMatchingLandscape(landscape, getSquare(new Location(row, column+1))));
    }

    private boolean isMatchingLandscape(Landscape landscape, Square square)
    {
        if (square == null)
        {
            return false;
        }

        Optional<Landscape> squareLandscape = square.getLandscape();
        return ! squareLandscape.isPresent() /* castle */ ||
                squareLandscape.get() == landscape; /* another tile */
    }

    public void placeTile(Tile tile, Location location0, Location location1)
        throws IllegalMoveException
    {
        LandscapeSquare square0 = tile.getSquare(0);
        LandscapeSquare square1 = tile.getSquare(1);

        if (! isAdjacentMatch(location0, square0) ||
            ! isAdjacentMatch(location1, square1))
        {
            throw new IllegalMoveException("The tile does not fit there");
        }

        setSquare(location0, square0);
        setSquare(location1, square1);
    }

    private void setSquare(Location location, LandscapeSquare square)
        throws IllegalMoveException
    {
        if (getSquare(location) != null)
        {
            throw new IllegalMoveException("Row " + location.getRow() +
                ", column " + location.getColumn() + " is already occupied");
        }

        squares[location.getRow()][location.getColumn()] = square;
    }

    private Square getSquare(Location location)
    {
        return squares[location.getRow()][location.getColumn()];
    }
}
