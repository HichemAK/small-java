package com.utils;

import com.gen.Small_JavaBaseVisitor;
import com.gen.Small_JavaParser;

public class MyVisitor<T> extends Small_JavaBaseVisitor<T> {
    @Override public T visitR(Small_JavaParser.RContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitImport_bib(Small_JavaParser.Import_bibContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitClass_declare(Small_JavaParser.Class_declareContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitClass_content(Small_JavaParser.Class_contentContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitVars_declare(Small_JavaParser.Vars_declareContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitMain(Small_JavaParser.MainContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitInstruction(Small_JavaParser.InstructionContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitAssign(Small_JavaParser.AssignContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitIf_cond(Small_JavaParser.If_condContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitRead(Small_JavaParser.ReadContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitWrite(Small_JavaParser.WriteContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitExp(Small_JavaParser.ExpContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitFactor(Small_JavaParser.FactorContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitV(Small_JavaParser.VContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitExp_b(Small_JavaParser.Exp_bContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitFactor_b(Small_JavaParser.Factor_bContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitLiteral(Small_JavaParser.LiteralContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitAtom(Small_JavaParser.AtomContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitVar_declare(Small_JavaParser.Var_declareContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitBibs(Small_JavaParser.BibsContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitType(Small_JavaParser.TypeContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitModif(Small_JavaParser.ModifContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitFormat(Small_JavaParser.FormatContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitString(Small_JavaParser.StringContext ctx) { return visitChildren(ctx); }
    
    @Override public T visitOp_compare(Small_JavaParser.Op_compareContext ctx) { return visitChildren(ctx); }
}
