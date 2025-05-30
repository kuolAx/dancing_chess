package com.kuolax.chess.core.model.move.validation;

import com.kuolax.chess.core.model.Board;
import com.kuolax.chess.core.model.Square;
import com.kuolax.chess.core.model.move.MoveType;
import com.kuolax.chess.core.model.piece.Piece;
import com.kuolax.chess.core.model.piece.PieceColor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.kuolax.chess.core.model.Square.A1;
import static com.kuolax.chess.core.model.Square.A8;
import static com.kuolax.chess.core.model.Square.B1;
import static com.kuolax.chess.core.model.Square.B8;
import static com.kuolax.chess.core.model.Square.C1;
import static com.kuolax.chess.core.model.Square.C8;
import static com.kuolax.chess.core.model.Square.D1;
import static com.kuolax.chess.core.model.Square.D8;
import static com.kuolax.chess.core.model.Square.F1;
import static com.kuolax.chess.core.model.Square.F8;
import static com.kuolax.chess.core.model.Square.G1;
import static com.kuolax.chess.core.model.Square.G8;
import static com.kuolax.chess.core.model.Square.H1;
import static com.kuolax.chess.core.model.Square.H8;
import static com.kuolax.chess.core.model.move.MoveType.KING_CASTLE_LONG;
import static com.kuolax.chess.core.model.move.MoveType.KING_CASTLE_SHORT;
import static com.kuolax.chess.core.model.move.MoveType.KING_MOVE;
import static com.kuolax.chess.core.model.move.MoveType.isKingMove;

public class KingMoveValidator extends AbstractMoveValidator {

    @Override
    protected List<MoveType> getLegalMoveTypes() {
        return List.of(KING_MOVE, KING_CASTLE_LONG, KING_CASTLE_SHORT);
    }

    @Override
    protected List<Square> getPotentialTargetSquares(Square from, Board board) {
        PieceColor c = board.getPieceAt(from).getColor();
        return Arrays.stream(Square.values())
                .filter(to -> isKingMove(from, to, c))
                .toList();
    }

    @Override
    public boolean canTakeOn(Piece piece, Square from, Square to, Board board) {
        return false;
    }

    @Override
    public boolean isLegalMove(Piece king, Square from, Square to, Board board) {
        PieceColor kingColor = king.getColor();
        MoveType kingMoveType = MoveType.determineKingMoveType(from, to, kingColor);
        if (kingMoveType == null) return false;

        boolean isLegalMove = switch (kingMoveType) {
            case KING_MOVE -> board.getPieceAt(to) == null || board.getPieceAt(to).getColor() != king.getColor();
            case KING_CASTLE_SHORT -> isCastlingConditionsMet(king, board, kingColor, KING_CASTLE_SHORT);
            case KING_CASTLE_LONG -> isCastlingConditionsMet(king, board, kingColor, KING_CASTLE_LONG);
            default -> false;
        };

        return isLegalMove && !board.movePutsKingInCheck(from, to);
    }

    private boolean isCastlingConditionsMet(Piece king, Board board, PieceColor kingColor, MoveType moveType) {
        if (king.isMoved()) return false;
        if (hasRookMovedBeforeCastling(board, kingColor, moveType)) return false;
        if (isCastlingPathBlocked(moveType, kingColor, board)) return false;
        if (board.isCheck(kingColor)) return false;
        return !castlingSquaresAreAttacked(board, kingColor, moveType);
    }

    private boolean hasRookMovedBeforeCastling(Board board, PieceColor color, MoveType moveType) {
        if (moveType == KING_CASTLE_SHORT) {
            return switch (color) {
                case WHITE -> {
                    Piece pieceAtH1 = board.getPieceAt(H1);
                    yield pieceAtH1 != null && pieceAtH1.isMoved();
                }
                case BLACK -> {
                    Piece pieceAtH8 = board.getPieceAt(H8);
                    yield pieceAtH8 != null && pieceAtH8.isMoved();
                }
            };
        } else if (moveType == KING_CASTLE_LONG) {
            return switch (color) {
                case WHITE -> {
                    Piece pieceAtA1 = board.getPieceAt(A1);
                    yield pieceAtA1 != null && pieceAtA1.isMoved();
                }
                case BLACK -> {
                    Piece pieceAtA8 = board.getPieceAt(A8);
                    yield pieceAtA8 != null && pieceAtA8.isMoved();
                }
            };
        }
        return false;
    }

    private boolean castlingSquaresAreAttacked(Board board, PieceColor color, MoveType moveType) {
        if (moveType == KING_CASTLE_SHORT) {
            return switch (color) {
                case WHITE -> board.canAnyPieceTakeOn(F1, color);
                case BLACK -> board.canAnyPieceTakeOn(F8, color);
            };
        } else if (moveType == KING_CASTLE_LONG) {
            return switch (color) {
                case WHITE -> board.canAnyPieceTakeOn(D1, color);
                case BLACK -> board.canAnyPieceTakeOn(D8, color);
            };
        }
        return false;
    }

    private boolean isCastlingPathBlocked(MoveType moveType, PieceColor color, Board board) {
        if (moveType == KING_CASTLE_SHORT) {
            return switch (color) {
                case WHITE -> Stream.of(F1, G1).anyMatch(board::hasPieceAt);
                case BLACK -> Stream.of(F8, G8).anyMatch(board::hasPieceAt);
            };
        } else if (moveType == KING_CASTLE_LONG) {
            return switch (color) {
                case WHITE -> Stream.of(D1, C1, B1).anyMatch(board::hasPieceAt);
                case BLACK -> Stream.of(D8, C8, B8).anyMatch(board::hasPieceAt);
            };
        }
        return false;
    }
}
