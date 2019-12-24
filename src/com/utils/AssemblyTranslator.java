package com.utils;

import java.io.*;

public class AssemblyTranslator {
    SymbolTable ST;
    QuadTable QT;

    public static void whenWriteStringUsingBufferedWritter_thenCorrect(String str)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("test.asm"));
        writer.write(str);

        writer.close();
    }

    public AssemblyTranslator(SymbolTable ST, QuadTable QT) throws IOException {
        this.ST = ST;
        this.QT = QT;
    }

    public String translate(){
        String res = "";
        res += ";;Assemble and link with\n" +
                ";nasm -fwin32 hello.asm\n" +
                ";gcc -o hello hello.obj\n" +
                "\n" +
                "global _main \n" +
                "extern _scanf \n" +
                "extern _printf     \n" +
                "\n";
        res += "section\t.data\n";
        for(Row r: ST){
            if(!r.getType().equals("string_SJ")){
                res += r.getName() + " dd 0\n";
            }
            else{
                res += r.getName() + " db ";
                res += r.getValue();
                res += ", 0\n";
            }
        }

        res +=  "segment .text\n" +
                "\n" +
                "_main:\n";

        for (Quad q: QT) {
            res += q.translate();
        }
        res += "ret \n";

        return res;
    }
}
