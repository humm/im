package com.hoomoomoo.im.dto;

import java.util.ArrayList;
import java.util.List;

public class MenuTreeDto {

    private String menuCode;

    private String menuName;

    private String parentMenuCode;

    private List<MenuTreeDto> subMenuList = new ArrayList<>();

    public MenuTreeDto(String menuCode, String menuName) {
        this.menuCode = menuCode;
        this.menuName = menuName;
    }

    public MenuTreeDto(String menuCode, String menuName, String parentMenuCode) {
        this.menuCode = menuCode;
        this.menuName = menuName;
        this.parentMenuCode = parentMenuCode;
    }

    public String getParentMenuCode() {
        return parentMenuCode;
    }

    public void setParentMenuCode(String parentMenuCode) {
        this.parentMenuCode = parentMenuCode;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public List<MenuTreeDto> getSubMenuList() {
        return subMenuList;
    }

    public void setSubMenuList(List<MenuTreeDto> subMenuList) {
        this.subMenuList = subMenuList;
    }
}
