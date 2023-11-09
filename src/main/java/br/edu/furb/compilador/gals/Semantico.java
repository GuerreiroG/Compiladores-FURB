package br.edu.furb.compilador.gals;

import java.util.Locale;
import java.util.Stack;

public class Semantico implements Constants {
    // declarar atributos conrrespondentes aos atributos semanticos
    // - operador_relacional (inicialmente igual a ""): usado para armazenar o operador relacional reconhecido pela ação
    // #108, para uso posterior na ação #109
    // - código_objeto: usado para armazenar o código objeto gerado
    // - pilha_tipos (inicialmente vazia): usada para determinar o tipo de uma expressão durante a compilação do
    // programa.

    private Stack<String> pilhaTipos;
    private StringBuilder codigo;
    private String operadorRelacional;

    private final String TIPO_INTEIRO = "int64";
    private final String TIPO_FLOAT = "float64";
    private final String TIPO_STRING = "string";
    private final String TIPO_BOOL = "bool";

    public Semantico() {
        this.pilhaTipos = new Stack<>();
        this.codigo = new StringBuilder();
        this.operadorRelacional = "";
    }

    public void executeAction(int action, Token token)	throws SemanticError {
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
            case 117 -> acao117(token);
        }
        System.out.println("Ação #"+action+", Token: "+token);
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
        adicionarCodigo("ldc.r4 1");
    }

    public void acao106() {
        pilhaTipos.push(TIPO_BOOL);
        adicionarCodigo("ldc.r4 0");
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

    public void acao117(Token token) {
        adicionarCodigo("ldc.i8 -1");
        adicionarCodigo("conv.r8");
        adicionarCodigo("mul");
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

    public void adicionarCodigo(String texto) {
        codigo.append(texto).append("\n");
    }

    public String getCodigo() {
        return codigo.toString();
    }
}
