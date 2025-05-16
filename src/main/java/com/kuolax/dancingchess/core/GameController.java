package com.kuolax.dancingchess.core;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Color;
import com.kuolax.dancingchess.pieces.Piece;
import lombok.Getter;

@Getter
public class GameController {

    private Board board;
    private Color currentPlayer;
    private int roundNumber;
    private GameState gameState;

    public GameController() {
        board = new Board();
        currentPlayer = Color.WHITE;
        roundNumber = 1;
        gameState = GameState.ONGOING;
    }

    public boolean makeMove(Square from, Square to) {
        Piece piece = board.getPieceAt(from);
        if (piece == null || piece.getColor() != currentPlayer) return false;

        boolean moveSuccessful = board.movePiece(from, to);

        if (moveSuccessful) {
            switchPlayer();
            if (currentPlayer == Color.WHITE) roundNumber++;
            updateGameState();
            return true;
        }
        return false;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private void updateGameState() {
        // Hier Logik für Spielstatus-Updates implementieren
        // z.B. Prüfen auf Schachmatt, Patt, etc.
        // Diese Methode sollte board.isCheck(), board.isCheckmate() etc. verwenden
    }

    public void resetGame() {
        board = new Board();
        currentPlayer = Color.WHITE;
        roundNumber = 1;
        gameState = GameState.ONGOING;
    }

    public enum GameState {
        ONGOING, CHECKMATE, STALEMATE, DRAW
    }
}
