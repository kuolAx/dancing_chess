package com.kuolax.chess.core.game;

import com.kuolax.chess.core.model.Board;
import com.kuolax.chess.core.model.GameState;
import com.kuolax.chess.core.model.Square;
import com.kuolax.chess.core.model.move.Move;
import com.kuolax.chess.core.model.piece.Piece;
import com.kuolax.chess.core.model.piece.PieceColor;
import com.kuolax.chess.core.model.piece.PieceType;
import com.kuolax.chess.util.ZobristHash;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kuolax.chess.core.model.GameState.BLACK_WINS;
import static com.kuolax.chess.core.model.GameState.DRAW_BY_REPETITION;
import static com.kuolax.chess.core.model.GameState.ONGOING;
import static com.kuolax.chess.core.model.GameState.STALEMATE;
import static com.kuolax.chess.core.model.GameState.WHITE_WINS;
import static com.kuolax.chess.core.model.piece.PieceColor.BLACK;
import static com.kuolax.chess.core.model.piece.PieceColor.WHITE;

@Getter
public class GameController {

    private List<Move> moveHistory;
    private Board board;
    private PieceColor currentPlayer;
    private PieceColor opponent;
    private int roundNumber;
    private GameState gameState;

    // variables necessary for zobrist hashing of positions for threefold repetition detection
    private final Map<Long, Integer> positionHistory;
    private boolean whiteCanCastleKingSide = true;
    private boolean whiteCanCastleQueenSide = true;
    private boolean blackCanCastleKingSide = true;
    private boolean blackCanCastleQueenSide = true;
    private Integer enPassantFile = null;
    @Getter
    private long currentPositionHash;

    public GameController() {
        board = new Board();
        currentPlayer = WHITE;
        opponent = BLACK;
        roundNumber = 1;
        gameState = ONGOING;
        moveHistory = new ArrayList<>();
        positionHistory = new HashMap<>();
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
            updateZobristHash(lastMove);
            updateGameState(lastMove);
            return true;
        }
        return false;
    }

    public boolean canUndo() {
        return !moveHistory.isEmpty();
    }

    public boolean undoLastMove() {
        if (canUndo()) return false;

        Move lastMove = moveHistory.removeLast();
        board.undoMove(lastMove);

        //create inverted Move for zobrist Hashing?
        updateZobristHash(lastMove);
        // remove zobrist hash counter for current hash

        // further undo logic

        return true;
    }

    public void resetGame() {
        board = new Board();
        currentPlayer = WHITE;
        opponent = BLACK;
        roundNumber = 1;
        gameState = ONGOING;
        moveHistory = new ArrayList<>();
    }

    public void updateLastMoveForPromotion() {
        moveHistory.removeLast();
        moveHistory.add(board.getLastMove());
    }

    private void switchPlayer() {
        currentPlayer = opponent;
        opponent = currentPlayer.getOpponent();
        if (currentPlayer == WHITE) roundNumber++;
    }

    private void updateGameState(Move lastMove) {
        if (isThreefoldRepetition(positionHistory))
            gameState = DRAW_BY_REPETITION;
        else if (lastMove.isCheckmate())
            gameState = (currentPlayer == WHITE) ? BLACK_WINS : WHITE_WINS;
        else if (lastMove.isStaleMate())
            gameState = STALEMATE;
        else
            gameState = ONGOING;
    }

    private boolean isThreefoldRepetition(Map<Long, Integer> positionHistory) {
        return positionHistory.getOrDefault(currentPositionHash, 0) >= 3;
    }

    private void updateEnPassant(Move move) {
        enPassantFile = null;
        if (move.enPassantTarget() != null) enPassantFile = move.enPassantTarget().getX();
    }

    private void recordCurrentPosition() {
        positionHistory.merge(currentPositionHash, 1, Integer::sum);
    }

    private void updateZobristHash(Move lastMove) {
        boolean oldWhiteKingSide = whiteCanCastleKingSide;
        boolean oldWhiteQueenSide = whiteCanCastleQueenSide;
        boolean oldBlackKingSide = blackCanCastleKingSide;
        boolean oldBlackQueenSide = blackCanCastleQueenSide;
        Integer oldEnPassantFile = enPassantFile;
        updateCastlingRights(lastMove);
        updateEnPassant(lastMove);

        currentPositionHash = ZobristHash.updateHashForMove(
                currentPositionHash,
                lastMove.from().getY(), lastMove.from().getX(),
                lastMove.to().getY(), lastMove.to().getX(),
                lastMove.movedPiece(), lastMove.capturedPiece()
        );
        currentPositionHash = ZobristHash.updateHashForCastlingChange(
                currentPositionHash,
                oldWhiteKingSide, oldWhiteQueenSide, oldBlackKingSide, oldBlackQueenSide,
                whiteCanCastleKingSide, whiteCanCastleQueenSide,
                blackCanCastleKingSide, blackCanCastleQueenSide
        );
        currentPositionHash = ZobristHash.updateHashForEnPassantChange(
                currentPositionHash, oldEnPassantFile, enPassantFile
        );
        currentPositionHash = ZobristHash.updateHashForPlayerChange(currentPositionHash);

        recordCurrentPosition();
    }

    // update castling rights based on last move
    private void updateCastlingRights(Move move) {
        Piece piece = move.movedPiece();
        PieceColor playerColor = move.playerColor();
        int fromRow = move.from().getY();
        int fromCol = move.from().getX();
        int toRow = move.to().getY();
        int toCol = move.to().getX();

        // king moves - lose all castling rights for that player
        updateCastlingAfterKingMoved(piece, playerColor);
        // rook moves - lose castling rights for that side
        updateCastlingAfterRookMoved(piece, playerColor, fromRow, fromCol);
        // capture of rook - lose castling rights
        updateCastlingAfterRookGotCaptured(toRow, toCol);
    }

    private void updateCastlingAfterKingMoved(Piece piece, PieceColor playerColor) {
        if (piece.getType() != PieceType.KING) return;

        if (playerColor == PieceColor.WHITE) {
            whiteCanCastleKingSide = false;
            whiteCanCastleQueenSide = false;
        } else {
            blackCanCastleKingSide = false;
            blackCanCastleQueenSide = false;
        }
    }

    private void updateCastlingAfterRookMoved(Piece piece, PieceColor playerColor, int fromRow, int fromCol) {
        if (piece.getType() != PieceType.ROOK) return;

        if (playerColor == PieceColor.WHITE) {
            if (fromRow == 8 && fromCol == 1) whiteCanCastleQueenSide = false;
            else if (fromRow == 8 && fromCol == 8) whiteCanCastleKingSide = false;
        } else {
            if (fromRow == 1 && fromCol == 1) blackCanCastleQueenSide = false;
            else if (fromRow == 1 && fromCol == 8) blackCanCastleKingSide = false;
        }
    }

    private void updateCastlingAfterRookGotCaptured(int toRow, int toCol) {
        if (toRow == 1 && toCol == 1) blackCanCastleQueenSide = false;
        else if (toRow == 1 && toCol == 8) blackCanCastleKingSide = false;
        else if (toRow == 8 && toCol == 1) whiteCanCastleQueenSide = false;
        else if (toRow == 8 && toCol == 8) whiteCanCastleKingSide = false;
    }

    // recalculate complete position hash (fallback method)
    private void updateCurrentPositionHash() {
        currentPositionHash = ZobristHash.calculateZobristHash(
                board, currentPlayer,
                whiteCanCastleKingSide, whiteCanCastleQueenSide,
                blackCanCastleKingSide, blackCanCastleQueenSide,
                enPassantFile
        );
    }
}
