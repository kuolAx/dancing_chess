package chess.dancing.pieces;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class PawnState implements PieceState {
    private boolean wasLastMove2Squares;

    private boolean isOnFirstOrSecondRow;

    private boolean isOnLastRow;
}