package chess.dancing.moves;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import chess.dancing.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class BishopMoveValidator implements MoveValidator {
    @Override
    public boolean isLegalMove(Piece piece, Square from, Square to, ChessBoard board) {
        return false;
    }

    @Override
    public List<Square> getAllLegalMoves(Piece piece, Square from, ChessBoard board) {
        List<Square> legalMoves = new ArrayList<>();

        for (Square square : Square.values()) {
            if (square.isDiagonalTo(from))
                legalMoves.add(square);
        }

        return legalMoves;
    }
}
