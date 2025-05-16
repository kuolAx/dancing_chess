package chess.dancing.moves;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import chess.dancing.pieces.Piece;

import java.util.List;

public class PawnMoveValidator implements MoveValidator {
    @Override
    public boolean isLegalMove(Piece piece, Square from, Square to, ChessBoard board) {
        return false;
    }

    @Override
    public List<Square> getAllLegalMoves(Piece piece, Square from, ChessBoard board) {
        

        return List.of();
    }
}
