package com.main;

import com.gen.Small_JavaParser;
import com.gen.Small_JavaLexer;
import com.utils.AssemblyTranslator;
import com.utils.QuadVisitor;
import com.utils.SemanticVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

import static org.antlr.v4.runtime.CharStreams.fromFileName;

public class Main {
    public static void main(String[] args){
        try {
            String source = "tests/test_block_if.txt";
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
                    QuadVisitor quad_visitor = new QuadVisitor(sem_visitor.getST());
                    quad_visitor.visit(pt);
                    System.out.println(quad_visitor.getST());
                    System.out.println("\n\n");
                    System.out.println(quad_visitor.getQT());
                    AssemblyTranslator AT = new AssemblyTranslator(quad_visitor.getST(), quad_visitor.getQT());
                    System.out.println("\n\n");
                    String res = AT.translate();
                    AssemblyTranslator.whenWriteStringUsingBufferedWritter_thenCorrect(res);
                    System.out.println(AT.translate());
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
