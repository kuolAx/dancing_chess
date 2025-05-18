package com.kuolax.dancingchess.board;

import com.kuolax.dancingchess.moves.MoveType;
import com.kuolax.dancingchess.moves.PawnMoveValidator;
import com.kuolax.dancingchess.pieces.Piece;
import com.kuolax.dancingchess.pieces.PieceColor;
import com.kuolax.dancingchess.pieces.PieceType;
import lombok.Getter;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @Getter
    private Move lastMove;
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

    public boolean movePiece(Square from, Square to, Piece movingPiece) {
        if (movingPiece.isLegalMove(from, to, this)) {
            pieces.put(from, null);
            Piece takenPiece = pieces.put(to, movingPiece);

            movingPiece.setPosition(to);
            movingPiece.setMoved(true);

            // checks for legal castling are done in king move validation beforehand
            if (isKingCastlingMove(from, to, movingPiece)) castleRook(to, movingPiece.getColor());
            if (isEnPassantCapture(from, to, movingPiece)) pieces.put(getEnPassantTarget(), null);

            updateCheckStatus();
            updateLastMove(movingPiece, from, to, takenPiece, null);

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

    public List<Piece> getPiecesByColor(PieceColor playerColor) {
        return Arrays.stream(Square.values())
                .map(this::getPieceAt)
                .filter(Objects::nonNull)
                .filter(piece -> piece.getColor() == playerColor)
                .toList();
    }

    public boolean isCheck(PieceColor playerColor) {
        Square kingSquare = getKingSquare(playerColor);
        return canAnyPieceTakeOn(kingSquare, playerColor);
    }

    public boolean canAnyPieceTakeOn(Square target, PieceColor playerColor) {
        if (target == null) return false;
        return getPiecesByColor((playerColor == WHITE) ? BLACK : WHITE).stream()
                .anyMatch(piece -> piece.canTakeOn(piece.getPosition(), target, this));
    }

    public boolean movePutsKingInCheck(Square from, Square to) {
        // simulate move and determine check status for new position
        Piece piece = pieces.put(from, null);
        if (piece == null) return false;

        Piece cashedPiece = pieces.put(to, piece);
        piece.setPosition(to);

        boolean isKingInCheck = isCheck(piece.getColor());

        // reset simulated move
        pieces.put(to, cashedPiece);
        pieces.put(from, piece);
        piece.setPosition(from);

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

        Piece dancePartner = pawn.getDancePartner();
        if (dancePartner != null) dancePartner.setDancePartner(promotedPiece);

        pieces.put(position, promotedPiece);

        // update check status, since the promoted pawn might attack the enemy king square now
        updateCheckStatus();

        // overwrite lastMove with promotion specific context
        Square from = Square.getByCoordinates(pawn.getPosition().getX(), pawn.getPosition().getY() - PawnMoveValidator.getPawnMoveDirection(pawn.getColor()));
        updateLastMove(pawn, from, pawn.getPosition(), null, desiredPromotionType);
    }

    public Square getKingSquare(PieceColor playerColor) {
        return getPiecesByColor(playerColor).stream()
                .filter(piece -> piece.getType() == KING)
                .map(Piece::getPosition)
                .findAny()
                .orElse(null);
    }

    public boolean hasNoLegalMoves(PieceColor playerColor) {
        List<Piece> pieceList = getPiecesByColor(playerColor);
        return pieceList.stream()
                .noneMatch(piece -> piece.hasLegalMoves(this));
    }

    public boolean isKingCastlingMove(Square from, Square to, Piece piece) {
        if (piece.getType() != KING) return false;
        return (E1 == from && (C1 == to || G1 == to))
                || (E8 == from && (C8 == to || G8 == to));
    }

    public Square getEnPassantTarget() {
        if (lastMove != null) return lastMove.enPassantTarget();
        return null;
    }

    private boolean canPromote(Piece piece, Square to) {
        return piece.getType() == PAWN && to.isOnLastRow(piece.getColor());
    }

    private void updateCheckStatus() {
        whiteChecked = isCheck(WHITE);
        blackChecked = isCheck(BLACK);
    }

    private void updateLastMove(Piece movingPiece, Square from, Square to, Piece takenPiece, PieceType promotionType) {
        PieceColor playerColor = movingPiece.getColor();
        PieceColor opponent = playerColor.getOpponent();

        boolean isTakingMove = takenPiece != null;
        boolean isCheck = isChecked(opponent);
        boolean isCastling = isKingCastlingMove(from, to, movingPiece);
        boolean isEnPassantCapture = isEnPassantCapture(from, to, movingPiece);
        boolean hasNoLegalMoves = hasNoLegalMoves(opponent);
        Square enPassantSquare = (movingPiece.getType() == PAWN) && (from.getYDiff(to)) == 2 ? movingPiece.getPosition() : null;

        lastMove = new Move(movingPiece, from, to, isTakingMove, isCheck, isCastling, isCheck && hasNoLegalMoves,
                !isCheck && hasNoLegalMoves, isEnPassantCapture, enPassantSquare, canPromote(movingPiece, to), promotionType);
    }

    private boolean isEnPassantCapture(Square from, Square to, Piece movingPiece) {
        if (lastMove != null && lastMove.enPassantTarget() != null && movingPiece.getType() == PAWN) {
            MoveType pawnMoveType = MoveType.determinePawnMoveType(from, to, PawnMoveValidator.getPawnMoveDirection(movingPiece.getColor()), getEnPassantTarget());
            return pawnMoveType == MoveType.PAWN_EN_PASSANT;
        }
        return false;
    }

    private void castleRook(Square to, PieceColor playerColor) {
        if (Objects.requireNonNull(playerColor) == WHITE) {
            if (to == G1) moveAndUpdateRook(H1, F1);
            if (to == C1) moveAndUpdateRook(A1, D1);
        } else if (playerColor == BLACK) {
            if (to == G8) moveAndUpdateRook(H8, F8);
            if (to == C8) moveAndUpdateRook(A8, D8);
        }
    }

    private void moveAndUpdateRook(Square rookFrom, Square rookTo) {
        Piece rook = getPieceAt(rookFrom);
        pieces.put(rookFrom, null);
        pieces.put(rookTo, rook);

        rook.setPosition(rookTo);
        rook.setMoved(true);
    }
}

