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
            Info exp = null;
            if(ctx.exp_b() != null){
                exp = visitExp_b(ctx.exp_b());
            }
            if(row.getType().equals("string_SJ") && exp != null && !exp.type.equals("string_SJ")){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: Cannot cast 'int_SJ/float_SJ' type to 'string_SJ' type");
                num_errors++;
            }
            else if(row.getType().equals("int_SJ") && exp != null && exp.type.equals("float_SJ")){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: Cannot cast 'float_SJ' type to 'int_SJ' type");
                num_errors++;
            }
            else if(!row.getType().equals("string_SJ") && ctx.string() != null){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: Cannot cast 'string_SJ' type to 'int_SJ/float_SJ' type");
                num_errors++;
            }
            else if(!row.getType().equals("string_SJ") && exp != null && exp.type.equals("string_SJ")){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: Cannot cast 'string_SJ' type to 'int_SJ/float_SJ' type");
                num_errors++;
            }
        }
        return null;
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

    @Override public Info visitExp(Small_JavaParser.ExpContext ctx) {
        if(ctx.factor().size() == 1){
            return visitFactor(ctx.factor(0));
        }
        Info temp = visitFactor(ctx.factor(0));
        if(temp.type.equals("string_SJ")){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: '" + ctx.plus_minus(0).getText() + "' is impossible between '" + temp.type + "' and '" + temp.type + "'" );
            num_errors++;
        }
        for(int i=1;i<ctx.factor().size();i++){
            Info info = visitFactor(ctx.factor(i));
            String op = ctx.plus_minus(i-1).getText();
            if(info.type.equals("float_SJ")){
                temp.type = "float_SJ";
            }
            else if (info.type.equals("string_SJ")){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: '" + op + "' is impossible between '" + temp.type + "' and '" + info.type + "'" );
                num_errors++;
            }
        }
        return temp;
    }

    @Override public Info visitFactor(Small_JavaParser.FactorContext ctx) {
        for(int i=1;i<ctx.v().size();i++){
            if(ctx.mul_div(i-1).DIV() != null && ctx.v(i).getText().equals("0")){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: ERROR Division by Zero");
                num_errors++;
            }
        }
        if(ctx.v().size() == 1){
            return visitV(ctx.v(0));
        }
        Info temp = visitV(ctx.v(0));
        if(temp.type.equals("string_SJ")){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: '" + ctx.mul_div(0).getText() + "' is impossible between '" + temp.type + "' and '" + temp.type + "'" );
            num_errors++;
        }
        for(int i=1;i<ctx.v().size();i++){
            Info info = visitV(ctx.v(i));
            String op = ctx.mul_div(i-1).getText();
            if(info.type.equals("float_SJ")){
                temp.type = "float_SJ";
            }
            else if(temp.type.equals("int_SJ") && op.equals("/")){
                temp.type = "float_SJ";
            }
            else if (info.type.equals("string_SJ")){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: '" + op + "' is impossible between '" + temp.type + "' and '" + info.type + "'" );
                num_errors++;
            }
        }
        return temp;
    }

    @Override public Info visitExp_b(Small_JavaParser.Exp_bContext ctx) {
        if(ctx.factor_b().size() == 1){
            return visitFactor_b(ctx.factor_b(0));
        }
        Info info;
        String op = null;
        for(int i=0;i<ctx.factor_b().size()-1;i++){
            info = visitFactor_b(ctx.factor_b(i));
            op = ctx.OR(i).getText();
            if(info.type.equals("string_SJ")){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: '" + op + "' is impossible if one the operands is of type 'string_SJ'");
                num_errors++;
            }
        }
        if(visitFactor_b(ctx.factor_b(ctx.factor_b().size()-1)).type.equals("string_SJ")){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: '" + op + "' is impossible if one the operands is of type 'string_SJ'");
            num_errors++;
        }
        return new Info("", "int_SJ");
    }

    @Override public Info visitFactor_b(Small_JavaParser.Factor_bContext ctx) {
        if(ctx.literal().size() == 1){
            return visitLiteral(ctx.literal(0));
        }
        Info info;
        String op = null;
        for(int i=0;i<ctx.literal().size()-1;i++){
            info = visitLiteral(ctx.literal(i));
            op = ctx.AND(i).getText();
            if(info.type.equals("string_SJ")){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: '" + op + "' is impossible if one the operands is of type 'string_SJ'");
                num_errors++;
            }
        }
        if(visitLiteral(ctx.literal(ctx.literal().size()-1)).type.equals("string_SJ")){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: '" + op + "' is impossible if one the operands is of type 'string_SJ'");
            num_errors++;
        }
        return new Info("", "int_SJ");
    }

    @Override public Info visitLiteral(Small_JavaParser.LiteralContext ctx) {
        if(ctx.NOT() == null){
            return visitAtom(ctx.atom());
        }
        Info v = visitAtom(ctx.atom());
        if(v.type.equals("string_SJ")){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: Cannot use 'string_SJ' type in '!' (NOT) operation" );
            num_errors++;
        }
        return new Info("", "int_SJ");
    }

    @Override public Info visitAtom(Small_JavaParser.AtomContext ctx) {
        if(ctx.exp_b() != null){
            return visitExp_b(ctx.exp_b());
        }
        if(ctx.exp().size() == 1){
            return visitExp(ctx.exp(0));
        }
        Info info;
        String op = null;
        for(int i=0;i<ctx.exp().size()-1;i++){
            info = visitExp(ctx.exp(i));
            op = ctx.op_compare(i).getText();
            if(info.type.equals("string_SJ")){
                System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                        " :: '" + op + "' is impossible if one the operands is of type 'string_SJ'");
                num_errors++;
            }
        }
        if(visitExp(ctx.exp(ctx.exp().size()-1)).type.equals("string_SJ")){
            System.err.println(ctx.stop.getLine()+ ":" + ctx.stop.getCharPositionInLine() +
                    " :: '" + op + "' is impossible if one the operands is of type 'string_SJ'");
            num_errors++;
        }
        return new Info("", "int_SJ");
    }

    @Override public Info visitV(Small_JavaParser.VContext ctx) {
        if(ctx.IDF() != null){
            boolean temp = checkIfDeclared(ctx.IDF().getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
            if(temp){
                Row row = ST.get(ST.indexOf(ctx.IDF().getText()));
                return new Info(row.getName(), row.getType());
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


    @Override public Info visitVar_declare(Small_JavaParser.Var_declareContext ctx) {
        for(int i=0;i<ctx.IDF().size();i++){
            checkIdfLength(ctx.IDF(i).getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
            checkIfAlreadyDeclared(ctx.IDF(i).getText(), ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
            ST.assign(new Row(ctx.IDF(i).getText(), ctx.type().getText(), ""));
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

