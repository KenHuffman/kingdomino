package com.huffmancoding.kingdomino;

import java.util.ArrayList;
import java.util.List;

public class TileBag
{
    private final List<Tile> tiles = new ArrayList<>();

    public TileBag()
    {
        addTile(Landscape.WHEAT, 0, Landscape.WHEAT, 0);
        addTile(Landscape.WHEAT, 0, Landscape.WHEAT, 0);
        addTile(Landscape.TREES, 0, Landscape.TREES, 0);
        addTile(Landscape.TREES, 0, Landscape.TREES, 0);
        addTile(Landscape.TREES, 0, Landscape.TREES, 0);
        addTile(Landscape.TREES, 0, Landscape.TREES, 0);
        addTile(Landscape.WATER, 0, Landscape.WATER, 0);
        addTile(Landscape.WATER, 0, Landscape.WATER, 0);
        addTile(Landscape.WATER, 0, Landscape.WATER, 0);
        addTile(Landscape.GRASS, 0, Landscape.GRASS, 0);
        addTile(Landscape.GRASS, 0, Landscape.GRASS, 0);
        addTile(Landscape.SWAMP, 0, Landscape.SWAMP, 0);
        addTile(Landscape.WHEAT, 0, Landscape.TREES, 0);
        addTile(Landscape.WHEAT, 0, Landscape.WATER, 0);
        addTile(Landscape.WHEAT, 0, Landscape.GRASS, 0);
        addTile(Landscape.WHEAT, 0, Landscape.SWAMP, 0);
        addTile(Landscape.TREES, 0, Landscape.WATER, 0);
        addTile(Landscape.TREES, 0, Landscape.GRASS, 0);
        addTile(Landscape.WHEAT, 1, Landscape.TREES, 0);
        addTile(Landscape.WHEAT, 1, Landscape.WATER, 0);
        addTile(Landscape.WHEAT, 1, Landscape.GRASS, 0);
        addTile(Landscape.WHEAT, 1, Landscape.SWAMP, 0);
        addTile(Landscape.WHEAT, 1, Landscape.ORE, 0);
        addTile(Landscape.TREES, 1, Landscape.WHEAT, 0);
        addTile(Landscape.TREES, 1, Landscape.WHEAT, 0);
        addTile(Landscape.TREES, 1, Landscape.WHEAT, 0);
        addTile(Landscape.TREES, 1, Landscape.WHEAT, 0);
        addTile(Landscape.TREES, 1, Landscape.WATER, 0);
        addTile(Landscape.TREES, 1, Landscape.GRASS, 0);
        addTile(Landscape.WATER, 1, Landscape.WHEAT, 0);
        addTile(Landscape.WATER, 1, Landscape.WHEAT, 0);
        addTile(Landscape.WATER, 1, Landscape.TREES, 0);
        addTile(Landscape.WATER, 1, Landscape.TREES, 0);
        addTile(Landscape.WATER, 1, Landscape.TREES, 0);
        addTile(Landscape.WATER, 1, Landscape.TREES, 0);
        addTile(Landscape.WHEAT, 0, Landscape.GRASS, 1);
        addTile(Landscape.WATER, 0, Landscape.GRASS, 1);
        addTile(Landscape.WHEAT, 0, Landscape.SWAMP, 1);
        addTile(Landscape.GRASS, 0, Landscape.SWAMP, 1);
        addTile(Landscape.ORE, 1, Landscape.WHEAT, 0);
        addTile(Landscape.WHEAT, 0, Landscape.GRASS, 2);
        addTile(Landscape.WATER, 0, Landscape.GRASS, 2);
        addTile(Landscape.WHEAT, 0, Landscape.SWAMP, 2);
        addTile(Landscape.GRASS, 0, Landscape.SWAMP, 2);
        addTile(Landscape.ORE, 2, Landscape.WHEAT, 0);
        addTile(Landscape.SWAMP, 0, Landscape.ORE, 2);
        addTile(Landscape.SWAMP, 0, Landscape.ORE, 2);
        addTile(Landscape.WHEAT, 0, Landscape.ORE, 3);
    }

    private void addTile(Landscape l1, int c1, Landscape l2, int c2)
    {
        int rank = tiles.size() + 1;
        tiles.add(new Tile(new LandscapeSquare(l1, c1), new LandscapeSquare(l2, c2), rank));
    }

    public Tile drawRandomTile() throws IllegalMoveException
    {
        if (isEmpty())
        {
            throw new IllegalMoveException("The bag is empty");
        }

        int position = (int) (Math.random() * tiles.size());
        return tiles.remove(position);
    }

    public boolean isEmpty()
    {
        return tiles.isEmpty();
    }
}
