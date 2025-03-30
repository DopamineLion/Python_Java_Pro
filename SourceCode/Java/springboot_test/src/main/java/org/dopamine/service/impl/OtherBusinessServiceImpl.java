package org.dopamine.service.impl;

import org.dopamine.bean.Business;
import org.dopamine.controller.view.Result;
import org.dopamine.service.OtherBusinessService;
import org.dopamine.utils.BusinessUtils;
import org.dopamine.utils.ExcelUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class OtherBusinessServiceImpl implements OtherBusinessService {

    public String pandian(String excelPath, String excelSheet, String resultSheet, Business business, boolean isWriteExcel) throws Exception{
        String outputPath = BusinessUtils.getOutputPath(excelPath);

        List<List<String>> initLists = ExcelUtils.readExcel(excelPath, excelSheet);
        BusinessUtils.indexList(initLists.get(0), business);
        List<List<String>> reserveColumnsList = BusinessUtils.reserveColumnsList(initLists, business);
        BusinessUtils.indexList(reserveColumnsList.get(0), business);
        List<List<String>> filterList = BusinessUtils.filterList(reserveColumnsList, business);
        List<List<String>> groupList = BusinessUtils.groupSumListByStream(filterList, business);
        String result = null;
        if(isWriteExcel) {
            result = ExcelUtils.writeExcel(groupList, outputPath, resultSheet);
        }
        return result==null ? "失败":result;
    }

    public Result pandianMultipart(InputStream excelInputStream, String resultSheet, Business business) throws Exception{
        List<List<String>> initLists = ExcelUtils.readExcelMultipart(excelInputStream, null);
        BusinessUtils.indexList(initLists.get(0), business);
        List<List<String>> reserveColumnsList = BusinessUtils.reserveColumnsList(initLists, business);
        BusinessUtils.indexList(reserveColumnsList.get(0), business);
        List<List<String>> filterList = BusinessUtils.filterList(reserveColumnsList, business);
        List<List<String>> groupList = BusinessUtils.groupSumListByStream(filterList, business);
        String excelBase64Data = ExcelUtils.writeExcelMultipart(groupList, resultSheet, null);
        /*输出*/
        Result result = new Result();
        result.setData(excelBase64Data);
        return result;
    }

    @Override
    public String sumMonth(String path, List<Integer> fileNames, Business business, String sheetName) throws Exception{
        String outputPath;
        List<List<String>> resultList = new ArrayList<>();
        boolean isFirst = true;
        for(Integer name : fileNames){
            System.out.println(name);
            outputPath = path + name + ".xlsx";
            List<List<String>> initLists = ExcelUtils.readExcel(outputPath, null);
            BusinessUtils.indexList(initLists.get(0), business);
            List<List<String>> reserveColumnsList = BusinessUtils.reserveColumnsList(initLists, business);
            BusinessUtils.indexList(reserveColumnsList.get(0), business);
            List<List<String>> filterList = BusinessUtils.filterList(reserveColumnsList, business);
            List<List<String>> groupList = BusinessUtils.groupSumListByStream(filterList, business);
            if(isFirst){
                resultList.addAll(groupList.subList(0, 1));
                isFirst = false;
            }
            resultList.addAll(groupList.subList(1,groupList.size()));
        }
        String result = ExcelUtils.writeExcel(resultList, path+"汇总结果.xlsx", "汇总结果"+sheetName);
        return result;
    }

    /**
     * 处理海参补打签按数量复制出行数
     * @param inputExcelPath
     * @param qty
     * @param sheetName
     */
    @Override
    public void budaqianRowsCopy(String inputExcelPath, String qty, String sheetName) throws Exception{
        List<List<String>> resultList = new ArrayList<>();
        List<List<String>> initLists = ExcelUtils.readExcel(inputExcelPath, null);
        int index = initLists.get(0).indexOf(qty);
        resultList.add(initLists.get(0));
        for (List<String> row : initLists.subList(1, initLists.size())) {
            int size = Integer.parseInt(row.get(index));
            if(size>1){
                row.set(index, "1");
                for (int i = 0; i < size; i++) {
                    resultList.add(row);
                }
            }else {
                resultList.add(row);
            }
        }
        String outputPath = BusinessUtils.getOutputPath(inputExcelPath);
        ExcelUtils.writeExcel(resultList, outputPath, sheetName);
    }

    /**
     * 处理海参补打签按数量复制出行数
     * @param inputStream
     * @param qty
     * @param sheetName
     */
    @Override
    public Result budaqianRowsCopyMultipart(InputStream inputStream, String qty, String sheetName) throws Exception{
        List<List<String>> resultList = new ArrayList<>();
        List<List<String>> initLists = ExcelUtils.readExcelMultipart(inputStream, null);
        int index = initLists.get(0).indexOf(qty);
        resultList.add(initLists.get(0));
        for (List<String> row : initLists.subList(1, initLists.size())) {
            String value = row.get(index);
            if(value.contains(".")){
                value = value.substring(0, value.indexOf("."));
            }
            int size = Integer.parseInt(value);
            if(size>1){
                row.set(index, "1");
                for (int i = 0; i < size; i++) {
                    resultList.add(row);
                }
            }else {
                resultList.add(row);
            }
        }
        String data = ExcelUtils.writeExcelMultipart(resultList, sheetName, null);
        Result result = new Result();
        result.setData(data);
        return result;
    }

    @Override
    public String appendExcel(Business business, String path) throws Exception {
        File filePath = new File(path);
        File[] files = filePath.listFiles();
        List<List<String>> list = new ArrayList<>();
        boolean tag = true;
        for (File f : files) {
            System.out.println(f.getAbsolutePath());
            if(f.isFile()){
                List<List<String>> initLists = ExcelUtils.readExcel(f.getAbsolutePath(), null);
                BusinessUtils.indexList(initLists.get(0), business);
                List<List<String>> reserveColumnsList = BusinessUtils.reserveColumnsList(initLists, business);
                if (tag){
                    list.addAll(reserveColumnsList);
                    tag = false;
                }else {
                    list.addAll(reserveColumnsList.subList(1,reserveColumnsList.size()));
                }
            }
        }
        ExcelUtils.writeExcel(list, path+"汇总数据.xlsx", "汇总数据");
        return "result";
    }
}
