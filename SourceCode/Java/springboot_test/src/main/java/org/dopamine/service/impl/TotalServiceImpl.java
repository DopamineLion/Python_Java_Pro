package org.dopamine.service.impl;

import org.dopamine.service.TotalService;
import org.dopamine.utils.DopamineUtils;
import org.dopamine.utils.ExcelUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import static javax.swing.UIManager.get;

@Service
public class TotalServiceImpl implements TotalService {
    @Value("${business.regex}")
    private String regex;
    @Value("${business.miniRegex}")
    private String miniRegex;

    public Map<String, Object> queryAllResult(File filePath) throws Exception{
        List<List<String>> allResult = new ArrayList<>();
        boolean isHeaderRowList = true;
        for(File f : Objects.requireNonNull(filePath.listFiles())){
            if(f.isFile()){
                List<List<String>> lists = ExcelUtils.readExcelMultipart(new FileInputStream(f), null);
                String headers = "项目号,商品编码,产品明细,规格,改数后数量,贴码";
                List<List<String>> excelList = ExcelUtils.sortListByArray(lists, headers.split(","));
                allResult.addAll(isHeaderRowList?excelList:excelList.subList(1,excelList.size()));
                isHeaderRowList = false;
            }
        }
        String regexStr = "贴码";
        String lenStr = "改数后数量";
        List<String> headerList = allResult.get(0);
        int regIndex = headerList.indexOf(regexStr);
        int lenIndex = headerList.indexOf(lenStr);
        List<List<String>> dataLists = allResult.subList(1, allResult.size());
        List<List<String>> allStandardList = new ArrayList<>();
        allStandardList.add(headerList);
        List<List<String>> allMiniList = new ArrayList<>();
        allMiniList.add(headerList);
        int sumStandard = 0;
        int sumMini = 0;
        for (int i = 0; i < dataLists.size(); i++) {
            List<String> row = dataLists.get(i);
            String tieMa = row.get(regIndex);
            String len = row.get(lenIndex);
            if(tieMa == null){
                tieMa = "";
            }
            if(DopamineUtils.search(regex, tieMa)){
                sumStandard+=Integer.parseInt(len);
                allStandardList.add(row);
            }else if(DopamineUtils.search(miniRegex, tieMa)){
                sumMini += Integer.parseInt(len);
                allMiniList.add(row);
            }
        }
        Map<String, Object> map = new HashMap<>();
//        map.put("standardList", allStandardList);
//        map.put("miniList", allMiniList);
//        map.put("allList", allResult);
        map.put("sumStandard", sumStandard);
        map.put("sumMini", sumMini);

        List<List<List<String>>> all = new ArrayList<>();
        all.add(allResult);
        all.add(allStandardList);
        all.add(allMiniList);
        List<String> resultSheet = new ArrayList<>();
        resultSheet.add("所有数据");
        resultSheet.add("大签");
        resultSheet.add("小签");
        String base64ExlCode = ExcelUtils.writeManySheetExcelMultipart(all, resultSheet);
        map.put("base64", base64ExlCode);
        return map;
    }

    @Override
    public Map<String, Object> queryHSAllResult(File filePath) throws Exception {
        List<List<String>> allResult = new ArrayList<>();
        boolean isHeaderRowList = true;
        for(File f : Objects.requireNonNull(filePath.listFiles())){
            if(f.isFile()){
                List<List<String>> lists = ExcelUtils.readExcelMultipart(new FileInputStream(f), null);
                String headers = "项目号,商品编码,商品名称,实发数量,备注,贴码";
                List<List<String>> excelList = ExcelUtils.sortListByArray(lists, headers.split(","));
                allResult.addAll(isHeaderRowList?excelList:excelList.subList(1,excelList.size()));
                isHeaderRowList = false;
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("sumStandard", allResult.size()-1);
        map.put("sumMini", 0);
        List<List<List<String>>> all = new ArrayList<>();
        all.add(allResult);
        List<String> resultSheet = new ArrayList<>();
        resultSheet.add("所有数据");
        String base64ExlCode = ExcelUtils.writeManySheetExcelMultipart(all, resultSheet);
        map.put("base64", base64ExlCode);
        return map;
    }
}
