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
    @Resource(name = "game")
    Game game;

    @GetMapping("/gettabletop")
    public ResponseEntity<?> getTabletop()
    {
        Map<String, Object> tabletop = new TreeMap<>();
        tabletop.put("kingdoms", game.getAllKingdoms());
        tabletop.put("thisRoundTiles", toJsonFriendly(game.getThisRoundTiles()));
        tabletop.put("nextRoundTiles", toJsonFriendly(game.getNextRoundTiles()));
        tabletop.put("currentTurn", game.getCurrentTurn());
        return ResponseEntity.ok(tabletop);
    }

    private List<Map<String, Object>> toJsonFriendly(StagedTiles stagedTiles)
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

        return list;
    }

    @PostMapping("/claimtile")
    public ResponseEntity<?> handleClaimTile(
        @RequestParam("rank") int rank)
    {
        try
        {
            game.claimTile(rank);
            return ResponseEntity.ok(null);
        }
        catch (IllegalMoveException ex)
        {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/placetile")
    public ResponseEntity<?> handlePlaceTile(
        @RequestParam("row0") int row0,
        @RequestParam("column0") int column0,
        @RequestParam("row1") int row1,
        @RequestParam("column1") int column1)
    {
        try
        {
            game.placeTile(new Location(row0, column0), new Location(row1, column1));
            return ResponseEntity.ok(null);
        }
        catch (IllegalMoveException ex)
        {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}