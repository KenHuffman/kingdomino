package com.huffmancoding.kingdomino;

public class CurrentTurn
{
    public static enum Task
    {
        CHOOSING_INITIAL_TILE,
        PLACING_TILE,
        CHOOSING_NEXT_TILE,
        GAME_OVER
    }

    private final Player player;
    private final Task task;

    public CurrentTurn(Player p, Task t)
    {
        player = p;
        task = t;
    }

    public Player getPlayer()
    {
        return player;
    }

    public Task getTask()
    {
        return task;
    }
}
