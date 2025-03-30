package org.dopamine.controller;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dopamine.bean.Business;
import org.dopamine.controller.view.Code;
import org.dopamine.controller.view.Result;
import org.dopamine.service.BusinessService;
import org.dopamine.utils.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/business")
public class BusinessController {
    /*京东仓*/
    @Value("${business.excelPath}")
    private String excelPath;
    @Value("${business.excelSheet}")
    private String excelSheet;
    @Value("${business.rightExcelPath}")
    private String rightExcelPath;
    @Value("${business.rightExcelSheet}")
    private String rightExcelSheet;
    @Value("${business.regex}")
    private String regex;
    @Value("${business.miniRegex}")
    private String miniRegex;
    @Value("${business.filterColumn}")
    private String filterColumn;
    @Value("${business.lengthColumn}")
    private String lengthColumn;

    /*海参*/
    @Value("${seaBusiness.seaExcelPath}")
    private String seaExcelPath;
    @Value("${seaBusiness.seaRightExcelPath}")
    private String seaRightExcelPath;

    @Autowired
    private BusinessService businessService;

    @GetMapping("/mini")
    public String mini(String inputExcelPath) throws Exception {
        Business business = new Business();
        business.setPrimaryKey("项目号");
        business.setLength(lengthColumn);
        business.setFilter(filterColumn);
//        "目的城市", "采购单号",
        String[] otherFields = {"商品编码", "产品明细", "规格"};
        business.setOtherFields(otherFields);
        business.setRegex(miniRegex);
        if (inputExcelPath != null) {
            excelPath = inputExcelPath;
        }
        Boolean isWriteExcel = true;
        String resultSheet = "小签";
        String msg = businessService.mini(excelPath, excelSheet, business, resultSheet, isWriteExcel);
        return msg;
    }

    @GetMapping("/miniPlan")
    public String miniPlan(String inputExcelPath) throws Exception {
        Business business = new Business();
        business.setPrimaryKey("项目号");
        business.setLength(lengthColumn);
        business.setFilter(filterColumn);
//        "目的城市", "采购单号",
        String[] otherFields = {"目的城市","采购单号","商品编码", "产品明细", "规格"};
        business.setOtherFields(otherFields);
        business.setRegex(miniRegex);
        if (inputExcelPath != null) {
            excelPath = inputExcelPath;
        }
        Boolean isWriteExcel = true;
        String resultSheet = "小签";
        String msg = businessService.mini(excelPath, excelSheet, business, resultSheet, isWriteExcel);
        return msg;
    }

    @PostMapping("/miniMultipart")
    public Result miniMultipart(HttpServletRequest request, String form, HttpServletResponse response) throws Exception {
        Business business = JSON.parseObject(form, Business.class);
        if (business.getFilter() == null || business.getFilter().isEmpty())
            business.setFilter(filterColumn);
        if (business.getLength() == null || business.getLength().isEmpty())
            business.setLength(lengthColumn);

        business.setPrimaryKey("项目号");

        List<String> list = new ArrayList<>();
        list.add("商品编码");
        list.add("产品明细");
        list.add("规格");
        if (business.getAdditional() != null && !business.getAdditional().isEmpty()){
            String additional = business.getAdditional();
            String[] split = additional.split(",");
            list.addAll(Arrays.asList(split));
        }
        String[] otherFields = list.toArray(new String[0]);
        business.setOtherFields(otherFields);
        business.setRegex(miniRegex);
        String resultSheet = "小签";
        String resultFile = "小签结果.xlsx";
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
        InputStream inputStream = map.get("files").get(0).getInputStream();
        ServletOutputStream outputStream = response.getOutputStream();

        Result result = businessService.miniMultipart(inputStream, outputStream, business, resultSheet);
        result.setCode(Code.SAVE_OK);
        result.setMsg(resultFile);
        return result;
    }


