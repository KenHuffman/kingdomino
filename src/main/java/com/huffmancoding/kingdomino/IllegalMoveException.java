package com.huffmancoding.kingdomino;

public class IllegalMoveException extends Exception
{
    private static final long serialVersionUID = 1;
    
    public IllegalMoveException(String message)
    {
        super(message);
    }
}
