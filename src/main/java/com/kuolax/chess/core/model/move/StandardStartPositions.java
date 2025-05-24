package com.kuolax.chess.core.model.move;

import com.kuolax.chess.core.model.piece.PieceColor;

import java.util.List;

import static com.kuolax.chess.core.model.Square.A1;
import static com.kuolax.chess.core.model.Square.A2;
import static com.kuolax.chess.core.model.Square.A7;
import static com.kuolax.chess.core.model.Square.A8;
import static com.kuolax.chess.core.model.Square.B1;
import static com.kuolax.chess.core.model.Square.B2;
import static com.kuolax.chess.core.model.Square.B7;
import static com.kuolax.chess.core.model.Square.B8;
import static com.kuolax.chess.core.model.Square.C1;
import static com.kuolax.chess.core.model.Square.C2;
import static com.kuolax.chess.core.model.Square.C7;
import static com.kuolax.chess.core.model.Square.C8;
import static com.kuolax.chess.core.model.Square.D1;
import static com.kuolax.chess.core.model.Square.D2;
import static com.kuolax.chess.core.model.Square.D7;
import static com.kuolax.chess.core.model.Square.D8;
import static com.kuolax.chess.core.model.Square.E1;
import static com.kuolax.chess.core.model.Square.E2;
import static com.kuolax.chess.core.model.Square.E7;
import static com.kuolax.chess.core.model.Square.E8;
import static com.kuolax.chess.core.model.Square.F1;
import static com.kuolax.chess.core.model.Square.F2;
import static com.kuolax.chess.core.model.Square.F7;
import static com.kuolax.chess.core.model.Square.F8;
import static com.kuolax.chess.core.model.Square.G1;
import static com.kuolax.chess.core.model.Square.G2;
import static com.kuolax.chess.core.model.Square.G7;
import static com.kuolax.chess.core.model.Square.G8;
import static com.kuolax.chess.core.model.Square.H1;
import static com.kuolax.chess.core.model.Square.H2;
import static com.kuolax.chess.core.model.Square.H7;
import static com.kuolax.chess.core.model.Square.H8;
import static com.kuolax.chess.core.model.piece.PieceColor.WHITE;

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
