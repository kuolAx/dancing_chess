package chess.dancing.pieces;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import chess.dancing.pieces.state.DefaultPieceState;
import chess.dancing.pieces.state.KingState;
import chess.dancing.pieces.state.PawnState;
import chess.dancing.pieces.state.PieceState;
import chess.dancing.pieces.state.RookState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Piece {

    private final String id;

    private final PieceType type;
    private final Color color;
    @Builder.Default
    private PieceState state = null;
    private Square square;

    private Piece dancePartner;

    private boolean isInUnion;

    public void resetDancePartner() {
        dancePartner = null;
    }

    public boolean canMoveTo(Square from, Square to, ChessBoard board) {
        return type.getMoveValidator().isLegalMove(this, from, to, board);
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
