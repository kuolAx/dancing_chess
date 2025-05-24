package com.kuolax.chess.core.model.piece;

public enum PieceColor {
    BLACK, WHITE;

    public PieceColor getOpponent() {
        return this == WHITE ? BLACK : WHITE;
    }
}

