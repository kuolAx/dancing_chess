package com.kuolax.dancingchess.core;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Color;
import com.kuolax.dancingchess.pieces.Piece;
import com.kuolax.dancingchess.pieces.PieceType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kuolax.dancingchess.core.GameState.BLACK_WINS;
import static com.kuolax.dancingchess.core.GameState.DRAW;
import static com.kuolax.dancingchess.core.GameState.ONGOING;
import static com.kuolax.dancingchess.core.GameState.STALEMATE;
import static com.kuolax.dancingchess.core.GameState.WHITE_WINS;
import static com.kuolax.dancingchess.pieces.Color.BLACK;
import static com.kuolax.dancingchess.pieces.Color.WHITE;
import static com.kuolax.dancingchess.pieces.PieceType.PAWN;

@Getter
public class GameController {

    private final List<MoveRecord> moveHistory;
    private Board board;
    private Color currentPlayer;
    private int roundNumber;
    private GameState gameState;

    public GameController() {
        board = new Board();
        currentPlayer = WHITE;
        roundNumber = 1;
        gameState = ONGOING;
        moveHistory = new ArrayList<>();
    }

    public boolean isGameOver() {
        return gameState != ONGOING;
    }

    public boolean makeMove(Square from, Square to) {
        Piece piece = board.getPieceAt(from);
        if (piece == null || piece.getColor() != currentPlayer) return false;

        boolean moveSuccessful = board.movePiece(from, to, piece);

        if (moveSuccessful) {
            boolean isCheck = board.isChecked(currentPlayer);
            boolean hasLegalMoves = hasLegalMoves(currentPlayer);
            boolean isCheckMate = isCheck && !hasLegalMoves;
            boolean isPromotion = false;
            boolean isCastling = false;
            boolean isEnPassant = false;
            PieceType promotionType = null;

            // promotion, castling, en passant
            if (piece.getType() == PAWN && piece.getPosition().isOnLastRow(currentPlayer)) {
                promotionType = triggerPromotion(piece, piece.getPosition());
                isPromotion = true;
            } else if (board.isKingCastlingMove(from, to, piece)) {
                isCastling = true;
            } else if (false) {
                isEnPassant = true;
            }

            moveHistory.add(new MoveRecord(promotionType, isPromotion, isEnPassant, isCastling, isCheckMate,
                    isCheck, from, to, piece));

            switchPlayer();
            updateGameState(isCheck, hasLegalMoves, isCheckMate);
            return true;
        }
        return false;
    }

    public void resetGame() {
        board = new Board();
        currentPlayer = WHITE;
        roundNumber = 1;
        gameState = ONGOING;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == WHITE) ? BLACK : WHITE;
        if (currentPlayer == WHITE) roundNumber++;
    }

    private void updateGameState(boolean isCheck, boolean hasLegalMoves, boolean isCheckMate) {
        if (roundNumber >= 50 || isThreeMoveRepetition(moveHistory))
            gameState = DRAW;
        else if (isCheckMate)
            gameState = (currentPlayer == WHITE) ? BLACK_WINS : WHITE_WINS;
        else if (!isCheck && !hasLegalMoves)
            gameState = STALEMATE;
        else
            gameState = ONGOING;
    }

    private boolean isThreeMoveRepetition(List<MoveRecord> moveRecords) {
        return moveRecords.parallelStream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .parallelStream()
                .anyMatch(moveRecordEntry -> moveRecordEntry.getValue() >= 3);
    }

    private boolean hasLegalMoves(Color currentPlayer) {
        List<Piece> pieces = board.getPiecesByColor(currentPlayer);
        return pieces.parallelStream()
                .anyMatch(piece -> piece.hasLegalMoves(board));
    }

    private PieceType triggerPromotion(Piece pawn, Square position) {
        // todo implement user interaction
        PieceType promotionType = PieceType.QUEEN;
        board.promotePawn(pawn, promotionType, position);
        return promotionType;
    }
}
