package com.example.kumkangreader.Object;

import java.io.Serializable;

public class ProductionInfo implements Serializable {

    public String OutPutQty;
    public String CostCenter;
    public String CostCenterName;
    public String WorksOrderNo;
    public String InputQty;
    public String IssueOutPutQty;

    public ProductionInfo(
            String OutPutQty,
            String CostCenter,
            String CostCenterName,
            String WorksOrderNo,
            String InputQty,
            String IssueOutPutQty
    ){
        this.OutPutQty=OutPutQty;
        this.CostCenter=CostCenter;
        this.CostCenterName=CostCenterName;
        this.WorksOrderNo=WorksOrderNo;
        this.InputQty=InputQty;
        this.IssueOutPutQty=IssueOutPutQty;

    }
}
