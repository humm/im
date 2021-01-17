package com.hoomoomoo.im.model;

import lombok.Data;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.model
 * @date 2020/12/17
 */

@Data
public class SelectItem {

    private String code;

    private String describe;

    public SelectItem(String code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    @Override
    public String toString() {
        return this.getDescribe();
    }
}
