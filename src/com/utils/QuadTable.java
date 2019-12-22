package com.utils;

import java.util.ArrayList;

public class QuadTable extends ArrayList<Quad> {

    public void translate(){
        // Translate quads to assembly
    }

    @Override
    public boolean add(Quad quad) {
        return super.add(quad);
    }

    @Override
    public String toString() {
        String res = "";
        for(int i=0;i<size();i++){
            res += i +"\t";
            res += get(i).toString();
            res += "\n";
        }
        return res;
    }
}
