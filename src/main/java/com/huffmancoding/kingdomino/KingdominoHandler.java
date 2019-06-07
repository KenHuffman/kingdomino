package com.huffmancoding.kingdomino;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is the web interface into game server.
 */
public class KingdominoHandler extends AbstractHandler
{
    /** the game to report on. */
    private final Game game;

    /** json converter from T to a string containing JSON. */
    private ObjectMapper objectMapper = new ObjectMapper();

    public KingdominoHandler(Game g)
    {
        game = g;
    }

    @Override
    public void handle(String target, Request baseRequest,
        HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        List<String> args = new ArrayList<>();
        for (String arg : target.split("/"))
        {
            // this takes care of any leading '/'s in target
            if (! arg.isEmpty())
            {
                args.add(arg);
            }
        }

        System.out.println("target=" + target + ", segments=" + args);
        if (! args.isEmpty())
        {
            String action = args.remove(0);
            switch (action)
            {
                case "getgame":
                    getGame(response);
                    break;

                case "reset":
                    getGame(response);
                    break;

                case "claimtile":
                    claimTile(args, response);
                    break;

                case "placetile":
                    placeTile(args, response);
                    break;
            }
        }

    }

    /**
     * Return the state of the game in one big json blob
     *
     * @param response the response to fill
     * @throws IOException error writing response
     */
    public void getGame(HttpServletResponse response) throws IOException
    {
        getGame(response, null, HttpServletResponse.SC_OK);
    }

    public void getGame(HttpServletResponse response, String errorMessage, int statusCode) throws IOException
    {
        response.setContentType("application/json");
        OutputStream out = response.getOutputStream();
        objectMapper.writeValue(out, getGameResponse(errorMessage));
        out.flush();
        response.setStatus(statusCode);
    }

    /**
     * Resets the game.
     *
     * @param response the response to fill
     * @throws IOException error writing response
     */
    public void reset(HttpServletResponse response) throws IOException
    {
        try
        {
            game.init(4, 5);
            getGame(response);
        }
        catch (IllegalMoveException ex)
        {
            getGame(response, ex.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
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
     * @param args array with: the name of the player claiming the tile
     *        (must match current player because we are picky that way) and
     *        the rank on the back of the tile being claimed
     * @param response returned with the game state, possibly with an error message
     * @throws IOException in case of serialization error
     */
    public void claimTile(List<String> args, HttpServletResponse response) throws IOException
    {
        try
        {
            int i = 0;
            String playerName = args.get(i++);
            int rank = Integer.parseInt(args.get(i++));
            game.claimTile(playerName, rank);
            getGame(response);
        }
        catch (IllegalMoveException | NumberFormatException ex)
        {
            getGame(response, ex.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Place a claimed tile on a player's kingdom.
     *
     * @param args array with: the name of the player claiming the tile
     *        (must match current player because we are picky that way),
     *        the rank on the back of the tile being claimed,
     *        the row to place the first square of the tile,
     *        the column to place the first square of the tile,
     *        the row to place the second square of the tile,
     *        the column to place the second square of the tile
     * @param response returned with the game state, possibly with an error message
     * @throws IOException in case of serialization error
     */
    public void placeTile(
        List<String> args, HttpServletResponse response) throws IOException
    {
        try
        {
            int i = 0;
            String playerName = args.get(i++);
            int rank = Integer.parseInt(args.get(i++));
            int row0 = Integer.parseInt(args.get(i++));
            int column0 = Integer.parseInt(args.get(i++));
            int row1 = Integer.parseInt(args.get(i++));
            int column1 = Integer.parseInt(args.get(i++));
            game.placeTile(playerName, rank, new Location(row0, column0), new Location(row1, column1));
            getGame(response);
        }
        catch (IllegalMoveException ex)
        {
            getGame(response, ex.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
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
//    @PutMapping("/skiptile/{playerName}/{rank}")
//    public ResponseEntity<?> handleSkipTile(
//        @PathVariable String playerName,
//        @PathVariable int rank)
//    {
//        try
//        {
//            game.skipTile(playerName, rank);
//            return ResponseEntity.ok(getGameResponse(null));
//        }
//        catch (IllegalMoveException ex)
//        {
//            return ResponseEntity.badRequest().body(getGameResponse(ex.getMessage()));
//        }
//    }
}