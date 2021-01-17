package com.hoomoomoo.im.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.model
 * @date 2020/12/15
 */

@Data
public class AppConfig {

    private Boolean svnUpdate;

    private String svnUsername;

    private String svnPassword;

    private String svnFileName;

    private Long svnVersion;

    private String fileSuffix;

    private Boolean updateDeleteSuccess;

    private String fileEncoding;

    private String lineContentOffset;

    private String lineSpecialContentOffset;

    private String mergerFileName;

    public String getLineContent() {
        if (StringUtils.isNotBlank(this.lineContentOffset)) {
            return this.lineContentOffset.split(SYMBOL_DOLLAR)[1];
        }
        return SYMBOL_EMPTY;
    }

    public Integer getLineOffset() {
        if (StringUtils.isNotBlank(this.lineContentOffset)) {
            return Integer.valueOf(this.lineContentOffset.split(SYMBOL_DOLLAR)[2]);
        }
        return 0;
    }

    public String[] getLineSpecialContentOffset(String versionName, String filePath) {
        String[] line = new String[]{getLineContent(), String.valueOf(getLineOffset())};
        if (StringUtils.isNotBlank(this.lineSpecialContentOffset)) {
            String[] specialList = this.lineSpecialContentOffset.split(SYMBOL_SEMICOLON);
            if (specialList != null) {
                for (String special : specialList) {
                    String[] item = special.split(SYMBOL_DOLLAR);
                    if (item != null && item.length == 4) {
                        if (versionName.equals(item[0]) && filePath.endsWith(item[1])) {
                            line[0] = item[2];
                            line[1] = item[3];
                            break;
                        }
                    }
                }
            }
        }
        return line;
    }
}
