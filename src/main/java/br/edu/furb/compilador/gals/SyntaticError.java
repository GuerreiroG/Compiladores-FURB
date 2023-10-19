package br.edu.furb.compilador.gals;

public class SyntaticError extends AnalysisError {

    private String lexeme;

    public SyntaticError(String msg, int position, String lexeme) {
        super(msg, position);
        this.lexeme = lexeme;
    }

    public SyntaticError(String msg, int position) {
        super(msg, position);
    }

    public SyntaticError(String msg) {
        super(msg);
    }

    public String getLexeme() {
        if (lexeme.equals("$")) {
            return "EOF";
        }
        return lexeme;
    }
}
