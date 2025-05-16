package chess.dancing.moves;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import chess.dancing.pieces.Piece;

import java.util.List;

public interface MoveValidator {

    boolean isLegalMove(Piece piece, Square from, Square to, ChessBoard board);

    List<Square> getAllLegalMoves(Piece piece, Square from, ChessBoard board);
}
