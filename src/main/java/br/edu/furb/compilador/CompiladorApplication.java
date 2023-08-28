package br.edu.furb.compilador;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class CompiladorApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CompiladorApplication.class.getResource("compilador-view.fxml"));
        Font.loadFont(getClass().getResourceAsStream("fonts/SourceCodePro-Regular.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("fonts/SourceCodePro-Bold.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("fonts/SourceCodePro-ExtraLight.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("fonts/SourceCodePro-Light.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("fonts/SourceCodePro-It.ttf"), 10);

        CompiladorController controller = new CompiladorController();
        fxmlLoader.setController(controller);

        Scene scene = new Scene(fxmlLoader.load());

        adicionarCSS(scene, "compilador.css");
        controller.configurarAtalhos(scene);

        stage.setWidth(910);
        stage.setHeight(600);
        stage.setMinWidth(910);
        stage.setMinHeight(600);
        stage.setTitle("Compilador");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icons/furbLogo.png")));
        stage.show();
    }

    private static void adicionarCSS(Scene scene, String nomeArquivo) {
        URL cssURL = CompiladorApplication.class.getResource(nomeArquivo);
        if (cssURL != null) {
            scene.getStylesheets().add(cssURL.toExternalForm());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}