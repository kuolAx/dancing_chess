package chess.dancing.core;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;

public class Application extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Dancing Chess (Chess with additional steps)");
        settings.setVersion("1.0");
    }

    @Override
    protected void initGame() {
        FXGL.entityBuilder()
                .at(400, 300)
                .view(new javafx.scene.shape.Rectangle(40, 40, javafx.scene.paint.Color.BLUE))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}