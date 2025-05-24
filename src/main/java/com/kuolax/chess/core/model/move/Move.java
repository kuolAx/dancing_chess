package com.kuolax.chess.core.model.move;

import com.kuolax.chess.core.model.Board;
import com.kuolax.chess.core.model.Square;
import com.kuolax.chess.core.model.piece.Piece;
import com.kuolax.chess.core.model.piece.PieceColor;
import com.kuolax.chess.core.model.piece.PieceType;

public record Move(PieceColor playerColor,
                   Piece movedPiece,
                   Square from,
                   Square to,
                   boolean isTakingMove,
                   Piece capturedPiece,
                   boolean isCheck,
                   boolean isCastlingMove,
                   boolean isCheckmate,
                   boolean isStaleMate,
                   boolean isEnPassantCapture,
                   Square enPassantTarget,
                   boolean isPromotion,
                   PieceType promotionType) {
    
    public void undo(Board board) {
        // update board state
        // update zobrist hash
        // send update to frontend for redrawing of board
    }
}
