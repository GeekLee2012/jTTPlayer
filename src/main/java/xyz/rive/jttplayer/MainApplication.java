package xyz.rive.jttplayer;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

//-Dfile.encoding=UTF-8
public class MainApplication extends Application {

    @Override
    public void start(Stage stage) {
        ApplicationContext.getInstance()
                .setApplication(this)
                .start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}