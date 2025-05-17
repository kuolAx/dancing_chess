package com.kuolax.dancingchess.core;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.Spawns;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import static com.kuolax.dancingchess.board.Square.SQUARE_SIZE;
import static com.kuolax.dancingchess.core.EntityType.CHECK_HIGHLIGHT;
import static com.kuolax.dancingchess.core.EntityType.DRAG_TARGET_HIGHLIGHT;
import static com.kuolax.dancingchess.core.EntityType.LAST_MOVE_HIGHLIGHT;
import static com.kuolax.dancingchess.core.EntityType.LEGAL_MOVE_HIGHLIGHT;
import static com.kuolax.dancingchess.core.EntityType.PIECE;
import static com.kuolax.dancingchess.core.EntityType.SELECTED_PIECE_HIGHLIGHT;
import static com.kuolax.dancingchess.core.EntityType.SQUARE;
import static com.kuolax.dancingchess.core.EntityType.TAKEABLE_PIECE_HIGHLIGHT;
import static javafx.scene.paint.Color.DARKRED;
import static javafx.scene.paint.Color.YELLOWGREEN;

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
                .view(new Rectangle(SQUARE_SIZE, SQUARE_SIZE, YELLOWGREEN))
                .zIndex(1)
                .at(square.getSpawnX(), square.getSpawnY())
                .opacity(0.5)
                .anchorFromCenter()
                .build();
    }

    @Spawns("piece")
    public Entity spawnPiece(Piece piece, Square at) {
        String fileName = piece.getColor() + "_" + piece.getType() + ".png";
        return FXGL.entityBuilder()
                .type(PIECE)
                .view(fileName)
                .scale(0.285 * SQUARE_SIZE / 70, 0.285 * SQUARE_SIZE / 70)
                .at(at.getSpawnX(), at.getSpawnY())
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
                .opacity(0.8)
                .anchorFromCenter()
                .build();
    }

    @Spawns("dragTargetHighlight")
    public Entity spawnDragTargetHighlight(Square at) {
        return FXGL.entityBuilder()
                .type(DRAG_TARGET_HIGHLIGHT)
                .view(new Rectangle(SQUARE_SIZE, SQUARE_SIZE,
                        javafx.scene.paint.Color.color(0.5, 0.5, 0.5, 1)))
                .zIndex(1)
                .at(at.getSpawnX(), at.getSpawnY())
                .opacity(0.8)
                .anchorFromCenter()
                .build();
    }

    @Spawns("checkHighlight")
    public Entity spawnCheckHighlight(Square at) {
        Circle highlight = new Circle(SQUARE_SIZE / 2.5);
        highlight.setFill(DARKRED);
        highlight.setCenterX(SQUARE_SIZE / 2);
        highlight.setCenterY(SQUARE_SIZE / 2);

        return FXGL.entityBuilder()
                .type(CHECK_HIGHLIGHT)
                .at(at.getSpawnX(), at.getSpawnY())
                .view(highlight)
                .zIndex(3)
                .opacity(0.35)
                .anchorFromCenter()
                .build();
    }


    @Spawns("takeablePieceHighlightPolygons")
    public Entity spawnTakeablePieceHighlightPolygons(Square at) {
        double x = at.getSpawnX();
        double y = at.getSpawnY();

        Group highlightGroup = new Group();

        double triangleSize = SQUARE_SIZE / 4;
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

    @Spawns("draggedPieceShadow")
    public Entity spawnPieceShadow(Piece draggedPiece, Square clickedSquare) {
        Entity draggedPieceEntity = spawnPiece(draggedPiece, clickedSquare);
        draggedPieceEntity.setOpacity(0.8);
        draggedPieceEntity.setZIndex(2);
        return draggedPieceEntity;
    }
}