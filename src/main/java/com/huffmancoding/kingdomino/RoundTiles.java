package com.huffmancoding.kingdomino;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class RoundTiles
{
    private final SortedSet<Tile> rankedTiles = new TreeSet<>();

    public RoundTiles(TileBag tileBag, int playerCount)
        throws IllegalMoveException
    {
        for (int i = 0; i < playerCount; ++i)
        {
            rankedTiles.add(tileBag.drawRandomTile());
        }

    }

    public void claimRank(Player player, int rank) throws IllegalMoveException
    {
        for (Tile tile : rankedTiles)
        {
            if (tile.getRank() == rank)
            {
                tile.setOwner(player);
                return;
            }
        }

        throw new IllegalMoveException("Cannot claim a tile that is not staged");
    }

    public SortedSet<Tile> getUnplacedTiles()
    {
        return rankedTiles;
    }

    public Set<Player> getPlayersWithTiles()
    {
        return rankedTiles
            .stream()
            .map(Tile::getOwner)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    public Player getNextPlacingPlayer()
    {
        if (rankedTiles.isEmpty())
        {
            return null;
        }

        return rankedTiles.iterator().next().getOwner();
    }

    public Tile removeNextTile(String playerName) throws IllegalMoveException
    {
        Player player = getNextPlacingPlayer();
        if (player == null || ! player.getName().equals(playerName))
        {
            throw new IllegalMoveException("Player " + playerName +
                " is not the next to place a tile.");
        }

        Tile tile = rankedTiles.first();
        rankedTiles.remove(tile);

        return tile;
    }
}
