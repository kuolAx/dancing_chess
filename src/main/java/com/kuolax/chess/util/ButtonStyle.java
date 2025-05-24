package com.kuolax.chess.util;

import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static com.kuolax.chess.core.model.Square.SQUARE_SIZE;

public enum ButtonStyle {
    INITIAL("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand;",
            ButtonConstants.defaultPrefSize, ButtonConstants.defaultFont),
    HOVER("-fx-background-color: #45a049; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand;",
            ButtonConstants.defaultPrefSize, ButtonConstants.defaultFont),
    DISABLED("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-border-radius: 5px; -fx-background-radius: 5px;",
            ButtonConstants.defaultPrefSize, ButtonConstants.defaultFont);


    private final String style;
    private final int[] prefSize;
    private final Font font;

    ButtonStyle(String style, int[] prefSize, Font font) {
        this.style = style;
        this.prefSize = prefSize;
        this.font = font;
    }

    public void applyFor(Button button) {
        button.setFont(font);
        button.setPrefSize(prefSize[0], prefSize[1]);
        button.setStyle(style);
    }

    private static final class ButtonConstants {
        private static final int[] defaultPrefSize = new int[]{(int) SQUARE_SIZE, 20};
        private static final Font defaultFont = Font.font("Arial", FontWeight.BOLD, 14);
    }
}
