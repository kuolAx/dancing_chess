package com.kuolax.dancingchess.pieces;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.state.DefaultPieceState;
import com.kuolax.dancingchess.pieces.state.KingState;
import com.kuolax.dancingchess.pieces.state.PawnState;
import com.kuolax.dancingchess.pieces.state.PieceState;
import com.kuolax.dancingchess.pieces.state.RookState;
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

    @Builder.Default
    private PieceState state = null;

    private Piece dancePartner;

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
                .filter(to -> !wouldLeaveKingInCheck(from, to, board))
                .toList();
    }

    private boolean wouldLeaveKingInCheck(Square from, Square to, Board board) {
        board.simulateMove(from, to);
        boolean isKingInCheck = board.isKingInCheck(getColor());
        board.simulateMove(to, from);
        return isKingInCheck;
    }

    public PieceState getState() {
        if (state == null) {
            state = switch (type) {
                case PAWN -> new PawnState();
                case KING -> new KingState();
                case ROOK -> new RookState();
                default -> new DefaultPieceState();
            };
        }
        return state;
    }
}
