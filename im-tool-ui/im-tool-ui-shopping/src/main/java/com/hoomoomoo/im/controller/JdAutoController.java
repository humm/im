package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GoodsDto;
import com.hoomoomoo.im.dto.ShoppingDto;
import com.hoomoomoo.im.util.ShoppingCommonUtil;
import com.hoomoomoo.im.utils.ComponentUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2022/1/8
 */
public class JdAutoController extends ShoppingBaseController implements Initializable {

    @FXML
    private Label type;

    private List<ShoppingDto> shoppingDtoList;

    private int waitHandlerNum;

    private int handlerNum;

    private Map<String, String> handle = new HashMap<>(16);

    private boolean isContinue;

    @FXML
    public TableView waitHandler;


    @FXML
    void showGoods(MouseEvent event) {
        if (execute.isDisable()) {
            return;
        }
        OutputUtils.clearLog(log);
        String type = ((ShoppingDto)waitHandler.getSelectionModel().getSelectedItem()).getType();
        outer: for (ShoppingDto shoppingDto : shoppingDtoList) {
            if (type.equals(shoppingDto.getType())) {
                List<GoodsDto> goodsDtoList = shoppingDto.getGoodsDtoList();
                for (GoodsDto goodsDto : goodsDtoList) {
                    OutputUtils.info(log, goodsDto);
                }
                break outer;
            }
        }
    }

    @FXML
    void execute(ActionEvent event) {
        handlerNum = 0;
        new Thread(() -> {
            try {
                OutputUtils.clearLog(log);
                LoggerUtils.info(String.format(BaseConst.MSG_USE, JD_AUTO.getName()));
                if (!ShoppingCommonUtil.checkConfig(log, JD_AUTO.getCode())) {
                    return;
                }
                setProgress(0);
                this.doExecute();
            } catch (Exception e) {
                LoggerUtils.info(e);
                ShoppingCommonUtil.info(log, e.getMessage());
            }
        }).start();
    }

    @FXML
    void query(ActionEvent event) {
        new Thread(() -> {
            try {
                setProgress(0);
                updateProgress(0.01);
                ComponentUtils.setButtonDisabled(execute, query, pause);
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                OutputUtils.info(type, SYMBOL_EMPTY);
                if (ShoppingCommonUtil.initJdUser(appConfigDto, log, userName, null)) {
                    queryData(appConfigDto, true);
                }
                setProgress(1);
            } catch (Exception e) {
                LoggerUtils.info(e);
                ShoppingCommonUtil.info(log, e.getMessage());
            } finally {
                ComponentUtils.setButtonEnabled(execute, query, pause);
            }
        }).start();
    }

    @FXML
    void pause(ActionEvent event) {
        super.executePause(JdAutoController.class, JD_AUTO);
    }

    public void queryData(AppConfigDto appConfigDto, boolean init) {
        shoppingDtoList.clear();
        OutputUtils.clearLog(waitHandler);
        if (init) {
            OutputUtils.clearLog(log);
        }
        ShoppingDto waitAppraise =  WaitAppraiseController.queryData(appConfigDto, true, null, null, null);
        ShoppingDto showOrder =  ShowOrderController.queryData(appConfigDto, true, null, null, null);
        ShoppingDto appendAppraise =  AppendAppraiseController.queryData(appConfigDto, true, null, null, null);
        ShoppingDto serviceAppraise =  ServiceAppraiseController.queryData(appConfigDto, true, null, null, null);
        shoppingDtoList.add(showOrder);
        shoppingDtoList.add(appendAppraise);
        shoppingDtoList.add(serviceAppraise);
        shoppingDtoList.add(waitAppraise);
        OutputUtils.infoList(waitHandler, shoppingDtoList);
        waitHandlerNum = waitAppraise.getOrderNumValue() + showOrder.getOrderNumValue() + appendAppraise.getOrderNumValue() + serviceAppraise.getOrderNumValue();
    }

