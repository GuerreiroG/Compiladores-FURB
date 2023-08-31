module br.edu.furb.compilador {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.fxmisc.richtext;

    opens br.edu.furb.compilador to javafx.fxml;
    exports br.edu.furb.compilador;
    exports br.edu.furb.compilador.interfaces;
    opens br.edu.furb.compilador.interfaces to javafx.fxml;
}