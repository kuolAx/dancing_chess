package com.kuolax.chess.core.model.move;

import com.kuolax.chess.core.model.Square;
import com.kuolax.chess.core.model.piece.PieceColor;

import java.util.List;

public interface StartPositionsProvider {
    List<Square> getStartSquaresForPiece(PieceColor color);
}
