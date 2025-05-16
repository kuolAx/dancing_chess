package com.kuolax.dancingchess.core;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.ui.Position;
import com.kuolax.dancingchess.board.Board;
import com.kuolax.dancingchess.board.Square;
import com.kuolax.dancingchess.pieces.Piece;
import javafx.scene.input.MouseButton;

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
    private Entity selectedPiece;
    private Position selectedPosition;

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
                        .view(new javafx.scene.shape.Rectangle(squareSize, squareSize, s.getSquareColor()))
                        .buildAndAttach());

        updateBoardView();
    }

    private void updateBoardView() {
        getGameWorld().getEntitiesByType(EntityType.PIECE).forEach(Entity::removeFromWorld);

        // Erstelle neue Figuren basierend auf dem aktuellen Board-Zustand
        Board board = gameController.getBoard();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                Piece piece = board.getPieceAt(pos);

                if (piece != null) {
                    Entity pieceEntity = EntityFactory.createPiece(piece);
                    getGameWorld().addEntity(pieceEntity);
                }
            }
        }
    }

    @Override
    protected void initInput() {
        onMousePressed(MouseButton.PRIMARY, event -> {
            int col = (int) (event.getX() / 64);
            int row = 7 - (int) (event.getY() / 64); // FXGL Y-Achse ist umgekehrt

            Position clickedPos = new Position(row, col);
            Piece clickedPiece = gameController.getBoard().getPieceAt(clickedPos);

            if (selectedPiece == null && clickedPiece != null &&
                    clickedPiece.getColor() == gameController.getCurrentPlayer()) {
                // Erste Auswahl: Figur selektieren
                selectedPosition = clickedPos;
                selectedPiece = getGameWorld().getEntitiesAt(event.getPosition())
                        .stream()
                        .filter(e -> e.getType() == EntityType.PIECE)
                        .findFirst()
                        .orElse(null);

                if (selectedPiece != null) {
                    // Markiere mögliche Züge
                    List<Position> legalMoves = gameController.getBoard().getLegalMoves(selectedPosition);
                    highlightLegalMoves(legalMoves);
                }
            } else if (selectedPiece != null) {
                // Zweite Auswahl: Zielfeld
                boolean moveSuccessful = gameController.makeMove(selectedPosition, clickedPos);

                if (moveSuccessful) {
                    // Aktualisiere die Ansicht
                    updateBoardView();

                    // Prüfe auf Spielende
                    if (gameController.isGameOver()) {
                        showGameOverDialog();
                    }

                    // Prüfe auf Bauernumwandlung
                    if (gameController.getBoard().canPromote(clickedPos)) {
                        showPromotionDialog(clickedPos);
                    }
                }

                selectedPiece = null;
                selectedPosition = null;
                clearHighlights();
            }
        });
    }

    private void highlightLegalMoves(List<Position> positions) {
        for (Position pos : positions) {
            Entity highlight = EntityFactory.createHighlight(pos);
            getGameWorld().addEntity(highlight);
        }
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
                "Pawn Promotion. \n Choose your piece:",
                pieceType -> {
                    Piece pawn = gameController.getBoard().getPieceAt(position);
                    gameController.getBoard().promotePawn(pawn, pieceType, position);
                    updateBoardView();
                },
                QUEEN, KNIGHT, BISHOP, ROOK);
    }
}
