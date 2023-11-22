package br.edu.furb.compilador.ui;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfiguracaoDestacador {

    private ConfiguracaoDestacador() {
    }

    private static final String[] PALAVRAS_RESERVADAS = new String[]{
            "do", "else", "false", "fun", "if", "in", "main", "out", "repeat", "true", "while"
    };
    private static final String RESERVADA_PADRAO =
            "\\b(" + String.join("|", PALAVRAS_RESERVADAS) + ")\\b";
    private static final String[] PREFIXOS = new String[]{
            "_i", "_f", "_b", "_s"
    };

    private static final String PARENTESES_PADRAO = "[()]";
    private static final String CHAVES_PADRAO = "[{}]";
    private static final String STRING_PADRAO = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMENTARIO_PADRAO =
            "#[^\n]*" + "|" + "\\[[^\\[\\]]*\\]";
    private static final String IDENTIFICADOR_PADRAO =
            "(?<=" + String.join("|", PREFIXOS) + ")"
                    + "(([a-z]|[A-Z][a-zA-Z0-9])([a-zA-Z0-9]|[A-Z][a-zA-Z0-9])*[A-Z]?|[A-Z])";
    private static final String PREFIXO_PADRAO =
            "(" + String.join("|", PREFIXOS) + ")(?=" + IDENTIFICADOR_PADRAO + ")";

    public static final String NUMERO_PADRAO = "([1-9][0-9]*|0)(\\.[0-9]+)?";


    private static final Pattern PADRAO = Pattern.compile(
            "(?<RESERVADA>" + RESERVADA_PADRAO + ")"
                    + "|(?<PAREN>" + PARENTESES_PADRAO + ")"
                    + "|(?<CHAVE>" + CHAVES_PADRAO + ")"
                    + "|(?<STRING>" + STRING_PADRAO + ")"
                    + "|(?<COMENTARIO>" + COMENTARIO_PADRAO + ")"
                    + "|(?<NUMERO>" + NUMERO_PADRAO + ")"
                    + "|(?<ID>" + IDENTIFICADOR_PADRAO + ")"
                    + "|(?<PREFIXO>" + PREFIXO_PADRAO + ")"

    );

    public static StyleSpans<Collection<String>> computarDestaque(String texto) {
        Matcher matcher = PADRAO.matcher(texto);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = null;
            if (matcher.group("RESERVADA") != null) {
                styleClass = "reservada";
            } else if (matcher.group("PAREN") != null) {
                styleClass = "paren";
            } else if (matcher.group("CHAVE") != null) {
                styleClass = "chave";
            } else if (matcher.group("STRING") != null) {
                styleClass = "string";
            } else if (matcher.group("COMENTARIO") != null) {
                styleClass = "comentario";
            } else if (matcher.group("ID") != null) {
                styleClass = "id";
            } else if (matcher.group("PREFIXO") != null) {
                styleClass = "prefixo";
            } else if (matcher.group("NUMERO") != null) {
                styleClass = "numero";
            }
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), texto.length() - lastKwEnd);
        return spansBuilder.create();
    }

}
