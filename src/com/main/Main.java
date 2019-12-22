package com.main;

import com.gen.Small_JavaParser;
import com.gen.Small_JavaLexer;
import com.utils.MyVisitor;
import com.utils.SemanticVisitor;
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
            int num_errors = parser.getNumberOfSyntaxErrors();
            if (num_errors == 0){
                SemanticVisitor sem_visitor = new SemanticVisitor();
                sem_visitor.visit(pt);
                num_errors = sem_visitor.getNum_errors();
                if(num_errors == 0){
                    MyVisitor visitor = new MyVisitor();
                    visitor.visit(pt);
                    System.out.println(visitor.getST());
                    System.out.println(visitor.getQT());
                }
                else{
                    System.err.println("There are " + num_errors + " semantic errors.");
                }
            }
            else{
                System.err.println("There are " + num_errors + " syntaxic errors.");
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
