package com.hoomoomoo.im.consts;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.consts
 * @date 2021/04/25
 */
public enum TabType {

    SVN_LOG("1", "svn提交文件记录", "/conf/fxml/svnLog.fxml");

    private String type;

    private String name;

    private String path;

    TabType(String type, String name, String path) {
        this.type = type;
        this.name = name;
        this.path = path;
    }

    public static String getName(String type) {
        for (TabType tabType : TabType.values()) {
            if (tabType.getType().equals(type)) {
                return tabType.name;
            }
        }
        return null;
    }

    public static String getPath(String type) {
        for (TabType tabType : TabType.values()) {
            if (tabType.getType().equals(type)) {
                return tabType.path;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

}
