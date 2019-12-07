package com.main;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

import static org.antlr.v4.runtime.CharStreams.fromFileName;

public class Main {
    public static void main(String[] args){
        try {
            String source = "test.txt";
            CharStream cs = fromFileName(source);
            Small_JavaLexer lexer = new Small_JavaLexer(cs);
            CommonTokenStream token = new CommonTokenStream(lexer);
            Small_JavaParser parser = new Small_JavaParser(token);
            ParseTree pt = parser.r();

            Small_JavaBaseVisitor visitor = new Small_JavaBaseVisitor();
            visitor.visit(pt);

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
