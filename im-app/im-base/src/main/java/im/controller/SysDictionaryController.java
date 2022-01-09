package im.controller;

import im.model.SysDictionaryModel;
import im.model.SysDictionaryQueryModel;
import im.model.base.PageModel;
import im.model.base.ResultData;
import im.service.SysDictionaryService;
import im.util.SysLogUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static im.consts.BaseCueConst.*;

/**
 * @author hoomoomoo
 * @description 字典信息控制类
 * @package im.controller
 * @date 2019/09/13
 */

@Controller
@RequestMapping("/dictionary")
public class SysDictionaryController {

    private static final Logger logger = LoggerFactory.getLogger(SysDictionaryController.class);

    @Autowired
    private SysDictionaryService sysDictionaryService;

    /**
     * 跳转列表页面
     *
     * @return
     */
    @ApiOperation("跳转列表页面")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list() {
        return "page/dictionaryList";
    }

    /**
     * 跳转详情页面
     *
     * @return
     */
    @ApiOperation("跳转详情页面")
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail() {
        return "page/dictionaryDetail";
    }

    /**
     * 跳转修改页面
     *
     * @return
     */
    @ApiOperation("跳转修改页面")
    @RequestMapping(value = "update", method = RequestMethod.GET)
    public String update() {
        return "page/dictionaryUpdate";
    }

    /**
     * 分页查询字典信息
     *
     * @param sysDictionaryQueryModel
     * @return
     */
    @ApiOperation("分页查询字典信息")
    @RequestMapping(value = "selectPage", method = RequestMethod.GET)
    @ResponseBody
    public PageModel<SysDictionaryModel> selectPage(SysDictionaryQueryModel sysDictionaryQueryModel) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_SELECT_PAGE);
        PageModel<SysDictionaryModel> page = sysDictionaryService.selectPage(sysDictionaryQueryModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_SELECT_PAGE);
        return page;
    }

    /**
     * 查询字典信息
     *
     * @param dictionaryCode
     * @return
     */
    @ApiOperation("查询字典信息")
    @RequestMapping(value = "selectOne", method = RequestMethod.GET)
    @ResponseBody
    public ResultData selectOne(
            @ApiParam(value = "字典代码", required = true)
            @RequestParam String dictionaryCode,
            @ApiParam(value = "是否翻译", required = true)
            @RequestParam Boolean isTranslate) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_SELECT);
        ResultData resultData = sysDictionaryService.selectOne(dictionaryCode, isTranslate);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }

    /**
     * 保存字典信息
     *
     * @param sysDictionaryModelList
     * @return
     */
    @ApiOperation("保存字典信息")
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResultData save(@RequestBody List<SysDictionaryModel> sysDictionaryModelList) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_UPDATE);
        ResultData resultData = sysDictionaryService.save(sysDictionaryModelList);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_UPDATE);
        return resultData;
    }

    /**
     * 刷新字典信息
     *
     * @return
     */
    @ApiOperation("刷新字典信息")
    @RequestMapping(value = "refresh", method = RequestMethod.GET)
    @ResponseBody
    public ResultData refresh() {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_REFRESH);
        ResultData resultData = sysDictionaryService.refresh();
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_REFRESH);
        return resultData;
    }

}
