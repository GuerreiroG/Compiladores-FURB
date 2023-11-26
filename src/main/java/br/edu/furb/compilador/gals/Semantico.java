package br.edu.furb.compilador.gals;

import java.util.*;

public class Semantico implements Constants {
    private final Deque<String> pilhaTipos = new ArrayDeque<>();
    private final Deque<String> pilhaRotulos = new ArrayDeque<>();
    private final List<Token> listaId = new ArrayList<>();
    private final HashMap<String, Simbolo> tabelaSimbolos = new HashMap<>();
    private final StringBuilder codigo = new StringBuilder();
    private String operadorRelacional = "";
    private static final String TIPO_INTEIRO = "int64";
    private static final String TIPO_FLOAT = "float64";
    private static final String TIPO_STRING = "string";
    private static final String TIPO_BOOL = "bool";

    public void executeAction(int action, Token token) throws SemanticError {
        switch (action) {
            case 100 -> acao100();
            case 101 -> acao101();
            case 102 -> acao102();
            case 103 -> acao103();
            case 104 -> acao104();
            case 105 -> acao105();
            case 106 -> acao106();
            case 107 -> acao107();
            case 108 -> acao108(token);
            case 109 -> acao109();
            case 110 -> acao110();
            case 111 -> acao111();
            case 112 -> acao112();
            case 113 -> acao113();
            case 114 -> acao114(token);
            case 115 -> acao115(token);
            case 116 -> acao116(token);
            case 117 -> acao117();
            case 118 -> acao118(token);
            case 119 -> acao119();
            case 120 -> acao120();
            case 121 -> acao121();
            case 122 -> acao122(token);
            case 123 -> acao123();
            case 124 -> acao124(token);
            case 125 -> acao125(token);
            case 126 -> acao126(token);
            case 127 -> acao127(token);
            case 128 -> acao128();
            case 129 -> acao129();
            case 130 -> acao130(token);
            case 131 -> acao131(token);
        }
    }

    public void adicionarCodigo(String texto) {
        codigo.append("      ").append(texto).append("\n");
    }

    public String getCodigo() {
        return codigo.toString();
    }

    public void acao100() {
        adicionarCodigo("""

                .assembly extern mscorlib {}
                .assembly _exemplo{}
                .module _exemplo.exe

                .class public _exemplo{
                   .method static public void _principal() {
                      .entrypoint
                """);
    }

    public void acao101() {
        adicionarCodigo("""

                      ret
                   }
                }
                """);
    }

    public void acao102() {
        String tipo = pilhaTipos.pop();
        if (TIPO_INTEIRO.equals(tipo)) {
            adicionarCodigo("conv.i8");
        }
        adicionarCodigo(String.format("call void [mscorlib]System.Console::WriteLine(%s)", tipo));
    }

    public void acao103() {
        pilhaTipos.pop();
        pilhaTipos.pop();
        adicionarCodigo("and");
        pilhaTipos.push(TIPO_BOOL);
    }

    public void acao104() {
        pilhaTipos.pop();
        pilhaTipos.pop();
        adicionarCodigo("or");
        pilhaTipos.push(TIPO_BOOL);
    }

    public void acao105() {
        pilhaTipos.push(TIPO_BOOL);
        adicionarCodigo("ldc.i4.1");
    }

    public void acao106() {
        pilhaTipos.push(TIPO_BOOL);
        adicionarCodigo("ldc.i4.0");
    }

    public void acao107() {
        adicionarCodigo("ldc.i4.1");
        adicionarCodigo("xor");
    }

    public void acao108(Token token) {
        operadorRelacional = token.getLexeme();
    }

    public void acao109() {
        pilhaTipos.pop();
        pilhaTipos.pop();
        pilhaTipos.push(TIPO_BOOL);
        switch (operadorRelacional) {
            case ">" -> adicionarCodigo("cgt");
            case "<" -> adicionarCodigo("clt");
            case "==" -> adicionarCodigo("ceq");
            case "!=" -> {
                adicionarCodigo("ceq");
                adicionarCodigo("ldc.i4.1");
                adicionarCodigo("xor");
            }
        }
    }

    public void acao110() {
        String tipo1 = pilhaTipos.pop();
        String tipo2 = pilhaTipos.pop();
        adicionarCodigo("add");
        if (TIPO_FLOAT.equals(tipo1) || TIPO_FLOAT.equals(tipo2)) {
            pilhaTipos.push(TIPO_FLOAT);
        } else {
            pilhaTipos.push(TIPO_INTEIRO);
        }
    }

