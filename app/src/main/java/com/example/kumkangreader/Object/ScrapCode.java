package com.example.kumkangreader.Object;

import java.io.Serializable;

public class ScrapCode implements Serializable {

    public String ScrapCode;
    public String ScrapCodeName;
    public String SortNo;

    public ScrapCode(
            String ScrapCode,
            String ScrapCodeName,
            String SortNo
    ){
        this.ScrapCode=ScrapCode;
        this.ScrapCodeName=ScrapCodeName;
        this.SortNo=SortNo;
    }
}
