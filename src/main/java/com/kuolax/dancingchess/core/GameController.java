package com.kuolax.dancingchess.core;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Color;
import com.kuolax.dancingchess.pieces.Piece;
import lombok.Getter;

import java.util.List;

import static com.kuolax.dancingchess.core.GameController.GameState.BLACK_WINS;
import static com.kuolax.dancingchess.core.GameController.GameState.WHITE_WINS;
import static com.kuolax.dancingchess.pieces.Color.BLACK;
import static com.kuolax.dancingchess.pieces.Color.WHITE;

@Getter
public class GameController {

    private Board board;
    private Color currentPlayer;
    private int roundNumber;
    private GameState gameState;

    public GameController() {
        board = new Board();
        currentPlayer = WHITE;
        roundNumber = 1;
        gameState = GameState.ONGOING;
    }

    public boolean isGameOver() {
        return gameState != GameState.ONGOING;
    }

    public boolean makeMove(Square from, Square to) {
        Piece piece = board.getPieceAt(from);
        if (piece == null || piece.getColor() != currentPlayer) return false;

        boolean moveSuccessful = board.movePiece(from, to, piece);

        if (moveSuccessful) {
            switchPlayer();
            if (currentPlayer == WHITE) roundNumber++;
            updateGameState();
            return true;
        }
        return false;
    }

    public void resetGame() {
        board = new Board();
        currentPlayer = WHITE;
        roundNumber = 1;
        gameState = GameState.ONGOING;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == WHITE) ? BLACK : WHITE;
    }

    private void updateGameState() {
        boolean isInCheck = board.isChecked(currentPlayer);
        boolean hasLegalMoves = hasLegalMoves(currentPlayer);

        if (isInCheck && !hasLegalMoves) {
            gameState = (currentPlayer == WHITE) ? BLACK_WINS : WHITE_WINS;
        } else if (!isInCheck && !hasLegalMoves) {
            gameState = GameState.STALEMATE;
        } else {
            gameState = GameState.ONGOING;
        }

        // draw
        // 50 move rule
        // 3 time position repeat
    }

    private boolean hasLegalMoves(Color currentPlayer) {
        List<Piece> pieces = board.getPiecesByColor(currentPlayer);
        return pieces.parallelStream()
                .anyMatch(piece -> piece.hasLegalMoves(board));
    }

    public enum GameState {
        ONGOING, WHITE_WINS, BLACK_WINS, STALEMATE, DRAW
    }
}
