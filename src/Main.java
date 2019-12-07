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
            Simple_copy2Lexer lexer = new Simple_copy2Lexer(cs);
            CommonTokenStream token = new CommonTokenStream(lexer);
            Simple_copy2Parser parser = new Simple_copy2Parser(token);
            ParseTree pt = parser.r();

            Simple_copy2BaseVisitor visitor = new Simple_copy2BaseVisitor();
            visitor.visit(pt);

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
