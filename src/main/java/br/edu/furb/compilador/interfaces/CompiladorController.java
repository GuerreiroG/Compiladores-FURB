package br.edu.furb.compilador.interfaces;

import br.edu.furb.compilador.gals.LexicalError;
import br.edu.furb.compilador.gals.Lexico;
import br.edu.furb.compilador.gals.Token;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompiladorController implements Initializable {

    private static final String[] PALAVRAS_RESERVADAS = new String[] {
            "do", "else", "false", "fun", "if", "in", "main", "out", "repeat", "true", "while"
    };
    private static final String PALAVRA_RESERVADA_PADRAO = "\\b(" + String.join("|", PALAVRAS_RESERVADAS) + ")\\b";

    private static final String PARENTESES_PADRAO = "\\(|\\)";
    private static final String CHAVES_PADRAO = "\\{|\\}";
    private static final String COLCHETES_PADRAO = "\\[|\\]";
    private static final String PONTO_VIRGULA_PADRAO = "\\;";
    private static final String STRING_PADRAO = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMENTARIO_PADRAO = "#[^\n]*" + "|" + "\\[[^\\[\\]]*\\]";

    private static final Pattern PADRAO = Pattern.compile(
        "(?<KEYWORD>" + PALAVRA_RESERVADA_PADRAO + ")"
            + "|(?<PAREN>" + PARENTESES_PADRAO + ")"
            + "|(?<BRACE>" + CHAVES_PADRAO + ")"
            + "|(?<BRACKET>" + COLCHETES_PADRAO + ")"
            + "|(?<SEMICOLON>" + PONTO_VIRGULA_PADRAO + ")"
            + "|(?<STRING>" + STRING_PADRAO + ")"
            + "|(?<COMMENT>" + COMENTARIO_PADRAO + ")"
    );


    private static final String FX_FONT_SIZE = "-fx-font-size: ";
    private static final int MIN_FONT = 8;
    private static final int MAX_FONT = 85;

    private final IntegerProperty tamanhoFonte = new SimpleIntegerProperty();
    private File arquivoAtual;

    @FXML
    private CodeArea codeArea;
    @FXML
    private Label status;
    @FXML
    private CodeArea areaMensagens;

    private Scene scene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarCodeArea();
    }

    private static StyleSpans<Collection<String>> computarDestaque(String texto) {
        Matcher matcher = PADRAO.matcher(texto);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = switch (matcher.group()) {
                case "KEYWORD" -> "keyword";
                case "PAREN" -> "paren";
                case "BRACE" -> "brace";
                case "BRACKET" -> "bracket";
                case "SEMICOLON" -> "semicolon";
                case "STRING" -> "string";
                case "COMMENT" -> "comment";
                default -> null;
            };
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), texto.length() - lastKwEnd);
        return spansBuilder.create();
    }



    public void configurarAtalhos(Scene scene) {

        KeyCodeCombination combinacaoNovo =
                new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        KeyCodeCombination combinacaoAbrir =
                new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        KeyCodeCombination combinacaoSalvar =
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (combinacaoNovo.match(event))
                novoArquivo();
            if (combinacaoAbrir.match(event))
                abrirArquivo();
            if (combinacaoSalvar.match(event))
                salvarArquivo();
            if (event.getCode() == KeyCode.F7)
                compilar();
            if (event.getCode() == KeyCode.F1)
                mostrarEquipe();
        });
    }

    public void compilar() {
        areaMensagens.clear();
        Lexico lexico = new Lexico();
        lexico.setInput(codeArea.getText());
        try {
            Token t = null;
            StringBuilder lexemas = new StringBuilder();
            while ((t = lexico.nextToken()) != null) {
                int linha = codeArea.offsetToPosition(t.getPosition(), null).getMajor() + 1;
                lexemas.append(String.format("%-8s%-20s%s%n", linha,
                        getClasse(t.getId(), t.getPosition(), t.getLexeme()), t.getLexeme()));
            }
            lexemas.append(String.format("%n        %s", "programa compilado com sucesso"));
            areaMensagens.insertText(0, lexemas.toString());
        }
        catch ( LexicalError e ) {
            int linha = codeArea.offsetToPosition(e.getPosition(), null).getMajor() + 1;
            String lexema = e.getLexeme() != null ? (e.getLexeme() + " ") : "";
            String mensagem = String.format("linha %d: %s%s", linha, lexema, e.getMessage());
            areaMensagens.clear();
            areaMensagens.insertText(0, mensagem);
        }
    }

    public String getClasse(int id, int posicao, String lexema) throws LexicalError {
        switch (id) {
            case 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 -> {
                return  "palavra_reservada";
            }
            case 3 -> {
                return "identificador";
            }
            case 4 -> {
                return "constante_int";
            }
            case 5 -> {
                return "constante_float";
            }
            case 6 -> {
                return "constante_string";
            }
            case 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36 -> {
                return "simbolo especial";
            }
            case 2 ->
                throw new LexicalError("palavra reservada inválida", posicao, lexema);
            default -> {
                return "simbolo desconhecido";
            }
        }
    }

    public void mostrarEquipe() {
        areaMensagens.clear();
        areaMensagens.insertText(0, "Desenvolvido por:\nGustavo Guerreiro");
    }

    public void criarArquivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName("novo_arquivo.txt");
        arquivoAtual = fileChooser.showSaveDialog(null);
        atualizarStatus();
        areaMensagens.clear();
    }

    private void atualizarStatus() {
        if (arquivoAtual == null) {
            status.setText("");
            return;
        }
        status.setText(arquivoAtual.getParentFile().getName() + File.separator + arquivoAtual.getName());
    }

    public void novoArquivo() {
        codeArea.clear();
        areaMensagens.clear();
        arquivoAtual = null;
        atualizarStatus();
    }

    public void abrirArquivo() {
        try {
            File arquivo = escolherArquivo();
            if (arquivo != null) {
                arquivoAtual = arquivo;
                codeArea.insertText(0,
                        Files.readString(Path.of(arquivo.getPath())));
                atualizarStatus();
                areaMensagens.clear();
            }
        } catch (IOException ioe) {
            mostrarErro("Um erro ocorreu ao abrir o arquivo. Por favor, tente novamente.");
        }
    }

    private File escolherArquivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escolha um arquivo");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser
                .getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("arquivos txt (*.txt)", "*.txt"));
        fileChooser
                .getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("outros (*.*)", "*.*"));
        return fileChooser.showOpenDialog(null);
    }

    public void salvarArquivo() {
        if (arquivoAtual == null) {
            criarArquivo();
        }
        if (arquivoAtual != null) {
            try (FileWriter writer = new FileWriter(arquivoAtual);
                 BufferedWriter bufferedWriter = new BufferedWriter(writer)
            ) {
                bufferedWriter.write(codeArea.getText());
                areaMensagens.clear();
            } catch (IOException ioe) {
                mostrarErro("Um erro ocorreu ao salvar o arquivo. Por favor, tente novamente.");
            }
        }
    }

    public void copiar() {
        codeArea.copy();
    }

    public void colar() {
        codeArea.paste();
    }

    public void recortar() {
        codeArea.cut();
    }

    private void configurarCodeArea() {
        adicionarContadorDeLinha();
        configurarZoom();
        configurarTamanhoFonte();
        configurarDestaque();
    }

    private void configurarDestaque() {
        codeArea
                .multiPlainChanges()
                .successionEnds(Duration.ofMillis(200))
                .subscribe(ignore -> codeArea.setStyleSpans(0, computarDestaque(codeArea.getText())));
    }

    private void configurarTamanhoFonte() {
        codeArea.styleProperty().bind(
                Bindings.concat(
                        FX_FONT_SIZE,
                        tamanhoFonte.asString(),
                        "; -fx-font-family: 'Source Code Pro';"));
        areaMensagens.styleProperty().bind(
                Bindings.concat(
                        FX_FONT_SIZE,
                        tamanhoFonte.asString(),
                        "; -fx-font-family: 'Source Code Pro';"));
        tamanhoFonte.set(16);
    }

    private void configurarZoom() {
        KeyCodeCombination combinacaoMais = getCombinacaoCrtlMais();
        KeyCodeCombination combinacaoMenos = getCombinacaoCrtlMenos();
        codeArea.setOnKeyPressed(event -> {
            if (combinacaoMais.match(event)) {
                aumentarFonte();
                event.consume();
            } else if (combinacaoMenos.match(event)) {
                diminuirFonte();
                event.consume();
            }
        });
    }

    private void mostrarErro(String mensagem) {
        areaMensagens.clear();
        areaMensagens.insertText(0, mensagem);
    }


    private static KeyCodeCombination getCombinacaoCrtlMais() {
        return new KeyCodeCombination(
                KeyCode.EQUALS,
                KeyCombination.CONTROL_DOWN);
    }

    private static KeyCodeCombination getCombinacaoCrtlMenos() {
        return new KeyCodeCombination(
                KeyCode.MINUS,
                KeyCombination.CONTROL_DOWN);
    }

    private void adicionarContadorDeLinha() {
        IntFunction<Node> numberFactory = line -> {
            String paddedLine = String.format("%5d ", line+1);
            Label label = new Label(paddedLine);
            label.getStyleClass().add("lineno");
            return label;
        };
        codeArea.setParagraphGraphicFactory(numberFactory);
    }

    private void aumentarFonte() {
        if (tamanhoFonte.get() + 2 > MAX_FONT) {
            return;
        }
        tamanhoFonte.set(tamanhoFonte.get() + 2);
    }

    private void diminuirFonte() {
        if (tamanhoFonte.get() - 2 < MIN_FONT) {
            return;
        }
        tamanhoFonte.set(tamanhoFonte.get() - 2);
    }

}
