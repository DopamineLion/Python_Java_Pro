Jasper打签项目

   注意事项

1.   simhei.jar是黑体的字体包。注意：官方教程讲的不是很清楚，simhei.jar是按官网Github-API自己生成的文件。pyJasperReport输出中文PDF会不显示，jar包需要放置到环境下Libraries/lib/site-packages/pyreportjasper/libs文件夹下。
2.   pyinstaller输出会报缺失pyjasperreport，需要虫Libraries中拷贝pyjasperreport文件夹到：pyinstaller --onedir xxx输出的文件夹中