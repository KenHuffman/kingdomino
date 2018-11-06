package com.huffmancoding.kingdomino;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

import com.huffmancoding.kingdomino.CurrentTurn.Task;

/**
 * This represents a game with players. It has multiple Kingdoms, tiles, and
 * keeps track of whose turn it is.
 *
 * @author Ken Huffman
 */
@Component("game")
public class Game
{
    /** the size, in each direction, of each Kingdom. Usually 5. */
    private int kingdomDimension;

    /** the number of tiles to lay out to choose from each round.
        typically the number of players. */
    private int roundTileCount;

    /** the list of kingdoms for the players. */
    private List<Kingdom> kingdoms;

    /** the bag that contains random tiles. */
    private TileBag tileBag;

    /** the tiles to play this round */
    private RoundTiles thisRoundTiles;

    /** the tiles to be claimed for the next round,
        can be null at the beginning and end of the game*/
    private RoundTiles nextRoundTiles;

    /** the current player and what action she should take. */
    private CurrentTurn currentTurn;

    /**
     * Constructor for a four player, five dimension game.
     *
     * @throws IllegalMoveException if the tile bag has a hole in it
     */
    public Game() throws IllegalMoveException
    {
        init(4, 5);
    }

    /**
     * Initialize the members of the instance.
     *
     * @param playerCount the number of kingdoms to create
     * @param dimension the size of each kingdom (in each direction)
     * @throws IllegalMoveException if there aren't enough tiles to play
     */
    public void init(int playerCount, int dimension) throws IllegalMoveException
    {
        kingdomDimension = dimension;
        roundTileCount = playerCount;

        kingdoms = new ArrayList<>();
        tileBag = new TileBag();

        String[] playerNames = new String[] { "Stark", "Lannister", "Arryn", "Hoare", "pink" };
        String[] playerColors = new String[] { "green", "yellow", "blue", "pink" };

        for (int i = 0; i < playerCount; ++i)
        {
            Player player = new Player(playerNames[i], playerColors[i]);
            Kingdom kingdom = new Kingdom(kingdomDimension, player);
            kingdoms.add(kingdom);
        }

        // subtract 1 for the castle square, divide 2 squares per tile
        //int roundsLeft = (size*size - 1) / 2;

        thisRoundTiles = new RoundTiles(tileBag, roundTileCount);
        nextRoundTiles = null;
        currentTurn = new CurrentTurn(
            kingdoms.get((int)(Math.random()*playerCount)).getPlayer(),
            Task.CHOOSING_INITIAL_TILE);
    }

    /**
     * Gets the current player and what she should do.
     *
     * @return the turn information
     */
    public CurrentTurn getCurrentTurn()
    {
        return currentTurn;
    }

    /**
     * Get the kingdom for a given player.
     *
     * @param player the player
     * @return the player's kingdom
     * @throws IllegalMoveException if the player doesn't have a kingdom
     */
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

    /**
     * Get the kingdoms for all the players.
     *
     * @return the list of kingdoms
     */
    public List<Kingdom> getAllKingdoms()
    {
        return kingdoms;
    }

    /**
     * Get the tiles that should be claimed and played this round.
     *
     * @return the set of tiles for this round
     */
    public RoundTiles getThisRoundTiles()
    {
        return thisRoundTiles;
    }

    /**
     * Get the tiles that should be claimed for next round as the other tiles
     * are played.
     *
     * @return the set of tiles to claim for the next round, can be null at
     *  the beginning and end of the game.
     */
    public RoundTiles getNextRoundTiles()
    {
        return nextRoundTiles;
    }

    /**
     * Have a player claim a tile for one of the rounds.
     *
     * @param playerName the name of the player
     * @param rank the rank of the tile she wants to claim.
     * @throws IllegalMoveException if the player cannot claim that tile.
     */
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

    /**
     * Claim a tile for {@link #thisRoundTiles}. The {@link #currentTurn}
     * is re-calculated.
     *
     * @param player the player to claim an initial tile.
     * @param rank the rank of the tile she wants to claim.
     * @throws IllegalMoveException if the player attempts to cheat
     */
    private void claimInitialTile(Player player, int rank)
        throws IllegalMoveException
    {
        thisRoundTiles.claimRank(player, rank);

        currentTurn = getNextPlayerForInitialSelect();
        if (currentTurn == null)
        {
            nextRoundTiles = new RoundTiles(tileBag, roundTileCount);
            currentTurn = getNextPlayerForTilePlacement();
        }
    }

