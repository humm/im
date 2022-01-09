package im.model.base;

import im.consts.BaseConst;
import im.consts.BaseCueConst;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * @author hoomoomoo
 * @description 分页信息
 * @package im.model.common
 * @date 2019/08/10
 */
@Data
public class PageModel<T> {

    @ApiModelProperty(value="状态码", required = false)
    private String code;

    @ApiModelProperty(value="提示信息", required = false)
    private String msg;

    @ApiModelProperty(value="当前页码", required = true)
    private Integer page;

    @ApiModelProperty(value="每页显示记录数", required = true)
    private Integer limit;

    @ApiModelProperty(value="总记录数", required = false)
    private Long count;

    @ApiModelProperty(value="分页数据", required = false)
    private List<T> data;

    public PageModel() {
    }

    public PageModel(Integer page, Integer limit) {
        this.page = page;
        this.limit = limit;
    }

    public PageModel(Long count, List<T> data) {
        this.code = BaseConst.PAGE_CODE_SUCCESS;
        this.msg = BaseCueConst.PAGE_MSG_SUCCESS;
        this.count = count;
        this.data = data;
    }

    public PageModel(String code, String msg, Long count, List<T> data) {
        this.code = code;
        this.msg = msg;
        this.count = count;
        this.data = data;
    }

}
