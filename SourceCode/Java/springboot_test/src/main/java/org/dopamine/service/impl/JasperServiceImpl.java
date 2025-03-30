package org.dopamine.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.util.Base64Util;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.Row;
import org.dopamine.bean.Bean;
import org.dopamine.bean.JianHuoBean;
import org.dopamine.bean.McnBoxBean;
import org.dopamine.controller.view.Result;
import org.dopamine.service.JasperService;
import org.dopamine.utils.BarCodeUtils;
import org.dopamine.utils.DopamineUtils;
import org.dopamine.utils.ExcelUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.dopamine.utils.ExcelUtils;

@Service
public class JasperServiceImpl implements JasperService {

    public String printJasper(String defaultInputPath, String jasperPath, Boolean isQrCode) throws Exception {
        List<List<String>> excelList = ExcelUtils.readExcel(defaultInputPath, null);
        List<Bean> beanList = manageExcelList(excelList.subList(1,excelList.size()), isQrCode);
//        InputStream is = new FileInputStream(jasperPath);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(jasperPath);
        JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(beanList);
        JasperPrint jp = JasperFillManager.fillReport(is, new HashMap<>(), jrBean);
        String outputPath = defaultInputPath + "_" + DopamineUtils.formatDate(null) + ".pdf";
        JasperExportManager.exportReportToPdfFile(jp, outputPath);
        return "输出" + outputPath + "成功";
    }

    @Override
    public Result printJasperMultipart(InputStream inputStream, String jasperPath, Boolean isQrCode) throws Exception {
        List<List<String>> excelList = ExcelUtils.readExcelMultipart(inputStream, null);
        List<Bean> beanList = manageExcelList(excelList.subList(1,excelList.size()), isQrCode);

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(jasperPath);
        JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(beanList);
        JasperPrint jp = JasperFillManager.fillReport(is, new HashMap<>(), jrBean);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        String base64PdfCode = Base64Util.encode(bytes);
        Result result = new Result();
        result.setData(base64PdfCode);
        return result;
    }

    private List<Bean> manageExcelList(List<List<String>> excelList, Boolean isQrCode) throws Exception{
        List<Bean> beanList = new ArrayList<>();
        for (List<String> strings : excelList) {
            Bean bean = new Bean();
            bean.setFIELDVALUE61(strings.get(61 - 1));
            bean.setFIELDVALUE50(strings.get(50 - 1));
            bean.setFIELDVALUE6(strings.get(6 - 1));
            bean.setFIELDVALUE62(strings.get(62 - 1));
            bean.setFIELDVALUE35(strings.get(35 - 1));
            bean.setFIELDVALUE21(strings.get(21 - 1));
            bean.setFIELDVALUE11(strings.get(11 - 1));
            bean.setFIELDVALUE13(strings.get(13 - 1));
            bean.setFIELDVALUE19(strings.get(19 - 1));
            bean.setFIELDVALUE16(strings.get(16 - 1));
            bean.setFIELDVALUE58(strings.get(58 - 1));
            bean.setFIELDVALUE59(strings.get(59 - 1));
            bean.setFIELDVALUE56(strings.get(56 - 1));
//            String proId = strings.get(59 - 1);
//            if(isQrCode != null) {
//                InputStream code1D2D = BarCodeUtils.getBarcodeOrQrCode(isQrCode, proId);
//                String imageBase64 = BarCodeUtils.getImageBase64ToString(code1D2D);
//                bean.setFIELDVALUE66(imageBase64);
//            }
            beanList.add(bean);
        }
        return beanList;
    }

    public String printStandardJasper(String defaultInputPath, String path, Boolean isQrCode)throws Exception{
        List<List<String>> excelList = ExcelUtils.readExcel(defaultInputPath, null);
        List<Bean> beanList = stadardExcelList(excelList.subList(1,excelList.size()), isQrCode);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(beanList);
        JasperPrint jp = JasperFillManager.fillReport(is, new HashMap<>(), jrBean);
        String outputPath = defaultInputPath + "_" + DopamineUtils.formatDate(null) + ".pdf";
        JasperExportManager.exportReportToPdfFile(jp, outputPath);
        return "输出" + outputPath + "成功";
    }

