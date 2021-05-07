package com.example.kumkangreader.Object;

import java.io.Serializable;

public class ScrapGrade implements Serializable {
    public String ScrapGrade;
    public String ScrapGradeName;
    public String SortNo;

    public ScrapGrade(
            String ScrapGrade,
            String ScrapGradeName,
            String SortNo
    ){
        this.ScrapGrade=ScrapGrade;
        this.ScrapGradeName=ScrapGradeName;
        this.SortNo=SortNo;
    }
}
