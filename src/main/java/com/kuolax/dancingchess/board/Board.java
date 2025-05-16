package com.kuolax.dancingchess.board;

import com.kuolax.dancingchess.pieces.Color;
import com.kuolax.dancingchess.pieces.Piece;
import com.kuolax.dancingchess.pieces.PieceType;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.kuolax.dancingchess.pieces.Color.BLACK;
import static com.kuolax.dancingchess.pieces.Color.WHITE;
import static com.kuolax.dancingchess.pieces.PieceType.KING;

public class Board {

    private final Map<Square, Piece> pieces = new EnumMap<>(Square.class);
    private boolean whiteChecked;
    private boolean blackChecked;

    public Board() {
        initializeBoard();
    }

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

    public boolean movePiece(Square from, Square to, Piece piece) {
        if (piece.isLegalMove(from, to, this)) {
            pieces.put(from, null);
            pieces.put(to, piece);
            piece.setMoved(true);
            piece.setPosition(to);

            updateCheckStatus();
            return true;
        }
        return false;
    }

    public Piece getPieceAt(Square at) {
        return pieces.get(at);
    }

    public List<Piece> getPiecesByColor(Color color) {
        return Arrays.stream(Square.values())
                .parallel()
                .filter(s -> getPieceAt(s) != null)
                .filter(s -> getPieceAt(s).getColor() == color)
                .map(this::getPieceAt)
                .toList();
    }

    public boolean isCheck(Color playerColor) {
        Square kingSquare = getPiecesByColor(playerColor).parallelStream()
                .filter(piece -> piece.getType() == KING)
                .map(Piece::getPosition)
                .findAny()
                .orElse(null);

        return canAnyPieceTakeOn(kingSquare, playerColor);
    }

    public boolean canAnyPieceTakeOn(Square target, Color playerColor) {
        if (target == null) return false;
        return getPiecesByColor((playerColor == WHITE) ? BLACK : WHITE).stream()
                .anyMatch(piece -> piece.canTakeOn(piece.getPosition(), target, this));
    }

    public boolean wouldMovePutKingInCheck(Square from, Square to, Piece piece) {
        simulateMove(from, to);
        boolean isKingInCheck = isCheck(piece.getColor());
        simulateMove(to, from);
        return isKingInCheck;
    }

    private void updateCheckStatus() {
        whiteChecked = isCheck(WHITE);
        blackChecked = isCheck(BLACK);
    }

    public boolean isChecked(Color playerColor) {
        return (playerColor == WHITE) ? whiteChecked : blackChecked;
    }

    private void simulateMove(Square from, Square to) {
        Piece piece = pieces.put(from, null);
        if (piece == null) return;
        pieces.put(to, piece);
    }
}

