package chess.dancing.moves;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import chess.dancing.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class QueenMoveValidator implements MoveValidator {

    private final RookMoveValidator rookMoveValidator;
    private final BishopMoveValidator bishopMoveValidator;

    public QueenMoveValidator() {
        rookMoveValidator = new RookMoveValidator();
        bishopMoveValidator = new BishopMoveValidator();
    }

    @Override
    public boolean isLegalMove(Piece piece, Square from, Square to, ChessBoard board) {
        boolean isLegalRookMove = rookMoveValidator.isLegalMove(piece, from, to, board);
        boolean isLegalBishopMove = bishopMoveValidator.isLegalMove(piece, from, to, board);
        return isLegalRookMove || isLegalBishopMove;
    }

    @Override
    public List<Square> getAllLegalMoves(Piece queen, Square from, ChessBoard board) {
        List<Square> legalMoves = new ArrayList<>(rookMoveValidator.getAllLegalMoves(queen, from, board));
        legalMoves.addAll(bishopMoveValidator.getAllLegalMoves(queen, from, board));
        return legalMoves;
    }
}