    @GetMapping("/standard")
    public String standard() throws Exception {
        Business business = new Business();
        Business rightBusiness = new Business();
        if (business.getFilter() == null || business.getFilter().isEmpty())
            business.setFilter(filterColumn);
        if (business.getLength() == null || business.getLength().isEmpty())
            business.setLength(lengthColumn);

        business.setPrimaryKey("项目号");
        String[] otherFields = {"商品编码", "产品明细", "规格"};
        business.setOtherFields(otherFields);
        business.setRegex(regex);

        rightBusiness.setPrimaryKey("SKU号");
        rightBusiness.setLength("数量");
        String[] rightOtherFields = {"名称", "生产日期", "失效日期"};
        rightBusiness.setOtherFields(rightOtherFields);
        String resultSheet = "大签";
        Boolean isWriteExcel = true;

        String msg = businessService.standard(excelPath, excelSheet, rightExcelPath, rightExcelSheet, business, rightBusiness, resultSheet, isWriteExcel);
        return "msg";
    }

    @PostMapping(value = "/standardMultipart", produces = "application/json;charset=UTF-8")
    public Result standardMultipart(HttpServletRequest request, String form) throws Exception {

        Business business = JSON.parseObject(form, Business.class);
        if (business.getFilter() == null || business.getFilter().isEmpty())
            business.setFilter(filterColumn);
        if (business.getLength() == null || business.getLength().isEmpty())
            business.setLength(lengthColumn);

        business.setPrimaryKey("项目号");

        List<String> list = new ArrayList<>();
        list.add("商品编码");
        list.add("产品明细");
        list.add("规格");
        if (business.getAdditional() != null && !business.getAdditional().isEmpty()){
            String additional = business.getAdditional();
            String[] split = additional.split(",");
            list.addAll(Arrays.asList(split));
        }
        String[] otherFields = list.toArray(new String[0]);
        business.setOtherFields(otherFields);
        business.setRegex(regex);

        Business rightBusiness = new Business();
        rightBusiness.setPrimaryKey("SKU号");
        rightBusiness.setLength("数量");
        String[] rightOtherFields = {"名称", "生产日期", "失效日期"};
        rightBusiness.setOtherFields(rightOtherFields);
        String resultSheet = "大签";
        String resultFile = "大签结果.xlsx";
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
        Result result = businessService.standardMultipart(map.get("files").get(0).getInputStream(), map.get("files").get(1).getInputStream(), business, rightBusiness, resultSheet);
        result.setCode(Code.SAVE_OK);
        result.setMsg(resultFile);
        return result;
    }

    @GetMapping("/sea")
    public String sea() throws Exception {
        Business business = new Business();
        business.setPrimaryKey("项目号");
        business.setLength("实发数量");
        business.setFilter("备注");
        String[] otherFields = {"商品编码", "商品名称", "贴码"};
        business.setOtherFields(otherFields);
        business.setRegex(".*");

        Business rightBusiness = new Business();
        rightBusiness.setPrimaryKey("SKU号");
        rightBusiness.setLength("数量");
        String[] rightOtherFields = {"名称", "生产日期", "失效日期"};
        rightBusiness.setOtherFields(rightOtherFields);
        String resultSheet = "海参联表结果";
        Boolean isWriteExcel = true;
        String msg = businessService.standard(seaExcelPath, excelSheet, seaRightExcelPath, rightExcelSheet, business, rightBusiness, resultSheet, isWriteExcel);
        return msg;
    }

    @PostMapping("/seaMultipart")
    public Result seaMultipart(HttpServletRequest request, String form) throws Exception {
        Business business = JSON.parseObject(form, Business.class);
        if (business.getFilter() == null || business.getFilter().isEmpty())
            business.setFilter(filterColumn);
        if (business.getLength() == null || business.getLength().isEmpty())
            business.setLength(lengthColumn);
        business.setPrimaryKey("项目号");
        business.setLength("实发数量");
        business.setFilter("备注");
        String[] otherFields = {"商品编码", "商品名称", "贴码"};
        business.setOtherFields(otherFields);
        business.setRegex(".*");

        Business rightBusiness = new Business();
        rightBusiness.setPrimaryKey("SKU号");
        rightBusiness.setLength("数量");
        String[] rightOtherFields = {"名称", "生产日期", "失效日期"};
        rightBusiness.setOtherFields(rightOtherFields);

//        String resultSheet = "海参";
//        Boolean isWriteExcel = true;
//        String msg = businessService.standard(seaExcelPath, excelSheet, seaRightExcelPath, rightExcelSheet, business, rightBusiness, resultSheet, isWriteExcel);


        String resultSheet = "海参";
        String resultFile = "海参结果.xlsx";
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
        Result result = businessService.standardMultipart(map.get("files").get(0).getInputStream(), map.get("files").get(1).getInputStream(), business, rightBusiness, resultSheet);
        result.setCode(Code.SAVE_OK);
        result.setMsg(resultFile);
        return result;
    }

