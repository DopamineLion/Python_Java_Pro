package org.dopamine.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Business {
    private String primaryKey;
    private Integer primaryKeyIndex;
    private String length;
    private Integer lengthIndex;
    private String filter;
    private Integer filterIndex;
    private String regex;

    private String[] otherFields;
    // 分组index集合
    private List<Integer> groupIndexList;
    private String additional;


    private String excelPath;

    public String getExcelPath() {
        return excelPath;
    }

    public void setExcelPath(String excelPath) {
        this.excelPath = excelPath;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Integer getPrimaryKeyIndex() {
        return primaryKeyIndex;
    }

    public void setPrimaryKeyIndex(Integer primaryKeyIndex) {
        this.primaryKeyIndex = primaryKeyIndex;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public Integer getLengthIndex() {
        return lengthIndex;
    }

    public void setLengthIndex(Integer lengthIndex) {
        this.lengthIndex = lengthIndex;
    }

    public String[] getOtherFields() {
        return otherFields;
    }

    public void setOtherFields(String[] otherFields) {
        this.otherFields = otherFields;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Integer getFilterIndex() {
        return filterIndex;
    }

    public void setFilterIndex(Integer filterIndex) {
        this.filterIndex = filterIndex;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public List<Integer> getGroupIndexList() {
        return groupIndexList;
    }

    public void setGroupIndexList(List<Integer> groupIndexList) {
        this.groupIndexList = groupIndexList;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }
}
