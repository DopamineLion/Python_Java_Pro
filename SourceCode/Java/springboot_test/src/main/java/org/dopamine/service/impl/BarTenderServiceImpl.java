package org.dopamine.service.impl;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.LibraryLoader;
import com.jacob.com.Variant;
import org.dopamine.service.BarTenderService;
import org.springframework.stereotype.Service;

@Service
public class BarTenderServiceImpl implements BarTenderService {
    /**
     *设置 DLL 文件路径（可以设置项目文件路径）
     * 自己开发时可以把dll放在C:\WINDOWS\System32路径下
     * 但是项目打包部署服务器也要同样放在路径下
     * 所以更推荐放在项目路径下，这样可以直接打包项目使用。
     * 根据jdk位数看看用什么位数的x64.dll还是x86
     * @param dllFile dll文件路径
     * @param printerName 打印机名字
     * @param templatePath 模板路径
     * @param printLength 打印数量,如果是读取Excel数据源，就传null
     */
    @Override
    public void printBarTender(String dllFile, String printerName, String templatePath, Integer printLength) {

        // 加载 Jacob DLL 文件
        System.setProperty(LibraryLoader.JACOB_DLL_PATH, dllFile);
        LibraryLoader.loadJacobLibrary();

        try {
            // 初始化 BarTender 应用程序
            ActiveXComponent btApp = new ActiveXComponent("BarTender.Application");
            btApp.setProperty("Visible", new Variant(false));

            // 打开指定的 BTW 文件
            Dispatch btFormats = btApp.getProperty("Formats").toDispatch();
            Dispatch btFormat = Dispatch.call(btFormats, "Open", templatePath, new Variant(false), new Variant("")).toDispatch();

            // 设置打印机名称
            Dispatch printSetup = Dispatch.get(btFormat, "PrintSetup").toDispatch();
            Dispatch.put(printSetup, "Printer", printerName);
            // 设置打印张数
            if (printLength!=null)
                Dispatch.put(btFormat, "IdenticalCopiesOfLabel", printLength);

            // 弹出打印提示框
            // Dispatch.call(btFormat, "PrintOut", "True", "True");

            // 设置标签字段值
//            Dispatch.call(btFormat, "SetNamedSubStringValue", "打签图层", "MCN");
//            Dispatch.call(btFormat, "SetNamedSubStringValue", "需求", "标准签");
//            Dispatch.call(btFormat, "SetPrompts", "标准签-商品名", "Test商品222222222");
//            Dispatch.call(btFormat, "SetPrompts", "标准签-商品编码", "NCD007");
//            Dispatch.call(btFormat, "SetPrompts", "标准签-生产日期", "2024-13-31");
//            Dispatch.call(btFormat, "SetPrompts", "标准签-失效日期", "2024-13-31");

            // 打印标签
            Dispatch.call(btFormat, "PrintOut", new Variant(true), new Variant(false));
            // 保存赋值数据
            Dispatch.call(btFormat, "Save");
            // 关闭 BTW 文件
            Dispatch.call(btFormat, "Close", new Variant(false));
            System.out.println("输出打印机已成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
