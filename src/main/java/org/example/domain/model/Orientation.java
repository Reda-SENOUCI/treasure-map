package com.example.treasuremap.domain.model;

public enum Direction {
    NORTH,EAST,WEST, UNKNOWN, SOUTH;

    @Override
    public String toString() {
        return switch (this){
            case NORTH -> "N";
            case EAST -> "E";
            case WEST -> "O";
            case UNKNOWN -> "";
            case SOUTH -> "S";
        };
    }
}
