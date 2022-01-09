package im.config;

import im.consts.BaseConst;
import im.consts.BaseCueConst;
import im.model.base.ResultData;
import im.util.SysLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static im.consts.BaseConst.*;
import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;

/**
 * @author hoomoomoo
 * @description 全局异常处理
 * @package im.config
 * @date 2019/10/19
 */

@Controller
@ApiIgnore
public class ExceptionHandlerConfig implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerConfig.class);

    @PostConstruct
    public void init(){
        SysLogUtils.load(logger, BaseCueConst.LOG_BUSINESS_TYPE_EXCEPTION);
    }

    @Override
    public String getErrorPath() {
        return BaseConst.ERROR_PATH;
    }

    @RequestMapping(value = BaseConst.ERROR_PATH, produces = {TEXT_HTML})
    public String viewHandler(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        Integer statusCode = (Integer) request.getAttribute(ERROR_STATUS_CODE);
        String errorCode = PAGE_ERROR;
        modelMap.addAttribute(MESSAGE, BaseCueConst.ERROR);
        if (BaseConst.STR_404.equals(statusCode)) {
            errorCode = BaseConst.PAGE_ERROR_404;
        }
        logger.error(request.getRequestURL().toString());
        return errorCode;
    }

    @RequestMapping(value = BaseConst.ERROR_PATH)
    @ResponseBody
    @ExceptionHandler(value = {Exception.class})
    public ResultData errorHandler(HttpServletRequest request, HttpServletResponse response) {
        logger.error(request.getRequestURL().toString());
        return new ResultData(BaseConst.STATUS_FAIL, BaseCueConst.ERROR);
    }
}
