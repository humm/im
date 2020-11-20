package com.hoomoomoo.im.model;

import lombok.Data;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.model
 * @date 2020/11/20
 */

@Data
public class Box<T> {

    private String id;

    private String name;

    private T data;
}
