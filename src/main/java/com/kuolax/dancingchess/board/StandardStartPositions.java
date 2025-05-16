package com.kuolax.dancingchess.board;

import com.kuolax.dancingchess.pieces.Color;

import java.util.List;

import static com.kuolax.dancingchess.board.Square.A1;
import static com.kuolax.dancingchess.board.Square.A2;
import static com.kuolax.dancingchess.board.Square.A3;
import static com.kuolax.dancingchess.board.Square.A4;
import static com.kuolax.dancingchess.board.Square.A5;
import static com.kuolax.dancingchess.board.Square.A6;
import static com.kuolax.dancingchess.board.Square.A7;
import static com.kuolax.dancingchess.board.Square.A8;
import static com.kuolax.dancingchess.board.Square.B1;
import static com.kuolax.dancingchess.board.Square.B2;
import static com.kuolax.dancingchess.board.Square.B3;
import static com.kuolax.dancingchess.board.Square.B4;
import static com.kuolax.dancingchess.board.Square.B5;
import static com.kuolax.dancingchess.board.Square.B6;
import static com.kuolax.dancingchess.board.Square.B7;
import static com.kuolax.dancingchess.board.Square.B8;
import static com.kuolax.dancingchess.board.Square.G1;
import static com.kuolax.dancingchess.board.Square.G2;
import static com.kuolax.dancingchess.board.Square.G3;
import static com.kuolax.dancingchess.board.Square.G4;
import static com.kuolax.dancingchess.board.Square.G5;
import static com.kuolax.dancingchess.board.Square.G6;
import static com.kuolax.dancingchess.board.Square.G7;
import static com.kuolax.dancingchess.board.Square.G8;
import static com.kuolax.dancingchess.board.Square.H1;
import static com.kuolax.dancingchess.board.Square.H2;
import static com.kuolax.dancingchess.board.Square.H3;
import static com.kuolax.dancingchess.board.Square.H4;
import static com.kuolax.dancingchess.board.Square.H5;
import static com.kuolax.dancingchess.board.Square.H6;
import static com.kuolax.dancingchess.board.Square.H7;
import static com.kuolax.dancingchess.board.Square.H8;
import static com.kuolax.dancingchess.pieces.Color.WHITE;

public class StandardStartPositions {
    public static final StartPositionsProvider KING = c -> List.of(isWhite(c) ? A5 : H5);

    public static final StartPositionsProvider QUEEN = c -> List.of(isWhite(c) ? A4 : H4);

    public static final StartPositionsProvider ROOK = c -> isWhite(c) ? List.of(A1, H1) : List.of(A8, H8);

    public static final StartPositionsProvider BISHOP = c -> isWhite(c) ? List.of(A3, A6) : List.of(H3, H6);

    public static final StartPositionsProvider KNIGHT = c -> isWhite(c) ? List.of(A2, A7) : List.of(H2, H7);

    public static final StartPositionsProvider PAWN =
            c -> isWhite(c) ? List.of(B1, B2, B3, B4, B5, B6, B7, B8) : List.of(G1, G2, G3, G4, G5, G6, G7, G8);

    private StandardStartPositions() {
    }

    private static boolean isWhite(Color c) {
        return c == WHITE;
    }
}
