package com.kuolax.dancingchess.core;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;
import static com.kuolax.dancingchess.board.Square.SQUARE_SIZE;
import static com.kuolax.dancingchess.board.Square.getSquareByMousePosition;
import static com.kuolax.dancingchess.pieces.PieceType.BISHOP;
import static com.kuolax.dancingchess.pieces.PieceType.KNIGHT;
import static com.kuolax.dancingchess.pieces.PieceType.QUEEN;
import static com.kuolax.dancingchess.pieces.PieceType.ROOK;

public class ChessApplication extends GameApplication {
    private static final int BOARD_X_OFFSET = 0;
    private static final int BOARD_Y_OFFSET = 0;
    private final ChessEntityFactory entityFactory = new ChessEntityFactory();
    private GameController gameController;
    private Piece selectedPiece;
    private Square selectedSquare;
    private List<Square> selectedPieceLegalMoves;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Dancing Chess (Chess with additional steps)");
        settings.setVersion("1.0");

        settings.setDeveloperMenuEnabled(true);
    }

    @Override
    protected void initGame() {
        gameController = new GameController();
        getGameWorld().addEntityFactory(entityFactory);

        Arrays.stream(Square.values())
                .forEach(at -> {
                    getGameWorld().addEntity(entityFactory.spawnSquare(at));
                    getGameWorld().addEntity(entityFactory.spawnSquareText(at, new Text(at.toString())));
                });

        updateBoard();
        setupMousePositionTracker();
    }

    private void updateBoard() {
        getGameWorld().getEntitiesByType(EntityType.PIECE).forEach(Entity::removeFromWorld);

        Board board = gameController.getBoard();

        Arrays.stream(Square.values())
                .filter(s -> board.getPieceAt(s) != null)
                .forEach(s -> {
                    Entity pieceEntity = entityFactory.spawnPiece(board.getPieceAt(s), s);
                    getGameWorld().addEntity(pieceEntity);
                });
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseClick);
    }

    private void handleMouseClick(MouseEvent mouseEvent) {
        Square clickedSquare = Square.getSquareByMousePosition(mouseEvent);
        if (clickedSquare != null) {
            processSquareClick(clickedSquare);
        }
    }

    private void processSquareClick(Square clickedSquare) {
        Piece clickedPiece = gameController.getBoard().getPieceAt(clickedSquare);

        // first click -> pick piece and highlight possible moves
        if (selectedSquare == null
                && clickedPiece != null
                && clickedPiece.getColor() == gameController.getCurrentPlayer()) {
            selectedSquare = clickedSquare;
            selectedPiece = clickedPiece;

            getGameWorld().addEntity(entityFactory.spawnSelectedPieceHighlight(selectedPiece.getPosition()));

            selectedPieceLegalMoves = selectedPiece.getLegalMoves(gameController.getBoard());
            if (!selectedPieceLegalMoves.isEmpty()) {
                selectedPieceLegalMoves.forEach(at -> getGameWorld().addEntity(entityFactory.spawnHighlight(at)));
            }
        } else {
            // second click - move selected piece
            boolean moveSuccessful = gameController.makeMove(selectedSquare, clickedSquare);

            if (moveSuccessful) {
                refreshLastMoveHighlights(clickedSquare);

                updateBoard();
                if (gameController.isGameOver()) showGameOverDialog();
                if (gameController.canPromote(selectedPiece, clickedSquare)) showPromotionDialog(clickedSquare);
            }

            // reset selection
            clearHighlights();
            selectedSquare = null;
            selectedPiece = null;
        }
    }

    private void refreshLastMoveHighlights(Square clickedSquare) {
        getGameWorld().getEntitiesByType(EntityType.LAST_MOVE_HIGHLIGHT).forEach(Entity::removeFromWorld);
        getGameWorld().addEntity(entityFactory.spawnLastMoveHighlight(selectedSquare));
        getGameWorld().addEntity(entityFactory.spawnLastMoveHighlight(clickedSquare));
    }

    private void clearHighlights() {
        getGameWorld().getEntitiesByType(EntityType.LEGAL_MOVE_HIGHLIGHT, EntityType.SELECTED_PIECE_HIGHLIGHT)
                .forEach(Entity::removeFromWorld);
    }

    private void showGameOverDialog() {
        GameState state = gameController.getGameState();

        String message = switch (state) {
            case WHITE_WINS -> "White wins by PACO!";
            case BLACK_WINS -> "Black wins by PACO!";
            case STALEMATE -> "Stalemate! Game ended in a draw.";
            case DRAW -> "Draw! Game ended in a draw.";
            default -> "Game ended";
        };

        getDialogService().showMessageBox(message, () -> {
            gameController.resetGame();
            updateBoard();
        });
    }

    private void showPromotionDialog(Square position) {
        getDialogService().showChoiceBox(
                "Pawn Promotion.\nChoose your piece:",
                pieceType -> {
                    Piece pawn = gameController.getBoard().getPieceAt(position);
                    gameController.getBoard().promotePawn(pawn, pieceType, position);
                    updateBoard();
                },
                QUEEN, KNIGHT, BISHOP, ROOK);
    }

    private void setupMousePositionTracker() {
        Text mousePositionText = new Text();
        mousePositionText.setTranslateX(8 * SQUARE_SIZE + 20);
        mousePositionText.setTranslateY(30);

        Rectangle background = new Rectangle(150, 70, Color.color(0, 0, 0, 0.3));
        background.setTranslateX(8 * SQUARE_SIZE + 10);
        background.setTranslateY(5);

        getGameScene().addUINode(background);
        getGameScene().addUINode(mousePositionText);

        getInput().addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            Square square = getSquareByMousePosition(event);

            mousePositionText.setText(String.format(
                    "Mouse: (%.1f, %.1f)%nSquare: %s%nOffset: (X=%d, Y=%d)",
                    mouseX, mouseY,
                    (square != null) ? square.toString() : "None",
                    BOARD_X_OFFSET, BOARD_Y_OFFSET
            ));
        });
    }
}
