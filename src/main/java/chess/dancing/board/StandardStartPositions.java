package chess.dancing.board;

import chess.dancing.pieces.Color;

import java.util.List;

import static chess.dancing.board.Square.A1;
import static chess.dancing.board.Square.A8;
import static chess.dancing.board.Square.B1;
import static chess.dancing.board.Square.B2;
import static chess.dancing.board.Square.B3;
import static chess.dancing.board.Square.B4;
import static chess.dancing.board.Square.B5;
import static chess.dancing.board.Square.B6;
import static chess.dancing.board.Square.B7;
import static chess.dancing.board.Square.B8;
import static chess.dancing.board.Square.C1;
import static chess.dancing.board.Square.C8;
import static chess.dancing.board.Square.D1;
import static chess.dancing.board.Square.D8;
import static chess.dancing.board.Square.E1;
import static chess.dancing.board.Square.E8;
import static chess.dancing.board.Square.F1;
import static chess.dancing.board.Square.F8;
import static chess.dancing.board.Square.G1;
import static chess.dancing.board.Square.G2;
import static chess.dancing.board.Square.G3;
import static chess.dancing.board.Square.G4;
import static chess.dancing.board.Square.G5;
import static chess.dancing.board.Square.G6;
import static chess.dancing.board.Square.G7;
import static chess.dancing.board.Square.G8;
import static chess.dancing.board.Square.H1;
import static chess.dancing.board.Square.H8;
import static chess.dancing.pieces.Color.WHITE;

public class StandardStartPositions {
    public static final StartPositionsProvider KING = c -> List.of(isWhite(c) ? E1 : E8);

    public static final StartPositionsProvider QUEEN = c -> List.of(isWhite(c) ? D1 : D8);

    public static final StartPositionsProvider ROOK = c -> isWhite(c) ? List.of(A1, H1) : List.of(A8, H8);

    public static final StartPositionsProvider BISHOP = c -> isWhite(c) ? List.of(C1, F1) : List.of(C8, F8);

    public static final StartPositionsProvider KNIGHT = c -> isWhite(c) ? List.of(B1, G1) : List.of(B8, G8);

    public static final StartPositionsProvider PAWN =
            c -> isWhite(c) ? List.of(B1, B2, B3, B4, B5, B6, B7, B8) : List.of(G1, G2, G3, G4, G5, G6, G7, G8);

    private static boolean isWhite(Color c) {
        return c == WHITE;
    }

    private StandardStartPositions() {}
}