    public Result printStandardJasperMultipart(InputStream inputStream, String jasperPath, Boolean isQrCode) throws Exception {
        List<List<String>> excelList = ExcelUtils.readExcelMultipart(inputStream, null);
        List<Bean> beanList = stadardExcelList(excelList.subList(1,excelList.size()), isQrCode);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(jasperPath);
        JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(beanList);
        JasperPrint jp = JasperFillManager.fillReport(is, new HashMap<>(), jrBean);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        String base64PdfCode = Base64Util.encode(bytes);
        Result result = new Result();
        result.setData(base64PdfCode);
        return result;
    }

    public List<Bean> stadardExcelList(List<List<String>> excelList, Boolean isQrCode) throws Exception{
        List<Bean> beanList = new ArrayList<>();
        for (List<String> strings : excelList) {
            if(strings == null || strings.size()<4){
                throw new Exception();
            }
            Bean bean = new Bean();
            bean.setFIELDVALUE1(strings.get(1 - 1));
            bean.setFIELDVALUE2(strings.get(2 - 1));
            bean.setFIELDVALUE3(strings.get(3 - 1));
            bean.setFIELDVALUE60("生产日期:");
            bean.setFIELDVALUE61("失效日期:");
            String proId = strings.get(4 - 1);
            if(isQrCode != null) {
                InputStream code1D2D = BarCodeUtils.getBarcodeOrQrCode(isQrCode, proId);
                String imageBase64 = BarCodeUtils.getImageBase64ToString(code1D2D);
                bean.setFIELDVALUE4(imageBase64);
            }
            beanList.add(bean);
        }
        return beanList;
    }

    public String printMcnJasper(String defaultInputPath, String path, Boolean isQrCode)throws Exception{
        List<List<String>> excelList = ExcelUtils.readExcel(defaultInputPath, null);
        List<String> headerList = excelList.get(0);
        List<Bean> beanList = mcnExcelList(excelList.subList(1,excelList.size()), headerList, isQrCode);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(beanList);
        JasperPrint jp = JasperFillManager.fillReport(is, new HashMap<>(), jrBean);
        String outputPath = defaultInputPath + "_" + DopamineUtils.formatDate(null) + ".pdf";
        JasperExportManager.exportReportToPdfFile(jp, outputPath);
        return "输出" + outputPath + "成功";
    }

    public Result printMcnJasperMultipart(InputStream inputStream, String path, Boolean isQrCode)throws Exception{
        List<List<String>> excelList = ExcelUtils.readExcelMultipart(inputStream, null);
        List<String> headerList = excelList.get(0);
        List<Bean> beanList = mcnExcelList(excelList.subList(1,excelList.size()), headerList, isQrCode);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(beanList);
        JasperPrint jp = JasperFillManager.fillReport(is, new HashMap<>(), jrBean);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        String base64PdfCode = Base64Util.encode(bytes);
        Result result = new Result();
        result.setData(base64PdfCode);
        return result;
    }

    private List<Bean> mcnExcelList(List<List<String>> excelList, List<String> headerList, Boolean isQrCode) throws Exception{
        List<Bean> beanList = new ArrayList<>();
        for (List<String> strings : excelList) {
            Bean bean = new Bean();
            bean.setFIELDVALUE1(strings.get(0));
            bean.setFIELDVALUE2(strings.get(1));
            bean.setFIELDVALUE3(strings.get(2));
            bean.setFIELDVALUE4(strings.get(3));
            bean.setFIELDVALUE6(headerList.get(0));
            bean.setFIELDVALUE7(headerList.get(1));
            bean.setFIELDVALUE8(headerList.get(2));
            bean.setFIELDVALUE9(headerList.get(3));
            String lengthStr = strings.get(4);
            Integer lengthInt = 1;
            if(lengthStr != null && !lengthStr.isEmpty()){
                lengthInt = Integer.parseInt(lengthStr);
            }
            for(int i = 0; i < lengthInt; i++){
                beanList.add(bean);
            }
        }
        return beanList;
    }



