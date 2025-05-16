package com.kuolax.dancingchess.board;

import com.kuolax.dancingchess.pieces.Piece;
import com.kuolax.dancingchess.pieces.PieceType;

public record Move(Piece piece, Square from, Square to, boolean isCheck, boolean isCastling, boolean isCheckmate,
                   Square enPassantTarget, boolean isPromotion, PieceType promotionType) {
}
