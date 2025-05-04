package com.kuolax.dancingchess.pieces;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class Piece {

    private final String id;
    private final PieceType type;
    private final PieceColor color;
    private Square position;
    private Piece dancePartner;
    private boolean isMoved;
    private boolean isInUnion;

    public boolean isLegalMove(Square from, Square to, Board board) {
        return type.getMoveValidator().isLegalMove(this, from, to, board);
    }

    public boolean canTakeOn(Square from, Square to, Board board) {
        return switch (getType()) {
            case KING -> false;
            case PAWN -> !from.isVerticalTo(to) && type.getMoveValidator().isLegalMove(this, from, to, board);
            case ROOK, BISHOP, KNIGHT, QUEEN -> type.getMoveValidator().isLegalMove(this, from, to, board);
        };
    }

    public List<Square> getLegalMoves(Board board) {
        Square from = getPosition();
        return type.getMoveValidator().getAllLegalMoves(this, from, board);
    }

    public boolean hasLegalMoves(Board board) {
        return !getLegalMoves(board).isEmpty();
    }
}
