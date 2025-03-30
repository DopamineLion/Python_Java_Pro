package org.dopamine.service;

import org.dopamine.bean.Bean;
import org.dopamine.controller.view.Result;

import java.io.InputStream;
import java.util.List;

public interface JasperService {
    String printJasper( String defaultInputPath, String judgeJasperFile, Boolean judge1D2D) throws Exception;
    Result printJasperMultipart(InputStream inputStream, String path, Boolean isQrCode) throws Exception;

    String printStandardJasper(String defaultInputPath, String path, Boolean isQrCode)throws Exception;
    Result printStandardJasperMultipart(InputStream inputStream, String path, Boolean isQrCode)throws Exception;

    String printMcnJasper(String defaultInputPath, String path, Boolean isQrCode)throws Exception;
    Result printMcnJasperMultipart(InputStream inputStream, String path, Boolean isQrCode)throws Exception;

    String printMiniJasper(String defaultInputPath, String path, Boolean isQrCode) throws Exception;
    Result printMiniJasperMultipart(InputStream defaultInputPath, String path, Boolean isQrCode) throws Exception;

    Result jianHuoMultipart(InputStream inputStream, String s, Boolean isQrCode) throws Exception;

    Result printMcnBoxJasperMultipart(InputStream inputStream, String s, Boolean isQrCode) throws Exception;
}