    @Override
    public Result printMcnBoxJasperMultipart(InputStream inputStream, String path, Boolean isQrCode) throws Exception{
        List<List<String>> excelList = ExcelUtils.readExcelMultipart(inputStream, null);
        List<String> headerList = excelList.get(0);
        List<McnBoxBean> beanList = mcnBoxExcelList(excelList.subList(1,excelList.size()), headerList, isQrCode);
        List<List<String>> outputList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        titleList.add("配送中心");
        titleList.add("采购单号");
        titleList.add("商品编号");
        titleList.add("商品名称");
        titleList.add("采购数量");
        titleList.add("整箱箱规");
        titleList.add("实际箱规");
        titleList.add("序号");
        titleList.add("小计");
        titleList.add("合计");
        titleList.add("总计");
        outputList.add(titleList);
        for(McnBoxBean bean : beanList){
            List<String> li = new ArrayList<>();
            li.add(bean.getCity());
            li.add(bean.getProjectNum());
            li.add(bean.getCode());
            li.add(bean.getName());
            li.add(bean.getQuantity());
            li.add(String.valueOf(bean.getBoxGauge()));
            li.add(bean.getRealGauge().toString());
            li.add(String.valueOf(bean.getIndex()));
            li.add(String.valueOf(bean.getXiaoJi()));
            li.add(String.valueOf(bean.getHeJi()));
            li.add(String.valueOf(bean.getZongJi()));
            outputList.add(li);
        }
        // 输出Excel
        ExcelUtils.writeExcel(outputList,"d:/MCN外箱签Patch"+DopamineUtils.formatDate("MM月dd日HH时mm分")+".xlsx","MCN外箱签Patch");
        // 输出PDF
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(beanList);
        JasperPrint jp = JasperFillManager.fillReport(is, new HashMap<>(), jrBean);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        String base64PdfCode = Base64Util.encode(bytes);
        Result result = new Result();
        result.setData(base64PdfCode);
        return result;
    }

