package com.example.andro.letscook.support;

import java.util.UUID;

public class UniqueIdGenerator {


    private StringBuffer bufferedUniqueID;

    public UniqueIdGenerator(){
        bufferedUniqueID=new StringBuffer("");
    }

    public String getUniqueID(){
        String arr[]= UUID.randomUUID().toString().split("-");
        for(String id: arr){
            bufferedUniqueID.append(id);
        }

        return bufferedUniqueID.toString();
    }

}
