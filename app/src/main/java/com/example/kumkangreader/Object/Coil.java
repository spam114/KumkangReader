package com.example.kumkangreader.Object;

import java.io.Serializable;

public class Coil implements Serializable {

    public String CoilNo;
    public String PartCode;
    public String PartSpec;
    public String LocationNo;

    public Coil(
            String CoilNo,
            String PartCode,
            String PartSpec,
            String LocationNo
    ){
        this.CoilNo=CoilNo;
        this.PartCode=PartCode;
        this.PartSpec=PartSpec;
        this.LocationNo=LocationNo;
    }
}