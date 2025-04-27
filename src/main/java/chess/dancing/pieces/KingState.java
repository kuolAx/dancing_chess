package chess.dancing.pieces;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KingState implements PieceState {
    private boolean isInCheck;

    private boolean hasMoved;
}
