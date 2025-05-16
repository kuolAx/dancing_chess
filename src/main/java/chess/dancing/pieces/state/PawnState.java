package chess.dancing.pieces.state;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PawnState implements PieceState {
    private boolean wasLastMove2Squares;

    private boolean isOnFirstOrSecondRow;

    private boolean isOnLastRow;
}