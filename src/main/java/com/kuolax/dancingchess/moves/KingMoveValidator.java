package com.kuolax.dancingchess.moves;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Color;
import com.kuolax.dancingchess.pieces.Piece;

import java.util.Arrays;
import java.util.List;

import static com.kuolax.dancingchess.moves.MoveType.KING_CASTLE_LONG;
import static com.kuolax.dancingchess.moves.MoveType.KING_CASTLE_SHORT;
import static com.kuolax.dancingchess.moves.MoveType.KING_MOVE;
import static com.kuolax.dancingchess.moves.MoveType.isKingMove;

public class KingMoveValidator extends AbstractMoveValidator {

    @Override
    protected List<MoveType> getLegalMoveTypes() {
        return List.of(KING_MOVE, KING_CASTLE_LONG, KING_CASTLE_SHORT);
    }

    @Override
    protected List<Square> getPotentialTargetSquares(Square from, Board board) {
        Color c = board.getPieceAt(from).getColor();

        return Arrays.stream(Square.values())
                .filter(to -> isKingMove(from, to, c))
                .toList();
    }

    @Override
    public boolean isLegalMove(Piece king, Square from, Square to, Board board) {
        MoveType kingMoveType = MoveType.determineKingMoveType(from, to, king.getColor());
        if (kingMoveType == null) return false;

        return switch (kingMoveType) {
            case KING_MOVE -> {
                if (board.getPieceAt(to) != null) yield false;
                yield board.wouldMoveLeaveKingInCheck(from, to, king);
            }
            case KING_CASTLE_SHORT -> {
                if (board.isKingInCheck(king.getColor())) yield false;
                if (board.wouldMoveLeaveKingInCheck(from, to, king)) yield false;
            }
            case KING_CASTLE_LONG -> {
            }
            default -> false;
        };
    }
}
