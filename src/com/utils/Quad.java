package com.utils;

public class Quad {
    public String op;
    public Info a, b, c;

    public Quad(String op, Info a, Info b, Info c){
        this.op = op;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public String toString() {
        return "(" + op + ", " + a + ", " + b + ", " + c + ")";
    }

    public String translate(){
        String res = "";
        res += "; " + toString() + "\n";
        switch (op){
            case ":=":
                if(a.type.equals("string_SJ")){
                    res += "MOV eax, " + b.name + "\n";
                    res += "MOV " + a.name + ", eax\n";
                }
                else{
                    res += "MOV eax, " + value(b) + "\n";
                    res += "MOV " + value(a) + ", eax\n";
                }

                break;
            case "+":
                if(b.type.equals("int_SJ") && c.type.equals("int_SJ")){
                    res += "MOV eax, " + value(b) + "\n";
                    res += "ADD eax, " + value(c) + "\n";
                    res += "MOV " + value(a) + ", eax\n";
                }
                else if(b.type.equals("float_SJ") && c.type.equals("int_SJ")) {
                    res += "FINIT\n";
                    res += "FLD DWORD [" + b.name + "]\n";
                    res += "FILD DWORD [" + c.name + "]\n";
                    res += "FADD ST0, ST1\n";
                    res += "FST DWORD " + value(a) + "\n";
                    res += "FWAIT\n";
                }

                else if(b.type.equals("float_SJ") && c.type.equals("float_SJ")) {
                    res += "FINIT\n";
                    res += "FLD " + b.name + "\n";
                    res += "FLD" + c.name + "\n";
                    res += "FADD ST, ST(1)\n";
                    res += "FST " + value(a) + "\n";
                    res += "FWAIT\n";
                }
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

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}