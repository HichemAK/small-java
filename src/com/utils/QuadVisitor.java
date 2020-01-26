package com.utils;

import com.gen.Small_JavaBaseVisitor;
import com.gen.Small_JavaParser;

import java.util.ArrayList;

public class QuadVisitor extends Small_JavaBaseVisitor<Info> {
    private SymbolTable ST = new SymbolTable();
    private QuadTable QT = new QuadTable();
    private int temp_n = 0;
    private int str_n = 0;
    private ArrayList<Info> temps = new ArrayList<>();

    public QuadVisitor(){}

    public QuadVisitor(SymbolTable ST){
        this.ST = ST;
    }

    private String getNextTemp(){
        return "__T" + temp_n++;
    }

    private String getNextStr(){
        return "__STR" + str_n++;
    }

    public SymbolTable getST() {
        return ST;
    }

    public QuadTable getQT() {
        return QT;
    }

    @Override
    public Info visitR(Small_JavaParser.RContext ctx) {
        visitChildren(ctx);
        for(int i=0;i<temps.size();i++){
            Info temp = temps.get(i);
            Row r = new Row(temp.name, temp.type, temp.value);
            ST.add(r);
        }
        QT.add(new Quad("END", null, null, null));
        return null;
    }

    @Override public Info visitAssign(Small_JavaParser.AssignContext ctx) {
        Row r = ST.get(ST.indexOf(ctx.IDF().getText()));
        Quad q;
        if(ctx.exp_b() != null){
            q = new Quad(":=", new Info(r.getName(), r.getType()), visitExp_b(ctx.exp_b()), null);
            QT.add(q);
        }
        else if(ctx.string() != null){
            Info temp = visitString(ctx.string());
            q = new Quad(":=", new Info(r.getName(), r.getType(), ""), temp, null);
            QT.add(q);
        }
        return null;
    }

    @Override public Info visitIf_cond(Small_JavaParser.If_condContext ctx) {
        Info cond = visitExp_b(ctx.exp_b());
        Quad q1 = new Quad("BZ", null, cond, null);
        Quad q2 = null;
        QT.add(q1);
        for(int i=0;i<ctx.instruction().size();i++){
            visitInstruction(ctx.instruction(i));
        }
        if(ctx.ELSE_KW() != null){
            q2 = new Quad("BR", null, null, null);
            QT.add(q2);
        }
        q1.a = new Info("__QUAD" + QT.size(), "adr");
        if(ctx.ELSE_KW() != null){
            for(int i=0;i<ctx.instruction2().size();i++){
                visitInstruction2(ctx.instruction2(i));
            }
            q2.a = new Info("__QUAD" + QT.size(), "adr");
        }
        return null;
    }

    @Override public Info visitRead(Small_JavaParser.ReadContext ctx) {
        Row r = ST.get(ST.indexOf(ctx.IDF().getText()));
        Quad q = new Quad("PUSH", new Info(r.getName(), r.getType()), new Info("ref"), null);
        QT.add(q);
        q = new Quad("PUSH", visitString(ctx.string()), new Info("ref"), null);
        QT.add(q);
        q = new Quad("CALL", new Info("_scanf"), null, null);
        QT.add(q);
        return null;
    }

    @Override public Info visitWrite(Small_JavaParser.WriteContext ctx) {
        for(int i=ctx.exp_b().size()-1;i>=0;i--){
            Info exp = visitExp_b(ctx.exp_b(i));
            Quad q;
            if(exp.type.equals("float_SJ")){
                q = new Quad("PRINT_FLOAT", exp, null, null);
            }
            else{
                if(exp.type.equals("string_SJ")){
                    q = new Quad("PUSH", exp, new Info("ref"), null);
                }
                else{
                    q = new Quad("PUSH", exp, new Info("value"), null);
                }
            }

            QT.add(q);
        }
        Info str = visitString(ctx.string());
        str.value += ", 0xA, 0xD ";
        Quad q = new Quad("PUSH", str, new Info("ref"), null);
        QT.add(q);
        q = new Quad("CALL", new Info("_printf"), null, null);
        QT.add(q);
        int pushnum = (ctx.exp_b().size() + 1) * 4;
        q = new Quad("DEPUSH", new Info(String.valueOf(pushnum)),null, null);
        QT.add(q);
        return null;
    }

    @Override public Info visitExp(Small_JavaParser.ExpContext ctx) {
        Info temp = visitFactor(ctx.factor(0));
        if(ctx.factor().size() == 1){
            return temp;
        }
        Quad q;
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
        Info temp = visitV(ctx.v(0));
        if(ctx.v().size() == 1){
            return temp;
        }

        Quad q;
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
            return new Info(row.getName(), row.getType());
        }
        else if(ctx.FLOAT() != null){
            Info temp = new Info(getNextTemp(), "float_SJ", ctx.FLOAT().getText());
            temps.add(temp);
            return temp;
        }
        else if (ctx.INT() != null){
            Info temp = new Info(getNextTemp(), "int_SJ", ctx.INT().getText());
            temps.add(temp);
            return temp;
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
        Info temp1 = new Info(getNextTemp(), "int_SJ", "1");
        temps.add(temp1);
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
        Info temp1 = new Info(getNextTemp(), "int_SJ", "1");
        temps.add(temp1);
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
        Info temp1 = new Info(getNextTemp(), "int_SJ", "1");
        temps.add(temp1);
        Info temp2 = new Info(getNextTemp(), "int_SJ", "");
        temps.add(temp2);
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

    @Override
    public Info visitString(Small_JavaParser.StringContext ctx) {
        String str = ctx.STRING().getText();
        Info temp = new Info(getNextStr(), "string_SJ", "'" + str.substring(1, str.length()-1) + "'");
        temps.add(temp);
        return temp;
    }
}