    private List<McnBoxBean> mcnBoxExcelList(List<List<String>> lists, List<String> headerList, Boolean isQrCode) {
        int cityIndex = headerList.indexOf("配送中心");
        int projectNumIndex = headerList.indexOf("采购单号");
        int codeIndex = headerList.indexOf("商品编号");
        int quantityIndex = headerList.indexOf("采购数量");
        int nameIndex = headerList.indexOf("商品名称");
        int zuTaoIndex = headerList.indexOf("组套要求");
        int inputBoxGaugeIndex = headerList.indexOf("手动录入箱规");
        List<McnBoxBean> mcnBoxBeansList = new ArrayList<>();
        for(List<String> row:lists) {
            McnBoxBean mcnBoxBean = new McnBoxBean();
            mcnBoxBean.setCity(row.get(cityIndex));
            mcnBoxBean.setProjectNum(row.get(projectNumIndex));
            mcnBoxBean.setCode(row.get(codeIndex));
            mcnBoxBean.setQuantity(row.get(quantityIndex));
            mcnBoxBean.setZuTao(row.get(zuTaoIndex));

            // 箱规
            int boxGauge = 0;
            mcnBoxBean.setName(row.get(nameIndex));
            mcnBoxBean.setZuTao(row.get(zuTaoIndex));
            mcnBoxBean.setInputBoxGauge(row.get(inputBoxGaugeIndex));

            if(mcnBoxBean.getInputBoxGauge() != null && !mcnBoxBean.getInputBoxGauge().isEmpty()){
                boxGauge = Integer.parseInt(mcnBoxBean.getInputBoxGauge());
            }else if(mcnBoxBean.getZuTao().contains("套装勿拆")){
                boxGauge = 1;
            }else if(mcnBoxBean.getName().contains("蜂蜜") && mcnBoxBean.getZuTao().contains("加气柱")){
                boxGauge = 6;
            }else if(mcnBoxBean.getName().contains("口服液")){
                boxGauge = 12;
            }else{
                boxGauge = 24;
            }
            mcnBoxBean.setBoxGauge(boxGauge);
            double quantity = Double.parseDouble(mcnBoxBean.getQuantity());
            // 小计
            mcnBoxBean.setXiaoJi((int) Math.ceil(quantity / boxGauge));
            mcnBoxBeansList.add(mcnBoxBean);
        }

        Map<String, Integer> heJiMap = new HashMap<>();
        Map<String, Integer> zongJiMap = new HashMap<>();

        // 合计和总计
        for(McnBoxBean tempBean : mcnBoxBeansList){
            String heJiKey = tempBean.getCity() + tempBean.getProjectNum();
            Integer sumXiaoJi = heJiMap.get(heJiKey);
            if(sumXiaoJi == null || sumXiaoJi==0){
                heJiMap.put(heJiKey, tempBean.getXiaoJi());
            }else{
                sumXiaoJi += tempBean.getXiaoJi();
                heJiMap.put(heJiKey,sumXiaoJi);
            }

            String zongJiKey = tempBean.getCity();
            Integer sumZongJi = zongJiMap.get(zongJiKey);
            if(sumZongJi == null || sumZongJi==0){
                zongJiMap.put(zongJiKey, tempBean.getXiaoJi());
            }else{
                sumZongJi += tempBean.getXiaoJi();
                zongJiMap.put(zongJiKey,sumZongJi);
            }
        }
        // 每条记录添加合计、总计
        List<McnBoxBean> resultBeanList = new ArrayList<>();
        for(McnBoxBean addSumBean : mcnBoxBeansList){
            String zongJiKey = addSumBean.getCity();
            Integer zongJi = zongJiMap.get(zongJiKey);
            addSumBean.setZongJi(zongJi);

            String heJiKey = addSumBean.getCity() + addSumBean.getProjectNum();
            addSumBean.setHeJi(heJiMap.get(heJiKey));

            // 查验是否有同一订单号下其他品，取最大index值
            int maxIndex = 0;
            for(McnBoxBean m : resultBeanList){
                if(m.getCity() == addSumBean.getCity() && m.getProjectNum()== addSumBean.getProjectNum()){
                    Integer index = m.getIndex();
                    maxIndex = index > maxIndex ? index : maxIndex;
                }
            }

            int quantity = Integer.parseInt(addSumBean.getQuantity());
            int boxGauge = addSumBean.getBoxGauge();

            int index = 1;
            if(maxIndex>0){
                index = maxIndex+1;
            }
            // 加序号、实际箱规
            Integer xiaoJi = addSumBean.getXiaoJi();
            for(; xiaoJi != null && xiaoJi-- > 0; index++){

                addSumBean.setIndex(index);
                int realGauge = 0;
                if(quantity < boxGauge){
                    realGauge = quantity;
                }else {
                    realGauge = boxGauge;
                }
                quantity -= realGauge;
                addSumBean.setRealGauge(realGauge);

                McnBoxBean bean = new McnBoxBean();
                try {
                    BeanUtils.copyProperties(bean, addSumBean);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                resultBeanList.add(bean);
            }
        }
        return resultBeanList;
    }


    public String printMiniJasper(String defaultInputPath, String path, Boolean isQrCode)throws Exception{
        List<List<String>> excelList = ExcelUtils.readExcel(defaultInputPath, null);
        List<Bean> beanList = miniExcelList(excelList.subList(1,excelList.size()), isQrCode);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(beanList);
        JasperPrint jp = JasperFillManager.fillReport(is, new HashMap<>(), jrBean);
        String outputPath = defaultInputPath + "_" + DopamineUtils.formatDate(null) + ".pdf";
        JasperExportManager.exportReportToPdfFile(jp, outputPath);
        return "输出" + outputPath + "成功";
    }

    public Result printMiniJasperMultipart(InputStream inputStream, String path, Boolean isQrCode)throws Exception{
        List<List<String>> excelList = ExcelUtils.readExcelMultipart(inputStream, null);
        List<Bean> beanList = miniExcelList(excelList.subList(1,excelList.size()), isQrCode);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(beanList);
        JasperPrint jp = JasperFillManager.fillReport(is, new HashMap<>(), jrBean);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        String base64PdfCode = Base64Util.encode(bytes);
        Result result = new Result();
        result.setData(base64PdfCode);
        return result;
    }

    private List<Bean> miniExcelList(List<List<String>> excelList, Boolean isQrCode) throws Exception{
        List<Bean> beanList = new ArrayList<>();
        for (List<String> strings : excelList) {
            Bean bean = new Bean();
            bean.setFIELDVALUE1(strings.get(1 - 1));
            String proId = strings.get(2 - 1);
            if(isQrCode != null) {
                InputStream code1D2D = BarCodeUtils.getBarcodeOrQrCode(isQrCode, proId);
                String imageBase64 = BarCodeUtils.getImageBase64ToString(code1D2D);
                bean.setFIELDVALUE2(imageBase64);
            }
            beanList.add(bean);
        }
        return beanList;
    }

    @Override
    public Result jianHuoMultipart(InputStream inputStream, String path, Boolean isQrCode) throws Exception {
        List<List<String>> excelList = ExcelUtils.readExcelMultipart(inputStream, null);
//        List<JianHuoBean> beanList = getJianHuoBean2(excelList, isQrCode);
        List<JianHuoBean> beanList = getJianHuoBeanByFenpeimingxi(excelList, isQrCode);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(beanList);
        JasperPrint jp = JasperFillManager.fillReport(is, new HashMap<>(), jrBean);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        String base64PdfCode = Base64Util.encode(bytes);
        Result result = new Result();
        result.setData(base64PdfCode);
        return result;
    }

    /**
     * 返回拣货签的Bean
     */
    public List<JianHuoBean> getJianHuoBean2 (List<List<String>> excelList, Boolean isQrCode) throws Exception{
        List<JianHuoBean> beanList = new ArrayList<>();
        for (List<String> strings : excelList.subList(1,excelList.size())) {
            JianHuoBean bean = new JianHuoBean();
            bean.setCOLUMNNAME11(strings.get(1 - 1));
            bean.setCOLUMNNAME45(strings.get(2 - 1));
            bean.setCOLUMNNAME34(strings.get(3 - 1) + "-" +strings.get(4 - 1));
            bean.setCOLUMNNAME33(strings.get(3 - 1));
            bean.setCOLUMNNAME1(strings.get(5 - 1));
            bean.setCOLUMNNAME7(strings.get(6 - 1));
            bean.setCOLUMNNAME36(strings.get(10 - 1));
            bean.setCOLUMNNAME22(strings.get(11 - 1));
            bean.setCOLUMNNAME10(strings.get(7 - 1));
            bean.setCOLUMNNAME15(strings.get(8 - 1));
            bean.setCOLUMNNAME20(strings.get(13 - 1));

            if(isQrCode != null) {
                String codeId = strings.get(1-1);
                InputStream code1D2D = BarCodeUtils.getBarcodeOrQrCode(isQrCode, codeId);
                String imageBase64 = BarCodeUtils.getImageBase64ToString(code1D2D);
                bean.setCOLUMNNAME50(imageBase64);
            }
            beanList.add(bean);
        }
        return beanList;
    }


    /**
     * 返回拣货签的Bean
     */
    public List<JianHuoBean> getJianHuoBeanByFenpeimingxi (List<List<String>> excelList, Boolean isQrCode) throws Exception{
        List<JianHuoBean> beanList = new ArrayList<>();

        List<String> headerList = excelList.get(0);
        List<Integer> headerIndexList = new ArrayList<>();
        String headerTag = "目标跟踪号,波次号,门店号,门店号,WMS单号,项目号,名称,规格,库位,最小数量,批号";
        String[] split = headerTag.split(",");
        for (String s : split) {
            int index = headerList.indexOf(s);
            headerIndexList.add(index);
        }


        for (List<String> strings : excelList.subList(1,excelList.size())) {
            String location = strings.get(headerIndexList.get(8));
            if(location.startsWith("0")){
                continue;
            }
            JianHuoBean bean = new JianHuoBean();
            bean.setCOLUMNNAME11(strings.get(headerIndexList.get(0)));
            bean.setCOLUMNNAME45(strings.get(headerIndexList.get(1)));
            bean.setCOLUMNNAME34(strings.get(headerIndexList.get(2)));
            bean.setCOLUMNNAME33(strings.get(headerIndexList.get(3)));
            bean.setCOLUMNNAME1(strings.get(headerIndexList.get(4)));
            bean.setCOLUMNNAME7(strings.get(headerIndexList.get(5)));
            bean.setCOLUMNNAME36(strings.get(headerIndexList.get(6)));
            bean.setCOLUMNNAME22(strings.get(headerIndexList.get(7)));
            bean.setCOLUMNNAME10(strings.get(headerIndexList.get(8)));
            bean.setCOLUMNNAME15(strings.get(headerIndexList.get(9)));
            bean.setCOLUMNNAME20(strings.get(headerIndexList.get(10)));

            if(isQrCode != null) {
                String codeId = strings.get(headerIndexList.get(0));
                InputStream code1D2D = BarCodeUtils.getBarcodeOrQrCode(isQrCode, codeId);
                String imageBase64 = BarCodeUtils.getImageBase64ToString(code1D2D);
                bean.setCOLUMNNAME50(imageBase64);
            }
            beanList.add(bean);
        }
        return beanList;
    }
}
