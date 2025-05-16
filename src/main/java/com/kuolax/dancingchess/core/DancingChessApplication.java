package com.kuolax.dancingchess.core;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.kuolax.dancingchess.pieces.PieceType.BISHOP;
import static com.kuolax.dancingchess.pieces.PieceType.KNIGHT;
import static com.kuolax.dancingchess.pieces.PieceType.QUEEN;
import static com.kuolax.dancingchess.pieces.PieceType.ROOK;

public class DancingChessApplication extends GameApplication {
    private GameController gameController;
    private Piece selectedPiece;
    private Square selectedSquare;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(800);
        settings.setTitle("Dancing Chess (Chess with additional steps)");
        settings.setVersion("1.0");
    }

    @Override
    protected void initGame() {
        gameController = new GameController();
        int squareSize = 85;

        Arrays.stream(Square.values())
                .forEach(s -> FXGL.entityBuilder()
                        .type(EntityType.PIECE)
                        .at((s.getX() * squareSize), (s.getY() * squareSize))
                        .view(new Rectangle(squareSize, squareSize, s.getSquareColor()))
                        .buildAndAttach());

        updateBoardView();
    }

    private void updateBoardView() {
        getGameWorld().getEntitiesByType(EntityType.PIECE).forEach(Entity::removeFromWorld);

        Board board = gameController.getBoard();

        Arrays.stream(Square.values())
                .filter(s -> board.getPieceAt(s) != null)
                .forEach(s -> {
                    Entity pieceEntity = EntityFactory.createPiece(board.getPieceAt(s));
                    getGameWorld().addEntity(pieceEntity);
                });
    }

    @Override
    protected void initInput() {
        onMousePressed(MouseButton.PRIMARY, event -> {
            int x = (int) (event.getX() / 64);
            int y = 7 - (int) (event.getY() / 64);

            Square clickedSquare = Square.getByCoordinates(x, y);
            Piece clickedPiece = gameController.getBoard().getPieceAt(clickedSquare);

            if (selectedPiece == null
                    && clickedPiece != null
                    && clickedPiece.getColor() == gameController.getCurrentPlayer()) {

                selectedSquare = clickedSquare;
                selectedPiece = getGameWorld().getEntitiesAt(event.getPosition())
                        .stream()
                        .filter(e -> e.getType() == EntityType.PIECE)
                        .findFirst()
                        .orElse(null);

                if (selectedPiece != null) {
                    List<Square> legalMoves = selectedPiece.getLegalMoves(gameController.getBoard());
                    highlightLegalMoves(legalMoves);
                }
            } else if (selectedPiece != null) {
                boolean moveSuccessful = gameController.makeMove(selectedSquare, clickedSquare);

                if (moveSuccessful) {
                    updateBoardView();

                    if (gameController.isGameOver()) {
                        showGameOverDialog();
                    }

                    if (gameController.canPromote(selectedPiece, clickedSquare)) {
                        showPromotionDialog(clickedSquare);
                    }
                }

                selectedPiece = null;
                selectedSquare = null;
                clearHighlights();
            }
        });
    }

    private void highlightLegalMoves(List<Square> squares) {
        squares.parallelStream()
                .forEach(s -> getGameWorld().addEntity(EntityFactory.createHighlight(s)));
    }

    private void clearHighlights() {
        getGameWorld().getEntitiesByType(EntityType.HIGHLIGHT).forEach(Entity::removeFromWorld);
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
            updateBoardView();
        });
    }

    private void showPromotionDialog(Square position) {
        getDialogService().showChoiceBox(
                "Pawn Promotion.\nChoose your piece:",
                pieceType -> {
                    Piece pawn = gameController.getBoard().getPieceAt(position);
                    gameController.getBoard().promotePawn(pawn, pieceType, position);
                    updateBoardView();
                },
                QUEEN, KNIGHT, BISHOP, ROOK);
    }
}
