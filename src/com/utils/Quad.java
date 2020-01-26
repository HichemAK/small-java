package com.utils;

public class Quad {
    public String op;
    public Info a, b, c;
    public static int jump_num = 0;
    public static int quad_num = 0;
    public String quad_idf;

    public Quad(String op, Info a, Info b, Info c){
        this.op = op;
        this.a = a;
        this.b = b;
        this.c = c;
        this.quad_idf = getNextQuad();
    }

    private String getNextQuad(){
        return "__QUAD" + quad_num++;
    }

    public String getNextJump(){
        return "__JUMP" + jump_num++;
    }


    @Override
    public String toString() {
        return "(" + op + ", " + a + ", " + b + ", " + c + ")";
    }

    public String translate(){
        String res = "";
        res += quad_idf + ": ; " + toString() + "\n";
        switch (op){
            case ":=":
                if(a.type.equals("string_SJ")){
                    res += copyString();
                }
                else if(a.type.equals("int_SJ") && b.type.equals("int_SJ") || a.type.equals("float_SJ") && b.type.equals("float_SJ")){
                    res += "MOV eax, " + value(b) + "\n";
                    res += "MOV " + value(a) + ", eax\n";
                }
                else if(a.type.equals("float_SJ") && b.type.equals("int_SJ")){
                    res += "MOV eax, " + b.name + "\n";
                    res += "MOV ebx, " + a.name + "\n";
                    res += "FILD DWORD [eax]\n";
                    res += "FSTP DWORD [ebx]\n";
                }

                break;
            case "+":
                if(b.type.equals("int_SJ") && c.type.equals("int_SJ")){
                    res += "MOV eax, " + value(b) + "\n";
                    res += "ADD eax, " + value(c) + "\n";
                    res += "MOV " + value(a) + ", eax\n";
                }
                else if(b.type.equals("float_SJ") && c.type.equals("int_SJ")) {
                    res += "MOV eax, " + a.name + "\n";
                    res += "MOV ebx, " + b.name + "\n";
                    res += "MOV ecx, " + c.name + "\n";
                    res += "FLD DWORD [ebx]\n";
                    res += "FILD DWORD [ecx]\n";
                    res += "FADD ST0, ST1\n";
                    res += "FSTP DWORD [eax]\n";
                    res += "FFREE ST1\n";
                }

                else if(b.type.equals("float_SJ") && c.type.equals("float_SJ")) {
                    res += "MOV eax, " + a.name + "\n";
                    res += "MOV ebx, " + b.name + "\n";
                    res += "MOV ecx, " + c.name + "\n";
                    res += "FLD DWORD [ebx]\n";
                    res += "FLD DWORD [ecx]\n";
                    res += "FADD ST0, ST1\n";
                    res += "FSTP DWORD [eax]\n";
                    res += "FFREE ST1\n";
                }
                break;
            case "-":
                if(b.type.equals("int_SJ") && c.type.equals("int_SJ")){
                    res += "MOV eax, " + value(b) + "\n";
                    res += "SUB eax, " + value(c) + "\n";
                    res += "MOV " + value(a) + ", eax\n";
                }
                else if(b.type.equals("float_SJ") && c.type.equals("int_SJ")) {
                    res += "MOV eax, " + a.name + "\n";
                    res += "MOV ebx, " + b.name + "\n";
                    res += "MOV ecx, " + c.name + "\n";
                    res += "FILD DWORD [ecx]\n";
                    res += "FLD DWORD [ebx]\n";
                    res += "FSUB ST0, ST1\n";
                    res += "FSTP DWORD [eax]\n";
                    res += "FFREE ST1\n";
                }

                else if(b.type.equals("float_SJ") && c.type.equals("float_SJ")) {
                    res += "MOV eax, " + a.name + "\n";
                    res += "MOV ebx, " + b.name + "\n";
                    res += "MOV ecx, " + c.name + "\n";
                    res += "FLD DWORD [ecx]\n";
                    res += "FLD DWORD [ebx]\n";
                    res += "FSUB ST0, ST1\n";
                    res += "FSTP DWORD [eax]\n";
                    res += "FFREE ST1\n";
                }
                break;
            case "*":
                if(b.type.equals("int_SJ") && c.type.equals("int_SJ")){
                    res += "MOV eax, " + value(b) + "\n";
                    res += "MOV ebx, " + value(c) + "\n";
                    res += "IMUL ebx\n";
                    res += "MOV " + value(a) + ", eax\n";
                }
                else if(b.type.equals("float_SJ") && c.type.equals("int_SJ")) {
                    res += "MOV eax, " + a.name + "\n";
                    res += "MOV ebx, " + b.name + "\n";
                    res += "MOV ecx, " + c.name + "\n";
                    res += "FLD DWORD [ebx]\n";
                    res += "FILD DWORD [ecx]\n";
                    res += "FMUL ST0, ST1\n";
                    res += "FSTP DWORD [eax]\n";
                    res += "FFREE ST1\n";
                }

                else if(b.type.equals("float_SJ") && c.type.equals("float_SJ")) {
                    res += "MOV eax, " + a.name + "\n";
                    res += "MOV ebx, " + b.name + "\n";
                    res += "MOV ecx, " + c.name + "\n";
                    res += "FLD DWORD [ebx]\n";
                    res += "FLD DWORD [ecx]\n";
                    res += "FMUL ST0, ST1\n";
                    res += "FSTP DWORD [eax]\n";
                    res += "FFREE ST1\n";
                }
                break;
            case "/":
                res += "MOV eax, " + a.name + "\n";
                res += "MOV ebx, " + b.name + "\n";
                res += "MOV ecx, " + c.name + "\n";
                if(c.type.equals("int_SJ")){
                    res += "FILD DWORD [ecx]\n";
                }
                else{
                    res += "FLD DWORD [ecx]\n";
                }

                if(b.type.equals("int_SJ")){
                    res += "FILD DWORD [ebx]\n";
                }
                else{
                    res += "FLD DWORD [ebx]\n";
                }
                res += "FDIV ST0, ST1\n";
                res += "FSTP DWORD [eax]\n";
                res += "FFREE ST1\n";
                break;
            case "OR":
                res += "MOV eax, " + value(b) + "\n";
                res += "MOV ebx, " + value(c) + "\n";
                res += "OR eax, ebx\n";
                res += "MOV "+ value(a) + ", eax\n";
                break;
            case "AND":
                res += "MOV eax, " + value(b) + "\n";
                res += "MOV ebx, " + value(c) + "\n";
                res += "AND eax, ebx\n";
                res += "MOV "+ value(a) + ", eax\n";
                break;
            case "NOT":
                res += "MOV eax, " + value(b) + "\n";
                res += "NOT eax\n";
                res += "AND eax, 0x00000001\n";
                res += "MOV "+ value(a) + ", eax\n";
                break;
            case "BR":
                res += "JMP " + a.name + "\n";
                break;
            case "BZ":
                res += "MOV ebx, " + b.name + "\n";
                if(b.type.equals("int_SJ")){
                    res += "FILD DWORD [ebx]\n";
                }
                else{
                    res += "FLD DWORD [ebx]\n";
                }
                res += "FTST\n";
                res += "FSTSW ax\n";
                res += "SAHF\n";
                res += "FFREE ST0\n";
                res += "JZ " + a.name + "\n";
                break;
            case ">":
                res += compare(">");
                break;
            case ">=":
                res += compare(">=");
                break;
            case "=":
                res += compare("=");
                break;
            case "<":
                res += compare("<");
                break;
            case "<=":
                res += compare("<=");
                break;
            case "!=":
                res += compare("!=");
                break;
            case "CALL":
                res += "CALL " + a.name + "\n";
                break;
            case "PUSH":
                if(b.name.equals("value")){
                    res += "PUSH DWORD [" + a.name + "]\n";
                }
                else if(b.name.equals("ref")){
                    res += "PUSH DWORD " + a.name + "\n";
                }
                break;
            case "DEPUSH":
                res += "ADD ESP, " + a.name + "\n";
                break;
            case "PRINT_FLOAT":
                res += "SUB esp, 8\n"
                        + "MOV eax, " + a.name + "\n"
                        + "FLD DWORD [eax]\n"
                        + "FSTP QWORD [esp]\n";
        }
        res += "\n";
        return res;
    }

