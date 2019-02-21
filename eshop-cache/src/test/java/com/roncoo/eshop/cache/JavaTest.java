package com.roncoo.eshop.cache;

public class JavaTest {

    public static void main(String[] args) {
        int c =0;
        while(c<30){
            try{
                c++;

                continue;
            }finally {
                System.out.println("finally");
            }
        }
    }
}
