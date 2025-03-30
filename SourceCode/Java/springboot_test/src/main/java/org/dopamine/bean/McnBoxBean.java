package org.dopamine.bean;

public class McnBoxBean {

    private String city;
    private String projectNum;
    private String quantity;//采购数量
    private String code;
    private String name;
    private String zuTao;
    private String inputBoxGauge;

    private Integer boxGauge;
    private Integer xiaoJi;
    private Integer heJi;
    private Integer zongJi;
    private Integer realGauge;//实际箱内数量
    private Integer index;

    private String cityText;
    private String nameText;
    private String indexText;
    private String realGaugeText;

    public String getCityText() {
        return city + "(共"+zongJi+"箱)";
    }

    public void setCityText(String cityText) {
        this.cityText = cityText;
    }

    public String getNameText() {
        return code+"\r\n"+name;
    }

    public void setNameText(String nameText) {
        this.nameText = nameText;
    }

    public String getIndexText() {
        return "第" + heJi + "-" + index + "箱";
    }

    public void setIndexText(String indexText) {
        this.indexText = indexText;
    }

    public String getRealGaugeText() {
        return realGauge + "个 (共"+quantity+"个)";
    }

    public void setRealGaugeText(String realGaugeText) {
        this.realGaugeText = realGaugeText;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getRealGauge() {
        return realGauge;
    }

    public void setRealGauge(Integer realGauge) {
        this.realGauge = realGauge;
    }

    public void setBoxGauge(Integer boxGauge) {
        this.boxGauge = boxGauge;
    }

    public void setXiaoJi(Integer xiaoJi) {
        this.xiaoJi = xiaoJi;
    }

    public Integer getHeJi() {
        return heJi;
    }

    public void setHeJi(Integer heJi) {
        this.heJi = heJi;
    }

    public Integer getZongJi() {
        return zongJi;
    }

    public void setZongJi(Integer zongJi) {
        this.zongJi = zongJi;
    }

    public int getXiaoJi() {
        return xiaoJi;
    }

    public void setXiaoJi(int xiaoJi) {
        this.xiaoJi = xiaoJi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZuTao() {
        return zuTao;
    }

    public void setZuTao(String zuTao) {
        this.zuTao = zuTao;
    }

    public String getInputBoxGauge() {
        return inputBoxGauge;
    }

    public void setInputBoxGauge(String inputBoxGauge) {
        this.inputBoxGauge = inputBoxGauge;
    }

    public int getBoxGauge() {
        return boxGauge;
    }

    public void setBoxGauge(int boxGauge) {
        this.boxGauge = boxGauge;
    }

    public String getProjectNum() {
        return projectNum;
    }

    public void setProjectNum(String projectNum) {
        this.projectNum = projectNum;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
