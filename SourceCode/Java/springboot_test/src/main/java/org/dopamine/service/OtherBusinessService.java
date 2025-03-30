package org.dopamine.service;

import org.dopamine.bean.Business;
import org.dopamine.controller.view.Result;

import java.io.InputStream;
import java.util.List;

public interface OtherBusinessService {
    String pandian(String excelPath, String excelSheet, String resultSheet, Business business,  boolean isWriteExcel) throws Exception;
    Result pandianMultipart(InputStream excelInputStream, String resultSheet, Business business) throws Exception;

    String sumMonth(String path, List<Integer> fileNames, Business business, String sheetName) throws Exception;

    void budaqianRowsCopy(String inputExcelPath, String qty, String sheetName) throws Exception;
    Result budaqianRowsCopyMultipart(InputStream inputExcelPath, String qty, String sheetName) throws Exception;

    String appendExcel(Business business, String path) throws Exception;
}
