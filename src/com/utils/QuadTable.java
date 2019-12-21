package com.utils;

import java.util.ArrayList;

public class QuadTable extends ArrayList<Quad> {

    public void translate(){
        // Translate quads to assembly
    }

    @Override
    public String toString() {
        String res = "";
        for(int i=0;i<size();i++){
            res += get(i).toString();
            res += "\n";
        }
        return res;
    }
}
