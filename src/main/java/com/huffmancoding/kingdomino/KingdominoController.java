package com.huffmancoding.kingdomino;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        Map<String, Object> gameMap = new TreeMap<>();
        gameMap.put("kingdoms", game.getAllKingdoms());
        addRoundTiles(gameMap, "thisRoundTiles", game.getThisRoundTiles());
        addRoundTiles(gameMap, "nextRoundTiles", game.getNextRoundTiles());
        gameMap.put("currentTurn", game.getCurrentTurn());
        return ResponseEntity.ok(gameMap);
    }

    /**
     * Convert the unplaced tiles of a staged collection into a game map.
     *
     * @param gameMap the map to add the list of tiles to
     * @param key the key for the list to be added
     * @param stagedTiles the tiles to convert to a list of maps
     */
    private void addRoundTiles(Map<String, Object> gameMap,
        String key, StagedTiles stagedTiles)
    {
        if (stagedTiles != null)
        {
            List<Map<String, Object>> list = new ArrayList<>();

            for (Entry<Tile, Player> entry : stagedTiles.getUnplacedTiles().entrySet())
            {
                Map<String, Object> map = new TreeMap<>();

                map.put("tile", entry.getKey());
                Player player = entry.getValue();
                if (player != null)
                {
                    map.put("player", player);
                }

                list.add(map);
            }

            gameMap.put(key, list);
        }
    }

    /**
     * Have the current player claim a tile for placing later.
     *
     * @param rank
     * @return
     */
    @PostMapping("/claimtile")
    public ResponseEntity<?> handleClaimTile(
        @RequestParam("playerName") String playerName,
        @RequestParam("rank") int rank)
    {
        try
        {
            game.claimTile(playerName, rank);
            return ResponseEntity.ok(null);
        }
        catch (IllegalMoveException ex)
        {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/placetile")
    public ResponseEntity<?> handlePlaceTile(
        @RequestParam("playerName") String playerName,
        @RequestParam("row0") int row0,
        @RequestParam("column0") int column0,
        @RequestParam("row1") int row1,
        @RequestParam("column1") int column1)
    {
        try
        {
            game.placeTile(playerName, new Location(row0, column0), new Location(row1, column1));
            return ResponseEntity.ok(null);
        }
        catch (IllegalMoveException ex)
        {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}