package com.utils;

import com.gen.Small_JavaBaseVisitor;
import com.gen.Small_JavaParser;

public class MyVisitor<T> extends Small_JavaBaseVisitor<T> {
    private SymbolTable ST = new SymbolTable();
    private String type = null;
    private boolean biblang_exist = false;
    private boolean bibio_exist = false;

    public SymbolTable getST() {
        return ST;
    }

    private void checkIfDeclared(String idf, int line, int column) {
        if (!ST.contains(idf)){
            System.err.println(line+":"+ column +
                    " :: Variable '" + idf +"' is used without declaration");
        }
    }

    private void checkIdfLength(String idf, int line, int column){
        if (idf.length() > 10){
            System.err.println(line+":"+column+" :: Identifier '" + idf + "' has exceeded the length limit of 10");
        }
    }

    @Override public T visitR(Small_JavaParser.RContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitImport_bib(Small_JavaParser.Import_bibContext ctx) {
        return visitChildren(ctx);
    }
    
    @Override public T visitClass_declare(Small_JavaParser.Class_declareContext ctx) {
        return visitChildren(ctx);
    }
    
    @Override public T visitClass_content(Small_JavaParser.Class_contentContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitVars_declare(Small_JavaParser.Vars_declareContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitMain(Small_JavaParser.MainContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitInstruction(Small_JavaParser.InstructionContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitAssign(Small_JavaParser.AssignContext ctx) {
        if(!biblang_exist){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: Cannot use assign ':=' without importing Small_Java.lang");
        }
        checkIfDeclared(ctx.IDF().getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
        return visitChildren(ctx);
    }
    
    @Override public T visitIf_cond(Small_JavaParser.If_condContext ctx) {
        if(!biblang_exist){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: Cannot use assign IF condition without importing Small_Java.lang");
        }
        return visitChildren(ctx);
    }
    
    @Override public T visitRead(Small_JavaParser.ReadContext ctx) {
        if(!bibio_exist){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: Cannot use function 'In_SJ' without importing Small_Java.io");
        }
        checkIfDeclared(ctx.IDF().getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
        return visitChildren(ctx);
    }
    
    @Override public T visitWrite(Small_JavaParser.WriteContext ctx) {
        if(!bibio_exist){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: Cannot use function 'Out_SJ' without importing Small_Java.io");
        }
        return visitChildren(ctx);
    }
    
    @Override public T visitExp(Small_JavaParser.ExpContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitFactor(Small_JavaParser.FactorContext ctx) {
        for(int i=1;i<ctx.v().size();i++){
            if(ctx.mul_div(i-1).DIV() != null && ctx.v(i).getText().equals("0")){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: ERROR Division by Zero");
            }
        }
        return visitChildren(ctx);
    }

    @Override public T visitV(Small_JavaParser.VContext ctx) {
        if(ctx.IDF() != null){
            checkIfDeclared(ctx.IDF().getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
        }
        return visitChildren(ctx);
    }



    @Override public T visitExp_b(Small_JavaParser.Exp_bContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitFactor_b(Small_JavaParser.Factor_bContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitLiteral(Small_JavaParser.LiteralContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitAtom(Small_JavaParser.AtomContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitVar_declare(Small_JavaParser.Var_declareContext ctx) {
        type = ctx.type().getText();
        for(int i=0;i<ctx.IDF().size();i++){
            checkIdfLength(ctx.IDF(i).getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
            checkIfAlreadyDeclared(ctx.IDF(i).getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
            ST.assign(new Row(ctx.IDF(i).getText(), type, 0));
        }
        return visitChildren(ctx);
    }

    private void checkIfAlreadyDeclared(String idf, int line, int column) {
        if (ST.contains(idf)){
            System.err.println(line+":"+ column +
                    " :: Variable '" + idf +"' has already been declared");
        }
    }


    @Override public T visitBibs(Small_JavaParser.BibsContext ctx) {
        if(ctx.BIB_IO() != null){
            bibio_exist = true;
        }
        if(ctx.BIB_LANG() != null){
            biblang_exist = true;
        }
        return visitChildren(ctx);
    }
    
    @Override public T visitType(Small_JavaParser.TypeContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitModif(Small_JavaParser.ModifContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitFormat(Small_JavaParser.FormatContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitString(Small_JavaParser.StringContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitOp_compare(Small_JavaParser.Op_compareContext ctx) { return visitChildren(ctx); }
}
