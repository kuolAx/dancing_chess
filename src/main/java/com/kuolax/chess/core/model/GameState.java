package com.kuolax.chess.core.model;

public enum GameState {
    ONGOING,
    WHITE_WINS,
    BLACK_WINS,
    STALEMATE,
    DRAW,
    DRAW_BY_REPETITION,
    DRAW_BY_INSUFFICIENT_MATERIAL,
    DRAW_BY_AGREEMENT
}