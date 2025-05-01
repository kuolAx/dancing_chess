package com.kuolax.dancingchess.board;

import com.kuolax.dancingchess.pieces.Piece;
import com.kuolax.dancingchess.pieces.PieceColor;
import com.kuolax.dancingchess.pieces.PieceType;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.kuolax.dancingchess.board.Square.A1;
import static com.kuolax.dancingchess.board.Square.A8;
import static com.kuolax.dancingchess.board.Square.C1;
import static com.kuolax.dancingchess.board.Square.C8;
import static com.kuolax.dancingchess.board.Square.D1;
import static com.kuolax.dancingchess.board.Square.D8;
import static com.kuolax.dancingchess.board.Square.E1;
import static com.kuolax.dancingchess.board.Square.E8;
import static com.kuolax.dancingchess.board.Square.F1;
import static com.kuolax.dancingchess.board.Square.F8;
import static com.kuolax.dancingchess.board.Square.G1;
import static com.kuolax.dancingchess.board.Square.G8;
import static com.kuolax.dancingchess.board.Square.H1;
import static com.kuolax.dancingchess.board.Square.H8;
import static com.kuolax.dancingchess.pieces.PieceColor.BLACK;
import static com.kuolax.dancingchess.pieces.PieceColor.WHITE;
import static com.kuolax.dancingchess.pieces.PieceType.KING;
import static com.kuolax.dancingchess.pieces.PieceType.PAWN;

public class Board {

    private final Map<Square, Piece> pieces = new EnumMap<>(Square.class);
    private boolean whiteChecked;
    private boolean blackChecked;

    public Board() {
        initializeBoard();
    }

    public void initializeBoard() {
        for (PieceColor color : PieceColor.values()) {
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
                            .position(square)
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
            piece.setPosition(to);

            // checks for legal castling are done in king move validation beforehand
            if (isKingCastlingMove(from, to, piece)) {
                return moveRookForCastling(to, piece);
            }

            updateCheckStatus();
            return true;
        }
        return false;
    }

    public Piece getPieceAt(Square at) {
        return pieces.get(at);
    }

    public boolean hasPieceAt(Square at) {
        return getPieceAt(at) != null;
    }

    public List<Piece> getPiecesByColor(PieceColor color) {
        return Arrays.stream(Square.values())
                .parallel()
                .filter(s -> getPieceAt(s) != null)
                .filter(s -> getPieceAt(s).getColor() == color)
                .map(this::getPieceAt)
                .toList();
    }

    public boolean isCheck(PieceColor playerColor) {
        Square kingSquare = getPiecesByColor(playerColor).parallelStream()
                .filter(piece -> piece.getType() == KING)
                .map(Piece::getPosition)
                .findAny()
                .orElse(null);

        return canAnyPieceTakeOn(kingSquare, playerColor);
    }

    public boolean canAnyPieceTakeOn(Square target, PieceColor playerColor) {
        if (target == null) return false;
        return getPiecesByColor((playerColor == WHITE) ? BLACK : WHITE).stream()
                .anyMatch(piece -> piece.canTakeOn(piece.getPosition(), target, this));
    }

    public boolean movePutsKingInCheck(Square from, Square to) {
        Piece piece = pieces.put(from, null);
        if (piece == null) return false;
        
        Piece cashedPiece = pieces.put(to, piece);

        boolean isKingInCheck = isCheck(piece.getColor());

        pieces.put(to, cashedPiece);
        pieces.put(from, piece);

        return isKingInCheck;
    }

    public boolean isChecked(PieceColor playerColor) {
        return (playerColor == WHITE) ? whiteChecked : blackChecked;
    }

    public void promotePawn(Piece pawn, PieceType desiredPromotionType, Square position) {
        if (pawn.getType() != PAWN) return;

        Piece promotedPiece = Piece.builder()
                .id(pawn.getId())
                .color(pawn.getColor())
                .type(desiredPromotionType)
                .position(position)
                .isInUnion(pawn.isInUnion())
                .dancePartner(pawn.getDancePartner())
                .build();

        pawn.getDancePartner().setDancePartner(promotedPiece);
        pieces.put(position, promotedPiece);
    }

    private void updateCheckStatus() {
        whiteChecked = isCheck(WHITE);
        blackChecked = isCheck(BLACK);
    }

    public boolean isKingCastlingMove(Square from, Square to, Piece piece) {
        if (piece.getType() != KING) return false;
        return (E1 == from && (C1 == to || G1 == to))
                || (E8 == from && (C8 == to || G8 == to));
    }

    private boolean moveRookForCastling(Square to, Piece piece) {
        return switch (piece.getColor()) {
            case WHITE -> {
                if (to == G1) yield movePiece(H1, F1, getPieceAt(H1));
                if (to == C1) yield movePiece(A1, D1, getPieceAt(A1));
                yield false;
            }
            case BLACK -> {
                if (to == G8) yield movePiece(H8, F8, getPieceAt(H8));
                if (to == C8) yield movePiece(A8, D8, getPieceAt(A8));
                yield false;
            }
        };
    }
}

