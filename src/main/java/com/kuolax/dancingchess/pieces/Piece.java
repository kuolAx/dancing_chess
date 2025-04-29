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

    private final Color color;

    private Piece dancePartner;

    private boolean hasMoved;

    private boolean isInUnion;

    public void resetDancePartner() {
        dancePartner = null;
    }

    public boolean canMoveTo(Square from, Square to, Board board) {
        return type.getMoveValidator().isLegalMove(this, from, to, board);
    }

    public List<Square> getAllLegalMoves(Square from, Board board) {
        List<Square> allLegalMoves = type.getMoveValidator().getAllLegalMoves(this, from, board);

        return allLegalMoves.stream()
                .filter(to -> !board.wouldMoveLeaveKingInCheck(from, to, this))
                .toList();
    }
}
