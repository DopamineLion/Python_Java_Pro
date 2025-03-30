package org.dopamine.service;

import org.dopamine.bean.Business;
import org.dopamine.controller.view.Result;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface BusinessService {
    String mini(String excelPath, String readSheetName, Business business, String resultSheet, Boolean isWriteExcel) throws Exception;

    String standard(String excelPath, String excelSheet, String rightExcelPath, String rightExcelSheet, Business business, Business rightBusiness, String resultSheet, Boolean isWriteExcel) throws Exception;

    Result standardMultipart(InputStream excelInputStream, InputStream rightExcelInputStream, Business business, Business rightBusiness, String resultSheet)  throws Exception;

    Result miniMultipart(InputStream excelInputStream, OutputStream outputStream, Business business, String resultSheet) throws Exception;

    List<List<List<String>>> allStandardMultipart(InputStream excelInputStream, InputStream rightExcelInputStream, Business business, Business rightBusiness, List<String> planStandardColumns, List<String> printStandardColumns) throws Exception;

    List<List<String>> allMiniMultipart(InputStream excelInputStream, Business business, List<String> miniColumns) throws Exception;

}
