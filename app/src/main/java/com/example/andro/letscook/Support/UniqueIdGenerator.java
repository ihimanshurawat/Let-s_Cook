package com.example.andro.letscook.Support;

import java.util.UUID;

public class UniqueIdGenerator {

    private String uniqueID;

    public UniqueIdGenerator(){
        uniqueID=null;
    }

    public String getUniqueID(){
        String arr[]= UUID.randomUUID().toString().split("-");
        for(String id: arr){
            uniqueID += id;
        }
        return uniqueID;
    }

}
