package chess.dancing.moves;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import chess.dancing.pieces.Piece;

public class PawnMoveValidator implements MoveValidator {
    @Override
    public boolean isLegalMove(Piece piece, Square from, Square to, ChessBoard board) {
        return false;}
}
