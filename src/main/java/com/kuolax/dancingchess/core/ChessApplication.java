package com.kuolax.dancingchess.core;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Move;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;
import com.kuolax.dancingchess.pieces.PieceColor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;
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

    private GameWorld gameWorld;
    private final GameController gameController = new GameController();
    private final ChessEntityFactory entityFactory = new ChessEntityFactory();

    private Piece selectedPiece;
    private Square selectedSquare;
    private List<Square> selectedPieceLegalMoves;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth((int) (8 * SQUARE_SIZE));
        settings.setHeight((int) (8 * SQUARE_SIZE));
        settings.setTitle("Dancing Chess");
        settings.setVersion("0.1");

        settings.setDeveloperMenuEnabled(false);
        settings.setMainMenuEnabled(false);
        settings.setGameMenuEnabled(false);
    }

    @Override
    protected void initGame() {
        gameWorld = FXGL.getGameWorld();
//        setupMousePositionTracker();
        gameWorld.addEntityFactory(entityFactory);

        Arrays.stream(Square.values())
                .forEach(at -> gameWorld.addEntities(entityFactory.spawnSquare(at)
                        /* ,entityFactory.spawnSquareText(at)*/));
        updateBoard();
        FXGL.getAssetLoader().loadSound("game_start.mp3").getAudio().play();
    }

    private void updateBoard() {
        PieceColor currentPlayer = gameController.getCurrentPlayer();

        gameWorld.getEntitiesByType(EntityType.PIECE).forEach(Entity::removeFromWorld);

        Board board = gameController.getBoard();

        if (board.isChecked(currentPlayer)) {
            gameWorld.addEntity(entityFactory.spawnCheckHighlight(board.getKingSquare(currentPlayer)));
        } else {
            gameWorld.getEntitiesByType(EntityType.CHECK_HIGHLIGHT).forEach(Entity::removeFromWorld);
        }

        playSoundForLastMove(gameController.getBoard().getLastMove());

        Arrays.stream(Square.values())
                .filter(s -> board.getPieceAt(s) != null)
                .forEach(s -> {
                    Entity pieceEntity = entityFactory.spawnPiece(board.getPieceAt(s), s);
                    gameWorld.addEntity(pieceEntity);
                });
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMouseClick);
    }

    private void playSoundForLastMove(Move lastMove) {
        if (lastMove == null) return;

        if (lastMove.isCheckmate() || lastMove.isStaleMate())
            FXGL.getAssetLoader().loadSound("game_end.wav").getAudio().play();
        else if (lastMove.isCheck()) FXGL.getAssetLoader().loadSound("move-check.mp3").getAudio().play();
        else if (lastMove.isCastling()) FXGL.getAssetLoader().loadSound("castle.mp3").getAudio().play();
        else if (lastMove.isPromotion()) FXGL.getAssetLoader().loadSound("promote.mp3").getAudio().play();
        else FXGL.getAssetLoader().loadSound("move-self.mp3").getAudio().play();
    }

    private void handleMouseClick(MouseEvent mouseEvent) {
        MouseButton button = mouseEvent.getButton();
        if (button == MouseButton.PRIMARY) {
            Square clickedSquare = Square.getSquareByMousePosition(mouseEvent);
            if (clickedSquare != null) {
                processSquareClick(clickedSquare);
            }
        } else if (button == MouseButton.SECONDARY) {
            selectedPiece = null;
            selectedSquare = null;
            selectedPieceLegalMoves = null;
            clearAllHighlights();
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

            gameWorld.addEntity(entityFactory.spawnSelectedPieceHighlight(selectedPiece.getPosition()));

            selectedPieceLegalMoves = selectedPiece.getLegalMoves(gameController.getBoard());
            if (!selectedPieceLegalMoves.isEmpty()) {
                setLegalMoveHighlights();
            }
        } else {
            // second click - move selected piece
            if (selectedPieceLegalMoves != null && selectedPieceLegalMoves.contains(clickedSquare)) {
                boolean moveSuccessful = gameController.makeMove(selectedSquare, clickedSquare);

                if (moveSuccessful) {
                    refreshLastMoveHighlights(clickedSquare);

                    updateBoard();
                    if (gameController.isGameOver()) showGameOverDialog();
                    if (gameController.getBoard().getLastMove().isPromotion()) showPromotionDialog(clickedSquare);
                }

                // reset selection
                clearHighlights();
                selectedSquare = null;
                selectedPiece = null;

            } else if (selectedPiece != null) {
                // select new piece on clicked square if present
                // reset current selection
                selectedPiece = null;
                selectedSquare = null;
                selectedPieceLegalMoves = null;
                clearHighlights();

                processSquareClick(clickedSquare);
            }
        }
    }

    private void setLegalMoveHighlights() {
        Map<Boolean, List<Square>> isTakingMoveMap = selectedPieceLegalMoves.stream()
                .collect(Collectors.partitioningBy(s -> gameController.getBoard().getPieceAt(s) == null));

        isTakingMoveMap.get(false)
                .forEach(at -> gameWorld.addEntity(entityFactory.spawnTakeablePieceHighlightPolygons(at)));
        isTakingMoveMap.get(true)
                .forEach(at -> gameWorld.addEntity(entityFactory.spawnLegalMoveHighlight(at)));
    }

    private void refreshLastMoveHighlights(Square clickedSquare) {
        gameWorld.getEntitiesByType(EntityType.LAST_MOVE_HIGHLIGHT).forEach(Entity::removeFromWorld);
        gameWorld.addEntity(entityFactory.spawnLastMoveHighlight(selectedSquare));
        gameWorld.addEntity(entityFactory.spawnLastMoveHighlight(clickedSquare));
    }

    private void clearHighlights() {
        gameWorld.getEntitiesByType(EntityType.LEGAL_MOVE_HIGHLIGHT,
                        EntityType.SELECTED_PIECE_HIGHLIGHT,
                        EntityType.TAKEABLE_PIECE_HIGHLIGHT)
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
            clearAllHighlights();
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

    private void clearAllHighlights() {
        gameWorld.getEntitiesByType(EntityType.LEGAL_MOVE_HIGHLIGHT,
                        EntityType.SELECTED_PIECE_HIGHLIGHT,
                        EntityType.TAKEABLE_PIECE_HIGHLIGHT,
                        EntityType.LAST_MOVE_HIGHLIGHT,
                        EntityType.CHECK_HIGHLIGHT)
                .forEach(Entity::removeFromWorld);
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
