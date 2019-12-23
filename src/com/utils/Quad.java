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
        res += toString() + "\n";
        switch (op){
            case ":=":
                res += "MOV eax, " + value(b) + "\n";
                res += "MOV " + value(a) + ", eax\n";
                break;
            case "+":
                res += "MOV eax, " + value(b) + "\n";
                res += "ADD ebx, " + value(c) + "\n";
                res += "MOV " + value(a) + ", ebx\n";

        }
        res += "\n";
        return res;
    }

    private String value(Info a){
        if(isNumeric(a.name)){
            return a.name;
        }
        return "offset " + a.name;
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