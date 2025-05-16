package chess.dancing.board;

import chess.dancing.pieces.Color;
import chess.dancing.pieces.Piece;
import chess.dancing.pieces.PieceType;
import lombok.Getter;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Getter
public class ChessBoard {
    private final Map<Square, Piece> pieces = new EnumMap<>(Square.class);

    public void initializeBoard() {
        for (Color color : Color.values()) {
            for (PieceType type : PieceType.values()) {
                List<Square> startingSquares = type.getStartingSquares(color);

                for (int i = 0; i < startingSquares.size(); i++) {
                    Square square = startingSquares.get(i);
                    String id = color.name().charAt(0)
                            + "_" + type.name()
                            + (type.getNumberOfPieces() > 1 ? "_" + (i + 1) : "");

                    Piece piece = Piece.builder()
                            .type(type)
                            .color(color)
                            .id(id)
                            .build();

                    pieces.put(square, piece);
                }
            }
        }
    }

    public boolean isLegalMove(Square from, Square to) {
        Piece piece = pieces.get(from);
        if (piece == null) return false;

        return piece.getType().getMoveValidator().isLegalMove(piece, from, to, this);
    }
}

