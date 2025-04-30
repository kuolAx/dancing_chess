package com.kuolax.dancingchess.pieces;

import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.board.StandardStartPositions;
import com.kuolax.dancingchess.board.StartPositionsProvider;
import com.kuolax.dancingchess.moves.BishopMoveValidator;
import com.kuolax.dancingchess.moves.KingMoveValidator;
import com.kuolax.dancingchess.moves.KnightMoveValidator;
import com.kuolax.dancingchess.moves.MoveValidator;
import com.kuolax.dancingchess.moves.PawnMoveValidator;
import com.kuolax.dancingchess.moves.QueenMoveValidator;
import com.kuolax.dancingchess.moves.RookMoveValidator;
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

    public List<Square> getStartingSquares(PieceColor color) {
        return startPositionsProvider.getStartSquaresForPiece(color);
    }
}
