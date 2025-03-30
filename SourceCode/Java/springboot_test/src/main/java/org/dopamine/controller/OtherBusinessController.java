package org.dopamine.controller;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.dopamine.bean.Business;
import org.dopamine.bean.TotalBean;
import org.dopamine.controller.view.Code;
import org.dopamine.controller.view.Result;
import org.dopamine.service.OtherBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@RestController
@RequestMapping("/otherBusiness")
public class OtherBusinessController {
    @Autowired
    private OtherBusinessService otherBusinessService;

    @Value("${otherBusiness.excelPath}")
    private String excelPath;
    @Value("${otherBusiness.excelSheet}")
    private String excelSheet;
    @Value("${business.filterColumn}")
    private String filterColumn;
    @Value("${business.regex}")
    private String regex;
    @Value("${business.miniRegex}")
    private String miniRegex;


    /**
     * 盘点表
     * @param inputExcelPath
     * @return
     * @throws Exception
     */
    @GetMapping("/pandian")
    public String pandian(String inputExcelPath) throws Exception {
        String location = "LZ.*";
        Business business = new Business();
        business.setPrimaryKey("产品");
        business.setLength("库存数量");
        business.setFilter("库位");
        String[] otherFields = {"英文描述","入库批号"};
        business.setOtherFields(otherFields);
        business.setRegex("^"+location+"$");
        if(inputExcelPath != null){
            excelPath = inputExcelPath;
        }
        String resultSheet = null;
        resultSheet = "盘点表"+ "二楼"+"库位";
        Boolean isWriteExcel = true;
        String rusult = otherBusinessService.pandian(excelPath, excelSheet, resultSheet, business, isWriteExcel);

        location = "28";
        business.setRegex("^"+location+"$");
        resultSheet = "盘点表"+ location +"库位";
        rusult += otherBusinessService.pandian(excelPath, excelSheet, resultSheet, business, isWriteExcel);

        location = "29";
        business.setRegex("^"+location+"$");
        resultSheet = "盘点表"+ location +"库位";
        rusult += otherBusinessService.pandian(excelPath, excelSheet, resultSheet, business, isWriteExcel);
        return rusult;
    }

    /**
     * 盘点表
     * @return
     * @throws Exception
     */
    @PostMapping("/pandianMultipart")
    public Result pandianMultipart(HttpServletRequest request, String form) throws Exception {
        Business business = JSON.parseObject(form, Business.class);
        String resultSheet = null;
        if(business.getRegex()!=null && !business.getRegex().isEmpty()) {
            String regexTemp = business.getRegex();
            business.setRegex(regexTemp);
//            business.setRegex("^"+regexTemp+"$");
        }else{
            business.setRegex("^.*$");
        }
        business.setPrimaryKey("产品");
        business.setLength("库存数量");
        business.setFilter("库位");
        String[] otherFields = {"英文描述", "入库批号"};
        business.setOtherFields(otherFields);

        String regex = "[`/\\\\?*\\[\\].^$]";
        String replacedAllString = business.getRegex().replaceAll(regex, "");

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
        InputStream inputStream = map.get("files").get(0).getInputStream();

        resultSheet = replacedAllString +"库位";
        String resultFile = "盘点表"+resultSheet+".xlsx";

        Result result = otherBusinessService.pandianMultipart(inputStream, resultSheet, business);
        result.setCode(Code.SAVE_OK);
        result.setMsg(resultFile);
        return result;
    }

    /**
     * 汇总Excel所有数据
     * @return
     * @throws Exception
     */
    @GetMapping("/appendExcel")
    public String appendExcel() throws Exception {
        String path = "C:\\Users\\Administrator\\Documents\\Data\\others\\月发货量\\12月\\京东\\处理数据\\";
//        String path = "C:\\Users\\Administrator\\Documents\\Data\\others\\月发货量\\8-9月新\\8-9(1)\\9月\\处理数据\\";
        Business business = new Business();
        business.setPrimaryKey("项目号");
        business.setLength("改数后数量");
        business.setFilter(filterColumn);
        String[] otherFields = {"商品编码","产品明细","规格"};
        business.setOtherFields(otherFields);
        return otherBusinessService.appendExcel(business, path);
    }

