package com.huffmancoding.kingdomino;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is the web interface into game server.
 */
@RestController
public class KingdominoController
{
    /** the game to report on. */
    @Resource(name = "game")
    Game game;

    /**
     * Return the state of the game in one big json blob
     *
     * @return the game
     */
    @GetMapping("/getgame")
    public ResponseEntity<?> getGame()
    {
        return ResponseEntity.ok(getGameResponse(null));
    }

    /**
     * Resets the game.
     *
     * @return the game, reinitialized.
     */
    @PostMapping("/reset")
    public ResponseEntity<?> reset()
    {
        try
        {
            game.init(4, 5);
            return ResponseEntity.ok(getGameResponse(null));
        }
        catch (IllegalMoveException ex)
        {
            return ResponseEntity.badRequest().body(getGameResponse(ex.getMessage()));
        }
    }

    /**
     * Return the game state as a JSONifiable map.
     *
     * @param errorMessage an optional error message to return.
     * @return the map of the game state
     */
    private Map<String, Object> getGameResponse(String errorMessage)
    {
        Map<String, Object> gameMap = new TreeMap<>();

        gameMap.put("kingdoms", game.getAllKingdoms());

        RoundTiles thisRoundTiles = game.getThisRoundTiles();
        if (thisRoundTiles != null)
        {
            gameMap.put("thisRoundTiles", thisRoundTiles.getRemainingTiles());
        }

        RoundTiles nextRoundTiles = game.getNextRoundTiles();
        if (nextRoundTiles != null)
        {
            gameMap.put("nextRoundTiles", nextRoundTiles.getRemainingTiles());
        }

        gameMap.put("currentTurn", game.getCurrentTurn());

        if (errorMessage != null)
        {
            gameMap.put("errorMessage", errorMessage);
        }

        return gameMap;
    }

    /**
     * Have the current player claim a tile for placing later.
     *
     * @param playerName the name of the player claiming the tile, must match
     *        current player because we picky that way
     * @param rank the rank on the back of the tile being claimed
     * @return the game state, possibly with an error message
     */
    @PutMapping("/claimtile/{playerName}/{rank}")
    public ResponseEntity<?> handleClaimTile(
        @PathVariable String playerName,
        @PathVariable int rank)
    {
        try
        {
            game.claimTile(playerName, rank);
            return ResponseEntity.ok(getGameResponse(null));
        }
        catch (IllegalMoveException ex)
        {
            return ResponseEntity.badRequest().body(getGameResponse(ex.getMessage()));
        }
    }

    /**
     * Place a claimed tile on a player's kingdom.
     *
     * @param playerName the name of the player placing the tile, must match
     *        current player because we picky that way
     * @param rank the rank on the back of the tile being place, must be one
     *        previously claimed
     * @param row0 the row to place the first square of the tile
     * @param column0 the column to place the first square of the tile
     * @param row1 the row to place the second square of the tile
     * @param column1 the column to place the second square of the tile
     * @return the game state, possibly with an error message
     */
    @PutMapping("/placetile/{playerName}/{rank}/{row0}/{column0}/{row1}/{column1}")
    public ResponseEntity<?> handlePlaceTile(
        @PathVariable String playerName,
        @PathVariable int rank,
        @PathVariable int row0,
        @PathVariable int column0,
        @PathVariable int row1,
        @PathVariable int column1)
    {
        try
        {
            game.placeTile(playerName, rank, new Location(row0, column0), new Location(row1, column1));
            return ResponseEntity.ok(getGameResponse(null));
        }
        catch (IllegalMoveException ex)
        {
            return ResponseEntity.badRequest().body(getGameResponse(ex.getMessage()));
        }
    }

    /**
     * Skip placing a claimed tile on a player's kingdom.
     *
     * @param playerName the name of the player placing the tile, must match
     *        current player because we picky that way
     * @param rank the rank on the back of the tile being place, must be one
     *        previously claimed
     * @return the game state, possibly with an error message if the tile was playable
     */
    @PutMapping("/skiptile/{playerName}/{rank}")
    public ResponseEntity<?> handleSkipTile(
        @PathVariable String playerName,
        @PathVariable int rank)
    {
        try
        {
            game.skipTile(playerName, rank);
            return ResponseEntity.ok(getGameResponse(null));
        }
        catch (IllegalMoveException ex)
        {
            return ResponseEntity.badRequest().body(getGameResponse(ex.getMessage()));
        }
    }
}