    public void acao111() {
        String tipo1 = pilhaTipos.pop();
        String tipo2 = pilhaTipos.pop();
        adicionarCodigo("sub");
        if (TIPO_FLOAT.equals(tipo1) || TIPO_FLOAT.equals(tipo2)) {
            pilhaTipos.push(TIPO_FLOAT);
        } else {
            pilhaTipos.push(TIPO_INTEIRO);
        }
    }

    public void acao112() {
        String tipo1 = pilhaTipos.pop();
        String tipo2 = pilhaTipos.pop();
        adicionarCodigo("mul");
        if (TIPO_FLOAT.equals(tipo1) || TIPO_FLOAT.equals(tipo2)) {
            pilhaTipos.push(TIPO_FLOAT);
        } else {
            pilhaTipos.push(TIPO_INTEIRO);
        }
    }

    public void acao113() {
        String tipo1 = pilhaTipos.pop();
        String tipo2 = pilhaTipos.pop();
        adicionarCodigo("div");
        if (TIPO_FLOAT.equals(tipo1) || TIPO_FLOAT.equals(tipo2)) {
            pilhaTipos.push(TIPO_FLOAT);
        } else {
            pilhaTipos.push(TIPO_INTEIRO);
        }
    }

    public void acao114(Token token) {
        pilhaTipos.push(TIPO_INTEIRO);
        adicionarCodigo(String.format("ldc.i8 %s", token.getLexeme()));
        adicionarCodigo("conv.r8");
    }

    public void acao115(Token token) {
        pilhaTipos.push(TIPO_FLOAT);
        adicionarCodigo(String.format(Locale.US, "ldc.r8 %s", token.getLexeme()));
    }

    public void acao116(Token token) {
        pilhaTipos.push(TIPO_STRING);
        adicionarCodigo(String.format("ldstr %s", token.getLexeme()));
    }

    public void acao117() {
        adicionarCodigo("ldc.i8 -1");
        adicionarCodigo("conv.r8");
        adicionarCodigo("mul");
    }

    public void acao118(Token token) throws SemanticError {
        String tipo = pilhaTipos.pop();
        if (!TIPO_BOOL.equals(tipo)) {
            throw new SemanticError("expressão incompatível em comando de seleção", token.getPosition());
        }
        String rotulo = String.format("novo_rotulo%d", pilhaRotulos.size() + 1);
        adicionarCodigo(String.format("brfalse %s", rotulo));
        pilhaRotulos.push(rotulo);
    }

    public void acao119() {
        String rotulo = pilhaRotulos.pop();
        adicionarCodigo(String.format("%s:", rotulo));
        pilhaRotulos.push(rotulo);
    }

    public void acao120() {
        String rotulo = String.format("novo_rotulo%d", pilhaRotulos.size() + 1);
        adicionarCodigo("br novo_rotulo2");
        String rotuloIf = pilhaRotulos.pop();
        adicionarCodigo(String.format("%s:", rotuloIf));
        pilhaRotulos.push(rotulo);
    }

    public void acao121() {
        String rotulo = String.format("novo_rotulo%d", pilhaRotulos.size() + 1);
        adicionarCodigo(String.format("%s:", rotulo));
        pilhaRotulos.push(rotulo);
    }

    public void acao122(Token token) throws SemanticError {
        String tipo = pilhaTipos.pop();
        if (!TIPO_BOOL.equals(tipo)) {
            throw new SemanticError("expressão incompatível em comando de repetição", token.getPosition());
        }
        String rotulo = String.format("novo_rotulo%d", pilhaRotulos.size() + 1);
        adicionarCodigo(String.format("brfalse %s", rotulo));
        pilhaRotulos.push(rotulo);
    }

    public void acao123() {
        String rotulo2 = pilhaRotulos.pop();
        String rotulo1 = pilhaRotulos.pop();
        adicionarCodigo(String.format("br %s", rotulo1));
        adicionarCodigo(String.format("%s:", rotulo2));
    }

    public void acao124(Token token) throws SemanticError {
        String tipo = pilhaTipos.pop();
        if (!TIPO_BOOL.equals(tipo)) {
            throw new SemanticError("expressão incompatível em comando de repetição", token.getPosition());
        }
        String rotulo = pilhaRotulos.pop();
        adicionarCodigo(String.format("brtrue %s", rotulo));
    }

    public void acao125(Token token) {
        listaId.add(token);
    }

    public void acao126(Token valorToken) throws SemanticError {
        for (Token token : listaId) {
            String identificador = token.getLexeme();
            if (tabelaSimbolos.containsKey(identificador)) {
                throw new SemanticError(String.format("%s já declarado", identificador), token.getPosition());
            }
            String tipo = getTipoDoIdentificador(identificador);
            String valor = valorToken.getLexeme();
            if (valor.startsWith("_")) {
                valor = null;
            }
            tabelaSimbolos.put(identificador, new Simbolo(identificador, tipo, valor));
        }
        listaId.clear();
    }

