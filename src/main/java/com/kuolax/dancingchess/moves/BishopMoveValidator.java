package com.kuolax.dancingchess.moves;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;

import java.util.Arrays;
import java.util.List;

import static com.kuolax.dancingchess.moves.MoveType.DIAGONAL;

public class BishopMoveValidator extends AbstractMoveValidator {

    @Override
    protected List<MoveType> getLegalMoveTypes() {
        return List.of(DIAGONAL);
    }

    @Override
    public List<Square> getPotentialTargetSquares(Square from, Board board) {
        return Arrays.stream(Square.values())
                .filter(to -> to.isDiagonalTo(from))
                .toList();
    }
}
