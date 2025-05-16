package chess.dancing.moves;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import chess.dancing.pieces.Piece;

public class KingMoveValidator implements MoveValidator {
    @Override
    public boolean isLegalMove(Piece piece, Square from, Square to, ChessBoard board) {
        int rowDiff = from.getRowDiff(to);
        int colDiff = from.getColumnDiff(to);

        // normal move
        boolean normalMove = rowDiff <= 1 && colDiff <= 1 && (rowDiff > 0 || colDiff > 0);

        // castling move
        boolean castling = isCastlingMove(piece, from, to, board);

        return normalMove || castling;
    }

    private boolean isCastlingMove(Piece piece, Square from, Square to, ChessBoard board) {
        // check for castling conditions
        return false;
    }
}
