package chess.dancing.moves;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import chess.dancing.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class KnightMoveValidator implements MoveValidator {
    private static final int[][] KNIGHT_MOVES = {
            {1, 2}, {2, 1}, {2, -1}, {1, -2},
            {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}
    };

    @Override
    public boolean isLegalMove(Piece knight, Square from, Square to, ChessBoard board) {
        return false;
    }

    @Override
    public List<Square> getAllLegalMoves(Piece knight, Square from, ChessBoard board) {
        List<Square> legalMoves = new ArrayList<>();

        int fromRow = from.getRow();
        int fromCol = from.getColumn();

        for (int[] move : KNIGHT_MOVES) {
            int toRow = fromRow + move[0];
            int toCol = fromCol + move[1];

            Square to = Square.getByCoordinates(toCol, toRow);

            if (to != null && isLegalMove(knight, from, to, board)) {
                legalMoves.add(to);
            }
        }
        return legalMoves;
    }
}
