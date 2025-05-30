package com.kuolax.chess.core.model.move.validation;

import com.kuolax.chess.core.model.Board;
import com.kuolax.chess.core.model.Square;
import com.kuolax.chess.core.model.move.MoveType;
import com.kuolax.chess.core.model.piece.Piece;
import com.kuolax.chess.core.model.piece.PieceColor;

import java.util.List;

import static com.kuolax.chess.core.model.move.MoveType.determineStandardMoveType;

public abstract class AbstractMoveValidator implements MoveValidator {

    protected abstract List<Square> getPotentialTargetSquares(Square from, Board board);

    protected abstract List<MoveType> getLegalMoveTypes();

    @Override
    public boolean isLegalMove(Piece piece, Square from, Square to, Board board) {
        return canTakeOn(piece, from, to, board)
                && !board.movePutsKingInCheck(from, to);
    }

    @Override
    public boolean canTakeOn(Piece piece, Square from, Square to, Board board) {
        MoveType moveType = determineStandardMoveType(from, to);

        return moveType != null
                && getLegalMoveTypes().contains(moveType)
                && isPathClear(from, to, moveType, board)
                && isEmptySquareOrCanTakeOnTargetSquare(piece, to, board);
    }

    @Override
    public final List<Square> getAllLegalMoves(Piece piece, Square from, Board board) {
        List<Square> potentialTargetSquares = getPotentialTargetSquares(from, board);
        return potentialTargetSquares.stream()
                .filter(to -> isLegalMove(piece, from, to, board))
                .toList();
    }

    protected final boolean isPathClear(Square from, Square to, MoveType moveType, Board board) {
        if (moveType == null) return false;

        return switch (moveType) {
            case HORIZONTAL -> isHorizontalPathClear(board, from, to);
            case VERTICAL -> isVerticalPathClear(board, from, to);
            case DIAGONAL -> isDiagonalPathClear(board, from, to);
            case KNIGHT_MOVE -> true; // knight move do not require path check
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
        int direction = Integer.compare(to.getY(), from.getY());

        for (int y = from.getY() + direction; y != to.getY(); y += direction) {
            Square squareBetween = Square.getByCoordinates(from.getX(), y);
            if (board.hasPieceAt(squareBetween)) return false;
        }
        return true;
    }

    protected final boolean isHorizontalPathClear(Board board, Square from, Square to) {
        int direction = Integer.compare(to.getX(), from.getX());

        for (int x = from.getX() + direction; x != to.getX(); x += direction) {
            Square squareBetween = Square.getByCoordinates(x, from.getY());
            if (board.hasPieceAt(squareBetween)) return false;
        }
        return true;
    }

    protected final boolean isEmptySquareOrCanTakeOnTargetSquare(Piece piece, Square to, Board board) {
        Piece pieceAtTarget = board.getPieceAt(to);
        if (pieceAtTarget == null) return true;

        PieceColor fromColor = piece.getColor();
        PieceColor toColor = pieceAtTarget.getColor();

        return fromColor != toColor;
    }
}
