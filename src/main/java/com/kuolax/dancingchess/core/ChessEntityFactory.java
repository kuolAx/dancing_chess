package com.kuolax.dancingchess.core;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.Spawns;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;
import com.kuolax.dancingchess.pieces.PieceColor;
import com.kuolax.dancingchess.pieces.PieceType;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.kuolax.dancingchess.board.Square.SQUARE_SIZE;
import static com.kuolax.dancingchess.core.EntityType.LAST_MOVE_HIGHLIGHT;
import static com.kuolax.dancingchess.core.EntityType.LEGAL_MOVE_HIGHLIGHT;
import static com.kuolax.dancingchess.core.EntityType.PIECE;
import static com.kuolax.dancingchess.core.EntityType.SELECTED_PIECE_HIGHLIGHT;
import static com.kuolax.dancingchess.core.EntityType.SQUARE;
import static com.kuolax.dancingchess.core.EntityType.TAKEABLE_PIECE_HIGHLIGHT;
import static javafx.scene.paint.Color.DARKOLIVEGREEN;
import static javafx.scene.paint.Color.HOTPINK;
import static javafx.scene.paint.Color.TRANSPARENT;

public class ChessEntityFactory implements EntityFactory {

    @Spawns("square")
    public Entity spawnSquare(Square square) {
        return FXGL.entityBuilder()
                .type(SQUARE)
                .view(new Rectangle(SQUARE_SIZE, SQUARE_SIZE, square.getSquareColor()))
                .zIndex(0)
                .at(square.getSpawnX(), square.getSpawnY())
                .opacity(0.5)
                .anchorFromCenter()
                .build();
    }

    @Spawns("squareHighlight")
    public Entity spawnLastMoveHighlight(Square square) {
        return FXGL.entityBuilder()
                .type(LAST_MOVE_HIGHLIGHT)
                .view(new Rectangle(SQUARE_SIZE, SQUARE_SIZE, HOTPINK))
                .zIndex(1)
                .at(square.getSpawnX(), square.getSpawnY())
                .opacity(0.5)
                .anchorFromCenter()
                .build();
    }

    @Spawns("piece")
    public Entity spawnPiece(Piece piece, Square at) {
        return FXGL.entityBuilder()
                .type(PIECE)
                .view(new Text(getPieceSymbol(piece.getType(), piece.getColor() == PieceColor.WHITE)))
                .at(at.getSpawnX() + 6, at.getSpawnY() + SQUARE_SIZE - 10)
                .scale(SQUARE_SIZE / 14.16, SQUARE_SIZE / 14.16)
                .zIndex(2)
                .anchorFromCenter()
                .build();
    }

    @Spawns("legalMoveHighlight")
    public Entity spawnLegalMoveHighlight(Square at) {
        Circle highlight = new Circle(SQUARE_SIZE / 5);
        highlight.setFill(javafx.scene.paint.Color.color(0.5, 0.5, 0.5, 0.5));
        highlight.setCenterX(SQUARE_SIZE / 2);
        highlight.setCenterY(SQUARE_SIZE / 2);

        return FXGL.entityBuilder()
                .type(LEGAL_MOVE_HIGHLIGHT)
                .at(at.getSpawnX(), at.getSpawnY())
                .view(highlight)
                .zIndex(3)
                .anchorFromCenter()
                .build();
    }

    @Spawns("selectedPieceHighlight")
    public Entity spawnSelectedPieceHighlight(Square at) {
        return FXGL.entityBuilder()
                .type(SELECTED_PIECE_HIGHLIGHT)
                .view(new Rectangle(SQUARE_SIZE, SQUARE_SIZE,
                        javafx.scene.paint.Color.color(0.5, 0.5, 0.5, 1)))
                .zIndex(1)
                .at(at.getSpawnX(), at.getSpawnY())
                .opacity(0.5)
                .anchorFromCenter()
                .build();
    }

    @Spawns("takeablePieceHighlight")
    public Entity spawnTakeablePieceHighlight(Square at) {
        Circle highlight = new Circle(SQUARE_SIZE / 3 - 2);
        highlight.setFill(TRANSPARENT);
        highlight.setStroke(DARKOLIVEGREEN);
        highlight.setStrokeWidth(5);
        highlight.setCenterX(SQUARE_SIZE / 2);
        highlight.setCenterY(SQUARE_SIZE / 2);

        return FXGL.entityBuilder()
                .type(SELECTED_PIECE_HIGHLIGHT)
                .at(at.getSpawnX(), at.getSpawnY())
                .view(highlight)
                .zIndex(1)
                .anchorFromCenter()
                .build();
    }

    @Spawns("takeablePieceHighlightPolygons")
    public Entity spawnTakeablePieceHighlightPolygons(Square at) {
        double x = at.getSpawnX();
        double y = at.getSpawnY();

        Group highlightGroup = new Group();

        double triangleSize = SQUARE_SIZE / 3;
        Color highlightColor = Color.GRAY;

        Polygon topLeft = new Polygon(0, 0, triangleSize, 0, 0, triangleSize);
        topLeft.setFill(highlightColor);
        Polygon topRight = new Polygon(SQUARE_SIZE - triangleSize, 0, SQUARE_SIZE, 0, SQUARE_SIZE, triangleSize);
        topRight.setFill(highlightColor);
        Polygon bottomLeft = new Polygon(0, SQUARE_SIZE - triangleSize, 0, SQUARE_SIZE, triangleSize, SQUARE_SIZE);
        bottomLeft.setFill(highlightColor);
        Polygon bottomRight = new Polygon(SQUARE_SIZE - triangleSize, SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE,
                SQUARE_SIZE, SQUARE_SIZE - triangleSize);
        bottomRight.setFill(highlightColor);

        highlightGroup.getChildren().addAll(topLeft, topRight, bottomLeft, bottomRight);

        return FXGL.entityBuilder()
                .type(TAKEABLE_PIECE_HIGHLIGHT)
                .at(x, y)
                .view(highlightGroup)
                .zIndex(1)
                .opacity(0.8)
                .build();
    }


    @Spawns("squareText")
    public Entity spawnSquareText(Square square) {
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