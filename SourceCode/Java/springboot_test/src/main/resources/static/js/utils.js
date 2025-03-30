/**
 * @description: base64转换为字节码
 * @param {base64} base64串
 * @return: 字节码
 */
function base64ToArrayBuffer(base64) {
    var binaryString = window.atob(base64);
    var binaryLen = binaryString.length;
    var bytes = new Uint8Array(binaryLen);
    for (var i = 0; i < binaryLen; i++) {
        var ascii = binaryString.charCodeAt(i);
        bytes[i] = ascii;
    }
    return bytes;
}
/**
 * @description: 使用流自定义下载
 * @param {reportName} 文件名
 * @param {byte} 文件字节
 */
function saveByteArray(reportName, byte) {
    const link = document.createElement("a"); // 创建a标签
    const blob = new Blob([byte], {
        type: "application/vnd.ms-excel;charset=utf-8",
    }); // response就是接口返回的文件流
    const objectUrl = URL.createObjectURL(blob);
    link.href = objectUrl;
    link.download = reportName;
    link.click(); // 下载文件
    URL.revokeObjectURL(objectUrl);
}

function resultManage(res){
    if(res!=null && res.data.code === 20011){
        let tableName = res.data.msg;
        const base64 = res.data.data
        saveByteArray(tableName,
            base64ToArrayBuffer(base64)
        )
    }else{
        alert("查询失败！原因："+res.data.msg)
    }
}
