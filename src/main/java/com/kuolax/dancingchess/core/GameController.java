package com.kuolax.dancingchess.core;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Move;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;
import com.kuolax.dancingchess.pieces.PieceColor;
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

@Getter
public class GameController {

    private List<Move> moveHistory;
    private Board board;
    private PieceColor currentPlayer;
    private PieceColor opponent;
    private int roundNumber;
    private GameState gameState;

    public GameController() {
        board = new Board();
        currentPlayer = WHITE;
        opponent = BLACK;
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
            switchPlayer();
            Move lastMove = board.getLastMove();
            moveHistory.add(lastMove);
            updateGameState(lastMove);
            return true;
        }
        return false;
    }

    public void resetGame() {
        board = new Board();
        currentPlayer = WHITE;
        opponent = BLACK;
        roundNumber = 1;
        gameState = ONGOING;
        moveHistory = new ArrayList<>();
    }

    private void switchPlayer() {
        currentPlayer = opponent;
        opponent = currentPlayer.getOpponent();
        if (currentPlayer == WHITE) roundNumber++;
    }

    private void updateGameState(Move lastMove) {
        if (roundNumber >= 50 || isThreeMoveRepetition(moveHistory))
            gameState = DRAW;
        else if (lastMove.isCheckmate())
            gameState = (currentPlayer == WHITE) ? BLACK_WINS : WHITE_WINS;
        else if (lastMove.isStaleMate())
            gameState = STALEMATE;
        else
            gameState = ONGOING;
    }

    private boolean isThreeMoveRepetition(List<Move> moves) {
        return moves.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .anyMatch(moveEntry -> moveEntry.getValue() >= 3);
    }
}
