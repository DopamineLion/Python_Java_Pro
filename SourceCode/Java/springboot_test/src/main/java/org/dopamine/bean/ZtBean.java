package org.dopamine.bean;

public class ZtBean {
//    项目号,商品编码,产品明细,规格,改数后数量,贴码
    private String projectId;
    private String code;
    private String name;
    private String boxGauge;
    private String number;
    private String tieMa;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoxGauge() {
        return boxGauge;
    }

    public void setBoxGauge(String boxGauge) {
        this.boxGauge = boxGauge;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTieMa() {
        return tieMa;
    }

    public void setTieMa(String tieMa) {
        this.tieMa = tieMa;
    }
}
