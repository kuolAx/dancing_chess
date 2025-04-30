package com.kuolax.dancingchess.board;

import com.kuolax.dancingchess.pieces.PieceColor;

import java.util.List;

public interface StartPositionsProvider {
    List<Square> getStartSquaresForPiece(PieceColor color);
}
