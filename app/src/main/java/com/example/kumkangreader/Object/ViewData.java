package com.example.kumkangreader.Object;
import java.io.Serializable;

public class ViewData implements Serializable {

    public String CustomerName = "";
    public String PartName = "";
    public String Qty = "";
    public String Weight = "";
    public String CostCenterName="";
    public String CommodityName="";

    public ViewData() {
        super();
    }
}
