package br.edu.furb.compilador.interfaces;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfiguracaoDestacador {

    private ConfiguracaoDestacador(){}
    private static final String[] PALAVRAS_RESERVADAS = new String[] {
            "do", "else", "false", "fun", "if", "in", "main", "out", "repeat", "true", "while"
    };
    private static final String PALAVRA_RESERVADA_PADRAO =
            "\\b(" + String.join("|", PALAVRAS_RESERVADAS) + ")\\b";
    private static final String[] PREFIXOS = new String[] {
            "_i", "_f", "_b", "_s"
    };

    private static final String PARENTESES_PADRAO = "[()]";
    private static final String CHAVES_PADRAO = "[{}]";
    private static final String PONTO_VIRGULA_PADRAO = ";";
    private static final String STRING_PADRAO = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMENTARIO_PADRAO =
            "#[^\n]*" + "|" + "\\[[^\\[\\]]*\\]";
    private static final String IDENTIFICADOR_PADRAO =
            "(?<=" + String.join("|", PREFIXOS) + ")"
                    + "(([a-z]|[A-Z][a-zA-Z0-9])([a-zA-Z0-9]|[A-Z][a-zA-Z0-9])*[A-Z]?|[A-Z])";
    private static final String PREFIXO_PADRAO =
            "(" + String.join("|", PREFIXOS) + ")(?=" + IDENTIFICADOR_PADRAO + ")";
    private static final Pattern PADRAO = Pattern.compile(
                "(?<KEYWORD>" + PALAVRA_RESERVADA_PADRAO + ")"
                    + "|(?<PAREN>" + PARENTESES_PADRAO + ")"
                    + "|(?<BRACE>" + CHAVES_PADRAO + ")"
                    + "|(?<SEMICOLON>" + PONTO_VIRGULA_PADRAO + ")"
                    + "|(?<STRING>" + STRING_PADRAO + ")"
                    + "|(?<COMMENT>" + COMENTARIO_PADRAO + ")"
                    + "|(?<ID>" + IDENTIFICADOR_PADRAO + ")"
                    + "|(?<PREFIX>" + PREFIXO_PADRAO + ")"
            );


    public static StyleSpans<Collection<String>> computarDestaque(String texto) {
        Matcher matcher = PADRAO.matcher(texto);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = null;
            if (matcher.group("KEYWORD") != null) {
                styleClass = "keyword";
            } else if (matcher.group("PAREN") != null) {
                styleClass = "paren";
            } else if (matcher.group("BRACE") != null) {
                styleClass = "brace";
            } else if (matcher.group("SEMICOLON") != null) {
                styleClass = "semicolon";
            } else if (matcher.group("STRING") != null) {
                styleClass = "string";
            } else if (matcher.group("COMMENT") != null) {
                styleClass = "comment";
            } else if (matcher.group("ID") != null) {
                styleClass = "id";
            } else if (matcher.group("PREFIX") != null) {
                styleClass= "prefix";
            }
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), texto.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
