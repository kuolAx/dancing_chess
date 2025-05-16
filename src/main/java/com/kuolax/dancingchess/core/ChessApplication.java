package com.kuolax.dancingchess.core;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.kuolax.dancingchess.board.Square.STANDARD_SQUARE_SIZE;
import static com.kuolax.dancingchess.pieces.PieceType.BISHOP;
import static com.kuolax.dancingchess.pieces.PieceType.KNIGHT;
import static com.kuolax.dancingchess.pieces.PieceType.QUEEN;
import static com.kuolax.dancingchess.pieces.PieceType.ROOK;

public class ChessApplication extends GameApplication {
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
        settings.setHeight(700);
        settings.setTitle("Dancing Chess (Chess with additional steps)");
        settings.setVersion("1.0");

        settings.setDeveloperMenuEnabled(true);
    }

    @Override
    protected void initGame() {
        gameController = new GameController();
        getGameWorld().addEntityFactory(entityFactory);

        Arrays.stream(Square.values())
                .forEach(at -> getGameWorld().addEntity(entityFactory.spawnSquare(at)));

        updateBoard();
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
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleMouseClick);
    }

    private void handleMouseClick(MouseEvent event) {
        int boardX = (int) (event.getX() / STANDARD_SQUARE_SIZE);
        int boardY = 8 - (int) (event.getY() / STANDARD_SQUARE_SIZE);

        Square clickedSquare = Square.getByCoordinates(boardX, boardY);
        if (clickedSquare != null) {
            processSquareClick(clickedSquare);
        }
    }

    private void processSquareClick(Square clickedSquare) {
        System.out.println("Clicked on: " + clickedSquare);

        Piece clickedPiece = gameController.getBoard().getPieceAt(clickedSquare);

        // first click -> pick piece and highlight possible moves
        if (selectedSquare == null
                && clickedPiece != null
                && clickedPiece.getColor() == gameController.getCurrentPlayer()) {
            selectedSquare = clickedSquare;
            selectedPiece = clickedPiece;
            System.out.println("Selected " + clickedPiece.getId() + "on " + clickedSquare);

            List<Square> legalMoves = selectedPiece.getLegalMoves(gameController.getBoard());
            if (!legalMoves.isEmpty()) highlightSquares(legalMoves);
        } else {
            // second click - move selected piece
            boolean moveSuccessful = gameController.makeMove(selectedSquare, clickedSquare);

            if (moveSuccessful) {
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

    private void highlightSquares(List<Square> squares) {
        squares.forEach(at -> getGameWorld().addEntity(entityFactory.spawnHighlight(at)));
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
}
