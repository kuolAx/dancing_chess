package com.kuolax.dancingchess.moves;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Color;
import com.kuolax.dancingchess.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

import static com.kuolax.dancingchess.moves.MoveType.determineStandardMoveType;

public abstract class AbstractMoveValidator implements MoveValidator {

    protected abstract List<Square> getPotentialTargetSquares(Square from, Board board);

    protected abstract List<MoveType> getLegalMoveTypes();

    @Override
    public boolean isLegalMove(Piece piece, Square from, Square to, Board board) {
        MoveType moveType = determineStandardMoveType(from, to);
    
        return getLegalMoveTypes().contains(moveType)
                && isPathClear(from, to, moveType, board)
                && isEmptySquareOrCanTakeOnTargetSquare(piece, to, board);
    }

    @Override
    public final List<Square> getAllLegalMoves(Piece piece, Square from, Board board) {
        List<Square> legalMoves = new ArrayList<>();
        List<Square> potentialTargetSquares = getPotentialTargetSquares(from, board);

        for (Square to : potentialTargetSquares) {
            if (isLegalMove(piece, from, to, board)) legalMoves.add(to);
        }
        return legalMoves;
    }

    protected boolean isPathClear(Square from, Square to, MoveType moveType, Board board) {
        if (moveType == null) return false;

        return switch (moveType) {
            case HORIZONTAL -> isHorizontalPathClear(board, from, to);
            case VERTICAL -> isVerticalPathClear(board, from, to);
            case DIAGONAL -> isDiagonalPathClear(board, from, to);
            case KNIGHT_MOVE -> true; // knight moves do not require path check
            default -> false;
        };
    }

    protected final boolean isDiagonalPathClear(Board board, Square from, Square to) {
        int stepX = Integer.compare(to.getX(), from.getX());
        int stepY = Integer.compare(to.getY(), from.getY());

        int x = from.getX() + stepX;
        int y = from.getY() + stepY;

        while (x != to.getX() && y != to.getY()) {
            if (board.getPieceAt(Square.getByCoordinates(x, y)) != null) {
                return false;
            }
            x += stepX;
            y += stepY;
        }

        return true;
    }

    protected final boolean isVerticalPathClear(Board board, Square from, Square to) {
        int y = from.getY();
        int startX = Math.min(from.getX(), to.getX());
        int endX = Math.max(from.getX(), to.getX());

        // check every square on the way, except start and target
        for (int curX = startX + 1; curX < endX; curX++) {
            if (board.getPieceAt(Square.getByCoordinates(curX, y)) != null) return false;
        }

        return true;
    }

    protected final boolean isHorizontalPathClear(Board board, Square from, Square to) {
        int x = from.getX();
        int startY = Math.min(from.getY(), to.getY()) + 1;
        int endY = Math.max(from.getY(), to.getY());

        // check every square on the way, except start and target
        for (int curY = startY; curY < endY; curY++) {
            if (board.getPieceAt(Square.getByCoordinates(x, curY)) != null) return false;
        }

        return true;
    }

    protected final boolean isEmptySquareOrCanTakeOnTargetSquare(Piece piece, Square to, Board board) {
        Piece pieceAtTarget = board.getPieceAt(to);
        if (pieceAtTarget == null) return true;
        // todo Pawn und King anders behandeln
        // todo wenn schlagbare Figur vorhanden, dann muss vorhandene Union geprüft werden

        Color fromColor = piece.getColor();
        Color toColor = pieceAtTarget.getColor();

        return fromColor != toColor;
    }
}
