package com.kuolax.dancingchess.core;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;
import com.kuolax.dancingchess.pieces.PieceColor;
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
import static com.kuolax.dancingchess.pieces.PieceColor.BLACK;
import static com.kuolax.dancingchess.pieces.PieceColor.WHITE;
import static com.kuolax.dancingchess.pieces.PieceType.PAWN;

@Getter
public class GameController {

    private final List<MoveRecord> moveHistory;
    private Board board;
    private PieceColor currentPlayer;
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

        System.out.println("Trying to move + " + piece.getId() + " from " + from + " to " + to);
        boolean moveSuccessful = board.movePiece(from, to, piece);

        if (moveSuccessful) {
            System.out.println("Moved + " + piece.getId() + "from" + from + " to " + to);
            boolean isCheck = board.isChecked(currentPlayer);
            boolean hasLegalMoves = hasLegalMoves(currentPlayer);
            boolean isCheckMate = isCheck && !hasLegalMoves;
            boolean isPromotion = false;
            boolean isCastling = false;
            boolean isEnPassant = false;
            PieceType promotionType = null;

            // promotion, castling, en passant
            if (canPromote(piece, to)) {
                // promotionType = triggerPromotion(piece, piece.getPosition());
                // todo put moveGeneration for History in Application - better access to promotion type
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

    private boolean hasLegalMoves(PieceColor currentPlayer) {
        List<Piece> pieces = board.getPiecesByColor(currentPlayer);
        return pieces.parallelStream()
                .anyMatch(piece -> piece.hasLegalMoves(board));
    }

    public boolean canPromote(Piece piece, Square to) {
        return piece.getType() == PAWN && to.isOnLastRow(currentPlayer);
    }
}
