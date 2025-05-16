package com.kuolax.dancingchess.board;

import com.kuolax.dancingchess.pieces.Color;

import java.util.List;

public interface StartPositionsProvider {
    List<Square> getStartSquaresForPiece(Color color);
}
