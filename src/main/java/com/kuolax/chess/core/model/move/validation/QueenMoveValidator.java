package com.kuolax.chess.core.model.move.validation;

import com.kuolax.chess.core.model.Board;
import com.kuolax.chess.core.model.Square;
import com.kuolax.chess.core.model.move.MoveType;

import java.util.Arrays;
import java.util.List;

import static com.kuolax.chess.core.model.move.MoveType.DIAGONAL;
import static com.kuolax.chess.core.model.move.MoveType.HORIZONTAL;
import static com.kuolax.chess.core.model.move.MoveType.VERTICAL;

public class QueenMoveValidator extends AbstractMoveValidator {

    @Override
    protected List<MoveType> getLegalMoveTypes() {
        return List.of(HORIZONTAL, VERTICAL, DIAGONAL);
    }

    @Override
    protected List<Square> getPotentialTargetSquares(Square from, Board board) {
        return Arrays.stream(Square.values())
                .filter(to -> from.isHorizontalTo(to) || from.isVerticalTo(to) || from.isDiagonalTo(to))
                .toList();
    }
}
