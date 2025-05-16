package com.kuolax.dancingchess.moves;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;

import java.util.Arrays;
import java.util.List;

import static com.kuolax.dancingchess.moves.MoveType.DIAGONAL;
import static com.kuolax.dancingchess.moves.MoveType.HORIZONTAL;
import static com.kuolax.dancingchess.moves.MoveType.VERTICAL;

public class QueenMoveValidator extends AbstractMoveValidator {

    @Override
    protected List<MoveType> getLegalMoveTypes() {
        return List.of(HORIZONTAL, VERTICAL, DIAGONAL);
    }

    @Override
    protected List<Square> getPotentialTargetSquares(Square from, Board board) {
        return Arrays.stream(Square.values())
                .filter(to -> from.isDiagonalTo(to) || from.isHorizontalTo(to) || from.isDiagonalTo(to))
                .toList();
    }
}
