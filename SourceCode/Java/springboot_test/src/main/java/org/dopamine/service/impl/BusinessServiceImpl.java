package org.dopamine.service.impl;

import org.dopamine.bean.Business;
import org.dopamine.controller.view.Code;
import org.dopamine.controller.view.Result;
import org.dopamine.exception.BusinessException;
import org.dopamine.service.BusinessService;
import org.dopamine.utils.BusinessUtils;
import org.dopamine.utils.ExcelUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class BusinessServiceImpl implements BusinessService{
    public String standard(String excelPath, String excelSheet, String rightExcelPath, String rightExcelSheet, Business business, Business rightBusiness, String resultSheet, Boolean isWriteExcel) throws Exception{
        String outputPath = BusinessUtils.getOutputPath(excelPath);

        List<List<String>> initLists = ExcelUtils.readExcel(excelPath, excelSheet);
        indexList(initLists.get(0), business);

        List<List<String>> reserveColumnsList = BusinessUtils.reserveColumnsList(initLists, business);
        indexList(reserveColumnsList.get(0), business);
        List<List<String>> filterList = BusinessUtils.filterList(reserveColumnsList, business);
        List<List<String>> groupList = BusinessUtils.groupSumListByStream(filterList, business);
        System.out.println(groupList.size());

        List<List<String>> rightInitLists = ExcelUtils.readExcel(rightExcelPath, rightExcelSheet);
        indexList(rightInitLists.get(0), rightBusiness);

        List<List<String>> reserveRightColumnsList = BusinessUtils.reserveColumnsList(rightInitLists, rightBusiness);
        indexList(reserveRightColumnsList.get(0), rightBusiness);
        List<List<String>> rightGroupList = BusinessUtils.groupSumListByStream(reserveRightColumnsList, rightBusiness);
        List<List<String>> lianbiao = BusinessUtils.lianbiao(groupList, rightGroupList, business, rightBusiness);
        System.out.println(lianbiao.size());
        if(isWriteExcel)
            ExcelUtils.writeExcel(lianbiao, outputPath, resultSheet);

        return "查询出"+lianbiao.size()+"条结果>>>成功输出至路径："+outputPath;
    }

    public Result standardMultipart(InputStream excelInputStream, InputStream rightExcelInputStream, Business business, Business rightBusiness, String resultSheet) throws Exception{

        List<List<String>> initLists = ExcelUtils.readExcelMultipart(excelInputStream, null);
        indexList(initLists.get(0), business);

        List<List<String>> reserveColumnsList = BusinessUtils.reserveColumnsList(initLists, business);
        indexList(reserveColumnsList.get(0), business);
        List<List<String>> filterList = BusinessUtils.filterList(reserveColumnsList, business);
        List<List<String>> groupList = BusinessUtils.groupSumListByStream(filterList, business);

        List<List<String>> rightInitLists = ExcelUtils.readExcelMultipart(rightExcelInputStream, null);
        indexList(rightInitLists.get(0), rightBusiness);

        List<List<String>> reserveRightColumnsList = BusinessUtils.reserveColumnsList(rightInitLists, rightBusiness);
        indexList(reserveRightColumnsList.get(0), rightBusiness);
        List<List<String>> rightGroupList = BusinessUtils.groupSumListByStream(reserveRightColumnsList, rightBusiness);
        List<List<String>> lianbiao = BusinessUtils.lianbiao(groupList, rightGroupList, business, rightBusiness);

        String excelBase64Data = ExcelUtils.writeExcelMultipart(lianbiao, resultSheet, null);
        Result result = new Result();
        result.setData(excelBase64Data);
        return result;
    }

    public String mini(String excelPath, String excelSheet, Business business, String resultSheet, Boolean isWriteExcel) throws Exception{
        String outputPath = BusinessUtils.getOutputPath(excelPath);

        List<List<String>> initLists = ExcelUtils.readExcel(excelPath, excelSheet);
        indexList(initLists.get(0), business);

        List<List<String>> reserveColumnsList = BusinessUtils.reserveColumnsList(initLists, business);
        indexList(reserveColumnsList.get(0), business);
        List<List<String>> filterList = BusinessUtils.filterList(reserveColumnsList, business);
        List<List<String>> groupList = BusinessUtils.groupSumListByStream(filterList, business);
        /*输出*/
        if(isWriteExcel)
            ExcelUtils.writeExcel(groupList, outputPath, resultSheet);

        return "查询出"+groupList.size()+"条结果>>>成功输出至路径："+outputPath;
    }

    public Result miniMultipart(InputStream excelInputStream, OutputStream outputStream, Business business, String resultSheet) throws Exception{
        List<List<String>> initLists = ExcelUtils.readExcelMultipart(excelInputStream, null);
        indexList(initLists.get(0), business);

        List<List<String>> reserveColumnsList = BusinessUtils.reserveColumnsList(initLists, business);
        indexList(reserveColumnsList.get(0), business);
        List<List<String>> filterList = BusinessUtils.filterList(reserveColumnsList, business);
        List<List<String>> groupList = BusinessUtils.groupSumListByStream(filterList, business);
        /*输出*/
        String excelBase64Data = ExcelUtils.writeExcelMultipart(groupList, resultSheet, outputStream);
        Result result = new Result();
        result.setData(excelBase64Data);
        return result;
    }

    /**
     * 获取字符串在标题的位置
     * @param lists  输入List标题行
     * @param business 对象中包含要获取index的字符串
     */
    public void indexList(List<String> lists, Business business) {
        if (lists != null && !lists.isEmpty()) {
            int length = lists.indexOf(business.getLength());
            if (length<0){
                throw new BusinessException(Code.BUSINESS_ERR, "上传的Excel查询不到列名："+business.getLength());
            }
            business.setLengthIndex(length);

            int primaryKey = lists.indexOf(business.getPrimaryKey());
            if (primaryKey<0){
                throw new BusinessException(Code.BUSINESS_ERR, "上传的Excel查询不到列名："+business.getPrimaryKey());
            }
            business.setPrimaryKeyIndex(primaryKey);

            Integer filter = null;
            if(business.getFilter()!=null) {
                filter = lists.indexOf(business.getFilter());
                if (filter < 0) {
                    throw new BusinessException(Code.BUSINESS_ERR, "上传的Excel查询不到列名："+business.getFilter());
                }
                business.setFilterIndex(filter);
            }

            List<Integer> otherFieldsIndex = new ArrayList<>();


            for(String otherFields : business.getOtherFields()){
                int index = lists.indexOf(otherFields);
                if(index<0){
                    throw new BusinessException(Code.BUSINESS_ERR, "上传的Excel查询不到列名："+otherFields);
                }
                otherFieldsIndex.add(index);
            }

            List<Integer> groupIndexList = new ArrayList<>();
            groupIndexList.add(primaryKey);
            if(filter!=null)
                groupIndexList.add(filter);
            groupIndexList.addAll(otherFieldsIndex);

            business.setGroupIndexList(groupIndexList);
        }
    }



    @Override
    public List<List<List<String>>> allStandardMultipart(InputStream excelInputStream, InputStream rightExcelInputStream, Business business, Business rightBusiness, List<String> planStandardColumns, List<String> printStandardColumns) throws Exception{
        List<List<String>> initLists = ExcelUtils.readExcelMultipart(excelInputStream, null);
        indexList(initLists.get(0), business);

        List<List<String>> reserveColumnsList = BusinessUtils.reserveColumnsList(initLists, business);
        indexList(reserveColumnsList.get(0), business);
        List<List<String>> filterList = BusinessUtils.filterList(reserveColumnsList, business);
        List<List<String>> groupList = BusinessUtils.groupSumListByStream(filterList, business);

        List<List<String>> rightInitLists = ExcelUtils.readExcelMultipart(rightExcelInputStream, null);
        indexList(rightInitLists.get(0), rightBusiness);

        List<List<String>> reserveRightColumnsList = BusinessUtils.reserveColumnsList(rightInitLists, rightBusiness);
        indexList(reserveRightColumnsList.get(0), rightBusiness);
        List<List<String>> rightGroupList = BusinessUtils.groupSumListByStream(reserveRightColumnsList, rightBusiness);
        List<List<String>> lianbiao = BusinessUtils.lianbiao(groupList, rightGroupList, business, rightBusiness);
        List<List<String>> planStandardList = reserveColumnsListByList(lianbiao, planStandardColumns);
        List<List<String>> printStandardList = reserveColumnsListByList(lianbiao, printStandardColumns);
        List<List<List<String>>> re = new ArrayList<>();
        re.add(planStandardList);
        re.add(printStandardList);
        return re;
    }

    public static List<List<String>> reserveColumnsListByList(List<List<String>> list, List<String> reserveList){
        List<Integer> indexList = new ArrayList<>();
        for(int i=0; i<reserveList.size(); i++){
            String column = reserveList.get(i);
            int index = list.get(0).indexOf(column);
            indexList.add(index);
        }
        List<List<String>> result = new ArrayList<>();
        for(List<String> row : list){
            List<String> resultRow = new ArrayList<>();
            for(int ind : indexList){
                if(ind > row.size()){
                    resultRow.add("");
                    continue;
                }
                String cell = row.get(ind);
                resultRow.add(cell);
            }
            result.add(resultRow);
        }
        return result;
    }

    @Override
    public List<List<String>> allMiniMultipart(InputStream excelInputStream, Business business, List<String> reserveColumns) throws Exception{
        List<List<String>> initLists = ExcelUtils.readExcelMultipart(excelInputStream, null);
        indexList(initLists.get(0), business);
        List<List<String>> reserveColumnsList = BusinessUtils.reserveColumnsList(initLists, business);
        indexList(reserveColumnsList.get(0), business);
        List<List<String>> filterList = BusinessUtils.filterList(reserveColumnsList, business);
        List<List<String>> groupList = BusinessUtils.groupSumListByStream(filterList, business);
        return reserveColumnsListByList(groupList, reserveColumns);
    }
}
