package com.utils;

public class AssemblyTranslator {
    SymbolTable ST;
    QuadTable QT;

    String print_int_proc = "print_al proc\n" +
            "cmp al, 0\n" +
            "jne print_al_r\n" +
            "    push ax\n" +
            "    mov al, '0'\n" +
            "    mov ah, 0eh\n" +
            "    int 10h\n" +
            "    pop ax\n" +
            "    ret \n" +
            "print_al_r:    \n" +
            "    pusha\n" +
            "    mov ah, 0\n" +
            "    cmp ax, 0\n" +
            "    je pn_done\n" +
            "    mov dl, 10\n" +
            "    div dl    \n" +
            "    call print_al_r\n" +
            "    mov al, ah\n" +
            "    add al, 30h\n" +
            "    mov ah, 0eh\n" +
            "    int 10h    \n" +
            "    jmp pn_done\n" +
            "pn_done:\n" +
            "    popa  \n" +
            "    ret  \n" +
            "endp\n";

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
