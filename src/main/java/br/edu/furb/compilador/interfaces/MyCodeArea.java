package br.edu.furb.compilador.interfaces;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import org.fxmisc.richtext.CodeArea;

public class MyCodeArea extends CodeArea {

    public static final Rectangle colunaLinhas = new Rectangle();

    public MyCodeArea() {
        colunaLinhas.heightProperty().bind(this.heightProperty());
        colunaLinhas.getStyleClass().add("coluna-linha");
    }

    @Override
    protected void layoutChildren() {
        ObservableList<Node> children = getChildren();
        if (!children.get(0).equals(colunaLinhas)) {
            children.add(0, colunaLinhas);
        }
        int index = visibleParToAllParIndex(0);
        Region paragraphGraphic = (Region) getParagraphGraphic(index);
        double wd = paragraphGraphic.prefWidth(-1);
        colunaLinhas.setWidth(wd);
        super.layoutChildren();
    }
}
