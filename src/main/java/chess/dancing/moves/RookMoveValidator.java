package chess.dancing.moves;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import chess.dancing.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class RookMoveValidator implements MoveValidator {
    @Override
    public boolean isLegalMove(Piece piece, Square from, Square to, ChessBoard board) {
        return false;
    }

    @Override
    public List<Square> getAllLegalMoves(Piece piece, Square from, ChessBoard board) {
        List<Square> legalMoves = new ArrayList<>();

        // todo only check squares relevant for rook movement
        for (Square to : Square.values()) {
            if (to.isInColumnWith(from) || to.isInRowWith(from))
                // todo filter moves blocked by same color or blocked by taking opposite color piece
                legalMoves.add(to);
        }

        return legalMoves;
    }
}
