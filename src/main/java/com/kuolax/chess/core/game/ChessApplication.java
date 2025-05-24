package com.kuolax.chess.core.game;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.kuolax.chess.core.model.Board;
import com.kuolax.chess.core.model.GameState;
import com.kuolax.chess.core.model.Square;
import com.kuolax.chess.core.model.move.Move;
import com.kuolax.chess.core.model.piece.Piece;
import com.kuolax.chess.core.model.piece.PieceColor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.kuolax.chess.core.model.Square.SQUARE_SIZE;
import static com.kuolax.chess.core.model.piece.PieceType.BISHOP;
import static com.kuolax.chess.core.model.piece.PieceType.KNIGHT;
import static com.kuolax.chess.core.model.piece.PieceType.QUEEN;
import static com.kuolax.chess.core.model.piece.PieceType.ROOK;

public class ChessApplication extends GameApplication {
    private GameWorld gameWorld;
    private final GameController gameController = new GameController();
    private final ChessEntityFactory entityFactory = new ChessEntityFactory();

    private Piece selectedPiece;
    private Square selectedSquare;
    private List<Square> selectedPieceLegalMoves;

    //dragging functionality
    private boolean isDragging;
    private Entity draggedPieceShadow;

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
        gameWorld.addEntityFactory(entityFactory);

