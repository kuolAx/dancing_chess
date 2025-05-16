package chess.dancing.moves;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import chess.dancing.pieces.Piece;

public class KingMoveValidator implements MoveValidator {
    @Override
    public boolean isLegalMove(Piece piece, Square from, Square to, ChessBoard board) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getColumn() - to.getColumn());

        // Normale Königsbewegung
        boolean normalMove = rowDiff <= 1 && colDiff <= 1 && (rowDiff > 0 || colDiff > 0);

        // Rochade-Logik
        boolean castling = isCastlingMove(piece, from, to, board);

        return normalMove || castling;
    }

    private boolean isCastlingMove(Piece piece, Square from, Square to, ChessBoard board) {
        // Komplexe Prüfung der Rochade-Bedingungen
        // ...
        return false;
    }
}
