package com.utils;

import com.gen.Small_JavaBaseVisitor;
import com.gen.Small_JavaParser;

import java.util.ArrayList;

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
    
    @Override public Info visitExp(Small_JavaParser.ExpContext ctx) {
        Info v0 = visitFactor(ctx.factor(0));
        if(ctx.factor().size() == 1){
            return v0;
        }
        String t = getNextTemp();
        Info temp = new Info(t, v0.type);
        Quad q = new Quad(":=", temp, v0, null);
        QT.add(q);
        for(int i=1;i<ctx.factor().size();i++){
            Info v = visitFactor(ctx.factor(i));
            if(temp.type.equals("int_SJ") && v.type.equals("float_SJ")){
                temp.type = "float_SJ";
            }
            q = new Quad(ctx.plus_minus(i-1).getText(), temp, temp, v);
            QT.add(q);
        }
        return temp;
    }
    
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
        return temp;
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
    
    @Override public Info visitFactor_b(Small_JavaParser.Factor_bContext ctx) {
        if(ctx.literal().size() == 1){
            return visitLiteral(ctx.literal(0));
        }
        Info v0 = visitLiteral(ctx.literal(0));
        Info v1;
        Quad q;
        Info temp1 = new Info(getNextTemp(), "int_SJ");
        q = new Quad(":=", temp1, new Info("1", "int_SJ"), null);
        QT.add(q);
        for(int i=1;i<ctx.literal().size();i++){
            v1 = visitLiteral(ctx.literal(i));
            q = new Quad("AND", temp1, v0, v1);
            QT.add(q);
        }
        return temp1;
    }
    
    @Override public Info visitLiteral(Small_JavaParser.LiteralContext ctx) {
        if(ctx.NOT() == null){
            return visitAtom(ctx.atom());
        }
        Info v = visitAtom(ctx.atom());
        Quad q = new Quad("NOT", v, v, null);
        QT.add(q);
        return v;
    }
    
    @Override public Info visitAtom(Small_JavaParser.AtomContext ctx) {
        if(ctx.exp_b() != null){
            return visitExp_b(ctx.exp_b());
        }
        if(ctx.exp().size() == 1){
            return visitExp(ctx.exp(0));
        }
        Quad q;
        Info temp1 = new Info(getNextTemp(), "int_SJ");
        q = new Quad(":=", temp1, new Info("1", "int_SJ"), null);
        QT.add(q);
        Info temp2 = new Info(getNextTemp(), "int_SJ");
        Info v1, v2;
        ArrayList<Info> vs = new ArrayList<Info>();
        for(int i=0;i<ctx.exp().size();i++){
            vs.add(visitExp(ctx.exp(i)));
        }

        for(int i=0;i<ctx.exp().size()-1;i++){
            v1 = vs.get(i);
            v2 = vs.get(i+1);
            q = new Quad(ctx.op_compare(i).getText(), temp2, v1, v2);
            QT.add(q);
            q = new Quad("AND", temp1, temp1, temp2);
            QT.add(q);
        }
        return temp1;
    }
    
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
