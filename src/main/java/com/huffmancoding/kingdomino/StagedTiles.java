package com.huffmancoding.kingdomino;

import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class StagedTiles
{
    private final SortedMap<Tile, Player> rankedTiles = new TreeMap<>();

    public StagedTiles(TileBag tileBag, int playerCount)
        throws IllegalMoveException
    {
        for (int i = 0; i < playerCount; ++i)
        {
            rankedTiles.put(tileBag.drawRandomTile(), null);
        }

    }

    public void claimRank(Player player, int rank) throws IllegalMoveException
    {
        for (Entry<Tile, Player> entry : rankedTiles.entrySet())
        {
            if (entry.getKey().getRank() == rank)
            {
                entry.setValue(player);
                return;
            }
        }
        throw new IllegalMoveException("Cannot claim tile that is not staged");
    }

    public SortedMap<Tile, Player> getUnplacedTiles()
    {
        return rankedTiles;
    }

    public Set<Player> getPlayersWithTiles()
    {
        return rankedTiles.entrySet()
            .stream()
            .map(Map.Entry::getValue)
            .filter(p -> p != null)
            .collect(Collectors.toSet());
    }

    public Player getNextPlacingPlayer()
    {
        if (rankedTiles.isEmpty())
        {
            return null;
        }

        return rankedTiles.values().iterator().next();
    }

    public Tile removeNextTile(String playerName) throws IllegalMoveException
    {
        Player player = getNextPlacingPlayer();
        if (player == null || ! player.getName().equals(playerName))
        {
            throw new IllegalMoveException("Player " + playerName +
                " is not the next to place a tile.");
        }

        Tile tile = rankedTiles.firstKey();
        rankedTiles.remove(tile);

        return tile;
    }
}