        Arrays.stream(Square.values())
                .forEach(at -> gameWorld.addEntities(entityFactory.spawnSquare(at)));
        updateBoard();
        FXGL.getAssetLoader().loadSound("game_start.mp3").getAudio().play();
    }

    private void updateBoard() {
        PieceColor currentPlayer = gameController.getCurrentPlayer();

        gameWorld.getEntitiesByType(EntityType.PIECE).forEach(Entity::removeFromWorld);

        // reset or redraw check highlight
        gameWorld.getEntitiesByType(EntityType.CHECK_HIGHLIGHT).forEach(Entity::removeFromWorld);
        Board board = gameController.getBoard();
        if (board.isChecked(currentPlayer)) {
            gameWorld.addEntity(entityFactory.spawnCheckHighlight(board.getKingSquare(currentPlayer)));
        }

        playSoundForLastMove(gameController.getBoard().getLastMove());

        Arrays.stream(Square.values())
                .filter(board::hasPieceAt)
                .forEach(s -> {
                    Entity pieceEntity = entityFactory.spawnPiece(board.getPieceAt(s), s);
                    gameWorld.addEntity(pieceEntity);
                });
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMouseClick);
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseReleased);
    }

    private void playSoundForLastMove(Move lastMove) {
        if (lastMove == null) return;

        if (lastMove.isCheckmate() || lastMove.isStaleMate())
            FXGL.getAssetLoader().loadSound("game_end.wav").getAudio().play();
        else if (lastMove.isCheck()) FXGL.getAssetLoader().loadSound("move-check.mp3").getAudio().play();
        else if (lastMove.isCastlingMove()) FXGL.getAssetLoader().loadSound("castle.mp3").getAudio().play();
        else if (lastMove.isPromotion()) FXGL.getAssetLoader().loadSound("promote.mp3").getAudio().play();
        else if (lastMove.isTakingMove()) FXGL.getAssetLoader().loadSound("capture.mp3").getAudio().play();
        else FXGL.getAssetLoader().loadSound("move-self.mp3").getAudio().play();
    }

    private void handleMouseClick(MouseEvent mouseEvent) {
        MouseButton button = mouseEvent.getButton();
        if (button == MouseButton.PRIMARY) {
            Square clickedSquare = Square.getByMousePosition(mouseEvent);
            if (clickedSquare != null) {
                processSquareClick(clickedSquare);
            }
        } else if (button == MouseButton.SECONDARY) {
            selectedPiece = null;
            selectedSquare = null;
            selectedPieceLegalMoves = null;
            clearHighlights();
            isDragging = false;
            if (draggedPieceShadow != null) draggedPieceShadow.removeFromWorld();
            draggedPieceShadow = null;
        }
    }

    private void processSquareClick(Square clickedSquare) {
        Piece clickedPiece = gameController.getBoard().getPieceAt(clickedSquare);

        // first click -> pick piece and highlight possible move
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

            // initialise piece dragging
            draggedPieceShadow = entityFactory.spawnPieceShadow(selectedPiece, clickedSquare);
            FXGL.getGameWorld().addEntity(draggedPieceShadow);
            isDragging = true;

        } else {
            // second click - move selected piece
            if (selectedPieceLegalMoves != null && selectedPieceLegalMoves.contains(clickedSquare)) {
                handleMoveLogic(clickedSquare);

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

    private void handleMoveLogic(Square to) {
        boolean moveSuccessful = gameController.makeMove(selectedSquare, to);

        if (moveSuccessful) {
            refreshLastMoveHighlights(to);

            updateBoard();
            if (gameController.isGameOver()) showGameOverDialog();
            if (gameController.getBoard().getLastMove().isPromotion()) showPromotionDialog(to);
        }

        // reset selection
        clearHighlights();
        selectedSquare = null;
        selectedPiece = null;
    }

    private void onMouseDragged(MouseEvent event) {
        if (!isDragging || draggedPieceShadow == null) {
            return;
        }
        draggedPieceShadow.setPosition(event.getX() - SQUARE_SIZE / 2, event.getY() - SQUARE_SIZE / 2);
        updateTargetSquareHighlight(event);
    }

    private void updateTargetSquareHighlight(MouseEvent event) {
        FXGL.getGameWorld().getEntitiesByType(EntityType.DRAG_TARGET_HIGHLIGHT).forEach(Entity::removeFromWorld);
        Square target = Square.getByMousePosition(event);

        if (target != null && selectedPieceLegalMoves != null && selectedPieceLegalMoves.contains(target)) {
            FXGL.getGameWorld().addEntity(entityFactory.spawnDragTargetHighlight(target));
        }
    }

    private void onMouseReleased(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY || !isDragging || selectedPiece == null || draggedPieceShadow == null) {
            return;
        }

        draggedPieceShadow.removeFromWorld();
        draggedPieceShadow = null;
        Square targetSquare = Square.getByMousePosition(event);

        if (targetSquare == selectedSquare)
            return;
        else if (targetSquare != null && selectedPieceLegalMoves != null && selectedPieceLegalMoves.contains(targetSquare))
            handleMoveLogic(targetSquare);
        else
            clearHighlights();

        isDragging = false;
        selectedPiece = null;
        selectedPieceLegalMoves = null;
        selectedSquare = null;
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

    private void showGameOverDialog() {
        GameState state = gameController.getGameState();

        String message = switch (state) {
            case WHITE_WINS -> "White wins by checkmate!";
            case BLACK_WINS -> "Black wins by checkmate!";
            case STALEMATE -> "Stalemate! Game ended in a draw.";
            case DRAW -> "Draw! Game ended in a draw due to exceeding the move limit.";
            case DRAW_BY_REPETITION -> "Draw! Game ended in a draw due to threefold repetition.";
            case DRAW_BY_INSUFFICIENT_MATERIAL -> "Draw! Game ended in a draw due to insufficient material.";
            default -> "Game ended";
        };

        getDialogService().showMessageBox(message, () -> {
            gameWorld.getEntitiesByType(EntityType.LAST_MOVE_HIGHLIGHT, EntityType.CHECK_HIGHLIGHT).forEach(Entity::removeFromWorld);
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
                    gameController.updateLastMove();
                },
                QUEEN, KNIGHT, BISHOP, ROOK);
    }

    private void clearHighlights() {
        gameWorld.getEntitiesByType(EntityType.LEGAL_MOVE_HIGHLIGHT,
                        EntityType.SELECTED_PIECE_HIGHLIGHT,
                        EntityType.TAKEABLE_PIECE_HIGHLIGHT,
                        EntityType.DRAG_TARGET_HIGHLIGHT)
                .forEach(Entity::removeFromWorld);
    }
}
