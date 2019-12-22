package com.utils;

import com.gen.Small_JavaBaseVisitor;
import com.gen.Small_JavaParser;

import java.util.ArrayList;

public class QuadVisitor extends Small_JavaBaseVisitor<Info> {
    private SymbolTable ST = new SymbolTable();
    private QuadTable QT = new QuadTable();
    private boolean biblang_exist = false;
    private boolean bibio_exist = false;
    private int temp_n = 0;

    public QuadVisitor(SymbolTable ST){
        this.ST = ST;
    }

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

    @Override public Info visitAssign(Small_JavaParser.AssignContext ctx) {
        Row r = ST.get(ST.indexOf(ctx.IDF().getText()));
        Quad q;
        if(ctx.exp_b() != null){
            q = new Quad(":=", new Info(r.getName(), r.getType()), visitExp_b(ctx.exp_b()), null);
            QT.add(q);
        }
        else if(ctx.string() != null){
            q = new Quad(":=", new Info(r.getName(), r.getType()), visitString(ctx.string()), null);
            QT.add(q);
        }
        return null;
    }

    @Override public Info visitIf_cond(Small_JavaParser.If_condContext ctx) {

        return visitChildren(ctx);
    }

    @Override public Info visitRead(Small_JavaParser.ReadContext ctx) {

        return visitChildren(ctx);
    }

    @Override public Info visitWrite(Small_JavaParser.WriteContext ctx) {

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
            Row row = ST.get(ST.indexOf(ctx.IDF().getText()));
            return new Info("$" + row.getName(), row.getType());
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

    @Override public Info visitExp_b(Small_JavaParser.Exp_bContext ctx) {
        if(ctx.factor_b().size() == 1){
            return visitFactor_b(ctx.factor_b(0));
        }
        Info v1;
        Quad q;
        Info temp1 = new Info(getNextTemp(), "int_SJ");
        q = new Quad(":=", temp1, new Info("0", "int_SJ"), null);
        QT.add(q);
        Info v0 = visitFactor_b(ctx.factor_b(0));
        for(int i=1;i<ctx.factor_b().size();i++){
            v1 = visitFactor_b(ctx.factor_b(i));
            q = new Quad("OR", temp1, v0, v1);
            QT.add(q);
        }
        return temp1;
    }

    @Override public Info visitFactor_b(Small_JavaParser.Factor_bContext ctx) {
        if(ctx.literal().size() == 1){
            return visitLiteral(ctx.literal(0));
        }
        Info v1;
        Quad q;
        Info temp1 = new Info(getNextTemp(), "int_SJ");
        q = new Quad(":=", temp1, new Info("1", "int_SJ"), null);
        QT.add(q);
        Info v0 = visitLiteral(ctx.literal(0));
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
}
