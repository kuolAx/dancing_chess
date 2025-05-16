package com.kuolax.dancingchess.core;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.Spawns;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;
import com.kuolax.dancingchess.pieces.PieceColor;
import com.kuolax.dancingchess.pieces.PieceType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.kuolax.dancingchess.board.Square.SQUARE_SIZE;

public class ChessEntityFactory implements EntityFactory {

    @Spawns("square")
    public Entity spawnSquare(Square square) {
        return FXGL.entityBuilder()
                .type(EntityType.SQUARE)
                .viewWithBBox(new Rectangle(SQUARE_SIZE, SQUARE_SIZE, square.getSquareColor()))
                .zIndex(0)
                .at(square.getSpawnX(), square.getSpawnY())
                .opacity(0.5)
                .anchorFromCenter()
                .build();
    }

    @Spawns("piece")
    public Entity spawnPiece(Piece piece, Square at) {
        return FXGL.entityBuilder()
                .type(EntityType.PIECE)
                .view(new Text(getPieceSymbol(piece.getType(), piece.getColor() == PieceColor.WHITE)))
                .at(at.getSpawnX() + 6, at.getSpawnY() + SQUARE_SIZE - 10)
                .scale(SQUARE_SIZE / 14.16, SQUARE_SIZE / 14.16)
                .zIndex(1)
                .anchorFromCenter()
                .build();
    }

    @Spawns("highlight")
    public Entity spawnHighlight(Square at) {
        Circle highlight = new Circle(SQUARE_SIZE / 3);
        highlight.setFill(javafx.scene.paint.Color.color(0.5, 0.5, 0.5, 0.4));
        highlight.setCenterX(SQUARE_SIZE / 2);
        highlight.setCenterY(SQUARE_SIZE / 2);

        return FXGL.entityBuilder()
                .type(EntityType.HIGHLIGHT)
                .at(at.getSpawnX(), at.getSpawnY())
                .view(highlight)
                .zIndex(2)
                .anchorFromCenter()
                .build();
    }

    @Spawns("squareText")
    public Entity spawnSquareText(Square square, Text text) {
        return FXGL.entityBuilder()
                .type(EntityType.TEXT)
                .view(new Text(square.toString()))
                .zIndex(0)
                .at(square.getSpawnX() + 5, square.getSpawnY() + SQUARE_SIZE - 5)
                .opacity(0.3)
                .anchorFromCenter()
                .build();
    }

    private String getPieceSymbol(PieceType type, boolean isWhite) {
        return switch (type) {
            case PAWN -> isWhite ? "♙" : "♟";
            case KNIGHT -> isWhite ? "♘" : "♞";
            case BISHOP -> isWhite ? "♗" : "♝";
            case ROOK -> isWhite ? "♖" : "♜";
            case QUEEN -> isWhite ? "♕" : "♛";
            case KING -> isWhite ? "♔" : "♚";
        };
    }
}