package chess.dancing.moves;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import chess.dancing.pieces.Color;
import chess.dancing.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public interface MoveValidator {

    List<Square> getPotentialTargetSquares(Piece piece, Square from, ChessBoard board);

    boolean isLegalTargetSquareForPiece(Square from, Square to);

    boolean isPathClear(Square from, Square to, ChessBoard board);

    default boolean isLegalMove(Piece piece, Square from, Square to, ChessBoard board) {
        if (!isLegalTargetSquareForPiece(from, to)) return false;
        if (!isPathClear(from, to, board)) return false;
        return isEmptySquareOrCanTakeOnTargetSquare(piece, to, board);
    }

    default List<Square> getAllLegalMoves(Piece piece, Square from, ChessBoard board) {
        List<Square> legalMoves = new ArrayList<>();
        List<Square> potentialTargetSquares = getPotentialTargetSquares(piece, from, board);

        for (Square to : potentialTargetSquares) {
            if (isLegalMove(piece, from, to, board)) legalMoves.add(to);
        }
        return legalMoves;
    }

    default boolean isEmptySquareOrCanTakeOnTargetSquare(Piece piece, Square to, ChessBoard board) {
        if (board.getPieceAt(to) == null) return true;

        Color fromColor = piece.getColor();
        Color toColor = board.getPieceAt(to).getColor();
        return fromColor != toColor;
    }
}
