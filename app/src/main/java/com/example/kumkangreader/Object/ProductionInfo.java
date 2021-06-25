package com.example.kumkangreader.Object;

import java.io.Serializable;

public class ProductionInfo implements Serializable {

    public String OutputQty;
    public String CostCenter;
    public String CostCenterName;
    public String WorksOrderNo;
    public String InputQty;
    public String IssueOutputQty;
    public String LocationNo;
    public String ScrappedQty;
    public String CenterSpec;
    public String PartName;
    public String PartSpecName;

    public ProductionInfo(
            String OutputQty,
            String CostCenter,
            String CostCenterName,
            String WorksOrderNo,
            String InputQty,
            String IssueOutputQty,
            String LocationNo,
            String ScrappedQty,
            String CenterSpec,
            String PartName,
            String PartSpecName
    ){
        this.OutputQty=OutputQty;
        this.CostCenter=CostCenter;
        this.CostCenterName=CostCenterName;
        this.WorksOrderNo=WorksOrderNo;
        this.InputQty=InputQty;
        this.IssueOutputQty=IssueOutputQty;
        this.LocationNo=LocationNo;
        this.ScrappedQty=ScrappedQty;
        this.CenterSpec=CenterSpec;
        this.PartName=PartName;
        this.PartSpecName=PartSpecName;

    }
}
