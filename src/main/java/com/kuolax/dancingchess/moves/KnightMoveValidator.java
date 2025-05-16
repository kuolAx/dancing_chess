package com.kuolax.dancingchess.moves;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;

import java.util.Arrays;
import java.util.List;

import static com.kuolax.dancingchess.moves.MoveType.KNIGHT_MOVE;
import static com.kuolax.dancingchess.moves.MoveType.isKnightMove;

public class KnightMoveValidator extends AbstractMoveValidator {

    @Override
    protected List<MoveType> getLegalMoveTypes() {
        return List.of(KNIGHT_MOVE);
    }

    @Override
    protected List<Square> getPotentialTargetSquares(Square from, Board board) {
        return Arrays.stream(Square.values())
                .filter(to -> isKnightMove(from, to))
                .toList();
    }
}
