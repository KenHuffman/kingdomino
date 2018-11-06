package com.huffmancoding.kingdomino;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This represents a tableau of ranked tiles to be chosen by the players and
 * later placed into each player's kingdoms.
 *
 * @author Ken Huffman
 */
public class RoundTiles
{
    /** the set of tiles in the tableau. */
    private final SortedSet<Tile> rankedTiles = new TreeSet<>();

    /**
     * Constructor.
     *
     * @param tileBag the bag to remove the tiles from.
     * @param drawSize the number of tiles to draw.
     * @throws IllegalMoveException if the bag is empty
     */
    public RoundTiles(TileBag tileBag, int drawSize)
        throws IllegalMoveException
    {
        for (int i = 0; i < drawSize; ++i)
        {
            rankedTiles.add(tileBag.drawRandomTile());
        }
    }

    /**
     * Set the owner to a tile in the tableau.
     *
     * @param player the player claiming the tile
     * @param rank the rank on the back of the tile to claim
     * @throws IllegalMoveException if the tile cannot be claimed
     */
    public void claimRank(Player player, int rank) throws IllegalMoveException
    {
        Tile tileBeingClaimed = null;

        for (Tile tile : rankedTiles)
        {
            Player owner = tile.getOwner();
            if (owner != null && owner.equals(player)) {
                throw new IllegalMoveException("Cannot claim multiple tiles");
            }

            if (tile.getRank() == rank)
            {
                if (owner != null)
                {
                    throw new IllegalMoveException("Cannot claim a tile that is already claimed");
                }

                tileBeingClaimed = tile;
            }
        }

        if (tileBeingClaimed == null)
        {
            throw new IllegalMoveException("Cannot claim a tile that is not staged");
        }

        tileBeingClaimed.setOwner(player);
    }

    /**
     * Return the tiles have haven't been played yet.
     *
     * @return the tiles.
     */
    public SortedSet<Tile> getRemainingTiles()
    {
        return rankedTiles;
    }

    /**
     * Return the players that have claimed but not yet played their tiles.
     *
     * @return the players to place tiles
     */
    public Set<Player> getPlayersWithTiles()
    {
        return rankedTiles
            .stream()
            .map(Tile::getOwner)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    /**
     * Get the next player that should place their tile.
     *
     * @return the player to place next, null if there are no claimed but unplayed
     */
    public Player getNextPlacingPlayer()
    {
        if (rankedTiles.isEmpty())
        {
            return null;
        }

        return rankedTiles.iterator().next().getOwner();
    }

    /**
     * Get the tile that the player should place next.
     *
     * @param playerName the name of the player to play next, it is validated
     * @param rank the tile to place next, it is validated
     * @return the tile of that rank
     * @throws IllegalMoveException if a player is going out of order
     */
    public Tile getNextTileToPlace(String playerName) throws IllegalMoveException
    {
        Player player = getNextPlacingPlayer();
        if (player == null || ! player.getName().equals(playerName))
        {
            throw new IllegalMoveException("Player " + playerName +
                " is not the next to place a tile.");
        }

        Tile tile = rankedTiles.first();

        return tile;
    }

    /**
     * Remove the tile from the tableau after it has been placed in a kingdom.
     *
     * @param tile the tile to remove, last returned by {@link #getNextTileToPlace}
     * @throws IllegalMoveException if the tile to remove is not the last placed
     */
    public void removeTile(Tile tile) throws IllegalMoveException
    {
        Tile firstTile = rankedTiles.first();
        if (firstTile.getRank() != tile.getRank())
        {
            throw new IllegalMoveException("Rank " + tile.getRank() +
                " is not the next tile to be removed.");
        }
        rankedTiles.remove(tile);
    }
}
