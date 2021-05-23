package com.example.lzp_interview.service;

import com.example.lzp_interview.domain.FooDO;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数据处理工具类
 * Created by zhuguowei on 2021/5/23.
 */
public class DataProcessUtils {
    /**
     * 补充两个平台数组缺失数据并保证平台数据顺序一致
     * @param list1
     * @param list2
     */
    public static void paddingMissData(List<FooDO> list1, List<FooDO> list2) {
        //所有平台
        List<String> all_platform = Stream.of(list1, list2).flatMap(o -> o.stream()).map(o -> o.getPlatform()).distinct().collect(Collectors.toList());
        //数组平台数据映射表
        Map<String, Integer> list1_map = list1.stream().collect(Collectors.toMap(o -> o.getPlatform(), o -> o.getValue()));
        Map<String, Integer> list2_map = list2.stream().collect(Collectors.toMap(o -> o.getPlatform(), o -> o.getValue()));
        //数组数据日期，每组数据的日期是一样的
        final String list1_date = list1.get(0).getDate();
        final String list2_date = list2.get(0).getDate();

        //重新组装两个数据数组，保证两个数组平台顺序一致并补充缺失平台的数据为null
        list1.clear();
        list2.clear();

        for (String platform : all_platform) {
            list1.add(FooDO.build(list1_date, platform, list1_map.get(platform)));

            list2.add(FooDO.build(list2_date, platform, list2_map.get(platform)));
        }
    }

    /**
     * [抽象]补充任意多个数据数组缺失数据并保证数据顺序一致
     * 数据实体类不确定，抽象为T，但作为图表数据都应有date和value这两个属性，只是中间的标识属性名不同，比如为platform或brand
     * 这里约定T数据实体类的属性顺序为日期、标识、数值，例如：
     * BarDo
     *  - date
     *  - brand
     *  - value
     *
     * @param lists
     * @param <T>
     */
    public static <T> void commonPaddingMissData(List<T>... lists) {
        //所有标识
        List<String> all_identity = new ArrayList<>();
        //数据映射表数组
        List<HashMap<String, Integer>> list_map_arr = new ArrayList<>();
        //日期数组
        List<String> date_arr = new ArrayList<>();
        //通过泛型获取数据实体类的第二个属性名即标识属性名并抓取整理数据
        try {
            T t = lists[0].get(0);
            Class<? extends Object> tClass = t.getClass();
            Field[] field = tClass.getDeclaredFields();
            field[0].setAccessible(true);
            field[1].setAccessible(true);
            field[2].setAccessible(true);
            String identity_name = field[1].getName();
            //将此标识属性名的首字母大写
            identity_name = identity_name.replaceFirst(identity_name.substring(0, 1), identity_name.substring(0, 1).toUpperCase());
            //拼出get属性的方法，例如getBrand
            Method getm = tClass.getMethod("get" + identity_name);
            Method getdatem = tClass.getMethod("getDate");
            Method getvaluem = tClass.getMethod("getValue");
            //拼出set属性的方法，例如setBrand
            Method setm = tClass.getMethod("set" + identity_name);
            Method setdatem = tClass.getMethod("setDate");
            Method setvaluem = tClass.getMethod("setValue");
            //获取数据
            for (int i=0; i<lists.length;i++) {
                List<T> list = lists[i];
                HashMap<String, Integer> list_map = new HashMap<>();
                for (T list_t : list) {
                    String identity = (String)getm.invoke(list_t);
                    if (!all_identity.contains(identity)) {
                        all_identity.add(identity);
                    }
                    Integer value = (Integer)getvaluem.invoke(list_t);
                    list_map.put(identity, value);
                    String date = (String)getdatem.invoke(list_t);
                    date_arr.add(i, date);
                }
                list_map_arr.add(i, list_map);
                //清空每个数据数组
                list.clear();
            }
            //重新组装每个数据数组，保证每个数组数据顺序一致并补充缺失的数据为null
            ParameterizedType pt = (ParameterizedType) tClass.getGenericSuperclass();
            Class<T> clazz = (Class<T>) pt.getActualTypeArguments()[0];
            for (String identity : all_identity) {
                for (int i=0;i<lists.length;i++) {
                    List<T> list = lists[i];
                    HashMap<String, Integer> list_map = list_map_arr.get(i);
                    String date = date_arr.get(i);
                    T new_t = clazz.newInstance();
                    setdatem.invoke(new_t, date);
                    setm.invoke(new_t, identity);
                    Integer value = list_map.containsKey(identity_name) ? list_map.get(identity) : null;
                    setvaluem.invoke(new_t, value);
                    list.add(new_t);
                }
            }
        } catch (Exception e) {
            System.out.println("未能正确解析泛型类的属性");
            return;
        }
    }
}
