package org.dopamine.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dopamine.bean.TotalBean;
import org.dopamine.controller.view.Code;
import org.dopamine.controller.view.Result;
import org.dopamine.service.TotalService;
import org.dopamine.utils.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@RestController
@RequestMapping("/total")
public class TotalController {

    // 假设我们要将文件保存在项目的 "uploads" 目录下
    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private TotalService totalService;

    /**
     * 统计表-添加Excel
     */
    @PostMapping("/addTotal")
    public String addTotalMultipart(HttpServletRequest request, String form) throws Exception {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
        try {
            // 获取项目的根路径
            String projectPath = System.getProperty("user.dir");
            // 创建完整的上传文件路径
            Path uploadPath = Paths.get(projectPath).resolve(UPLOAD_DIR);
            // 确保上传目录存在
            Files.createDirectories(uploadPath);

            for (MultipartFile files : map.get("files")) {
                // 保存文件
                Path filePath = uploadPath.resolve(Objects.requireNonNull(files.getOriginalFilename()));

                boolean exists = Files.exists(filePath);
                if(exists){
                    return filePath.getFileName()+"已存在";
                }
                Files.copy(files.getInputStream(), filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "文件上传失败：" + e.getMessage();
        }
        return "上传成功";
    }

    /**
     * 统计表-获取Excel列表
     */
    @GetMapping("/total")
    public Result getTotal() throws Exception {
        // 获取项目的根路径
        String projectPath = System.getProperty("user.dir");
        // 创建完整的上传文件路径
        Path uploadPath = Paths.get(projectPath).resolve(UPLOAD_DIR);
        // 确保上传目录存在
        List<Path> list1 = new ArrayList<>();
        List<TotalBean> resultList = new ArrayList<>();
        if(Files.exists(uploadPath)) {
            Stream<Path> list = Files.list(uploadPath);
            list1 = list.toList();
            for (Path p : list1) {
                TotalBean totalBean = new TotalBean();
                FileTime lastModifiedTime = Files.getLastModifiedTime(p);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd HH时mm分");
                String format = sdf.format(lastModifiedTime.toMillis());
                totalBean.setUpdate(format);
                totalBean.setFileName(p.getFileName().toString());
                resultList.add(totalBean);
            }
        }
        Result result = new Result();
        result.setCode(Code.GET_OK);
        result.setData(resultList);
        result.setMsg("");
        return result;
    }

    /**
     * 统计表-下载
     */
    @DeleteMapping("/deleteTotal")
    public Result deleteTotal(String fileName) throws Exception {
        // 获取项目的根路径
        String projectPath = System.getProperty("user.dir");
        // 创建完整的上传文件路径
        Path uploadPath = Paths.get(projectPath).resolve(UPLOAD_DIR);
        Result result = new Result();
        if(!Files.exists(uploadPath)) {
            result.setMsg("文件夹不存在");
            result.setCode(Code.DELETE_ERR);
            return result;
        }
        if (fileName == null || fileName.isEmpty()) {
            result.setMsg(fileName+"文件名为空");
            result.setCode(Code.DELETE_ERR);
            return result;
        }
        File file = new File(uploadPath + "/" + fileName);
        if(file.exists()){
            if(file.delete()){
                result.setCode(Code.DELETE_OK);
                result.setMsg("删除成功");
                return result;
            }else {
                result.setCode(Code.DELETE_ERR);
                result.setMsg("文件删除失败");
                return result;
            }
        }else {
            result.setCode(Code.DELETE_ERR);
            result.setMsg("文件不存在");
            return result;
        }
    }

    /**
     * 统计表-获取Excel内容
     */
    @GetMapping("/getListByFileName")
    public Result getListByFileName(String fileName) throws Exception {
        Result result = new Result();
        // 获取项目的根路径
        String projectPath = System.getProperty("user.dir");
        // 创建完整的上传文件路径
        Path uploadPath = Paths.get(projectPath).resolve(UPLOAD_DIR);
        String flag = "";

        if (fileName == null || fileName.isEmpty()) {
            result.setCode(Code.GET_ERR);
            result.setMsg("查询文件名为空");
            return result;
        }else{
            flag = fileName;
        }
        File file = new File(uploadPath + "/" + flag);
        List<List<String>> lists = ExcelUtils.readExcelMultipart(new FileInputStream(file), null);
        result.setData(lists);
        result.setCode(Code.GET_OK);
        return result;
    }

    /**
     * 统计表-下载
     */
    @GetMapping("/downloadTotal")
    public void downloadTotal(HttpServletResponse response, String fileName) throws Exception {
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        // 获取项目的根路径
        String projectPath = System.getProperty("user.dir");
        // 创建完整的上传文件路径
        Path uploadPath = Paths.get(projectPath).resolve(UPLOAD_DIR);
        String flag = "";
        if (fileName == null || fileName.isEmpty()) {
            return;
        }else{
            flag = fileName;
        }
        byte[] buffer = new byte[1024];
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        OutputStream os = null; //输出流
        try {
            //获取resource中的文件，并生成流信息
//            resource = resourceLoader.getResource("classpath:data/userTemplate.xls");
            File file = new File(uploadPath +"/"+ flag);

            inputStream = new FileInputStream(file);
            //设置返回文件信息
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(fileName,"UTF-8"));
            //将内容使用字节流写入输出流中
            os = response.getOutputStream();
            bis = new BufferedInputStream(inputStream);
            while(bis.read(buffer) != -1){
                os.write(buffer);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭流信息
            try {
                if(inputStream !=null ) {
                    inputStream.close();
                }
                if(bis != null) {
                    bis.close();
                }
                if(os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取所有的Excel组套结果
     */
    @GetMapping("/queryAllResult")
    public Result queryAllResult(String type) throws Exception {
        Result result = new Result();
        // 获取项目的根路径
        String projectPath = System.getProperty("user.dir");
        // 创建完整的上传文件路径
        Path uploadPath = Paths.get(projectPath).resolve(UPLOAD_DIR);
        File allFile = uploadPath.toFile();
        Map<String, Object> map = new HashMap<>();
        if(type.equals("海参")){
            // 海参
            map = totalService.queryHSAllResult(allFile);
        }else{
            // 京东
            map = totalService.queryAllResult(allFile);
        }
        result.setData(map);
        result.setCode(Code.GET_OK);
        return result;
    }
}
