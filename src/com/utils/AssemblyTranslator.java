package com.utils;

public class AssemblyTranslator {
    SymbolTable ST;
    QuadTable QT;

    public AssemblyTranslator(SymbolTable ST, QuadTable QT){
        this.ST = ST;
        this.QT = QT;
    }

    public String translate(){
        String res = "section\t.text\n" +
                "global _start\n" +
                "extern _printf\n" +
                "_start: \n";

        for (Quad q: QT) {
            res += q.translate();
        }
        res += "section\t.data\n";
        for(Row r: ST){
            if(!r.getType().equals("string_SJ")){
                res += r.getName() + " dd 0\n";
            }
            else{
                res += r.getName() + " db ";
                if (r.getValue().length() != 0){
                    res += "'" + r.getValue() + "'";
                }
                res += "\n";
            }
        }
        return res;
    }
}
