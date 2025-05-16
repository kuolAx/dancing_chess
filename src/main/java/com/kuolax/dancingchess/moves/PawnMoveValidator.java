package com.kuolax.dancingchess.moves;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Move;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;
import com.kuolax.dancingchess.pieces.PieceColor;

import java.util.Arrays;
import java.util.List;

import static com.kuolax.dancingchess.moves.MoveType.PAWN_CAPTURE;
import static com.kuolax.dancingchess.moves.MoveType.PAWN_DOUBLE_FORWARD;
import static com.kuolax.dancingchess.moves.MoveType.PAWN_SINGLE_FORWARD;
import static com.kuolax.dancingchess.moves.MoveType.determinePawnMoveType;
import static com.kuolax.dancingchess.moves.MoveType.isPawnMove;
import static com.kuolax.dancingchess.pieces.PieceColor.WHITE;

public class PawnMoveValidator extends AbstractMoveValidator {

    public static int getPawnMoveDirection(PieceColor pawnColor) {
        return pawnColor == WHITE ? 1 : -1;
    }

    @Override
    protected List<MoveType> getLegalMoveTypes() {
        return List.of(PAWN_SINGLE_FORWARD, PAWN_DOUBLE_FORWARD, PAWN_CAPTURE);
    }

    @Override
    protected List<Square> getPotentialTargetSquares(Square from, Board board) {
        int moveDirection = getPawnMoveDirection(board.getPieceAt(from).getColor());

        return Arrays.stream(Square.values())
                .filter(to -> isPawnMove(from, to, moveDirection))
                .toList();
    }

    @Override
    public boolean canTakeOn(Piece pawn, Square from, Square to, Board board) {
        return determinePawnMoveType(from, to, getPawnMoveDirection(pawn.getColor())) == PAWN_CAPTURE
                && board.hasPieceAt(to)
                && board.getPieceAt(to).getColor() != pawn.getColor()
                && !pawn.isInUnion();
    }

    @Override
    public boolean isLegalMove(Piece pawn, Square from, Square to, Board board) {
        PieceColor playerColor = pawn.getColor();
        int direction = getPawnMoveDirection(playerColor);

        MoveType pawnMoveType = MoveType.determinePawnMoveType(from, to, direction);
        if (pawnMoveType == null) return false;

        boolean isLegalMove = switch (pawnMoveType) {
            case PAWN_SINGLE_FORWARD -> board.getPieceAt(to) == null;
            case PAWN_DOUBLE_FORWARD -> {
                Square between = Square.getByCoordinates(from.getX(), from.getY() + direction);
                yield board.getPieceAt(between) == null && board.getPieceAt(to) == null;
            }
            case PAWN_CAPTURE -> {
                Piece targetPiece = board.getPieceAt(to);
                yield ((targetPiece != null) && !pawn.isInUnion() && (targetPiece.getColor() != playerColor))
                        || isEnPassant(to, board, direction);
            }
            default -> false;
        };

        return isLegalMove && !board.movePutsKingInCheck(from, to);
    }

    private boolean isEnPassant(Square to, Board board, int direction) {
        Move lastMove = board.getLastMove();
        if (lastMove == null) return false;
        Square enPassantTarget = lastMove.enPassantTarget();

        return enPassantTarget != null
                && enPassantTarget == Square.getByCoordinates(to.getX(), to.getY() - direction);
    }
}
