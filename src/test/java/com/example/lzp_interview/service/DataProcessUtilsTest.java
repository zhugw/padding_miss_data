package com.example.lzp_interview.service;

import com.example.lzp_interview.domain.FooDO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by zhuguowei on 2021/5/23.
 */
class DataProcessUtilsTest {

    @Test
    void paddingMissData_padding_left() {
        /**
         * 测试填充左边列表
         * A B
         * B C A
         */
        final List<FooDO> list1 = buildList("2021-01", "A", "B");
        final List<FooDO> list2 = buildList("2021-02", "B", "C", "A");

        DataProcessUtils.paddingMissData(list1, list2);

        assertEquals(Arrays.asList("A","B","C"), list1.stream().map(o->o.getPlatform()).collect(Collectors.toList()));
        assertEquals(Arrays.asList("A","B","C"), list2.stream().map(o->o.getPlatform()).collect(Collectors.toList()));

        assertEquals("2021-01", list1.get(2).getDate());
        assertNull(list1.get(2).getValue());

        assertNotNull(list2.get(2).getValue());
    }


    @Test
    void paddingMissData_padding_right() {
        /**
         * 测试填充右边列表
         * B A C
         * B C
         */
        final List<FooDO> list1 = buildList("2021-01", "B", "A","C");
        final List<FooDO> list2 = buildList("2021-02", "B", "C");

        DataProcessUtils.paddingMissData(list1, list2);

        assertEquals(Arrays.asList("B","A","C"), list1.stream().map(o->o.getPlatform()).collect(Collectors.toList()));
        assertEquals(Arrays.asList("B","A","C"), list2.stream().map(o->o.getPlatform()).collect(Collectors.toList()));

        assertEquals("2021-02", list2.get(1).getDate());
        assertNull(list2.get(1).getValue());

        assertNotNull(list1.get(1).getValue());
    }
    @Test
    void paddingMissData_padding_both() {
        /**
         * 测试两边都有填充
         * A B C
         * B C D
         */
        final List<FooDO> list1 = buildList("2021-01", "A", "B","C");
        final List<FooDO> list2 = buildList("2021-02", "B", "C","D");

        DataProcessUtils.paddingMissData(list1, list2);

        assertEquals(Arrays.asList("A","B","C","D"), list1.stream().map(o->o.getPlatform()).collect(Collectors.toList()));
        assertEquals(Arrays.asList("A","B","C","D"), list2.stream().map(o->o.getPlatform()).collect(Collectors.toList()));

        assertEquals("2021-01", list1.get(3).getDate());
        assertNull(list1.get(3).getValue());
        assertNotNull(list2.get(3).getValue());

        assertEquals("2021-02", list2.get(0).getDate());
        assertNull(list2.get(0).getValue());
        assertNotNull(list1.get(0).getValue());


    }
    @Test
    public void commonPaddingMissData_padding_left() {
        final List<FooDO> list1 = buildList("2021-01", "A", "B");
        final List<FooDO> list2 = buildList("2021-02", "B", "C", "A");

        DataProcessUtils.commonPaddingMissData(list1, list2);

        assertEquals(Arrays.asList("A","B","C"), list1.stream().map(o->o.getPlatform()).collect(Collectors.toList()));
        assertEquals(Arrays.asList("A","B","C"), list2.stream().map(o->o.getPlatform()).collect(Collectors.toList()));

        assertEquals("2021-01", list1.get(2).getDate());
        assertNull(list1.get(2).getValue());

        assertNotNull(list2.get(2).getValue());
    }
    @Test
    public void commonPaddingMissData_padding_right() {

    }
    @Test
    public void commonPaddingMissData_padding_both() {

    }

    private List<FooDO> buildList(String date, String... platforms) {
        List<FooDO> list = new ArrayList<>(platforms.length);
        for (int i = 0; i < platforms.length; i++) {
            list.add(FooDO.build(date, platforms[i], i));
        }
        return list;
    }




}