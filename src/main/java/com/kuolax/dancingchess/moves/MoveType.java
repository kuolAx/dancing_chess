package com.kuolax.dancingchess.moves;

import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Color;

public enum MoveType {
    HORIZONTAL,
    VERTICAL,
    DIAGONAL,
    KNIGHT_MOVE,
    PAWN_SINGLE_FORWARD,
    PAWN_DOUBLE_FORWARD,
    PAWN_CAPTURE,
    KING_MOVE,
    KING_CASTLE_SHORT,
    KING_CASTLE_LONG;

    public static MoveType determineStandardMoveType(Square from, Square to) {
        if (from.isHorizontalTo(to)) return HORIZONTAL;
        else if (from.isVerticalTo(to)) return VERTICAL;
        else if (from.isDiagonalTo(to)) return DIAGONAL;
        else if (isKnightMove(from, to)) return KNIGHT_MOVE;

        return null;
    }

    public static boolean isKnightMove(Square from, Square to) {
        int xDiff = Math.abs(from.getX() - to.getX());
        int yDiff = Math.abs(from.getY() - to.getY());

        return (xDiff == 2 && yDiff == 1) || (xDiff == 1 && yDiff == 2);
    }

    public static boolean isKingMove(Square from, Square to, Color c) {
        return isNormalKingMove(from, to, c)
                || isKingShortCastleMove(from, to, c)
                || isKingLongCastleMove(from, to, c);
    }

    public static MoveType determineKingMoveType(Square from, Square to, Color c) {
        if (isNormalKingMove(from, to, c)) return KING_MOVE;
        else if (isKingShortCastleMove(from, to, c)) return KING_CASTLE_SHORT;
        else if (isKingLongCastleMove(from, to, c)) return KING_CASTLE_LONG;

        return null;
    }

    private static boolean isNormalKingMove(Square from, Square to, Color c) {
        return false;
    }

    private static boolean isKingShortCastleMove(Square from, Square to, Color c) {
        return switch (c) {
            case WHITE -> from ==
            case BLACK -> from ==
            default -> false;
        };
    }

    private static boolean isKingLongCastleMove(Square from, Square to, Color c) {
        return false;
    }

    public static MoveType determinePawnMoveType(Square from, Square to, int direction) {
        if (isPawnSingleForward(from, to, direction)) return PAWN_SINGLE_FORWARD;
        else if (isPawnDoubleForward(from, to, direction)) return PAWN_DOUBLE_FORWARD;
        else if (isPawnDiagonalCapture(from, to, direction)) return PAWN_CAPTURE;

        return null;
    }

    public static boolean isPawnMove(Square from, Square to, int direction) {
        return isPawnSingleForward(from, to, direction)
                || isPawnDoubleForward(from, to, direction)
                || isPawnDiagonalCapture(from, to, direction);
    }

    private static boolean isPawnSingleForward(Square from, Square to, int direction) {
        // todo fix logic
        return from.isVerticalTo(to) &&
                to.getX() == from.getX() + direction;
    }

    private static boolean isPawnDoubleForward(Square from, Square to, int direction) {
        boolean isFirstOrSecondRank = (direction == 1 && from.getX() <= 2) || (direction == -1 && from.getX() >= 6);

        // todo fix logic
        return isFirstOrSecondRank
                && to.getX() == from.getX() + (direction * 2)
                && from.isVerticalTo(to);
    }

    private static boolean isPawnDiagonalCapture(Square from, Square to, int direction) {
        return to.getX() == from.getX() + direction
                && from.getYDiff(to) == 1
                && from.isDiagonalTo(to);
    }
}