    /**
     * Determine the next player to play after the initial selection of a player.
     *
     * @return the next player to do an initial selection, null if all players
     *         have completed their initial selection.
     */
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

    /**
     * Place a tile by a player in her kingdom.
     *
     * @param playerName the name of the player
     * @param rank the tile to place
     * @param location0 the location of the first square of the tile.
     * @param location1 the location of the second square of the tile.
     * @throws IllegalMoveException if the player is trying to cheat
     */
    public void placeTile(String playerName, int rank, Location location0, Location location1)
        throws IllegalMoveException
    {
        Tile tile = validatePlacingPlayer(playerName, rank);
        Player player = tile.getOwner();
        Kingdom kingdom = getKingdom(player);

        kingdom.placeTile(tile, location0, location1);

        // if the tile was properly placed (didn't throw), remove it from round
        thisRoundTiles.removeTile(tile);
        determineTurnAfterPlacement(player);
    }

    /**
     * Skip placing an unplaceable tile by a player
     *
     * @param playerName the name of the player
     * @param rank the tile to place
     * @throws IllegalMoveException if the player cannot skip her turn
     */
    public void skipTile(String playerName, int rank)
        throws IllegalMoveException
    {
        Tile tile = validatePlacingPlayer(playerName, rank);
        Player player = tile.getOwner();
        Kingdom kingdom = getKingdom(player);

        if (kingdom.isValidTilePlacementAnywhere(tile))
        {
            throw new IllegalMoveException("Tile can be placed and turn cannot be skipped.");
        }

        thisRoundTiles.removeTile(tile);
        determineTurnAfterPlacement(player);
    }

    /**
     * Validate that it is time for player to place a tile.
     *
     * @param playerName the name of the player
     * @param rank the rank of the tile to be placed, validate
     * @return the tile matching the rank
     * @throws IllegalMoveException if the player is going out of turn
     */
    private Tile validatePlacingPlayer(String playerName, int rank)
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

        Tile tile = thisRoundTiles.getNextTileToPlace(playerName);
        if (tile.getRank() != rank)
        {
            throw new IllegalMoveException("Rank " + rank +
                " is not the next tile to be placed.");
        }

        return tile;
    }

    /**
     * Figure out whose turn it should be after a tile is placed.
     *
     * @param player the play who just placed a tile
     * @throws IllegalMoveException if the game is in a bad state
     */
    private void determineTurnAfterPlacement(Player player) throws IllegalMoveException
    {
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

    /**
     * Return the player with the highest score.
     *
     * TODO: Return multiple players in the case of a tie.
     *
     * @return the winning player
     */
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

    /**
     * Claim a tile for {@link #nextRoundTiles}. The {@link #currentTurn}
     * is re-calculated.
     *
     * @param player the player to claim a tile for the next found
     * @param rank the rank of the tile she wants to claim.
     * @throws IllegalMoveException if the player attempts to cheat
     */
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
                nextRoundTiles = new RoundTiles(tileBag, roundTileCount);
            }
            currentTurn = getNextPlayerForTilePlacement();
        }
    }

    /**
     * Determine the next player to place her tile in this round.
     *
     * @return the next player to place her tile, null if there are no
     *         unplaced tiles in this round.
     * @throws IllegalMoveException if game is in a bad state
     */
    private CurrentTurn getNextPlayerForTilePlacement() throws IllegalMoveException
    {
        Player nextPlayer = thisRoundTiles.getNextPlacingPlayer();
        if (nextPlayer == null)
        {
            return null;
        }

        CurrentTurn turn = new CurrentTurn(nextPlayer, Task.PLACING_TILE);

        Kingdom kingdom = getKingdom(nextPlayer);
        Tile tile = thisRoundTiles.getNextTileToPlace(nextPlayer.getName());
        if (! kingdom.isValidTilePlacementAnywhere(tile))
        {
            turn.setSkipReason("Tile cannot be placed anywhere.");
        }

        return turn;
    }
}
