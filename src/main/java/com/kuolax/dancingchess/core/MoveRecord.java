package com.kuolax.dancingchess.core;

import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;
import com.kuolax.dancingchess.pieces.PieceType;

public record MoveRecord(PieceType promotionType, boolean isPromotion, boolean isEnPassant, boolean isCastling,
                         boolean isCheckmate, boolean isCheck, Square from, Square to,
                         Piece piece) {
}
