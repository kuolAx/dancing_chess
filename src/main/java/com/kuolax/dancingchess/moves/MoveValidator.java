package com.kuolax.dancingchess.moves;

import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;

import java.util.List;

public interface MoveValidator {

    boolean isLegalMove(Piece piece, Square from, Square to, Board board);

    List<Square> getAllLegalMoves(Piece piece, Square from, Board board);

    boolean canTakeOn(Piece piece, Square from, Square to, Board board);
}
