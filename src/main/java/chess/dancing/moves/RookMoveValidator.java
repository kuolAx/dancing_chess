package chess.dancing.moves;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import chess.dancing.pieces.Piece;

import java.util.Arrays;
import java.util.List;

public class RookMoveValidator implements MoveValidator {
    @Override
    public List<Square> getPotentialTargetSquares(Piece piece, Square from, ChessBoard board) {
        return Arrays.stream(Square.values())
                .filter(s -> from.isInRowWith(s) || from.isInColumnWith(s))
                .toList();
    }

    @Override
    public boolean isLegalTargetSquareForPiece(Square from, Square to) {
        return !from.isInColumnWith(to) || !from.isInRowWith(to);
    }

    @Override
    public boolean isPathClear(Square from, Square to, ChessBoard board) {
        if (from.isInRowWith(to)) {
            int row = from.getRow();
            int startCol = Math.min(from.getColumn(), to.getColumn());
            int endCol = Math.max(from.getColumn(), to.getColumn());

            // Check every square on the way, except start and target
            for (int col = startCol + 1; col < endCol; col++) {
                if (board.getPieceAt(Square.getByCoordinates(col, row)) != null) return false;
            }
        } else if (from.isInColumnWith(to)) {
            int col = from.getColumn();
            int startRow = Math.min(from.getRow(), to.getRow());
            int endRow = Math.max(from.getRow(), to.getRow());

            // Check every square on the way, except start and target
            for (int row = startRow + 1; row < endRow; row++) {
                if (board.getPieceAt(Square.getByCoordinates(col, row)) != null) return false;
            }
        }
        return true;
    }
}
