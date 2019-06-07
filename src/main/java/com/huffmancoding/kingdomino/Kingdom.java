package com.huffmancoding.kingdomino;

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
     * Returns whether a tile can be placed anywhere on the board.
     *
     * @param tile the tile
     * @return true if it could be played anywhere
     */
    public boolean isValidTilePlacementAnywhere(Tile tile)
    {
        // try placing the tile in every horizontal spot
        for (int row = 0; row < squares.length; ++row)
        {
            for (int column = 0; column < squares.length-1; ++column)
            {
                Location location0 = new Location(row, column);
                Location location1 = new Location(row, column+1);
                if (isValidTilePlacementInEitherOrientation(tile, location0, location1))
                {
                    return true;
                }
            }
        }

        // try placing the tile in every vertical spot
        for (int row = 0; row < squares.length-1; ++row)
        {
            for (int column = 0; column < squares.length; ++column)
            {
                Location location0 = new Location(row, column);
                Location location1 = new Location(row+1, column);
                if (isValidTilePlacementInEitherOrientation(tile, location0, location1))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns whether a tile can be placed at particular location in either
     * orientation.
     *
     * @param tile the tile to be placed
     * @param location0 the location for the first square of the tile
     * @param location1 the location for the second square of the tile
     * @return true if it could be played in either orientation.
     */
    private boolean isValidTilePlacementInEitherOrientation(Tile tile, Location location0, Location location1)
    {
        try
        {
            validateTilePlacement(tile, location0, location1);
            return true;
        }
        catch (IllegalMoveException ex)
        {
            try
            {
                validateTilePlacement(tile, location1, location0);
                return true;
            }
            catch (IllegalMoveException ex2)
            {
                // both throw exceptions, neither direction is good
                return false;
            }
        }
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
        validateTilePlacement(tile, location0, location1);

        squares[location0.getRow()][location0.getColumn()] = tile.getSquare(0);
        squares[location1.getRow()][location1.getColumn()] = tile.getSquare(1);
    }

    /**
     * Validates that a tile can be placed at particular location in a
     * particular orientation.
     *
     * @param tile the tile to be placed
     * @param location0 the location for the first square of the tile
     * @param location1 the location for the second square of the tile
     * @throws IllegalMoveException if the tile cannot be placed there
     */
    private void validateTilePlacement(Tile tile, Location location0, Location location1)
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

        if (! isAdjacentMatch(location0, tile.getSquare(0)) &&
            ! isAdjacentMatch(location1, tile.getSquare(1)))
        {
            throw new IllegalMoveException("Tile must be adjacent to castle or matching landscape");
        }
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
        Landscape landscape = square.getLandscape();
        int row = location.getRow();
        int column = location.getColumn();
        return (row > 0 && isMatchingLandscape(landscape, new Location(row-1, column))) ||
               (row+1 < squares.length && isMatchingLandscape(landscape, new Location(row+1, column))) ||
               (column > 0 && isMatchingLandscape(landscape, new Location(row, column-1))) ||
               (column+1 < squares[row].length && isMatchingLandscape(landscape, new Location(row, column+1)));
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

        Landscape squareLandscape = square.getLandscape();
        return squareLandscape == null /* castle */ ||
               squareLandscape == landscape; /* another tile */
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
