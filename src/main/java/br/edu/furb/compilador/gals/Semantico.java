package br.edu.furb.compilador.gals;

import java.util.*;

public class Semantico implements Constants {
    // declarar atributos conrrespondentes aos atributos semanticos
    // - operador_relacional (inicialmente igual a ""): usado para armazenar o operador relacional reconhecido pela ação
    // #108, para uso posterior na ação #109
    // - código_objeto: usado para armazenar o código objeto gerado
    // - pilha_tipos (inicialmente vazia): usada para determinar o tipo de uma expressão durante a compilação do
    // programa.

    private Stack<String> pilhaTipos;
    private Stack<String> pilhaRotulos;
    private List<Token> listaId;
    private HashMap<String, Simbolo> tabelaSimbolos;
    private StringBuilder codigo;
    private String operadorRelacional;

    private final String TIPO_INTEIRO = "int64";
    private final String TIPO_FLOAT = "float64";
    private final String TIPO_STRING = "string";
    private final String TIPO_BOOL = "bool";

    public Semantico() {
        pilhaTipos = new Stack<>();
        pilhaRotulos = new Stack<>();
        codigo = new StringBuilder();
        operadorRelacional = "";
        listaId = new ArrayList<>();

    }

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
            case 131 -> acao131(token);
        }
        System.out.println("Ação #"+action+", Token: "+token);
    }

    public void adicionarCodigo(String texto) {
        codigo.append(texto).append("\n");
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
                   .method static public void _principal(){
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
            case ">"  -> adicionarCodigo("cgt");
            case "<"  -> adicionarCodigo("clt");
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
            tabelaSimbolos.put(identificador, new Simbolo(identificador, tipo, valorToken.getLexeme()));
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
        for (Token token: listaId) {
            String identificador = token.getLexeme();
            if (tabelaSimbolos.containsKey(identificador)) {
                throw new SemanticError(String.format("%s já declarado", identificador), token.getPosition());
            }
            String tipo = getTipoDoIdentificador(identificador);
            tabelaSimbolos.put(identificador, new Simbolo(identificador, tipo, valorToken.getLexeme()));
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
            adicionarCodigo(String.format("stloc %s", identificador));
            Simbolo simbolo = tabelaSimbolos.get(identificador);
            if (TIPO_INTEIRO.equals(simbolo.tipo())) {
                adicionarCodigo("conv.i8");
            }
        }
        listaId.clear();
    }

    public void acao129() throws SemanticError {
        for (Token token : listaId) {
            String identificador = token.getLexeme();
            if (!tabelaSimbolos.containsKey(identificador)) {
                throw new SemanticError(String.format("%s não declarado", identificador), token.getPosition());
            }
        }
    }

    public void acao130(Token token) {
        adicionarCodigo(String.format("ldstr %s", token));
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
        if (simbolo.valor() != null) {
            tratarIdentificadorDeConstante(tipo, valor);
            return;
        }
        adicionarCodigo(String.format("ldloc %s", valor));
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
            case TIPO_FLOAT -> {
                adicionarCodigo(String.format("ldc.r8 %s", valor));
            }
            case TIPO_STRING -> {
                adicionarCodigo(String.format("ldstr %s", valor));
            }
            case TIPO_BOOL -> {
                adicionarCodigo(valor.equals("true") ? "ldc.i4.1" : "ldc.i4.0");
            }
        }
        pilhaTipos.push(tipo);
    }

}
