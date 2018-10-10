package com.huffmancoding.kingdomino;

public class IllegalMoveException extends Exception
{
    public IllegalMoveException(String message)
    {
        super(message);
    }
}
