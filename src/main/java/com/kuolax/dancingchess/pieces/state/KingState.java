package com.kuolax.dancingchess.pieces.state;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KingState implements PieceState {
    private boolean isInCheck;

    private boolean hasMoved;
}
