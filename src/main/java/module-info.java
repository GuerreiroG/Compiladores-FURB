module br.edu.furb.compilador {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires reactfx;
    requires org.fxmisc.richtext;

    opens br.edu.furb.compilador to javafx.fxml;
    exports br.edu.furb.compilador;
    exports br.edu.furb.compilador.ui;

    opens br.edu.furb.compilador.ui to javafx.fxml;
}