package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LogDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.COPY_CODE;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/10/15
 */
public class CopyCodeController extends BaseController implements Initializable {

    @FXML
    private AnchorPane copyCode;

    @FXML
    private AnchorPane xxx;

    @FXML
    private TextField sourcePath;

    @FXML
    private TextField targetPath;

    @FXML
    private ComboBox sourceVersion;

    @FXML
    private ComboBox targetVersion;

    @FXML
    private TableView log;

    @FXML
    private Button execute;

    @FXML
    private TextArea filePath;

    private int successNum;

    @FXML
    void selectSource(ActionEvent event) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            String version = (String)sourceVersion.getSelectionModel().getSelectedItem();
            OutputUtils.info(sourcePath, appConfigDto.getCopyCodeVersion().get(version));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    @FXML
    void selectTarget(ActionEvent event) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            String version =  (String)targetVersion.getSelectionModel().getSelectedItem();
            OutputUtils.info(targetPath, appConfigDto.getCopyCodeVersion().get(version));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }


    @FXML
    void execute(ActionEvent event) {
        try {
            OutputUtils.clearLog(log);
            LoggerUtils.info(String.format(BaseConst.MSG_USE, COPY_CODE.getName()));
            if (StringUtils.isBlank(sourcePath.getText().trim())) {
                infoMsg("源版本路径不能为空");
                return;
            }
            if (StringUtils.isBlank(targetPath.getText().trim())) {
                infoMsg("目标版本路径不能为空");
                return;
            }
            if (StringUtils.isBlank(filePath.getText().trim())) {
                infoMsg("文件路径不能为空");
                return;
            }
            setProgress(0);
            copyCode();
            updateProgress();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    private void copyCode() throws Exception {
        new Thread(() -> {
            try {
                execute.setDisable(true);
                successNum = 0;
                Date currentDate = new Date();
                String filePathConfig = filePath.getText().trim();
                if (StringUtils.isNotBlank(filePathConfig)) {
                    AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                    List<String> fileLog = new ArrayList<>(16);
                    String[] fileList = filePathConfig.split(SYMBOL_NEXT_LINE);
                    if (fileList != null && fileList.length != 0) {
                        for (int i=0; i<fileList.length; i++) {
                            String item = fileList[i].trim();
                            if (StringUtils.isBlank(item) || item.startsWith(ANNOTATION_TYPE_NORMAL)) {
                                continue;
                            }

                            String copyCodePrefix = appConfigDto.getCopyCodePrefix();
                            if (StringUtils.isNotBlank(copyCodePrefix)) {
                                String[] items = copyCodePrefix.split(SYMBOL_COMMA);
                                for (String prefix : items) {
                                    int index = item.indexOf(prefix);
                                    if (index != -1) {
                                        item = item.substring(index + prefix.length());
                                    }
                                }
                            }
                            String source = sourcePath.getText();
                            String target = targetPath.getText();
                            item = item.replaceAll("\\\\", "/").replace(source, SYMBOL_EMPTY);
                            String fileLocation = source + SYMBOL_SLASH + item;
                            String targetFileLocation = target + SYMBOL_SLASH + item;
                            Iterator<String> iterator = appConfigDto.getReplaceTargetUrl().keySet().iterator();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                if (targetFileLocation.contains("views/fundAccount")) {
                                    if (appConfigDto.getCopyCodeLocationReplaceSkipAccountVersion().toLowerCase(Locale.ROOT).contains((String)targetVersion.getSelectionModel().getSelectedItem())) {
                                        targetFileLocation = targetFileLocation.replace(key, appConfigDto.getReplaceTargetUrl().get(key).split(SYMBOL_SEMICOLON)[1]);
                                    } else {
                                        targetFileLocation = targetFileLocation.replace(key, appConfigDto.getReplaceTargetUrl().get(key).split(SYMBOL_SEMICOLON)[0]);
                                    }
                                    break;
                                }
                                if (targetFileLocation.contains(key)) {
                                    targetFileLocation = targetFileLocation.replace(key, appConfigDto.getReplaceTargetUrl().get(key));
                                    break;
                                }
                            }
                            Iterator<String> iteratorSource = appConfigDto.getReplaceSourceUrl().keySet().iterator();
                            while (iteratorSource.hasNext()) {
                                String key = iteratorSource.next();
                                if (fileLocation.contains(key)) {
                                    fileLocation = fileLocation.replace(key, appConfigDto.getReplaceSourceUrl().get(key));
                                    break;
                                }
                            }
                            if (fileLocation.equals(targetFileLocation)) {
                                infoMsg(getFileName(targetFileLocation) + " 同路径同名文件不复制");
                                continue;
                            }
                            List<String> sourceContent = FileUtils.readNormalFile(fileLocation, false);
                            File file = new File(fileLocation);
                            String encode = ENCODING_UTF8;
                            if (file.exists()) {
                                encode = FileUtils.getFileEncode(fileLocation);
                            }

                            File targetFile = new File(targetFileLocation);
                            if (targetFile.exists()) {
                                targetFile.delete();
                            }
                            if (CollectionUtils.isNotEmpty(sourceContent)) {
                                for (int j=0; j<sourceContent.size(); j++) {
                                    FileUtils.writeFile(targetFileLocation, sourceContent.get(j) + SYMBOL_NEXT_LINE, encode, true);
                                }
                                fileLog.add(targetFileLocation);
                                infoMsg(getFileName(targetFileLocation) + " 复制完成");
                                successNum++;
                            }
                        }
                        String msg = SYMBOL_EMPTY;
                        if (fileList.length == successNum) {
                            msg += "复制成功 总文件数[ " + successNum + " ]";
                        } else {
                            msg += "复制失败 总文件数[ " + successNum + " ] 失败文件数[ " + (fileList.length - successNum) + " ]";
                        }
                        infoMsg(msg);
                    }
                    LoggerUtils.writeCopyCodeInfo(currentDate, fileLog);
                }
                setProgress(1);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(log, e.getMessage());
            } finally {
                setProgress(1);
                execute.setDisable(false);
            }
        }).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Map<String, Integer> versionIndex = new LinkedHashMap<>(16);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            ObservableList sourceVersionItems = sourceVersion.getItems();
            ObservableList targetVersionItems = targetVersion.getItems();
            Map<String, String> copyCodeVersion = appConfigDto.getCopyCodeVersion();
            if (MapUtils.isNotEmpty(copyCodeVersion)) {
                int index = 0;
                Iterator<String> version = copyCodeVersion.keySet().iterator();
                while (version.hasNext()) {
                    String ver = version.next();
                    sourceVersionItems.add(ver);
                    targetVersionItems.add(ver);
                    versionIndex.put(ver, index);
                    index++;
                }
            }

            String defaultSource = appConfigDto.getCopyCodeDefaultSource();
            if (MapUtils.isNotEmpty(versionIndex)) {
                if (StringUtils.isNotBlank(defaultSource)) {
                    defaultSource = defaultSource.toLowerCase();
                    sourceVersion.getSelectionModel().select(defaultSource);
                    OutputUtils.info(sourcePath, appConfigDto.getCopyCodeVersion().get(defaultSource));
                }
                String defaultTarget = appConfigDto.getCopyCodeDefaultTarget();
                if (StringUtils.isNotBlank(defaultTarget)) {
                    defaultTarget = defaultTarget.toLowerCase();
                    targetVersion.getSelectionModel().select(defaultTarget);
                    OutputUtils.info(targetPath, appConfigDto.getCopyCodeVersion().get(defaultTarget));
                }
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    private void infoMsg(String msg) {
        LogDto logDto = new LogDto();
        logDto.setTime(CommonUtils.getCurrentDateTime8(new Date()));
        logDto.setMsg(msg);
        OutputUtils.info(log, logDto);
    }

    private String getFileName(String filePath) {
        int index = filePath.lastIndexOf(SYMBOL_SLASH);
        return filePath.substring(index + 1);
    }
}
