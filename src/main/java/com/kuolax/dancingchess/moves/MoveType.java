package com.kuolax.dancingchess.moves;

import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;
import com.kuolax.dancingchess.pieces.PieceColor;

import static com.kuolax.dancingchess.board.Square.C1;
import static com.kuolax.dancingchess.board.Square.C8;
import static com.kuolax.dancingchess.board.Square.E1;
import static com.kuolax.dancingchess.board.Square.E8;
import static com.kuolax.dancingchess.board.Square.G1;
import static com.kuolax.dancingchess.board.Square.G8;

public enum MoveType {
    HORIZONTAL,
    VERTICAL,
    DIAGONAL,
    KNIGHT_MOVE,
    PAWN_SINGLE_FORWARD,
    PAWN_DOUBLE_FORWARD,
    PAWN_CAPTURE,
    PAWN_EN_PASSANT,
    PAWN_PROMOTION,
    KING_MOVE,
    KING_CASTLE_SHORT,
    KING_CASTLE_LONG;

    public static MoveType determineMoveType(Piece piece, Square from, Square to) {
        return switch (piece.getType()) {
            case QUEEN, KNIGHT, ROOK, BISHOP -> determineStandardMoveType(from, to);
            case KING -> determineKingMoveType(from, to, piece.getColor());
            case PAWN -> determinePawnMoveType(from, to, PawnMoveValidator.getPawnMoveDirection(piece.getColor()));
        };
    }

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

    public static boolean isKingMove(Square from, Square to, PieceColor c) {
        return isKingShortCastleMove(from, to, c)
                || isKingLongCastleMove(from, to, c)
                || isNormalKingMove(from, to);
    }

    public static MoveType determineKingMoveType(Square from, Square to, PieceColor c) {
        if (isKingShortCastleMove(from, to, c)) return KING_CASTLE_SHORT;
        else if (isKingLongCastleMove(from, to, c)) return KING_CASTLE_LONG;
        else if (isNormalKingMove(from, to)) return KING_MOVE;

        return null;
    }

    private static boolean isNormalKingMove(Square from, Square to) {
        int xDiff = from.getXDiff(to);
        int yDiff = from.getYDiff(to);
        return xDiff <= 1 && yDiff <= 1;
    }

    private static boolean isKingShortCastleMove(Square from, Square to, PieceColor c) {
        return switch (c) {
            case WHITE -> from == E1 && to == G1;
            case BLACK -> from == E8 && to == G8;
        };
    }

    private static boolean isKingLongCastleMove(Square from, Square to, PieceColor c) {
        return switch (c) {
            case WHITE -> from == E1 && to == C1;
            case BLACK -> from == E8 && to == C8;
        };
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
        return from.isVerticalTo(to) &&
                to.getY() == (from.getY() + direction);
    }

    private static boolean isPawnDoubleForward(Square from, Square to, int direction) {
        boolean isFirstOrSecondRank = (direction == 1 && from.getY() <= 2) || (direction == -1 && from.getY() >= 6);

        return isFirstOrSecondRank
                && from.isVerticalTo(to)
                && to.getY() == from.getY() + (direction * 2);
    }

    private static boolean isPawnDiagonalCapture(Square from, Square to, int direction) {
        return from.getXDiff(to) == 1
                && (to.getY() - from.getY()) == direction
                && from.isDiagonalTo(to);
    }
}
