package com.kuolax.chess.core.model.move;

import com.kuolax.chess.core.model.Square;
import com.kuolax.chess.core.model.piece.Piece;
import com.kuolax.chess.core.model.piece.PieceType;

public record Move(Piece piece,
                   Square from,
                   Square to,
                   boolean isTakingMove,
                   boolean isCheck,
                   boolean isCastling,
                   boolean isCheckmate,
                   boolean isStaleMate,
                   boolean isEnPassantCapture,
                   Square enPassantTarget,
                   boolean isPromotion,
                   PieceType promotionType) {
}
