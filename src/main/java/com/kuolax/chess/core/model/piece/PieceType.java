package com.kuolax.chess.core.model.piece;

import com.kuolax.chess.core.model.Square;
import com.kuolax.chess.core.model.move.StandardStartPositions;
import com.kuolax.chess.core.model.move.StartPositionsProvider;
import com.kuolax.chess.core.model.move.validation.BishopMoveValidator;
import com.kuolax.chess.core.model.move.validation.KingMoveValidator;
import com.kuolax.chess.core.model.move.validation.KnightMoveValidator;
import com.kuolax.chess.core.model.move.validation.MoveValidator;
import com.kuolax.chess.core.model.move.validation.PawnMoveValidator;
import com.kuolax.chess.core.model.move.validation.QueenMoveValidator;
import com.kuolax.chess.core.model.move.validation.RookMoveValidator;
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
