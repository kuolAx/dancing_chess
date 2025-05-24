package com.kuolax.chess.core.model.move.validation;

import com.kuolax.chess.core.model.Board;
import com.kuolax.chess.core.model.Square;
import com.kuolax.chess.core.model.move.MoveType;
import com.kuolax.chess.core.model.piece.Piece;
import com.kuolax.chess.core.model.piece.PieceColor;

import java.util.Arrays;
import java.util.List;

import static com.kuolax.chess.core.model.move.MoveType.PAWN_CAPTURE;
import static com.kuolax.chess.core.model.move.MoveType.PAWN_DOUBLE_FORWARD;
import static com.kuolax.chess.core.model.move.MoveType.PAWN_EN_PASSANT;
import static com.kuolax.chess.core.model.move.MoveType.PAWN_SINGLE_FORWARD;
import static com.kuolax.chess.core.model.move.MoveType.determinePawnMoveType;
import static com.kuolax.chess.core.model.move.MoveType.isPawnMove;
import static com.kuolax.chess.core.model.piece.PieceColor.WHITE;
import static com.kuolax.chess.core.model.piece.PieceType.PAWN;

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
        int direction = getPawnMoveDirection(board.getPieceAt(from).getColor());

        return Arrays.stream(Square.values())
                .filter(to -> isPawnMove(from, to, direction))
                .toList();
    }

    @Override
    public boolean canTakeOn(Piece pawn, Square from, Square to, Board board) {
        MoveType pawnMoveType = determinePawnMoveType(from, to, getPawnMoveDirection(pawn.getColor()), board.getEnPassantTarget());
        if (pawnMoveType == PAWN_EN_PASSANT) {
            Square enPassantTargetSquare = Square.getByCoordinates(to.getX(), to.getY() - getPawnMoveDirection(pawn.getColor()));
            return board.hasPieceAt(enPassantTargetSquare)
                    && board.getPieceAt(enPassantTargetSquare).getColor() != pawn.getColor()
                    && board.getPieceAt(enPassantTargetSquare).getType() == PAWN
                    && !pawn.isInUnion();
        }
        return pawnMoveType == PAWN_CAPTURE
                && board.hasPieceAt(to)
                && board.getPieceAt(to).getColor() != pawn.getColor()
                && !pawn.isInUnion();
    }

    @Override
    public boolean isLegalMove(Piece pawn, Square from, Square to, Board board) {
        int direction = getPawnMoveDirection(pawn.getColor());

        MoveType pawnMoveType = MoveType.determinePawnMoveType(from, to, direction, board.getEnPassantTarget());
        if (pawnMoveType == null) return false;

        boolean isLegalMove = switch (pawnMoveType) {
            case PAWN_SINGLE_FORWARD -> board.getPieceAt(to) == null;
            case PAWN_DOUBLE_FORWARD -> {
                Square between = Square.getByCoordinates(from.getX(), from.getY() + direction);
                yield board.getPieceAt(between) == null && board.getPieceAt(to) == null;
            }
            case PAWN_CAPTURE -> board.hasPieceAt(to)
                    && board.getPieceAt(to).getColor() != pawn.getColor()
                    && !pawn.isInUnion();
            case PAWN_EN_PASSANT -> {
                Square enPassantTargetSquare = Square.getByCoordinates(to.getX(), to.getY() - getPawnMoveDirection(pawn.getColor()));
                yield board.hasPieceAt(enPassantTargetSquare)
                        && board.getPieceAt(enPassantTargetSquare).getColor() != pawn.getColor()
                        && board.getPieceAt(enPassantTargetSquare).getType() == PAWN
                        && !pawn.isInUnion();
            }
            default -> false;
        };

        return isLegalMove && !board.movePutsKingInCheck(from, to);
    }
}
