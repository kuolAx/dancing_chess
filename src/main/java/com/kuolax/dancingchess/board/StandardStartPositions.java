package com.kuolax.dancingchess.board;

import com.kuolax.dancingchess.pieces.PieceColor;

import java.util.List;

import static com.kuolax.dancingchess.board.Square.A1;
import static com.kuolax.dancingchess.board.Square.A2;
import static com.kuolax.dancingchess.board.Square.A7;
import static com.kuolax.dancingchess.board.Square.A8;
import static com.kuolax.dancingchess.board.Square.B1;
import static com.kuolax.dancingchess.board.Square.B2;
import static com.kuolax.dancingchess.board.Square.B7;
import static com.kuolax.dancingchess.board.Square.B8;
import static com.kuolax.dancingchess.board.Square.C1;
import static com.kuolax.dancingchess.board.Square.C2;
import static com.kuolax.dancingchess.board.Square.C7;
import static com.kuolax.dancingchess.board.Square.C8;
import static com.kuolax.dancingchess.board.Square.D1;
import static com.kuolax.dancingchess.board.Square.D2;
import static com.kuolax.dancingchess.board.Square.D7;
import static com.kuolax.dancingchess.board.Square.D8;
import static com.kuolax.dancingchess.board.Square.E1;
import static com.kuolax.dancingchess.board.Square.E2;
import static com.kuolax.dancingchess.board.Square.E7;
import static com.kuolax.dancingchess.board.Square.E8;
import static com.kuolax.dancingchess.board.Square.F1;
import static com.kuolax.dancingchess.board.Square.F2;
import static com.kuolax.dancingchess.board.Square.F7;
import static com.kuolax.dancingchess.board.Square.F8;
import static com.kuolax.dancingchess.board.Square.G1;
import static com.kuolax.dancingchess.board.Square.G2;
import static com.kuolax.dancingchess.board.Square.G7;
import static com.kuolax.dancingchess.board.Square.G8;
import static com.kuolax.dancingchess.board.Square.H1;
import static com.kuolax.dancingchess.board.Square.H2;
import static com.kuolax.dancingchess.board.Square.H7;
import static com.kuolax.dancingchess.board.Square.H8;
import static com.kuolax.dancingchess.pieces.PieceColor.WHITE;

public class StandardStartPositions {
    public static final StartPositionsProvider KING = c -> List.of(isWhite(c) ? E1 : E8);

    public static final StartPositionsProvider QUEEN = c -> List.of(isWhite(c) ? D1 : D8);

    public static final StartPositionsProvider ROOK = c -> isWhite(c) ? List.of(A1, H1) : List.of(A8, H8);

    public static final StartPositionsProvider BISHOP = c -> isWhite(c) ? List.of(C1, F1) : List.of(C8, F8);

    public static final StartPositionsProvider KNIGHT = c -> isWhite(c) ? List.of(B1, G1) : List.of(B8, G8);

    public static final StartPositionsProvider PAWN =
            c -> isWhite(c) ? List.of(A2, B2, C2, D2, E2, F2, G2, H2) : List.of(A7, B7, C7, D7, E7, F7, G7, H7);

    private StandardStartPositions() {
    }

    private static boolean isWhite(PieceColor c) {
        return c == WHITE;
    }
}
