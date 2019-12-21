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
}