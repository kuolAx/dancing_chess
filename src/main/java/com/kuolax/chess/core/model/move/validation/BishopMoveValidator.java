package com.kuolax.chess.core.model.move.validation;

import com.kuolax.chess.core.model.Board;
import com.kuolax.chess.core.model.Square;
import com.kuolax.chess.core.model.move.MoveType;

import java.util.Arrays;
import java.util.List;

import static com.kuolax.chess.core.model.move.MoveType.DIAGONAL;

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
