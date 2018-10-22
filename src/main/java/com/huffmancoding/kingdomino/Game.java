package com.huffmancoding.kingdomino;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

import com.huffmancoding.kingdomino.CurrentTurn.Task;

@Component("game")
public class Game
{
    private static final int kingdomDimension = 5;
    private static final int sizeOfStage = 4;
    private final List<Kingdom> kingdoms = new ArrayList<>();
    private final TileBag tileBag = new TileBag();
    private RoundTiles thisRoundTiles;
    private RoundTiles nextRoundTiles;
    private CurrentTurn currentTurn;

    public Game() throws IllegalMoveException
    {
        addPlayer("Stark", "green");
        addPlayer("Lannister", "yellow");
        addPlayer("Arryn", "blue");
        addPlayer("Hoare", "pink");

        // subtract 1 for the castle square, divide 2 squares per tile
        //int roundsLeft = (size*size - 1) / 2;

        thisRoundTiles = new RoundTiles(tileBag, sizeOfStage);
        nextRoundTiles = null;
        currentTurn = new CurrentTurn(
            kingdoms.get(0).getPlayer(),
            Task.CHOOSING_INITIAL_TILE);
    }

    private void addPlayer(String playerName, String colorName)
    {
        Player player = new Player(playerName, colorName);
        Kingdom kingdom = new Kingdom(kingdomDimension, player);
        kingdoms.add(kingdom);
    }

    public CurrentTurn getCurrentTurn()
    {
        return currentTurn;
    }

    private Kingdom getKingdom(Player player) throws IllegalMoveException
    {
        for (Kingdom kingdom : kingdoms)
        {
            if (kingdom.getPlayer().equals(player))
            {
                return kingdom;
            }
        }

        throw new IllegalMoveException("Player " + player.getName() + " not playing");
    }

    public List<Kingdom> getAllKingdoms()
    {
        return kingdoms;
    }

    public RoundTiles getThisRoundTiles()
    {
        return thisRoundTiles;
    }

    public RoundTiles getNextRoundTiles()
    {
        return nextRoundTiles;
    }

    public void claimTile(String playerName, int rank) throws IllegalMoveException
    {
        Player player = currentTurn.getPlayer();
        if (! player.getName().equals(playerName))
        {
            throw new IllegalMoveException("It is not player " + playerName + " turn.");
        }

        switch (currentTurn.getTask())
        {
            case CHOOSING_INITIAL_TILE:
                claimInitialTile(player, rank);
                break;

            case CHOOSING_NEXT_TILE:
                claimNextTile(player, rank);
                break;

            default:
                throw new IllegalMoveException("It is not time to claim an initial tile");
        }
    }

    private void claimInitialTile(Player player, int rank)
        throws IllegalMoveException
    {
        thisRoundTiles.claimRank(player, rank);

        currentTurn = getNextPlayerForInitialSelect();
        if (currentTurn == null)
        {
            nextRoundTiles = new RoundTiles(tileBag, sizeOfStage);
            currentTurn = getNextPlayerForTilePlacement();
        }
    }

    private CurrentTurn getNextPlayerForInitialSelect()
    {
        Set<Player> playersWithTiles = thisRoundTiles.getPlayersWithTiles();

        for (Kingdom kingdom : kingdoms)
        {
            Player player = kingdom.getPlayer();
            if (! playersWithTiles.contains(player))
            {
                return new CurrentTurn(player, Task.CHOOSING_INITIAL_TILE);
            }
        }

        return null;
    }

    public void placeTile(String playerName, int rank, Location location0, Location location1)
        throws IllegalMoveException
    {
        Player player = currentTurn.getPlayer();
        if (! player.getName().equals(playerName))
        {
            throw new IllegalMoveException("It is not player " + playerName + " turn.");
        }

        if (currentTurn.getTask() != Task.PLACING_TILE)
        {
            throw new IllegalMoveException("It is not time to place a tile");
        }

        Tile tile = thisRoundTiles.removeNextTile(playerName, rank);
        Kingdom kingdom = getKingdom(player);

        kingdom.placeTile(tile, location0, location1);

        if (nextRoundTiles != null)
        {
            currentTurn = new CurrentTurn(player, Task.CHOOSING_NEXT_TILE);
        }
        else
        {
            currentTurn = getNextPlayerForTilePlacement();
            if (currentTurn == null)
            {
                currentTurn = new CurrentTurn(getWinningPlayer(), Task.GAME_OVER);
            }
        }
    }

    private Player getWinningPlayer()
    {
        // note: this doesn't handle ties

        int highestScore = 0;
        Player winningPlayer = null;
        for (Kingdom kingdom : kingdoms)
        {
            int score = kingdom.getScore();
            if (score > highestScore)
            {
                highestScore = score;
                winningPlayer = kingdom.getPlayer();
            }
        }

        return winningPlayer;
    }

    private void claimNextTile(Player player, int rank)
        throws IllegalMoveException
    {
        nextRoundTiles.claimRank(player, rank);

        currentTurn = getNextPlayerForTilePlacement();
        if (currentTurn == null)
        {
            thisRoundTiles = nextRoundTiles;
            if (tileBag.isEmpty())
            {
                nextRoundTiles = null;
            }
            else
            {
                nextRoundTiles = new RoundTiles(tileBag, sizeOfStage);
            }
            currentTurn = getNextPlayerForTilePlacement();
        }
    }

    private CurrentTurn getNextPlayerForTilePlacement()
    {
        Player nextPlayer = thisRoundTiles.getNextPlacingPlayer();
        if (nextPlayer == null)
        {
            return null;
        }

        return new CurrentTurn(nextPlayer, Task.PLACING_TILE);
    }
}
