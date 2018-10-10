package com.huffmancoding.kingdomino;

import java.util.Optional;

public class CastleSquare implements Square
{
    @Override
    public Optional<Landscape> getLandscape()
    {
        return Optional.empty();
    }
}
