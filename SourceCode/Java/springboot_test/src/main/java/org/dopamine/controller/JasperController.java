package org.dopamine.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.dopamine.controller.view.Code;
import org.dopamine.controller.view.Result;
import org.dopamine.service.BarTenderService;
import org.dopamine.service.JasperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.InputStream;

@RestController
@RequestMapping("/jasper")
public class JasperController {
    @Value("${jasper.defaultInputPath}")
    private String defaultInputPath;
    @Value("${jasper.isQrCode}")
    private Boolean isQrCode;

    @Autowired
    private JasperService jasperService;

    @GetMapping("/budaqian")
    public String budaqian() throws Exception{
//        String path = resourceLoader.getResource("classpath:jasper/Report_V_HZ_BuDaQian.jasper").getFile().getPath();
        return jasperService.printJasper(defaultInputPath, "jasper/budaqian_7.jasper", isQrCode);
    }

    @PostMapping("/budaqianMultipart")
    public Result budaqianMultipart(HttpServletRequest request) throws Exception{
        String resultFile = "补打签.pdf";
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
        InputStream inputStream = map.get("files").get(0).getInputStream();
        Result result = jasperService.printJasperMultipart(inputStream, "jasper/budaqian_7.jasper", isQrCode);
        result.setCode(Code.SAVE_OK);
        result.setMsg(resultFile);
        return result;
    }

    @GetMapping("/standard")
    public String standard() throws Exception{
        return jasperService.printStandardJasper(defaultInputPath, "jasper/standard_7.jasper", isQrCode);
    }

    @PostMapping("/standardMultipart")
    public Result standardMultipart(HttpServletRequest request) throws Exception{
        String resultFile = "大签.pdf";
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
        InputStream inputStream = map.get("files").get(0).getInputStream();
        Result result = jasperService.printStandardJasperMultipart(inputStream, "jasper/standard_7.jasper", isQrCode);
        result.setCode(Code.SAVE_OK);
        result.setMsg(resultFile);
        return result;
    }

    @GetMapping("/mcn")
    public String mcn() throws Exception{
        return jasperService.printMcnJasper(defaultInputPath, "jasper/mcn_7_1.jasper", isQrCode);
    }

    @PostMapping("/mcnMultipart")
    public Result mcnMultipart(HttpServletRequest request) throws Exception{
        String resultFile = "MCN.pdf";
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
        InputStream inputStream = map.get("files").get(0).getInputStream();
        Result result = jasperService.printMcnJasperMultipart(inputStream, "jasper/mcn_7_1.jasper", isQrCode);
        result.setCode(Code.SAVE_OK);
        result.setMsg(resultFile);
        return result;
    }



    @PostMapping("/mcnBoxMultipart")
    public Result mcnBoxMultipart(HttpServletRequest request) throws Exception{
        String resultFile = "MCNBox.pdf";
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
        InputStream inputStream = map.get("files").get(0).getInputStream();
        Result result = jasperService.printMcnBoxJasperMultipart(inputStream, "jasper/MCNBox.jasper", isQrCode);
        result.setCode(Code.SAVE_OK);
        result.setMsg(resultFile);
        return result;
    }



    @GetMapping("/mini")
    public String mini() throws Exception{
        return jasperService.printMiniJasper(defaultInputPath, "jasper/mini_7.jasper", isQrCode);
    }

    @PostMapping("/miniMultipart")
    public Result miniMultipart(HttpServletRequest request) throws Exception{
        String resultFile = "小签.pdf";
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
        InputStream inputStream = map.get("files").get(0).getInputStream();
        Result result = jasperService.printMiniJasperMultipart(inputStream, "jasper/mini_7.jasper", isQrCode);
        result.setCode(Code.SAVE_OK);
        result.setMsg(resultFile);
        return result;
    }

    @PostMapping("/jianHuoMultipart")
    public Result jianHuoMultipart(HttpServletRequest request) throws Exception{
        String resultFile = "拣货签.pdf";
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
        InputStream inputStream = map.get("files").get(0).getInputStream();
        Result result = jasperService.jianHuoMultipart(inputStream, "jasper/jianhuo_7.jasper", isQrCode);
        result.setCode(Code.SAVE_OK);
        result.setMsg(resultFile);
        return result;
    }
}
