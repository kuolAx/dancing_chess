package chess.dancing.pieces.state;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RookState implements PieceState {
    private boolean hasMoved;
}