    private String getTipoDoIdentificador(String identificador) {
        if (identificador.startsWith("_i")) {
            return TIPO_INTEIRO;
        } else if (identificador.startsWith("_f")) {
            return TIPO_FLOAT;
        } else if (identificador.startsWith("_s")) {
            return TIPO_STRING;
        } else {
            return TIPO_BOOL;
        }
    }

    public void acao127(Token valorToken) throws SemanticError {
        for (Token token : listaId) {
            String identificador = token.getLexeme();
            if (tabelaSimbolos.containsKey(identificador)) {
                throw new SemanticError(String.format("%s já declarado", identificador), token.getPosition());
            }
            String tipo = getTipoDoIdentificador(identificador);
            String valor = valorToken.getLexeme();
            if (valor.startsWith("_")) {
                valor = null;
            }
            tabelaSimbolos.put(identificador, new Simbolo(identificador, tipo, valor));
            adicionarCodigo(String.format(".locals(%s %s)", tipo, identificador));
        }
        listaId.clear();
    }

    public void acao128() throws SemanticError {
        for (int i = 0; i < listaId.size() - 1; i++) {
            adicionarCodigo("dup");
        }
        for (Token token : listaId) {
            String identificador = token.getLexeme();
            if (!tabelaSimbolos.containsKey(identificador)) {
                throw new SemanticError(String.format("%s não declarado", identificador), token.getPosition());
            }
            Simbolo simbolo = tabelaSimbolos.get(identificador);
            if (TIPO_INTEIRO.equals(simbolo.tipo())) {
                adicionarCodigo("conv.i8");
            }
            adicionarCodigo(String.format("stloc %s", identificador));
        }
        listaId.clear();
    }

    public void acao129() throws SemanticError {
        for (Token token : listaId) {
            String identificador = token.getLexeme();
            if (!tabelaSimbolos.containsKey(identificador)) {
                throw new SemanticError(String.format("%s não declarado", identificador), token.getPosition());
            }
            Simbolo simbolo = tabelaSimbolos.get(identificador);
            adicionarCodigo("call string [mscorlib]System.Console::ReadLine()");
            String tipo = simbolo.tipo();
            if (!TIPO_STRING.equals(tipo)) {
                String classe = getClasse(tipo);
                adicionarCodigo(String.format("call %s [mscorlib]System.%s::Parse(string)", tipo, classe));
            }
            adicionarCodigo(String.format("stloc %s", identificador));
        }
        listaId.clear();
    }

    public String getClasse(String tipo) {
        return switch (tipo) {
            case TIPO_BOOL -> "Boolean";
            case TIPO_INTEIRO -> "Int64";
            case TIPO_FLOAT -> "Float64";
            default -> "String";
        };
    }

    public void acao130(Token token) {
        adicionarCodigo(String.format("ldstr %s", token.getLexeme()));
        adicionarCodigo("call void [mscorlib]System.Console::Write(string)");
    }

    public void acao131(Token token) throws SemanticError {
        String lexema = token.getLexeme();
        if (!tabelaSimbolos.containsKey(lexema)) {
            throw new SemanticError(String.format("%s não declarado", lexema), token.getPosition());
        }
        Simbolo simbolo = tabelaSimbolos.get(lexema);
        String valor = simbolo.valor();
        String tipo = simbolo.tipo();
        String identificador = simbolo.id();
        if (simbolo.valor() != null) {
            tratarIdentificadorDeConstante(tipo, valor);
            return;
        }
        adicionarCodigo(String.format("ldloc %s", identificador));
        if (TIPO_INTEIRO.equals(tipo)) {
            adicionarCodigo("conv.r8");
        }
        pilhaTipos.push(tipo);
    }

    private void tratarIdentificadorDeConstante(String tipo, String valor) {
        switch (tipo) {
            case TIPO_INTEIRO -> {
                adicionarCodigo(String.format("ldc.i8 %s", valor));
                adicionarCodigo("conv.r8");
            }
            case TIPO_FLOAT -> adicionarCodigo(String.format("ldc.r8 %s", valor));
            case TIPO_STRING -> adicionarCodigo(String.format("ldstr %s", valor));
            case TIPO_BOOL -> adicionarCodigo(valor.equals("true") ? "ldc.i4.1" : "ldc.i4.0");
        }
        pilhaTipos.push(tipo);
    }

}
