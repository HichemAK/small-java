package com.utils;

import java.io.*;
import java.util.ArrayList;

public class AssemblyTranslator {
    SymbolTable ST;
    QuadTable QT;
    ArrayList<Row> bss = new ArrayList<>();

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
            if(r.getType().equals("int_SJ")){
                if(r.getValue().length() == 0){
                    res += r.getName() + " dd 0\n";
                }
                else {
                    res += r.getName() + " dd " + r.getValue() + "\n";
                }
            }
            else if (r.getType().equals("float_SJ")){
                if(r.getValue().length() == 0){
                    res += r.getName() + " dd 0\n";
                }
                else if(r.getValue().contains(".")){
                    res += r.getName() + " dd " + r.getValue() + "\n";
                }
                else{
                    res += r.getName() + " dd " + r.getValue() + ".0\n";
                }
            }
            else if (r.getType().equals("string_SJ")){
                if(r.getValue().length() > 0){
                    res += r.getName() + " db ";
                    res += r.getValue();
                    res += ", 0\n";
                }
                else{
                    bss.add(r);
                }

            }
        }
        res += "\n";
        if(bss.size()>0){
            res += "segment .bss\n";
            for(Row r : bss){
                res += r.getName() + " resb 200\n";
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
