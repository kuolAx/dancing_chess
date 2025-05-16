package com.kuolax.dancingchess.pieces;

public enum PieceColor {
    BLACK, WHITE;
    
    public PieceColor getOpponent() {
        return this == WHITE ? BLACK : WHITE;
    }
}

