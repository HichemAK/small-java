package com.utils;

import com.gen.Small_JavaBaseVisitor;
import com.gen.Small_JavaParser;

public class MyVisitor extends Small_JavaBaseVisitor<Info> {
    private SymbolTable ST = new SymbolTable();
    private QuadTable QT = new QuadTable();
    private String type = null;
    private boolean biblang_exist = false;
    private boolean bibio_exist = false;
    private int temp_n = 0;

    public String getNextTemp(){
        temp_n++;
        return "T" + temp_n;
    }

    public SymbolTable getST() {
        return ST;
    }

    public QuadTable getQT() {
        return QT;
    }

    private boolean checkIfDeclared(String idf, int line, int column) {
        if (!ST.contains(idf)){
            System.err.println(line+":"+ column +
                    " :: Variable '" + idf +"' is used without declaration");
            return false;
        }
        return true;
    }

    private boolean checkIdfLength(String idf, int line, int column){
        if (idf.length() > 10){
            System.err.println(line+":"+column+" :: Identifier '" + idf + "' has exceeded the length limit of 10");
            return false;
        }
        return true;
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
        boolean temp = checkIfDeclared(ctx.IDF().getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
        if(temp){
            Row row = ST.get(ST.indexOf(ctx.IDF().getText()));
            if(row.getType().equals("string_SJ") && ctx.string() == null){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: Cannot cast 'int_SJ/float_SJ' type to 'string_SJ' type");
            }
        }
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
                return null;
            }
        }
        Info v0 = visitV(ctx.v(0));
        if(ctx.v().size() == 1){
            return v0;
        }
        String t = getNextTemp();
        Info temp = new Info(t, v0.type);
        Quad q = new Quad(":=", temp, v0, null);
        QT.add(q);
        for(int i=1;i<ctx.v().size();i++){
            Info v = visitV(ctx.v(i));
            if(ctx.mul_div(i-1).DIV() != null){
                temp.type = "float_SJ";
            }
            else if(ctx.mul_div(i-1).MUL() != null){
                if(temp.type.equals("int_SJ") && v.type.equals("float_SJ")){
                    temp.type = "float_SJ";
                }
            }
            q = new Quad(ctx.mul_div(i-1).getText(), temp, temp, v);
            QT.add(q);
        }
        return new Info(t);
    }

    @Override public Info visitV(Small_JavaParser.VContext ctx) {
        if(ctx.IDF() != null){
            boolean temp = checkIfDeclared(ctx.IDF().getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
            if(temp){
                Row row = ST.get(ST.indexOf(ctx.IDF().getText()));
                if(row.getType().equals("string_SJ")){
                    System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                            " :: Cannot use a 'string_SJ' variable in an arithmetic/boolean expression");
                    return null;
                }
                return new Info("$" + row.getName(), row.getType());
            }
        }
        else if(ctx.FLOAT() != null){
            return new Info(ctx.FLOAT().getText(), "float_SJ");
        }
        else if (ctx.INT() != null){
            return new Info(ctx.INT().getText(), "int_SJ");
        }
        else if(ctx.exp() != null){
            return visitExp(ctx.exp());
        }
        return null;
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

    private boolean checkIfAlreadyDeclared(String idf, int line, int column) {
        if (ST.contains(idf)){
            System.err.println(line+":"+ column +
                    " :: Variable '" + idf +"' has already been declared");
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
    
    @Override public Info visitType(Small_JavaParser.TypeContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitModif(Small_JavaParser.ModifContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitFormat(Small_JavaParser.FormatContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitString(Small_JavaParser.StringContext ctx) { return visitChildren(ctx); }
    
    @Override public Info visitOp_compare(Small_JavaParser.Op_compareContext ctx) { return visitChildren(ctx); }
}
