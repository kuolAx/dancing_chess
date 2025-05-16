package chess.dancing.board;

import chess.dancing.pieces.Color;

import java.util.List;

public interface StartPositionsProvider {
    List<Square> getStartSquaresForPiece(Color color);
}
