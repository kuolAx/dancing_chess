package com.kuolax.dancingchess.moves;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Color;
import com.kuolax.dancingchess.pieces.Piece;

import java.util.Arrays;
import java.util.List;

import static com.kuolax.dancingchess.moves.MoveType.PAWN_CAPTURE;
import static com.kuolax.dancingchess.moves.MoveType.PAWN_FORWARD;
import static com.kuolax.dancingchess.pieces.Color.BLACK;
import static com.kuolax.dancingchess.pieces.Color.WHITE;

public class PawnMoveValidator extends AbstractMoveValidator {

    private static int getPawnMoveDirection(Color color) {
        return (color == WHITE) ? 1 : -1;
    }

    @Override
    protected List<MoveType> getLegalMoveTypes() {
        return List.of(PAWN_FORWARD, PAWN_CAPTURE);
    }

    @Override
    protected List<Square> getPotentialTargetSquares(Square from, Board board) {
        int moveDirection = getPawnMoveDirection(board.getPieceAt(from).getColor());

        return Arrays.stream(Square.values())
                .filter(to -> isPawnMove(from, to, moveDirection))
                .toList();
    }

    @Override
    public boolean isLegalMove(Piece pawn, Square from, Square to, Board board) {
        Color color = pawn.getColor();

        int moveDirection = getPawnMoveDirection(color);

        if (isForwardMove(from, to, moveDirection, 1)) return board.getPieceAt(to) == null;

        if (isPawnOnFirstOrSecondRow(from, color) && isForwardMove(from, to, moveDirection, 2)) {
            Square between = Square.getByCoordinates(from.getX(), from.getY() + moveDirection);
            return board.getPieceAt(between) == null && board.getPieceAt(to) == null;
        }

        if (isDiagonalPawnMove(from, to, moveDirection)) {
            Piece targetFigure = board.getPieceAt(to);
            return (targetFigure != null) && (targetFigure.getColor() != color);
        }

        return false;
    }

    private boolean isForwardMove(Square from, Square to, int direction, int steps) {
        return from.getX() == to.getX() &&
                to.getY() == from.getY() + (direction * steps);
    }

    private boolean isPawnOnFirstOrSecondRow(Square from, Color c) {
        // row 1,2 (white) and 7,8 (black) allow 2 square moves for pawn
        return (c == WHITE && from.getY() <= 2) || (c == BLACK && from.getY() >= 6);
    }

    private boolean isDiagonalPawnMove(Square from, Square to, int direction) {
        return from.getXDiff(to) == 1 &&
                to.getY() == from.getY() + direction &&
                from.isDiagonalTo(to);
    }

    private boolean isPawnMove(Square from, Square to, int moveDirection) {
        return isForwardMove(from, to, moveDirection, 1)
                || isForwardMove(from, to, moveDirection, 2)
                || isDiagonalPawnMove(from, to, moveDirection);
    }
}
