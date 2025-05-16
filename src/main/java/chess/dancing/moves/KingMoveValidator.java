package chess.dancing.moves;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import chess.dancing.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class KingMoveValidator implements MoveValidator {
    @Override
    public boolean isLegalMove(Piece king, Square from, Square to, ChessBoard board) {
        int rowDiff = from.getRowDiff(to);
        int colDiff = from.getColumnDiff(to);

        // normal move
        boolean normalMove = rowDiff <= 1 && colDiff <= 1 && (rowDiff > 0 || colDiff > 0);

        // castling move
        boolean castling = isCastlingMove(king, from, to, board);

        return normalMove || castling;
    }

    private boolean isCastlingMove(Piece piece, Square from, Square to, ChessBoard board) {
        // check for castling conditions
        return false;
    }

    @Override
    public List<Square> getAllLegalMoves(Piece king, Square from, ChessBoard board) {
        List<Square> legalMoves = new ArrayList<>();

        for (Square square : Square.values()) {
            if (isLegalMove(king, from, square, board)) {
                legalMoves.add(square);
            }
        }

        return legalMoves;
    }
}