    protected void doExecute() {
        AppConfigDto appConfigDto = null;
        try {
            pauseStatus = false;
            handle.clear();
            appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            appConfigDto.setExecuteType(JD_AUTO.getCode());
            if(!ShoppingCommonUtil.initJdUser(appConfigDto, log, userName, null)) {
                return;
            }
            ComponentUtils.setButtonDisabled(execute, query);
            List<String> logs = new ArrayList<>();
            Date currentDate = new Date();

            for (int i=0; i<4; i++) {
                isContinue = true;
                if (pauseStatus) {
                    break;
                }
                while (true) {
                    ShoppingDto shoppingDto = shoppingDtoList.get(i);
                    OutputUtils.info(type, shoppingDto.getTypeName());
                    FunctionConfig functionConfig = FunctionConfig.getFunctionConfig(shoppingDto.getType());
                    List<GoodsDto> goodsDtoList = shoppingDto.getGoodsDtoList();
                    if (pauseStatus || CollectionUtils.isEmpty(goodsDtoList)) {
                        break;
                    }
                    appraise(appConfigDto, shoppingDto, functionConfig, logs);
                    LoggerUtils.writeLogInfo(functionConfig.getCode(), currentDate, logs);
                    if (!isContinue) {
                        break;
                    }
                }
            }
            if (pauseStatus) {
                ShoppingCommonUtil.info(log, NAME_PAUSE_COMPLETE);
                ComponentUtils.setButtonEnabled(pause);
                return;
            }
            ShoppingCommonUtil.appraiseComplete(appConfigDto, log);
            LoggerUtils.writeLogInfo(JD_AUTO.getCode(), currentDate, logs);
            setProgress(1);
        } catch (Exception e) {
            LoggerUtils.info(e);
            ShoppingCommonUtil.info(log, e.getMessage());
        } finally {
            if (appConfigDto != null) {
                appConfigDto.setExecuteType(SYMBOL_EMPTY);
            }
            ComponentUtils.setButtonEnabled(execute, query);
        }
    }

    private void appraise(AppConfigDto appConfigDto, ShoppingDto shoppingDto,
                          FunctionConfig functionConfig, List<String> logs) throws Exception {
        List<GoodsDto> goodsList = shoppingDto.getGoodsDtoList();
        int orderNumValue = shoppingDto.getOrderNumValue();
        if (CollectionUtils.isNotEmpty(goodsList)) {
            for (GoodsDto goodsDto : goodsList) {
                goodsDto.setTypeName(functionConfig.getName());
                goodsDto.setType(functionConfig.getCode());
                if (pauseStatus) {
                    break;
                }
                if (StringUtils.isNotBlank(handle.get(getHandlerKey(goodsDto)))) {
                    isContinue = false;
                    break;
                }
                ShoppingCommonUtil.appraiseStart(appConfigDto, log, logs, goodsDto);
                if (StringUtils.isBlank(goodsDto.getGoodsId())) {
                    ShoppingCommonUtil.goodsNotExists(appConfigDto, log, logs, (GoodsDto) BeanUtils.cloneBean(goodsDto));
                    continue;
                }
                if (WAIT_APPRAISE.getCode().equals(functionConfig.getCode())) {
                    WaitAppraiseController.goodsAppraise(appConfigDto, goodsDto);
                    ServiceAppraiseController.goodsAppraise(appConfigDto, goodsDto);
                } else if (SHOW_ORDER.getCode().equals(functionConfig.getCode())) {
                    ShowOrderController.goodsAppraise(appConfigDto, goodsDto);
                } else if (APPEND_APPRAISE.getCode().equals(functionConfig.getCode())) {
                    AppendAppraiseController.goodsAppraise(appConfigDto, goodsDto);
                } else if (SERVICE_APPRAISE.getCode().equals(functionConfig.getCode())) {
                    ServiceAppraiseController.goodsAppraise(appConfigDto, goodsDto);
                } else {
                    ShoppingCommonUtil.typeNotExists(appConfigDto, log, logs, (GoodsDto) BeanUtils.cloneBean(goodsDto));
                }
                ShoppingCommonUtil.appraiseComplete(appConfigDto, log, logs, (GoodsDto) BeanUtils.cloneBean(goodsDto));
                recordHandler(goodsDto);
                handlerNum++;
                double percent = new BigDecimal(handlerNum).divide(new BigDecimal(waitHandlerNum), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                percent = percent > 1 ? 1 : percent;
                setProgress(percent);
                shoppingDto.setOrderNumValue(--orderNumValue);
                new Thread(() -> {
                    OutputUtils.clearLog(waitHandler);
                    OutputUtils.infoList(waitHandler, shoppingDtoList);
                }).start();
                ShoppingCommonUtil.restMoment(appConfigDto, log);
            }
        } else {
            ShoppingCommonUtil.noAppraiseGoods(appConfigDto, log);
        }
    }

    private void recordHandler(GoodsDto goods) {
        String key = getHandlerKey(goods);
        handle.put(key, key);
    }

    public String getHandlerKey(GoodsDto goods) {
        return goods.getOrderId() + SYMBOL_HYPHEN + goods.getGoodsId() + SYMBOL_HYPHEN + goods.getType();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shoppingDtoList = new ArrayList<>();
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (appConfigDto.getJdInitQuery()) {
                if (ShoppingCommonUtil.initJdUser(appConfigDto, log, userName, orderNum)) {
                    query(null);
                }
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            ShoppingCommonUtil.info(log, e.getMessage());
        }
    }
}