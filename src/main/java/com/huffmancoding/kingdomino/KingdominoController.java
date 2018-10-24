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
 * See https://www.mkyong.com/spring-boot/spring-boot-ajax-example/
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
        return ResponseEntity.ok(getGameResponse());
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset()
    {
        try
        {
            game.init(4, 5);
            return ResponseEntity.ok(getGameResponse());
        }
        catch (IllegalMoveException ex)
        {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    private Map<String, Object> getGameResponse()
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

        return gameMap;
    }

    /**
     * Have the current player claim a tile for placing later.
     *
     * @param rank
     * @return
     */
    @PutMapping("/claimtile/{playerName}/{rank}")
    public ResponseEntity<?> handleClaimTile(
        @PathVariable String playerName,
        @PathVariable int rank)
    {
        try
        {
            game.claimTile(playerName, rank);
            return ResponseEntity.ok(getGameResponse());
        }
        catch (IllegalMoveException ex)
        {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

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
            return ResponseEntity.ok(getGameResponse());
        }
        catch (IllegalMoveException ex)
        {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}