package chess.dancing.pieces;

import chess.dancing.moves.BishopMoveValidator;
import chess.dancing.moves.KingMoveValidator;
import chess.dancing.moves.KnightMoveValidator;
import chess.dancing.moves.MoveValidator;
import chess.dancing.board.Square;
import chess.dancing.board.StandardStartPositions;
import chess.dancing.board.StartPositionsProvider;
import chess.dancing.moves.PawnMoveValidator;
import chess.dancing.moves.QueenMoveValidator;
import chess.dancing.moves.RookMoveValidator;
import lombok.Getter;

import java.util.List;

@Getter
public enum PieceType {
    KING(1, StandardStartPositions.KING, new KingMoveValidator()),

    QUEEN(1, StandardStartPositions.QUEEN, new QueenMoveValidator()),

    ROOK(2, StandardStartPositions.ROOK, new RookMoveValidator()),

    BISHOP(2, StandardStartPositions.BISHOP, new BishopMoveValidator()),

    KNIGHT(2, StandardStartPositions.KNIGHT, new KnightMoveValidator()),

    PAWN(8, StandardStartPositions.PAWN, new PawnMoveValidator());

    private final int numberOfPieces;
    private final StartPositionsProvider startPositionsProvider;
    private final MoveValidator moveValidator;

    PieceType(int numberOfPieces, StartPositionsProvider startPositionsProvider, MoveValidator moveValidator) {
        this.numberOfPieces = numberOfPieces;
        this.startPositionsProvider = startPositionsProvider;
        this.moveValidator = moveValidator;
    }

    public List<Square> getStartingSquares(Color color) {
        return startPositionsProvider.getStartSquaresForPiece(color);
    }
}
