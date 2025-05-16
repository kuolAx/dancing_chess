package com.kuolax.dancingchess.board;

import com.kuolax.dancingchess.pieces.PieceColor;
import lombok.Getter;

import java.util.Arrays;

import static com.kuolax.dancingchess.pieces.PieceColor.WHITE;
import static javafx.scene.paint.Color.BEIGE;
import static javafx.scene.paint.Color.SADDLEBROWN;

@Getter
public enum Square {
    A8(1, 8), B8(2, 8), C8(3, 8), D8(4, 8), E8(5, 8), F8(6, 8), G8(7, 8), H8(8, 8),
    A7(1, 7), B7(2, 7), C7(3, 7), D7(4, 7), E7(5, 7), F7(6, 7), G7(7, 7), H7(8, 7),
    A6(1, 6), B6(2, 6), C6(3, 6), D6(4, 6), E6(5, 6), F6(6, 6), G6(7, 6), H6(8, 6),
    A5(1, 5), B5(2, 5), C5(3, 5), D5(4, 5), E5(5, 5), F5(6, 5), G5(7, 5), H5(8, 5),
    A4(1, 4), B4(2, 4), C4(3, 4), D4(4, 4), E4(5, 4), F4(6, 4), G4(7, 4), H4(8, 4),
    A3(1, 3), B3(2, 3), C3(3, 3), D3(4, 3), E3(5, 3), F3(6, 3), G3(7, 3), H3(8, 3),
    A2(1, 2), B2(2, 2), C2(3, 2), D2(4, 2), E2(5, 2), F2(6, 2), G2(7, 2), H2(8, 2),
    A1(1, 1), B1(2, 1), C1(3, 1), D1(4, 1), E1(5, 1), F1(6, 1), G1(7, 1), H1(8, 1);

    public static final double STANDARD_SQUARE_SIZE = 85;
    private final int x;
    private final int y;
    private final javafx.scene.paint.Color squareColor;

    Square(int x, int y) {
        this.x = x;
        this.y = y;
        squareColor = determineSquareColor(x, y);
    }

    public static Square getByCoordinates(int x, int y) {
        return Arrays.stream(Square.values())
                .filter(s -> s.x == x && s.y == y)
                .findAny()
                .orElse(null);
    }

    public boolean isDiagonalTo(Square target) {
        int rowDiff = getXDiff(target);
        int columnDiff = getYDiff(target);

        return (rowDiff == columnDiff) && rowDiff > 0;
    }

    public boolean isHorizontalTo(Square square) {
        return y == square.y;
    }

    public boolean isVerticalTo(Square square) {
        return x == square.x;
    }

    public int getXDiff(Square square) {
        return Math.abs(x - square.x);
    }

    public int getYDiff(Square square) {
        return Math.abs(y - square.y);
    }

    public boolean isOnLastRow(PieceColor playerColor) {
        return playerColor == WHITE ? y == 8 : y == 1;
    }

    public double getSpawnX() {
        return x * STANDARD_SQUARE_SIZE;
    }

    public double getSpawnY() {
        // invert y-axis due to fxgl axis starting with 0,0 in the upper left corner
        return (8 - y) * STANDARD_SQUARE_SIZE;
    }

    private javafx.scene.paint.Color determineSquareColor(int x, int y) {
        boolean isLightSquare = (x + y) % 2 == 0;
        return isLightSquare ? BEIGE : SADDLEBROWN;
    }
}
