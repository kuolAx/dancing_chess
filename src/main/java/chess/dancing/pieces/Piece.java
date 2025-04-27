package chess.dancing.pieces;

import chess.dancing.board.ChessBoard;
import chess.dancing.board.Square;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Piece {

    private final String id;

    private final PieceType pieceType;

    private Color color;

    private Square square;

    private Piece dancePartner;

    private boolean isInUnion;

    public void resetDancePartner() {
        dancePartner = null;
    }

    public boolean canMoveTo(Square from, Square to, ChessBoard board) {
        // Delegiere an die Strategie des jeweiligen Figurtyps
        return pieceType.getMoveValidator().isValidMove(this, from, to, board);
    }
}