    @PostMapping(value = "/allMultipart", produces = "application/json;charset=UTF-8")
    public Result allMultipart(HttpServletRequest request, String form) throws Exception {

        Business business = JSON.parseObject(form, Business.class);
        if (business.getFilter() == null || business.getFilter().isEmpty())
            business.setFilter(filterColumn);
        if (business.getLength() == null || business.getLength().isEmpty())
            business.setLength(lengthColumn);

        business.setPrimaryKey("项目号");

        List<String> list = new ArrayList<>();
        list.add("产品明细");
        list.add("规格");
        list.add("商品编码");
        if (business.getAdditional() != null && !business.getAdditional().isEmpty()){
            String additional = business.getAdditional();
            String[] split = additional.split(",");
            list.addAll(Arrays.asList(split));
        }
        String[] otherFields = list.toArray(new String[0]);
        business.setOtherFields(otherFields);
        business.setRegex(regex);

        Business rightBusiness = new Business();
        rightBusiness.setPrimaryKey("SKU号");
        rightBusiness.setLength("数量");
        String[] rightOtherFields = {"名称", "生产日期", "失效日期"};
        rightBusiness.setOtherFields(rightOtherFields);

        List<String> planStandardColumns = new ArrayList<>();
        planStandardColumns.addAll(list);
        planStandardColumns.add(business.getPrimaryKey());
        planStandardColumns.add(business.getLength());
        planStandardColumns.add("生产日期");
        planStandardColumns.add("失效日期");
        planStandardColumns.add(rightBusiness.getLength());
        planStandardColumns.add(business.getFilter());

        List<String> printStandardColumns = new ArrayList<>();
        printStandardColumns.addAll(list);
        printStandardColumns.add("名称");
        printStandardColumns.add("生产日期");
        printStandardColumns.add("失效日期");
        printStandardColumns.add(rightBusiness.getLength());
        printStandardColumns.add(business.getLength());
        printStandardColumns.add(business.getPrimaryKey());


        List<String> planMiniColumns = new ArrayList<>();
        planMiniColumns.add("目的城市");
        planMiniColumns.add("采购单号");
        planMiniColumns.add(business.getPrimaryKey());
        planMiniColumns.addAll(list);
        planMiniColumns.add(business.getLength());
        planMiniColumns.add(business.getFilter());

        List<String> printMiniColumns = new ArrayList<>();
        printMiniColumns.add(business.getPrimaryKey());
        printMiniColumns.addAll(list);
        printMiniColumns.add(business.getLength());
        printMiniColumns.add(business.getFilter());


        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
        List<List<List<String>>> allList = businessService.allStandardMultipart(map.get("files").get(0).getInputStream(), map.get("files").get(1).getInputStream(), business, rightBusiness, planStandardColumns, printStandardColumns);
        business.setRegex(miniRegex);
        List<List<String>> printMiniList = businessService.allMiniMultipart(map.get("files").get(0).getInputStream(), business, printMiniColumns);



        list = new ArrayList<>();
        list.add("目的城市");
        list.add("采购单号");
        list.add("商品编码");
        list.add("产品明细");
        list.add("规格");
        if (business.getAdditional() != null && !business.getAdditional().isEmpty()){
            String additional = business.getAdditional();
            String[] split = additional.split(",");
            list.addAll(Arrays.asList(split));
        }
        otherFields = list.toArray(new String[0]);
        business.setOtherFields(otherFields);

        List<List<String>> planMiniList = businessService.allMiniMultipart(map.get("files").get(0).getInputStream(), business, planMiniColumns);
        allList.add(planMiniList);
        allList.add(printMiniList);
        List<String> resultSheet = new ArrayList<>();
        resultSheet.add("大签生产计划");
        resultSheet.add("大签打印");
        resultSheet.add("小签生产计划");
        resultSheet.add("小签打印");
        String resultFile = "京东仓查询结果.xlsx";
        String base64ExlCode = ExcelUtils.writeManySheetExcelMultipart(allList, resultSheet);
        Result result = new Result();
        result.setData(base64ExlCode);
        result.setCode(Code.SAVE_OK);
        result.setMsg(resultFile);
        return result;
    }
}
