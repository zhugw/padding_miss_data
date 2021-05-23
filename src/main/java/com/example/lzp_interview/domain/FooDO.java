package com.example.lzp_interview.domain;

import lombok.Data;

/**
 * Created by zhuguowei on 2021/5/23.
 */
@Data
public class FooDO {
    private String date;
    private String platform;
    private Integer value;

    public static FooDO build(String date, String platform, Integer value) {
        final FooDO fooDO = new FooDO();
        fooDO.setDate(date);
        fooDO.setPlatform(platform);
        fooDO.setValue(value);
        return fooDO;
    }
}
