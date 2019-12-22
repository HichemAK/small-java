package com.utils;

import com.gen.Small_JavaBaseVisitor;
import com.gen.Small_JavaParser;

public class SemanticVisitor extends Small_JavaBaseVisitor<Info> {
    private SymbolTable ST = new SymbolTable();
    private boolean biblang_exist = false;
    private boolean bibio_exist = false;
    private int num_errors = 0;

    public int getNum_errors() {
        return num_errors;
    }

    public SymbolTable getST() {
        return ST;
    }

    private boolean checkIfDeclared(String idf, int line, int column) {
        if (!ST.contains(idf)){
            System.err.println(line+":"+ column +
                    " :: Variable '" + idf +"' is used without declaration");
            num_errors++;
            return false;
        }
        return true;
    }

    private boolean checkIdfLength(String idf, int line, int column){
        if (idf.length() > 10){
            System.err.println(line+":"+column+" :: Identifier '" + idf + "' has exceeded the length limit of 10");
            num_errors++;
            return false;
        }
        return true;
    }

    @Override public Info visitAssign(Small_JavaParser.AssignContext ctx) {
        if(!biblang_exist){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: Cannot use assign ':=' without importing Small_Java.lang");
            num_errors++;
        }
        boolean temp = checkIfDeclared(ctx.IDF().getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
        if(temp){
            Row row = ST.get(ST.indexOf(ctx.IDF().getText()));
            if(row.getType().equals("string_SJ") && ctx.string() == null){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: Cannot cast 'int_SJ/float_SJ' type to 'string_SJ' type");
                num_errors++;
            }

        }
        return visitChildren(ctx);
    }

    @Override public Info visitIf_cond(Small_JavaParser.If_condContext ctx) {
        if(!biblang_exist){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: Cannot use assign IF condition without importing Small_Java.lang");
            num_errors++;
        }
        return visitChildren(ctx);
    }

    @Override public Info visitRead(Small_JavaParser.ReadContext ctx) {
        if(!bibio_exist){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: Cannot use function 'In_SJ' without importing Small_Java.io");
            num_errors++;
        }
        checkIfDeclared(ctx.IDF().getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
        return visitChildren(ctx);
    }

    @Override public Info visitWrite(Small_JavaParser.WriteContext ctx) {
        if(!bibio_exist){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: Cannot use function 'Out_SJ' without importing Small_Java.io");
            num_errors++;
        }
        return visitChildren(ctx);
    }


    @Override public Info visitFactor(Small_JavaParser.FactorContext ctx) {
        for(int i=1;i<ctx.v().size();i++){
            if(ctx.mul_div(i-1).DIV() != null && ctx.v(i).getText().equals("0")){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: ERROR Division by Zero");
                num_errors++;
            }
        }
        return visitChildren(ctx);
    }

    @Override public Info visitV(Small_JavaParser.VContext ctx) {
        if(ctx.IDF() != null){
            boolean temp = checkIfDeclared(ctx.IDF().getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
            if(temp){
                Row row = ST.get(ST.indexOf(ctx.IDF().getText()));
                if(row.getType().equals("string_SJ")){
                    System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                            " :: Cannot use a 'string_SJ' variable in an arithmetic/boolean expression");
                    num_errors++;
                }
            }
        }
        return visitChildren(ctx);
    }

    @Override public Info visitVar_declare(Small_JavaParser.Var_declareContext ctx) {
        for(int i=0;i<ctx.IDF().size();i++){
            checkIdfLength(ctx.IDF(i).getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
            checkIfAlreadyDeclared(ctx.IDF(i).getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
            ST.assign(new Row(ctx.IDF(i).getText(), ctx.type().getText(), 0));
        }
        return visitChildren(ctx);
    }

    private boolean checkIfAlreadyDeclared(String idf, int line, int column) {
        if (ST.contains(idf)){
            System.err.println(line+":"+ column +
                    " :: Variable '" + idf +"' has already been declared");
            num_errors++;
            return false;
        }
        return true;
    }


    @Override public Info visitBibs(Small_JavaParser.BibsContext ctx) {
        if(ctx.BIB_IO() != null){
            bibio_exist = true;
        }
        if(ctx.BIB_LANG() != null){
            biblang_exist = true;
        }
        return visitChildren(ctx);
    }

    @Override
    public Info visitClass_declare(Small_JavaParser.Class_declareContext ctx) {
        checkIdfLength(ctx.CLASS_IDF().getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
        return visitChildren(ctx);
    }
}

