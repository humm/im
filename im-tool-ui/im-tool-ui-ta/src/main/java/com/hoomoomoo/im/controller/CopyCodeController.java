package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.task.CopyCodeTask;
import com.hoomoomoo.im.task.CopyCodeTaskParam;
import com.hoomoomoo.im.utils.*;
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
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.COPY_CODE;

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
    private TextArea log;

    @FXML
    private Button execute;

    @FXML
    private TextArea filePath;

    @FXML
    private RadioButton yes;

    @FXML
    private RadioButton no;

    private int successNum;

    private Map<String, Set<String>> locationReplace = new HashMap<>();

    @FXML
    void selectNo(ActionEvent event) {
        OutputUtils.selected(no, true);
        OutputUtils.selected(yes, false);
    }

    @FXML
    void selectYes(ActionEvent event) {
        OutputUtils.selected(yes, true);
        OutputUtils.selected(no, false);
    }

    @FXML
    void selectSource(ActionEvent event) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String version = (String)sourceVersion.getSelectionModel().getSelectedItem();
            String path = getLocation(version, appConfigDto.getCopyCodeVersion().get(version));
            if (version.equalsIgnoreCase(KEY_DESKTOP)) {
                path += STR_SLASH + CommonUtils.getCurrentDateTime2();
            }
            OutputUtils.info(sourcePath, path);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    @FXML
    void selectTarget(ActionEvent event) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String version =  (String)targetVersion.getSelectionModel().getSelectedItem();
            String path = getLocation(version, appConfigDto.getCopyCodeVersion().get(version));
            if (version.equalsIgnoreCase(KEY_DESKTOP)) {
                path += STR_SLASH + CommonUtils.getCurrentDateTime2();
            }
            OutputUtils.info(targetPath, path);
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
            TaskUtils.execute(new CopyCodeTask(new CopyCodeTaskParam(this)));
            updateProgress();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    public void copyCode() throws Exception {
        String fileLocation = STR_BLANK;
        try {
            execute.setDisable(true);
            successNum = 0;
            Date currentDate = new Date();
            String filePathConfig = filePath.getText().trim();
            int skipNum = 0;
            if (StringUtils.isNotBlank(filePathConfig)) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                List<String> fileLog = new ArrayList<>(16);
                String[] fileList = filePathConfig.split(STR_NEXT_LINE);
                if (fileList != null && fileList.length != 0) {
                    for (int i=0; i<fileList.length; i++) {
                        String item = fileList[i].trim();
                        if (StringUtils.isBlank(item) || item.startsWith(ANNOTATION_NORMAL)) {
                            skipNum++;
                            continue;
                        }
                        if (yes.isSelected() && !item.trim().toLowerCase().endsWith(FILE_TYPE_JAVA)) {
                            skipNum++;
                            continue;
                        }
                        String copyCodePrefix = appConfigDto.getCopyCodePrefix();
                        if (StringUtils.isNotBlank(copyCodePrefix)) {
                            String[] items = copyCodePrefix.split(STR_COMMA);
                            for (String prefix : items) {
                                int index = item.indexOf(prefix);
                                if (index != -1) {
                                    item = item.substring(index + prefix.length());
                                }
                            }
                        }
                        String source = sourcePath.getText();
                        String target = targetPath.getText();
                        if (item.endsWith(FILE_TYPE_RPX)) {
                            infoMsg("RPX报表文件不能复制");
                            continue;
                        }
                        item = item.replaceAll("\\\\", "/").replace(source, STR_BLANK);
                        if (yes.isSelected()) {
                            item = item.replace("src/main/java", "target/classes").replace(FILE_TYPE_JAVA, FILE_TYPE_CLASS);
                        }

                        String targetVersionSelected = (String)targetVersion.getSelectionModel().getSelectedItem();
                        String sourceVersionSelected = (String)sourceVersion.getSelectionModel().getSelectedItem();
                        String targetVersionYear = targetVersionSelected.replaceAll(STR_VERSION_PREFIX, STR_BLANK).split("\\.")[0];
                        String subDir = STR_BLANK;
                        if (targetVersionYear.compareTo(KEY_GIT_VERSION_YEAR) >= 0 || StringUtils.equals(targetVersionSelected, KEY_TRUNK)) {
                            File sourceFile = new File(source);
                            if (sourceFile.isDirectory()) {
                                File[] sourceFileList = sourceFile.listFiles();
                                for (int j=0; j<sourceFileList.length; j++) {
                                    File subFile = sourceFileList[j];
                                    String subFilePath = subFile.getAbsolutePath() + STR_SLASH + item;
                                    if (new File(subFilePath).exists()) {
                                        fileLocation = subFilePath.replaceAll("\\\\", "/");
                                        subDir = subFile.getName();
                                        break;
                                    }
                                }
                            }
                        }
                        if (!fileLocation.startsWith(source)) {
                            fileLocation = source + STR_SLASH + item;
                        } else {
                            target += STR_SLASH + subDir;
                        }
                        String targetFileLocation = target + STR_SLASH + item;
                        if (targetVersionYear.compareTo(KEY_GIT_VERSION_YEAR) < 0) {
                            if (!appConfigDto.getCopyCodeLocationReplaceSkipVersion().contains(targetVersionSelected) || KEY_TRUNK.equals(sourceVersionSelected)) {
                                Iterator<String> iterator = appConfigDto.getReplaceTargetUrl().keySet().iterator();
                                while (iterator.hasNext()) {
                                    String key = iterator.next();
                                    if (targetFileLocation.contains("views/fundAccount")) {
                                        if (appConfigDto.getCopyCodeLocationReplaceSkipAccountVersion().contains(targetVersionSelected)) {
                                            targetFileLocation = targetFileLocation.replace(key, appConfigDto.getReplaceTargetUrl().get(key).split(STR_$_SLASH)[1]);
                                        } else {
                                            targetFileLocation = targetFileLocation.replace(key, appConfigDto.getReplaceTargetUrl().get(key).split(STR_$_SLASH)[0]);
                                        }
                                        break;
                                    }
                                    LoggerUtils.info(key + " " + targetFileLocation.contains(key));
                                    if (targetFileLocation.contains(key)) {
                                        targetFileLocation = targetFileLocation.replace(key, appConfigDto.getReplaceTargetUrl().get(key));
                                        break;
                                    }
                                }
                            }
                            if (KEY_TRUNK.equals(sourceVersionSelected)) {
                                Iterator<String> iteratorSource = appConfigDto.getReplaceSourceUrl().keySet().iterator();
                                while (iteratorSource.hasNext()) {
                                    String key = iteratorSource.next();
                                    if (fileLocation.contains(key)) {
                                        fileLocation = fileLocation.replace(key, appConfigDto.getReplaceSourceUrl().get(key));
                                        break;
                                    }
                                }
                            }
                        }
                        if (fileLocation.equals(targetFileLocation)) {
                            infoMsg(getFileName(targetFileLocation) + " 同路径同名文件不复制");
                            skipNum++;
                            continue;
                        }
                        List<String> sourceContent = FileUtils.readNormalFile(fileLocation);
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
                                FileUtils.writeFile(targetFileLocation, sourceContent.get(j) + STR_NEXT_LINE, encode,true);
                            }
                            fileLog.add("原始文件: " + fileLocation);
                            fileLog.add("目标文件: " + targetFileLocation);
                            infoMsg(getFileName(targetFileLocation) + " 复制完成");
                            successNum++;
                        }
                    }
                    String msg = STR_NEXT_LINE;
                    if (fileList.length - skipNum == successNum) {
                        msg += "复制成功 总文件数【 " + successNum + " 】";
                    } else {
                        msg += "复制失败 总文件数【 " + successNum + " 】 失败文件数【 " + (fileList.length - successNum) + " 】";
                    }
                    infoMsg(msg);
                }
                LoggerUtils.writeCopyCodeInfo(currentDate, fileLog);
            }
            setProgress(1);
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                OutputUtils.info(log, fileLocation.substring(fileLocation.lastIndexOf("/") + 1)+ " 不存在" + STR_NEXT_LINE);
            } else {
                OutputUtils.info(log, e.getMessage());
            }
            LoggerUtils.info(e);
        } finally {
            setProgress(1);
            execute.setDisable(false);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String copyCodeLocationReplaceVersion = appConfigDto.getCopyCodeLocationReplaceVersion();
            if (StringUtils.isNotBlank(copyCodeLocationReplaceVersion)) {
                String[] itemList = copyCodeLocationReplaceVersion.split(STR_SEMICOLON);
                for (String item : itemList) {
                    String[] element = item.split(STR_COLON);
                    if (element.length != 2) {
                        continue;
                    }
                    String locationKey = element[0];
                    String[] locationVersion = element[1].split(STR_COMMA);
                    for (String version : locationVersion) {
                        if (locationReplace.containsKey(version)) {
                            Set<String> ele = new HashSet<>();
                            ele.add(locationKey);
                            locationReplace.put(version, ele);
                        } else {
                            Set<String> ele = new HashSet<>();
                            ele.add(locationKey);
                            locationReplace.put(version, ele);
                        }
                    }
                }
            }

            ObservableList sourceVersionItems = sourceVersion.getItems();
            ObservableList targetVersionItems = targetVersion.getItems();
            Map<String, String> copyCodeVersion = appConfigDto.getCopyCodeVersion();
            if (MapUtils.isNotEmpty(copyCodeVersion)) {
                Iterator<String> version = copyCodeVersion.keySet().iterator();
                while (version.hasNext()) {
                    String ver = version.next();
                    sourceVersionItems.add(ver);
                    targetVersionItems.add(ver);
                }
            }

            if (MapUtils.isNotEmpty(copyCodeVersion)) {
                String defaultSource = appConfigDto.getCopyCodeDefaultSource();
                if (StringUtils.isNotBlank(defaultSource)) {
                    if (!(defaultSource.equalsIgnoreCase(KEY_TRUNK) || defaultSource.equalsIgnoreCase(KEY_DESKTOP))) {
                        defaultSource = defaultSource.toUpperCase();
                    }
                    sourceVersion.getSelectionModel().select(defaultSource);
                    String path = getLocation(defaultSource, appConfigDto.getCopyCodeVersion().get(defaultSource));
                    if (defaultSource.equalsIgnoreCase(KEY_DESKTOP)) {
                        path += STR_SLASH + CommonUtils.getCurrentDateTime2();
                    }
                    OutputUtils.info(sourcePath, path);
                }
                String defaultTarget = appConfigDto.getCopyCodeDefaultTarget();
                if (StringUtils.isNotBlank(defaultTarget)) {
                    if (!(defaultTarget.equalsIgnoreCase(KEY_TRUNK) || defaultTarget.equalsIgnoreCase(KEY_DESKTOP))) {
                        defaultTarget = defaultTarget.toUpperCase();
                    }
                    targetVersion.getSelectionModel().select(defaultTarget);
                    String path = getLocation(defaultTarget, appConfigDto.getCopyCodeVersion().get(defaultTarget));
                    if (defaultTarget.equalsIgnoreCase(KEY_DESKTOP)) {
                        path += STR_SLASH + CommonUtils.getCurrentDateTime2();
                    }
                    OutputUtils.info(targetPath, path);
                }
                String onlyClass = appConfigDto.getCopyCodeOnlyClass();
                if (StringUtils.equals(STR_1, onlyClass)) {
                    selectYes(null);
                } else {
                    selectNo(null);
                }
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    private void infoMsg(String msg) {
        OutputUtils.info(log, msg + STR_NEXT_LINE);
    }

    private String getFileName(String filePath) {
        int index = filePath.lastIndexOf(STR_SLASH);
        return filePath.substring(index + 1);
    }

    private String getLocation(String version, String path) {
        if (!locationReplace.containsKey(version)) {
            return path;
        }
        Iterator<String> iterator = locationReplace.get(version).iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            path = path.replace(item, STR_BLANK);
        }
        return path;
    }
}
