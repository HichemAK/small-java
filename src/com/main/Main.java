package com.main;

import com.gen.Small_JavaParser;
import com.gen.Small_JavaLexer;
import com.utils.MyVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

import static org.antlr.v4.runtime.CharStreams.fromFileName;

public class Main {
    public static void main(String[] args){
        try {
            String source = "test2.txt";
            CharStream cs = fromFileName(source);
            Small_JavaLexer lexer = new Small_JavaLexer(cs);
            CommonTokenStream token = new CommonTokenStream(lexer);
            Small_JavaParser parser = new Small_JavaParser(token);
            ParseTree pt = parser.literal();

            MyVisitor visitor = new MyVisitor();
            visitor.visit(pt);
            System.out.println(visitor.getST());
            System.out.println(visitor.getQT());
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
