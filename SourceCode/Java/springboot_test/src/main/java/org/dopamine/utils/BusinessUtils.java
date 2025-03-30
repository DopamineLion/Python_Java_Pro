package org.dopamine.utils;

import org.dopamine.bean.Business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BusinessUtils {
    /**
     * 保留有需要的列
     * @param lists
     * @param business
     * @return
     */
    public static List<List<String>> reserveColumnsList(List<List<String>> lists, Business business) {
        List<List<String>> resultList = new ArrayList<>();
        for(int i=0; i<lists.size(); i++){
            List<String> rowList = new ArrayList<>();
            for(Integer index : business.getGroupIndexList()) {
                rowList.add(lists.get(i).get(index));
            }
            rowList.add(lists.get(i).get(business.getLengthIndex()));
            resultList.add(rowList);
        }
        return resultList;
    }


    /**
     * 分组求和
     * @param lists
     * @param business
     * @return
     */
    public static List<List<String>> groupSumListByStream(List<List<String>> lists, Business business) {
        List<List<String>> resultList = new ArrayList<>();
        List<List<String>> delHeaderList = lists.subList(1, lists.size());
        resultList.add(lists.get(0));
        Map<String, List<List<String>>> collect = delHeaderList.stream().collect(Collectors.groupingBy(new Function<List<String>, String>() {
            @Override
            public String apply(List<String> rows) {
                StringBuilder str = new StringBuilder();
                for (Integer index : business.getGroupIndexList()){
                    str.append(rows.get(index));
                }
                return str.toString();
            }
        }));
        collect.forEach((str, list) -> {
            Integer lengthIndex = business.getLengthIndex();
            Double d = Double.parseDouble("0");
            for (List<String> row : list) {
                String num = row.get(lengthIndex);
                d += Double.parseDouble(num);
            }
            if (!d.equals(0.0)) {
                List<String> sumList = list.get(0);
                if(d%1==0) {
                    int d_i = (int) Math.floor(d);
                    sumList.set(lengthIndex, d_i+"");
                }else
                    sumList.set(lengthIndex, String.valueOf(d));
                resultList.add(sumList);
            }
        });
        return resultList;
    }

    /**
     * 筛选组套
     * @param lists
     * @param business
     * @return
     */
    public static List<List<String>> filterList(List<List<String>> lists, Business business) {
        List<List<String>> resultList = new ArrayList<>();
        resultList.add(lists.get(0));
        int index = lists.get(0).indexOf(business.getFilter());
        for(List<String> row : lists.subList(1, lists.size())){
            String value = row.get(index);
            if(value != null && DopamineUtils.search(business.getRegex(), value.trim())){
                resultList.add(row);
            }
        }
        return resultList;
    }

    /**
     * 获取输出的Excel路径
     * @param excelPath 读取的Excel路径
     * @return
     */
    public static String getOutputPath(String excelPath){
        StringBuffer sb = new StringBuffer();
        sb.append(excelPath.substring(0, excelPath.lastIndexOf(".")));
        sb.append("###");
        sb.append("[");
        sb.append(DopamineUtils.formatDate(null));
        sb.append("]");
        sb.append(".xlsx");
        return sb.toString();
    }

    /**
     * 获取字符串在标题的位置
     * @param lists  输入List标题行
     * @param business 对象中包含要获取index的字符串
     */
    public static void indexList(List<String> lists, Business business) {
        if (lists != null && !lists.isEmpty()) {
            int length = lists.indexOf(business.getLength());
            business.setLengthIndex(length);

            int primaryKey = lists.indexOf(business.getPrimaryKey());
            business.setPrimaryKeyIndex(primaryKey);

            int filter = lists.indexOf(business.getFilter());
            business.setFilterIndex(filter);

            List<Integer> otherFieldsIndex = new ArrayList<>();


            for(String otherFields : business.getOtherFields()){
                int index = lists.indexOf(otherFields);
                otherFieldsIndex.add(index);
            }

            List<Integer> groupIndexList = new ArrayList<>();
            groupIndexList.add(primaryKey);
            groupIndexList.add(filter);
            groupIndexList.addAll(otherFieldsIndex);

            business.setGroupIndexList(groupIndexList);
        }
    }

    public static List<List<String>> lianbiao(List<List<String>> groupList, List<List<String>> rightGroupList, Business business, Business rightBusiness) {
        List<List<String>> resultList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        titleList.addAll(groupList.get(0));
        titleList.addAll(rightGroupList.get(0));
        resultList.add(titleList);
        int index = groupList.get(0).indexOf(business.getPrimaryKey());
        for (List<String> list : groupList.subList(1, groupList.size())) {
            String key = list.get(index);
            int notRight = 0;
            int rightIndex = rightGroupList.get(0).indexOf(rightBusiness.getPrimaryKey());
            for (List<String> rightList : rightGroupList.subList(1, rightGroupList.size())){
                String rightKey = rightList.get(rightIndex);
                if (key.equals(rightKey)){
                    List<String> elementList = new ArrayList<>();
                    elementList.addAll(list);
                    elementList.addAll(rightList);
                    resultList.add(elementList);
                    notRight++;
                }
            }
            if(notRight == 0){
                List<String> elementList = new ArrayList<>();
                elementList.addAll(list);
                resultList.add(elementList);
            }
        }
        return resultList;
    }
}
