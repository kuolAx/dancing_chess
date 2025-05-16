package chess.dancing.pieces;

import chess.dancing.board.MoveValidator;
import chess.dancing.board.Square;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static chess.dancing.board.Square.A1;
import static chess.dancing.board.Square.A8;
import static chess.dancing.board.Square.B1;
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
import static chess.dancing.board.Square.G8;
import static chess.dancing.board.Square.H1;
import static chess.dancing.board.Square.H8;

@Getter
public enum PieceType {
    KING(1, c -> List.of(c == Color.WHITE ? E1 : E8)),

    QUEEN(1, c -> List.of(c == Color.WHITE ? D1 : D8)),

    ROOK(2, c -> List.of(
            c == Color.WHITE ? A1 : A8,
            c == Color.WHITE ? H1 : H8)),

    BISHOP(2, c -> Arrays.asList(
            c == Color.WHITE ? C1 : C8,
            c == Color.WHITE ? F1 : F8)),

    KNIGHT(2, c -> Arrays.asList(
            c == Color.WHITE ? B1 : B8,
            c == Color.WHITE ? G1 : G8)),

    PAWN(8, c -> {
        List<Square> squares = new ArrayList<>();
        int row = (c == Color.WHITE) ? 2 : 7;
        IntStream.rangeClosed(1, 8).forEach(col -> squares.add(Square.getByCoordinates(col, row)));
        return squares;
    });

    private final int numberOfPieces;
    private final Function<Color, List<Square>> startingPositionsGenerator;

    PieceType(int numberOfPieces, Function<Color, List<Square>> startingPositionsGenerator) {
        this.numberOfPieces = numberOfPieces;
        this.startingPositionsGenerator = startingPositionsGenerator;
    }

    public List<Square> getStartingSquares(Color color) {
        return startingPositionsGenerator.apply(color);
    }

    public MoveValidator getMoveValidator() {
        return moveValidator;
    }

}
