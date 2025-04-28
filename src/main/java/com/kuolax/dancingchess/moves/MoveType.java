package com.kuolax.dancingchess.moves;

import com.kuolax.dancingchess.board.Square;

public enum MoveType {
    HORIZONTAL,
    VERTICAL,
    DIAGONAL,
    KNIGHT_MOVE,
    PAWN_MOVE,
    CASTLE;

    public static MoveType determineMoveType(Square from, Square to) {
        if (from.isHorizontalTo(to)) return HORIZONTAL;
        else if (from.isVerticalTo(to)) return VERTICAL;
        else if (from.isDiagonalTo(to)) return DIAGONAL;
        else if (isKnightMove(from, to)) return KNIGHT_MOVE;
        // todo Pawn and Castle Type detection
        throw new IllegalArgumentException("no legal move type detected.");
    }

    public static boolean isKnightMove(Square from, Square to) {
        int xDiff = Math.abs(from.getX() - to.getX());
        int yDiff = Math.abs(from.getY() - to.getY());

        return (xDiff == 2 && yDiff == 1) || (xDiff == 1 && yDiff == 2);
    }
}
