package br.edu.furb.compilador.gals;

public class LexicalError extends AnalysisError {
    private String lexeme;

    public LexicalError(String msg, int position, String lexeme) {
        super(msg, position);
        this.lexeme = lexeme;
    }

    public LexicalError(String msg, int position) {
        super(msg, position);
    }

    public String getLexeme() {
        return lexeme;
    }
}
