package com.kuolax.dancingchess.moves;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;

import java.util.List;

import static com.kuolax.dancingchess.moves.MoveType.PAWN_MOVE;

public class PawnMoveValidator extends AbstractMoveValidator {

    @Override
    protected List<MoveType> getLegalMoveTypes() {
        return List.of(PAWN_MOVE);
    }

    @Override
    protected List<Square> getPotentialTargetSquares(Square from, Board board) {
        return List.of();
    }
}
