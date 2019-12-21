package com.utils;

import com.gen.Small_JavaBaseVisitor;
import com.gen.Small_JavaParser;

public class MyVisitor extends Small_JavaBaseVisitor<Info> {
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

    @Override public Info visitR(Small_JavaParser.RContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitImport_bib(Small_JavaParser.Import_bibContext ctx) {
        return visitChildren(ctx);
    }
    
    @Override public Info visitClass_declare(Small_JavaParser.Class_declareContext ctx) {
        return visitChildren(ctx);
    }
    
    @Override public Info visitClass_content(Small_JavaParser.Class_contentContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitVars_declare(Small_JavaParser.Vars_declareContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitMain(Small_JavaParser.MainContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitInstruction(Small_JavaParser.InstructionContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitAssign(Small_JavaParser.AssignContext ctx) {
        if(!biblang_exist){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: Cannot use assign ':=' without importing Small_Java.lang");
        }
        checkIfDeclared(ctx.IDF().getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
        return visitChildren(ctx);
    }
    
    @Override public Info visitIf_cond(Small_JavaParser.If_condContext ctx) {
        if(!biblang_exist){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: Cannot use assign IF condition without importing Small_Java.lang");
        }
        return visitChildren(ctx);
    }
    
    @Override public Info visitRead(Small_JavaParser.ReadContext ctx) {
        if(!bibio_exist){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: Cannot use function 'In_SJ' without importing Small_Java.io");
        }
        checkIfDeclared(ctx.IDF().getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
        return visitChildren(ctx);
    }
    
    @Override public Info visitWrite(Small_JavaParser.WriteContext ctx) {
        if(!bibio_exist){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: Cannot use function 'Out_SJ' without importing Small_Java.io");
        }
        return visitChildren(ctx);
    }
    
    @Override public Info visitExp(Small_JavaParser.ExpContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitFactor(Small_JavaParser.FactorContext ctx) {
        for(int i=1;i<ctx.v().size();i++){
            if(ctx.mul_div(i-1).DIV() != null && ctx.v(i).getText().equals("0")){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: ERROR Division by Zero");
            }
        }
        return visitChildren(ctx);
    }

    @Override public Info visitV(Small_JavaParser.VContext ctx) {
        if(ctx.IDF() != null){
            checkIfDeclared(ctx.IDF().getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
        }
        return visitChildren(ctx);
    }



    @Override public Info visitExp_b(Small_JavaParser.Exp_bContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitFactor_b(Small_JavaParser.Factor_bContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitLiteral(Small_JavaParser.LiteralContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitAtom(Small_JavaParser.AtomContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitVar_declare(Small_JavaParser.Var_declareContext ctx) {
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


    @Override public Info visitBibs(Small_JavaParser.BibsContext ctx) {
        if(ctx.BIB_IO() != null){
            bibio_exist = true;
        }
        if(ctx.BIB_LANG() != null){
            biblang_exist = true;
        }
        return visitChildren(ctx);
    }
    
    @Override public Info visitType(Small_JavaParser.TypeContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitModif(Small_JavaParser.ModifContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitFormat(Small_JavaParser.FormatContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitString(Small_JavaParser.StringContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitOp_compare(Small_JavaParser.Op_compareContext ctx) { return visitChildren(ctx); }
}
