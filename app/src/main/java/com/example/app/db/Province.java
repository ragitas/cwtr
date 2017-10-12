package com.example.app.db;

import org.litepal.crud.DataSupport;

/**
 * Created by roach on 2017/10/12.
 */

public class Province extends DataSupport{
    private int id;
    private String provinceName;
    private int provinceCode;
    private int getId;

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setGetId(int getId) {
        this.getId = getId;
    }

    public int getId() {
        return id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public int getGetId() {
        return getId;
    }
}
