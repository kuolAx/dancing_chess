package com.kuolax.chess.core.model.move.validation;

import com.kuolax.chess.core.model.Board;
import com.kuolax.chess.core.model.Square;
import com.kuolax.chess.core.model.piece.Piece;

import java.util.List;

public interface MoveValidator {

    boolean isLegalMove(Piece piece, Square from, Square to, Board board);

    List<Square> getAllLegalMoves(Piece piece, Square from, Board board);

    boolean canTakeOn(Piece piece, Square from, Square to, Board board);
}