    private String value(Info a){
        if(isNumeric(a.name)){
            return a.name;
        }
        return '[' + a.name + ']';
    }

    private String compare(String op){
        String res = "";
        res += "MOV edx, " + a.name + "\n";
        res += "MOV ebx, " + b.name + "\n";
        res += "MOV ecx, " + c.name + "\n";
        if(c.type.equals("int_SJ")){
            res += "FILD DWORD [ecx]\n";
        }
        else{
            res += "FLD DWORD [ecx]\n";
        }

        if(b.type.equals("int_SJ")){
            res += "FILD DWORD [ebx]\n";
        }
        else{
            res += "FLD DWORD [ebx]\n";
        }
        res += "FCOM ST0, ST1\n";
        res += "FSTSW ax\n";
        res += "FFREE ST0\n";
        res += "FFREE ST1\n";
        res += "SAHF\n";
        String jump1 = getNextJump();
        String jump2 = getNextJump();
        switch(op){
            case ">":
                res += "JA " + jump1 + "\n";
                break;
            case">=":
                res += "JAE " + jump1 + "\n";
                break;
            case "=":
                res += "JE " + jump1 + "\n";
                break;
            case "<":
                res += "JB " + jump1 + "\n";
                break;
            case "<=":
                res += "JBE " + jump1 + "\n";
                break;
            case "!=":
                res += "JNE " + jump1 + "\n";
                break;
        }
        res += "MOV eax, 0x00000000\n";
        res += "JMP " + jump2 + "\n";
        res += jump1 + ": MOV eax, 0x00000001\n";
        res += jump2 + ": " + "MOV [edx], eax\n";
        return res;
    }

    public String copyString(){
        String res = "";
        res += "MOV eax, " + a.name + "\n";
        res += "MOV ebx, " + b.name + "\n";
        res += "MOV ecx, 0\n";
        String jump1 = getNextJump();
        String jump2 = getNextJump();
        res += jump1 + ": " + "MOV edx, [ebx + ecx]\n";
        res += "MOV [eax + ecx], edx\n";
        res += "INC ecx\n";
        res += "CMP edx, 0\n";
        res += "JE " + jump2 + "\n";
        res += "CMP ecx, 200\n";
        res += "JNE " + jump1 + "\n";
        res += jump2 + ": ";
        return res;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}