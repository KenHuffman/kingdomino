package com.huffmancoding.kingdomino;

import java.util.Optional;

/**
 * This class represents the kingdom grid with the square containing played
 * tiles.
 *
 * @author Ken Huffman
 */
public class Kingdom
{
    /** the player for the kingdom. */
    private final Player player;

    /** the squares that comprise the kingdom. */
    private final Square[][] squares;

    /**
     * Constructor.
     *
     * @param size the number of squares on each side
     * @param p the player for the kingdom
     */
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

    /**
     * Get the dimension of the kingdom in each direction.
     *
     * @return the size of the squares on each side
     */
    public int getDimension()
    {
        return squares.length;
    }

    /**
     * Get the play for the kingdom.
     *
     * @return the player
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * Get all the squares on the board.
     *
     * @return the squares as a doubly-indexed array
     */
    public Square[][] getAllSquares()
    {
        return squares;
    }

    /**
     * Get the current score for the kingdom.
     *
     * @return the total score
     */
    public int getScore()
    {
        KingdomScorer scorer = new KingdomScorer(this);
        return scorer.getScore();
    }

    /**
     * Place a tile at two locations.
     *
     * @param tile the tile to place
     * @param location0 the place for the first square of the tile
     * @param location1 the place for the second square of the tile
     * @throws IllegalMoveException if the locations are not valid for the tile
     */
    public void placeTile(Tile tile, Location location0, Location location1)
        throws IllegalMoveException
    {
        if (! location0.isAdjacent(location1))
        {
            throw new IllegalMoveException("The locations specified are not adjacent");
        }

        if (getSquare(location0) != null || getSquare(location1) != null)
        {
            throw new IllegalMoveException("Both locations need to be empty to place a tile");
        }

        LandscapeSquare square0 = tile.getSquare(0);
        LandscapeSquare square1 = tile.getSquare(1);

        if (! isAdjacentMatch(location0, square0) &&
            ! isAdjacentMatch(location1, square1))
        {
            throw new IllegalMoveException("The tile does not match kingdom");
        }

        squares[location0.getRow()][location0.getColumn()] = square0;
        squares[location1.getRow()][location1.getColumn()] = square1;
    }

    /**
     * Checks whether a landscape could be placed at a location a match an
     * existing square in any direction already on the board.
     *
     * @param location the location to place the square
     * @param square the square to place
     * @return true if it would match something surrounding it
     */
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
        return (row > 0 && isMatchingLandscape(landscape, new Location(row-1, column))) ||
               (row < squares.length && isMatchingLandscape(landscape, new Location(row+1, column))) ||
               (column > 0 && isMatchingLandscape(landscape, new Location(row, column-1))) ||
               (column < squares[row].length && isMatchingLandscape(landscape, new Location(row, column+1)));
    }

    /**
     * Checks whether a landscape matches the square at a location on the board.
     *
     * @param landscape the landscape to look up
     * @param location the location to compare against
     * @return true if location occupied with castle or matching landscape,
     *         false if location empty or has a different landscape.
     */
    private boolean isMatchingLandscape(Landscape landscape, Location location)
    {
        Square square = getSquare(location);
        if (square == null)
        {
            return false;
        }

        Optional<Landscape> squareLandscape = square.getLandscape();
        return ! squareLandscape.isPresent() /* castle */ ||
                squareLandscape.get() == landscape; /* another tile */
    }

    /**
     * Get the Square at the location of the kingdom.
     *
     * @param location the location on the grid
     * @return the Square placed there, or null if still empty.
     */
    public Square getSquare(Location location)
    {
        return squares[location.getRow()][location.getColumn()];
    }
}
