package chess.dancing.board;

import chess.dancing.pieces.Piece;

public interface MoveValidator {

    boolean isValidMove(Piece piece, Square from, Square to, ChessBoard board);
}
