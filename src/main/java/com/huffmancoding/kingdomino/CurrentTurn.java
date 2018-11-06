package com.huffmancoding.kingdomino;

/**
 * This indicates whose turn it is and what they should do.
 *
 * @author Ken Huffman
 */
public class CurrentTurn
{
    /** The set of possible things a player can do. */
    public static enum Task
    {
        /** the player should claim a tile for this round (only at beginning of game). */
        CHOOSING_INITIAL_TILE,

        /** the player should place a tile previously claimed. */
        PLACING_TILE,

        /** the player should claim a tile for the next round. */
        CHOOSING_NEXT_TILE,

        /** the game is over. */
        GAME_OVER
    }

    /** the player that should take an action. */
    private final Player player;

    /** the action the player should take. */
    private final Task task;

    /** the reason it is impossible for the player to take her action. */
    private String skipReason = null;

    /**
     * Constructor.
     *
     * @param p the player
     * @param t her action
     */
    public CurrentTurn(Player p, Task t)
    {
        player = p;
        task = t;
    }

    /**
     * The player to take an action.
     *
     * @return the player
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * The action the player should take.
     *
     * @return the action
     */
    public Task getTask()
    {
        return task;
    }

    /**
     * Set the reason the player really cannot take her action.
     *
     * @param message the message to display
     */
    public void setSkipReason(String message)
    {
        skipReason = message;
    }

    /**
     * Get the reason the player really cannot take her action.
     *
     * @return the message to display
     */
    public String getSkipReason()
    {
        return skipReason;
    }
}
