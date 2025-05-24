package com.kuolax.chess.util;

import com.kuolax.chess.core.model.Board;
import com.kuolax.chess.core.model.Square;
import com.kuolax.chess.core.model.piece.Piece;
import com.kuolax.chess.core.model.piece.PieceColor;

import java.util.Random;

/**
 * Zobrist Hashing implementation for position identification.
 * Used for threefold repetition detection and transposition tables.
 */
public class ZobristHash {
    // hash arrays
    private static final long[][][] PIECE_KEYS = new long[8][8][12];    // [row][col][piece_type]
    private static final long[] CASTLING_KEYS = new long[16];           // 4 bits for castling rights
    private static final long[] EN_PASSANT_KEYS = new long[8];          // for each file
    private static final long BLACK_TO_MOVE_KEY;

    // piece mapping for array indexing
    private static final int WHITE_PAWN = 0;
    private static final int WHITE_ROOK = 1;
    private static final int WHITE_KNIGHT = 2;
    private static final int WHITE_BISHOP = 3;
    private static final int WHITE_QUEEN = 4;
    private static final int WHITE_KING = 5;

    static {
        // initialize with fixed seed for consistent hashes across program runs
        Random random = new Random(8012787123L);

        // initialize piece random keys
        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                for (int piece = 0; piece < 12; piece++) {
                    PIECE_KEYS[col][row][piece] = random.nextLong();
                }
            }
        }

        // initialize castling keys (for 16 possible combinations of KQkq)
        for (int i = 0; i < 16; i++) {
            CASTLING_KEYS[i] = random.nextLong();
        }

        // initialize en passant keys (one for each file)
        for (int file = 0; file < 8; file++) {
            EN_PASSANT_KEYS[file] = random.nextLong();
        }

        BLACK_TO_MOVE_KEY = random.nextLong();
    }

    private ZobristHash() {
        // util class with only static methods shall not be instantiated
    }

    public static long calculateZobristHash(Board board, PieceColor currentPlayer,
                                            boolean whiteCanCastleKingSide, boolean whiteCanCastleQueenSide,
                                            boolean blackCanCastleKingSide, boolean blackCanCastleQueenSide,
                                            Integer enPassantFile) {
        long hash = 0L;

        hash ^= calculateBoardHash(board);

        if (currentPlayer == PieceColor.BLACK) {
            hash ^= BLACK_TO_MOVE_KEY;
        }

        hash ^= calculateCastlingHash(whiteCanCastleKingSide, whiteCanCastleQueenSide, blackCanCastleKingSide, blackCanCastleQueenSide);

        if (enPassantFile != null && enPassantFile >= 0 && enPassantFile < 8) {
            hash ^= EN_PASSANT_KEYS[enPassantFile];
        }

        return hash;
    }

    public static long calculateBoardHash(Board board) {
        long hash = 0L;

        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                Piece piece = board.getPieceAt(Square.getByCoordinates(col, row));
                if (piece != null) {
                    int pieceIndex = getPieceIndex(piece);
                    hash ^= PIECE_KEYS[col][row][pieceIndex];
                }
            }
        }

        return hash;
    }

    /* Incrementally update hash when a piece is moved.
     * Usable for moves and undo moves.
     */
    public static long updateHashForMove(long currentHash, int fromRow, int fromCol,
                                         int toRow, int toCol, Piece movingPiece,
                                         Piece capturedPiece) {
        long newHash = currentHash;

        // remove piece from old position
        int pieceIndex = getPieceIndex(movingPiece);
        newHash ^= PIECE_KEYS[fromCol - 1][fromRow - 1][pieceIndex];

        // add piece to new position
        newHash ^= PIECE_KEYS[toCol - 1][toRow - 1][pieceIndex];

        // remove captured piece if any
        if (capturedPiece != null) {
            int capturedIndex = getPieceIndex(capturedPiece);
            newHash ^= PIECE_KEYS[toCol - 1][toRow - 1][capturedIndex];
        }

        return newHash;
    }

    public static long updateHashForPlayerChange(long currentHash) {
        return currentHash ^ BLACK_TO_MOVE_KEY;
    }

    public static long updateHashForEnPassantChange(long currentHash,
                                                    Integer oldEnPassantFile,
                                                    Integer newEnPassantFile) {
        long newHash = currentHash;

        // Remove old en passant hash
        if (oldEnPassantFile != null && oldEnPassantFile >= 0 && oldEnPassantFile < 8) {
            newHash ^= EN_PASSANT_KEYS[oldEnPassantFile];
        }

        // Add new en passant hash
        if (newEnPassantFile != null && newEnPassantFile >= 0 && newEnPassantFile < 8) {
            newHash ^= EN_PASSANT_KEYS[newEnPassantFile];
        }

        return newHash;
    }

    public static long updateHashForCastlingChange(long currentHash,
                                                   boolean oldWhiteKingSide, boolean oldWhiteQueenSide,
                                                   boolean oldBlackKingSide, boolean oldBlackQueenSide,
                                                   boolean newWhiteKingSide, boolean newWhiteQueenSide,
                                                   boolean newBlackKingSide, boolean newBlackQueenSide) {
        long newHash = currentHash;

        // Remove old castling hash
        newHash ^= calculateCastlingHash(oldWhiteKingSide, oldWhiteQueenSide, oldBlackKingSide, oldBlackQueenSide);
        // Add new castling hash
        newHash ^= calculateCastlingHash(newWhiteKingSide, newWhiteQueenSide, newBlackKingSide, newBlackQueenSide);

        return newHash;
    }

    private static long calculateCastlingHash(boolean whiteKingSide, boolean whiteQueenSide,
                                              boolean blackKingSide, boolean blackQueenSide) {
        int castlingIndex = 0;
        if (whiteKingSide) castlingIndex |= 1;    // K
        if (whiteQueenSide) castlingIndex |= 2;   // Q
        if (blackKingSide) castlingIndex |= 4;    // k
        if (blackQueenSide) castlingIndex |= 8;   // q

        return CASTLING_KEYS[castlingIndex];
    }

    private static int getPieceIndex(Piece piece) {
        int baseIndex = switch (piece.getType()) {
            case PAWN -> WHITE_PAWN;
            case ROOK -> WHITE_ROOK;
            case KNIGHT -> WHITE_KNIGHT;
            case BISHOP -> WHITE_BISHOP;
            case QUEEN -> WHITE_QUEEN;
            case KING -> WHITE_KING;
        };
        // BLACK_PAWN = 6, BLACK_ROOK = 7, BLACK_KNIGHT = 8, BLACK_BISHOP = 9, BLACK_QUEEN = 10, BLACK_KING = 11
        return piece.getColor() == PieceColor.WHITE ? baseIndex : baseIndex + 6;
    }
}
