package com.age.steward.car.bean;

import java.io.Serializable;
import java.util.List;

public class MenuBean extends ResultBean implements Serializable {
    private String id;//
    private String fatherId;//
    private String name;//
    private String url;//
    private String orderId;//
    private String selected;//
    private List<MenuBean> childMenuList;//
    public MenuBean(){

    }
    public MenuBean(String name){
        this.name=name;
    }
    public MenuBean(String id,String name,String fatherId,String url){
        this.id=id;
        this.name=name;
        this.fatherId=fatherId;
        this.url=url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public List<MenuBean> getChildMenuList() {
        return childMenuList;
    }

    public void setChildMenuList(List<MenuBean> childMenuList) {
        this.childMenuList = childMenuList;
    }
}