    /**
     * 汇总一个月组套总数
     * @return
     * @throws Exception
     */
    @PostMapping("/sumMonth")
    public String sumMonth(String inputPath, String inputType) throws Exception {
        Integer maxIndex = 7;
        String path = "";
        if(inputPath == null || inputPath.isEmpty())
            return "文件夹地址不能为空";
        else
            path = inputPath;
        if(inputType.equals("京东")){
            Business business = new Business();
            business.setPrimaryKey("项目号");
            business.setLength("改数后数量");
            business.setFilter(filterColumn);
            String[] otherFields = {"商品编码","产品明细","规格"};
            business.setOtherFields(otherFields);
            business.setRegex(miniRegex);
            //小签
            sumMonthPublic(path, maxIndex, business, "小签");
            //大签
            business.setRegex(regex);
            sumMonthPublic(path, maxIndex, business, "大签");
        }else if(inputType.equals("海参")){
            Business seaBusiness = new Business();
            seaBusiness.setPrimaryKey("项目号");
            seaBusiness.setLength("实发数量");
            seaBusiness.setFilter("贴码");
            String[] seaOtherFields = {"商品编码","商品名称"};
            seaBusiness.setOtherFields(seaOtherFields);
            seaBusiness.setRegex("^.+$");
            sumMonthPublic(path, maxIndex, seaBusiness, "海参大签");
        }
        return "月组套全部数据导出成功至"+path;
    }

    /**
     * 汇总一个月组套总数
     * @param path
     * @param maxIndex
     * @param business
     * @param sheetName
     * @return
     * @throws Exception
     */
    public String sumMonthPublic(String path, Integer maxIndex, Business business, String sheetName) throws Exception{
        List<Integer> fileNames = new ArrayList<>();
        for (int i = 1; i<=maxIndex; i++) {
            fileNames.add(i);
        }
//        File filePath = new File(path);
//        File[] files = filePath.listFiles();
//        for(File f : files){
//            fileNames.add(f.getAbsolutePath());
////            System.out.println(f.getAbsolutePath());
//        }
        return otherBusinessService.sumMonth(path, fileNames, business, sheetName);
//        return "";
    }

    /**
     * 海参补打签按照数量复制出行数
     * @return
     * @throws Exception
     */
    @GetMapping("/budaqianRowsCopy")
    public String budaqianRowsCopy(String inputExcelPath) throws Exception {
        if(inputExcelPath == null)
            inputExcelPath = "C:\\Users\\Administrator\\Documents\\Data\\海参\\11.12\\海参补打签.xlsx";
        String sheetName = "海参处理完行数后";
        otherBusinessService.budaqianRowsCopy(inputExcelPath, "QTY", sheetName);
        return "海参补打签数据已经按数量一列处理完毕";
    }

    /**
     * 海参补打签按照数量复制出行数
     * @return
     * @throws Exception
     */
    @PostMapping("/budaqianRowsCopyMultipart")
    public Result budaqianRowsCopyMultipart(HttpServletRequest request, String form) throws Exception {
        Map<String, String> map1 = JSON.parseObject(form, Map.class);
        String lengthColumn = map1.get("lengthColumn");
        String sheetName = "海参处理箱数结果";
        String resultFile = "海参处理箱数结果.xlsx";
        if(lengthColumn == null || lengthColumn.isEmpty()){
            lengthColumn = "QTY";
        }
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
        InputStream inputStream = map.get("files").get(0).getInputStream();
        Result result = otherBusinessService.budaqianRowsCopyMultipart(inputStream, lengthColumn, sheetName);
        result.setCode(Code.SAVE_OK);
        result.setMsg(resultFile);
        return result;
    }
}
