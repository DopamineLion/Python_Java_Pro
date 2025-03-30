package org.dopamine.utils;

import net.sf.jasperreports.util.Base64Util;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dopamine.bean.ZtBean;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {
    /**
     * 根据补打印Excel获得数据列表
     * @param excelInputStream 要查询的Excel输入流
     * @sheetName sheet表名称 如果为null，默认读取当前激活的sheet
     * @return 返回String 格式的List
     * @throws Exception
     */
    public static List<List<String>> readExcelMultipart(InputStream excelInputStream, String sheetName) throws Exception {
        Workbook wb = new XSSFWorkbook(excelInputStream);
        Sheet sheet = null;
        if(sheetName == null || sheetName.isEmpty()) {
            sheet = wb.getSheetAt(wb.getActiveSheetIndex());
        }else{
            sheet = wb.getSheet(sheetName);
        }
        int firstRowNum = sheet.getFirstRowNum();
        List<List<String>> resultList = new ArrayList<>();
        int lastCellNum = sheet.getRow(0).getLastCellNum();
        for (Row row : sheet) {
            List<String> rowList = new ArrayList<>();
//            if (row.getRowNum() == firstRowNum) {
//                continue;
//            }
            for(int i=firstRowNum; i<lastCellNum; i++){
                rowList.add(convertCellValueToString(row.getCell(i)));
            }
            boolean tag = false;
            for(int i=0; i<rowList.size(); i++){
                String element = rowList.get(i);
                if(element!= null && !element.isEmpty()){
                    tag = true;
                }
            }
            if(tag)
                resultList.add(rowList);
        }
        return resultList;
    }


    /**
     * 根据补打印Excel获得数据列表
     * @param excelFilePath 要查询的Excel路径
     * @sheetName sheet表名称 如果为null，默认读取当前激活的sheet
     * @return 返回String 格式的List
     * @throws Exception
     */
    public static List<List<String>> readExcel(String excelFilePath, String sheetName) throws Exception {
        FileInputStream file = new FileInputStream(new File(excelFilePath));
        Workbook wb = new XSSFWorkbook(file);
        Sheet sheet = null;
        if(sheetName == null || sheetName.isEmpty()) {
            sheet = wb.getSheetAt(wb.getActiveSheetIndex());
        }else{
            sheet = wb.getSheet(sheetName);
        }
        int firstRowNum = sheet.getFirstRowNum();
        List<List<String>> resultList = new ArrayList<>();
        for (Row row : sheet) {
            List<String> rowList = new ArrayList<>();
//            if (row.getRowNum() == firstRowNum) {
//                continue;
//            }
            for(int i=firstRowNum; i<=row.getLastCellNum(); i++){
                rowList.add(convertCellValueToString(row.getCell(i)));
            }
            resultList.add(rowList);
        }
        return resultList;
    }

    /**
     * 输出List到Excel
     * @param list 要输出的List
     * @param outputSheetName 输出的路径
     */
    public static String writeExcel(List<List<String>> list, String outputPath, String outputSheetName) throws Exception {
        Workbook workbook;
        FileInputStream fis = null;

        if(Files.exists(Paths.get(outputPath))) {
            fis = new FileInputStream(outputPath);
            workbook = new XSSFWorkbook(fis);
        }else{
            workbook = new XSSFWorkbook();
        }
        Sheet sheet = workbook.createSheet(outputSheetName);
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("General"));
//        Row row0 = sheet.createRow(0);
//        Cell cell0 = row0.createCell(0);
//        String Title = outputSheetName + "    " + formatDate("MM月dd日");
//        cell0.setCellValue(Title);
        int rowNum = 0;
        for (List<String> rows : list) {
            Row row = sheet.createRow(rowNum++);
            int columnNum = 0;
            for (String data : rows) {
                Cell cell = row.createCell(columnNum++);
                // 设置单元格的样式为常规
                cell.setCellValue(data);
                cell.setCellStyle(style);
            }
        }
        try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("输出Excel异常");
        } finally {
            // 清理资源
            try {
                workbook.close();
                if(fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("关闭workbook和FileInputStream异常");
            }
        }
        return "组套工作簿:"+outputPath+"\t>>>>>\t写入工作表成功："+outputSheetName;
    }



    /**
     * 输出List到Excel
     * @param list 要输出的List
     * @param outputSheetName 输出的路径
     */
    public static String writeExcelMultipart(List<List<String>> list, String outputSheetName, OutputStream outputStream) throws Exception {
        Workbook workbook;
        FileInputStream fis = null;
        workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(outputSheetName);
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("General"));
//        Row row0 = sheet.createRow(0);
//        Cell cell0 = row0.createCell(0);
//        String Title = outputSheetName + "    " + formatDate("MM月dd日");
//        cell0.setCellValue(Title);
        int rowNum = 0;
        for (List<String> rows : list) {
            Row row = sheet.createRow(rowNum++);
            int columnNum = 0;
            for (String data : rows) {
                Cell cell = row.createCell(columnNum++);
                // 设置单元格的样式为常规
                cell.setCellValue(data);
                cell.setCellStyle(style);
            }
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // 将EXCEL文件写进流内
            workbook.write(byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            String base64ExlCode = Base64Util.encode(bytes);
            return base64ExlCode;
//            String base64ExlCode = Base64Utils.encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("输出Excel异常");
        } finally {
            // 清理资源
            try {
                workbook.close();
                if(fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("关闭workbook和FileInputStream异常");
            }
        }
        return "组套工作簿:"+"\t>>>>>\t写入工作表成功："+outputSheetName;
    }

    /**
     * 将单元格内容转换为字符串
     * @param cell
     * @return
     */
    public static String convertCellValueToString(Cell cell) {
        if(cell==null){
            return "";
        }
        String returnValue = null;
        switch (cell.getCellType()) {
            case NUMERIC:   //数字
                Double doubleValue = cell.getNumericCellValue();
                // 格式化科学计数法，取一位整数
                DecimalFormat df = new DecimalFormat("0");
                returnValue = df.format(doubleValue);
                break;
            case STRING:    //字符串
                returnValue = cell.getStringCellValue();
                break;
            case BOOLEAN:   //布尔
                Boolean booleanValue = cell.getBooleanCellValue();
                returnValue = booleanValue.toString();
                break;
            case BLANK:     // 空值
                returnValue = "";
                break;
            case FORMULA:   // 公式
                returnValue = cell.getCellFormula();
                break;
            case ERROR:     // 故障
                returnValue = "";
                break;
            default:
                returnValue = "";
                break;
        }
        return returnValue;
    }

    public static String writeManySheetExcelMultipart(List<List<List<String>>> allList, List<String> resultSheet) {
        Workbook workbook;
        FileInputStream fis = null;
        workbook = new XSSFWorkbook();

        for(int i = 0; i < resultSheet.size(); i++){
            Sheet sheet = workbook.createSheet(resultSheet.get(i));
            CellStyle style = workbook.createCellStyle();
            style.setDataFormat(workbook.createDataFormat().getFormat("General"));
            int rowNum = 0;
            for (List<String> rows : allList.get(i)) {
                Row row = sheet.createRow(rowNum++);
                int columnNum = 0;
                for (String data : rows) {
                    Cell cell = row.createCell(columnNum++);
                    // 设置单元格的样式为常规
                    cell.setCellValue(data);
                    cell.setCellStyle(style);
                }
            }
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // 将EXCEL文件写进流内
            workbook.write(byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            String base64ExlCode = Base64Util.encode(bytes);
            return base64ExlCode;
//            String base64ExlCode = Base64Utils.encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("输出Excel异常");
        } finally {
            // 清理资源
            try {
                workbook.close();
                if(fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("关闭workbook和FileInputStream异常");
            }
        }
        throw new RuntimeException("输出有误");
    }

    public static List<List<String>> sortListByArray(List<List<String>> inputList, String[] headerArray){
        /** 获取index列表 **/
        List<Integer> indexList = new ArrayList<>();
        List<String> headerList = inputList.get(0);
        for(int i = 0; i<headerArray.length; i++){
            int queryIndex = headerList.indexOf(headerArray[i]);
            indexList.add(queryIndex);
        }

        List<List<String>> data = inputList.subList(1, inputList.size());
        /** 处理数据内容 **/
        List<List<String>> resultList = new ArrayList<>();
        /** 处理标题行，去除不显示的列 **/
        List<String> headerResultList = new ArrayList<>();
        for(Integer i : indexList){
            headerResultList.add(headerList.get(i));
        }
        resultList.add(headerResultList);
        for(int x = 0; x<data.size(); x++){
            List<String> resultRow = new ArrayList<>();
            for(Integer index :indexList){
                String s = data.get(x).get(index);
                resultRow.add(s);
            }
            resultList.add(resultRow);
        }
        return resultList;
    }
}
