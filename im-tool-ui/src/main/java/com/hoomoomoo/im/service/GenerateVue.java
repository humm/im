package com.hoomoomoo.im.service;

import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.service.generate
 * @date 2021/10/15
 */
public class GenerateVue {

    public static String init(GenerateCodeDto generateCodeDto) throws Exception {
        String functionCode = generateCodeDto.getFunctionCode();
        String functionName = generateCodeDto.getFunctionName();
        String firstMenu = generateCodeDto.getMenuList().get(0)[0];
        String secondMenu = generateCodeDto.getMenuList().get(1)[0];
        String packageName = PACKAGE_VUE_PREFIX + firstMenu + "." + secondMenu;
        Map<String, String> component = initComponent(generateCodeDto);
        StringBuilder content = new StringBuilder();
        content.append("<template>").append(SYMBOL_NEXT_LINE);
        content.append("  <div class=\"layout\">").append(SYMBOL_NEXT_LINE);
        content.append("    <show-version-log>").append(SYMBOL_NEXT_LINE);
        content.append("      <div slot=\"log\">").append(SYMBOL_NEXT_LINE);
        content.append("        <div>").append(SYMBOL_NEXT_LINE);
        content.append("          * V6.0.0.0 " + CommonUtils.getCurrentDateTime3() + " " + generateCodeDto.getAuthor() + " " + generateCodeDto.getFunctionName()).append(SYMBOL_NEXT_LINE);
        content.append("        </div>").append(SYMBOL_NEXT_LINE);
        content.append("      </div>").append(SYMBOL_NEXT_LINE);
        content.append("    </show-version-log>").append(SYMBOL_NEXT_LINE);
        content.append("    <h-row name=\"flex\" class=\"layout-menu-wrapper\">").append(SYMBOL_NEXT_LINE);
        content.append("      <h-col span=\"24\" class=\"layout-menu-box\">").append(SYMBOL_NEXT_LINE);
        content.append("        <div>").append(SYMBOL_NEXT_LINE);
        content.append("          <!--查询表单-->").append(SYMBOL_NEXT_LINE);
        content.append("          <h-panel @changeCollapse=\"handleResize\" class=\"h-main-search clearfix\" collapse>").append(SYMBOL_NEXT_LINE);
        content.append("            <h-form :model=\"searchForm\" :label-width=\"120\" ref=\"searchForm\" cols=\"4\">").append(SYMBOL_NEXT_LINE);
        content.append(component.get(KEY_SEARCH_FORM));
        content.append("            </h-form>").append(SYMBOL_NEXT_LINE);
        content.append("          </h-panel>").append(SYMBOL_NEXT_LINE);
        content.append("          <!--查询表单-->").append(SYMBOL_NEXT_LINE);
        content.append("          <h-row>").append(SYMBOL_NEXT_LINE);
        content.append("            <h-col span=\"24\">").append(SYMBOL_NEXT_LINE);
        content.append("              <h-datagrid ellipsis").append(SYMBOL_NEXT_LINE);
        content.append("                          :value.sync=\"queryDisabled\"").append(SYMBOL_NEXT_LINE);
        content.append("                          :columns=\"columns\"").append(SYMBOL_NEXT_LINE);
        content.append("                          :downloadParams=\"downloadParams\"").append(SYMBOL_NEXT_LINE);
        content.append("                          @downloadValid=\"handleExcelExport\"").append(SYMBOL_NEXT_LINE);
        content.append("                          :stripe=\"false\"").append(SYMBOL_NEXT_LINE);
        content.append("                          apiHome=\"console-fund-ta-vue\"").append(SYMBOL_NEXT_LINE);
        content.append("                          hasPage").append(SYMBOL_NEXT_LINE);
        content.append("                          highlight-row").append(SYMBOL_NEXT_LINE);
        content.append("                          :height=\"300\"").append(SYMBOL_NEXT_LINE);
        content.append("                          url=\"/" + generateCodeDto.getFunctionCode() + "/" + generateCodeDto.getFunctionCode() + "Query\"").append(SYMBOL_NEXT_LINE);
        content.append("                          :bindForm=\"searchParams\"").append(SYMBOL_NEXT_LINE);
        content.append("                          :onSelectChange=\"handleSelectClick\"").append(SYMBOL_NEXT_LINE);
        content.append("                          @on-page-change-url=\"handlePageChange\"").append(SYMBOL_NEXT_LINE);
        content.append("                          @on-row-dblclick=\"handleOpenDetail\"").append(SYMBOL_NEXT_LINE);
        content.append("                          :selfAdaptTable=\"tableRef\"").append(SYMBOL_NEXT_LINE);
        content.append("                          showListCkeckBox").append(SYMBOL_NEXT_LINE);
        content.append("                          ref=\"datagrid\"").append(SYMBOL_NEXT_LINE);
        content.append("                >").append(SYMBOL_NEXT_LINE);
        content.append("                <div slot=\"toolbar\" class=\"pull-left\">").append(SYMBOL_NEXT_LINE);
        content.append("                  <h-button type=\"primary\" v-if='authObj.query' :disabled='queryDisabled' @click='formSearch()'>").append(SYMBOL_NEXT_LINE);
        content.append("                    {{ $t('m.i.common.search') }}").append(SYMBOL_NEXT_LINE);
        content.append("                  </h-button>").append(SYMBOL_NEXT_LINE);
        content.append("                  <h-button type=\"ghost\" @click='formSearchReset()'>{{ $t('m.i.common.reset') }}</h-button>").append(SYMBOL_NEXT_LINE);
        content.append("                  <h-button v-if=\"authObj.add\" @click=\"handleOpenAdd()\">{{ $t('m.i.common.add') }}</h-button>").append(SYMBOL_NEXT_LINE);
        content.append("                  <h-button v-if=\"authObj.edit\" @click=\"handleOpenBatchEdit()\">{{ $t('m.i.common.edit') }}</h-button>").append(SYMBOL_NEXT_LINE);
        content.append("                  <h-button v-if=\"authObj.delete\" @click=\"handleDelete()\">{{ $t('m.i.common.delete') }}</h-button>").append(SYMBOL_NEXT_LINE);
        content.append("                  <h-button v-if=\"authObj.import\" @click=\"handleExcelImport()\">导入</h-button>").append(SYMBOL_NEXT_LINE);
        content.append("                </div>").append(SYMBOL_NEXT_LINE);
        content.append("              </h-datagrid>").append(SYMBOL_NEXT_LINE);
        content.append("            </h-col>").append(SYMBOL_NEXT_LINE);
        content.append("          </h-row>").append(SYMBOL_NEXT_LINE);
        content.append("        </div>").append(SYMBOL_NEXT_LINE);
        content.append("      </h-col>").append(SYMBOL_NEXT_LINE);
        content.append("    </h-row>").append(SYMBOL_NEXT_LINE_2);
        content.append("    <!-- 新增弹框 -->").append(SYMBOL_NEXT_LINE);
        content.append("    <h-msg-box width=\"800\" v-model=\"showModal\" title=\"新增\">").append(SYMBOL_NEXT_LINE);
        content.append("      <h-form label-width=\"170\" cols=\"2\" ref=\"addForm\" :model=\"addForm\" :rules=\"rules\">").append(SYMBOL_NEXT_LINE);
        content.append(component.get(KEY_ADD_FORM));
        content.append("      </h-form>").append(SYMBOL_NEXT_LINE);
        content.append("      <div slot='footer'>").append(SYMBOL_NEXT_LINE);
        content.append("        <h-button type='ghost' @click='handleCancel()'>取消</h-button>").append(SYMBOL_NEXT_LINE);
        content.append("        <h-button type='primary' @click='handleAddSubmit()'>提交</h-button>").append(SYMBOL_NEXT_LINE);
        content.append("      </div>").append(SYMBOL_NEXT_LINE);
        content.append("      <h-spin size='large' fix v-if='spinShow'></h-spin>").append(SYMBOL_NEXT_LINE);
        content.append("    </h-msg-box>").append(SYMBOL_NEXT_LINE_2);
        content.append("    <!-- 修改弹框 -->").append(SYMBOL_NEXT_LINE);
        content.append("    <h-msg-box width=\"800\" v-model=\"showEditModal\" title=\"修改\">").append(SYMBOL_NEXT_LINE);
        content.append("      <h-form label-width=\"170\" cols=\"2\" ref=\"updateForm\" :model=\"updateForm\" :rules=\"rules\">").append(SYMBOL_NEXT_LINE);
        content.append(component.get(KEY_UPDATE_FORM));
        content.append("      </h-form>").append(SYMBOL_NEXT_LINE);
        content.append("      <div slot='footer'>").append(SYMBOL_NEXT_LINE);
        content.append("        <h-button type='ghost' @click='handleCancel()'>取消</h-button>").append(SYMBOL_NEXT_LINE);
        content.append("        <h-button type='primary' @click='handleUpdateSubmit()'>提交</h-button>").append(SYMBOL_NEXT_LINE);
        content.append("      </div>").append(SYMBOL_NEXT_LINE);
        content.append("      <h-spin size='large' fix v-if='spinEditShow'></h-spin>").append(SYMBOL_NEXT_LINE);
        content.append("    </h-msg-box>").append(SYMBOL_NEXT_LINE_2);
//        content.append("    <!-- 批量修改弹框 -->").append(SYMBOL_NEXT_LINE);
        content.append("    <!-- 导入弹窗 -->").append(SYMBOL_NEXT_LINE);
        content.append("    <upload-excel").append(SYMBOL_NEXT_LINE);
        content.append("      ref='uploadExcel'").append(SYMBOL_NEXT_LINE);
        content.append("      v-model='showImport'").append(SYMBOL_NEXT_LINE);
        content.append("      menuCode='" + functionCode + "'").append(SYMBOL_NEXT_LINE);
        content.append("      opCode='" + functionCode + "Import'").append(SYMBOL_NEXT_LINE);
        content.append("      @successUpload='formSearch'").append(SYMBOL_NEXT_LINE);
        content.append("      title='" + functionName + "'>").append(SYMBOL_NEXT_LINE);
        content.append("    </upload-excel>").append(SYMBOL_NEXT_LINE);
        content.append("  </div>").append(SYMBOL_NEXT_LINE);
        content.append("</template>").append(SYMBOL_NEXT_LINE);
        content.append("<script>").append(SYMBOL_NEXT_LINE);
        content.append(buildImport(generateCodeDto)).append(SYMBOL_NEXT_LINE);
        content.append("</script>").append(SYMBOL_NEXT_LINE_2);
        content.append("<style scoped lang='scss'>").append(SYMBOL_NEXT_LINE);
        content.append("  .btn-hidden {").append(SYMBOL_NEXT_LINE);
        content.append("    display: none;").append(SYMBOL_NEXT_LINE);
        content.append("  }").append(SYMBOL_NEXT_LINE);
        content.append("</style>").append(SYMBOL_NEXT_LINE);
        return GenerateCommon.generateVueFile(generateCodeDto, packageName, functionCode, content.toString());
    }

    private static String buildImport(GenerateCodeDto generateCodeDto) {
        StringBuilder content = new StringBuilder();
        
        return content.toString();
    }

    private static Map<String, String> initComponent(GenerateCodeDto generateCodeDto) {
        Map<String, String> component = new HashMap<>(16);
        StringBuilder queryForm = new StringBuilder();
        StringBuilder addForm = new StringBuilder();
        StringBuilder updateForm = new StringBuilder();
        Map<String, Map<String, String>> columnInfo = generateCodeDto.getColumnMap();
        Iterator<String> iterator = columnInfo.keySet().iterator();

        queryForm.append("              <h-form-item label='TA代码' prop='multTaCode' required v-if='isMultTa'>").append(SYMBOL_NEXT_LINE);
        queryForm.append("                <mult-ta-select transfer v-model='searchForm.multTaCode' setDefSelect :clearable='false'").append(SYMBOL_NEXT_LINE);
        queryForm.append("                            @on-change='searchChange'/>").append(SYMBOL_NEXT_LINE);
        queryForm.append("              </h-form-item>").append(SYMBOL_NEXT_LINE);

        addForm.append("        <h-form-item label='TA代码' prop='multTaCode' required v-if='isMultTa'>").append(SYMBOL_NEXT_LINE);
        addForm.append("          <mult-ta-select v-model='addForm.multTaCode'/>").append(SYMBOL_NEXT_LINE);
        addForm.append("        </h-form-item>").append(SYMBOL_NEXT_LINE);

        updateForm.append("        <h-form-item label='TA代码' prop='multTaCode' required v-if='isMultTa'>").append(SYMBOL_NEXT_LINE);
        updateForm.append("          <mult-ta-select v-model='updateForm.multTaCode' disabled/>").append(SYMBOL_NEXT_LINE);
        updateForm.append("        </h-form-item>").append(SYMBOL_NEXT_LINE);

        while (iterator.hasNext()) {
            String columnCode = iterator.next();
            Map<String, String> item = columnInfo.get(columnCode);
            if (GenerateCommon.skipColumn(columnCode)) {
                continue;
            }
            String columnName = item.get(KEY_COLUMN_NAME);
            String columnType = item.get(KEY_COLUMN_TYPE);
            String columnDict = item.get(KEY_COLUMN_DICT);
            String columnMulti = item.get(KEY_COLUMN_MULTI);
            String columnRequired = item.get(KEY_COLUMN_REQUIRED);
            String columnPrecision = item.get(KEY_COLUMN_PRECISION);

            queryForm.append("              <h-form-item label='" + columnName + "' prop='" + columnCode + "'>").append(SYMBOL_NEXT_LINE);
            addForm.append("        <h-form-item label='" + columnName + "' prop='" + columnCode + "'>").append(SYMBOL_NEXT_LINE);
            updateForm.append("        <h-form-item label='" + columnName + "' prop='" + columnCode + "'>").append(SYMBOL_NEXT_LINE);

            if (KEY_PRD_CODE.equals(columnCode)) {
                getPrdCodeQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getPrdCodeAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getPrdCodeAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
            } else if (KEY_SELLER_CODE.equals(columnCode)) {
                getSellerCodeQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getSellerCodeAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getSellerCodeAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
            } else if (KEY_BRANCH_NO.equals(columnCode)) {
                getBranchNoQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getBranchNoAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getBranchNoAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
            } else if (StringUtils.isNotBlank(columnDict)) {
                getDictQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getDictAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getDictAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
            } else if (KEY_COLUMN_TYPE_DATE.equals(columnType)) {
                getDateQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getDateAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getDateAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
            } else if (KEY_COLUMN_TYPE_NUMBER.equals(columnType) || KEY_COLUMN_TYPE_INTEGER.equals(columnType)) {
                getNumberQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getNumberAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getNumberAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
            } else {
                getTextQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getTextAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getTextAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
            }

            queryForm.append("              </h-form-item>").append(SYMBOL_NEXT_LINE);
            addForm.append("        </h-form-item>").append(SYMBOL_NEXT_LINE);
            updateForm.append("        </h-form-item>").append(SYMBOL_NEXT_LINE);
        }
        component.put(KEY_SEARCH_FORM, queryForm.toString());
        component.put(KEY_ADD_FORM, addForm.toString());
        component.put(KEY_UPDATE_FORM, updateForm.toString());
        return component;
    }

    private static void getPrdCodeQueryForm(StringBuilder queryForm, String columnCode, Map<String, String> item) {
        queryForm.append("                <dialog-select").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  v-model='" + columnCode + "'").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  multiple").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  multClearable").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  type='prd'").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  apiHome=\"console-fund-ta-vue\" interFace=\"/fundPrdCodeList/fundPrdCodeListQuery\"").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  :alias=\"{value: 'prdCode', label: 'prdCode:prdName'}\"/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getPrdCodeAddUpdateForm(StringBuilder addForm, String columnCode, Map<String, String> item) {
        String columnMulti = item.get(KEY_COLUMN_MULTI);
        addForm.append("          <dialog-select").append(SYMBOL_NEXT_LINE);
        addForm.append("            v-model='" + columnCode + "'").append(SYMBOL_NEXT_LINE);
        if (STR_1.equals(columnMulti)) {
            addForm.append("            multiple").append(SYMBOL_NEXT_LINE);
            addForm.append("            multClearable").append(SYMBOL_NEXT_LINE);
        }
        addForm.append("            type='prd'").append(SYMBOL_NEXT_LINE);
        addForm.append("            :params=\"{ prdStatus: '6,9'}\"").append(SYMBOL_NEXT_LINE);
        addForm.append("            apiHome=\"console-fund-ta-vue\" interFace=\"/fundPrdCodeList/fundPrdCodeListQuery\"").append(SYMBOL_NEXT_LINE);
        addForm.append("            :alias=\"{value: 'prdCode', label: 'prdCode:prdName'}\"/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getSellerCodeQueryForm(StringBuilder queryForm, String columnCode, Map<String, String> item) {
        queryForm.append("                <simple-auto-select").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  v-model='" + columnCode + "'").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  transfer").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  multiple").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  multClearable").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  isCheckall").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  apiHome=\"console-fund-ta-vue\" interFace=\"/fundSellerCodeList/fundSellerCodeListQuery\"").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  :alias=\"{value: 'sellerCode', label: 'sellerCode:sellerName'}\"/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getSellerCodeAddUpdateForm(StringBuilder addForm, String columnCode, Map<String, String> item) {
        String columnMulti = item.get(KEY_COLUMN_MULTI);
        addForm.append("          <simple-auto-select").append(SYMBOL_NEXT_LINE);
        addForm.append("            v-model='" + columnCode + "'").append(SYMBOL_NEXT_LINE);
        addForm.append("            transfer").append(SYMBOL_NEXT_LINE);
        if (STR_1.equals(columnMulti)) {
            addForm.append("            multiple").append(SYMBOL_NEXT_LINE);
            addForm.append("            multClearable").append(SYMBOL_NEXT_LINE);
            addForm.append("            isCheckall").append(SYMBOL_NEXT_LINE);
        } else {
            addForm.append("            :multiple='false'").append(SYMBOL_NEXT_LINE);
        }
        addForm.append("            apiHome=\"console-fund-ta-vue\" interFace=\"/fundSellerCodeList/fundSellerCodeListQuery\"").append(SYMBOL_NEXT_LINE);
        addForm.append("            :alias=\"{value: 'sellerCode', label: 'sellerCode:sellerName'}\"/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getBranchNoQueryForm(StringBuilder queryForm, String columnCode, Map<String, String> item) {
        queryForm.append("                <simple-auto-select").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  v-model='" + columnCode + "'").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  transfer").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  multiple").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  multClearable").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  isCheckall").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  :params=\"{'sellerCode':" + columnCode.replace(KEY_BRANCH_NO, KEY_SELLER_CODE) + "}\"").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  apiHome=\"console-fund-ta-vue\" interFace=\"/fundNetInfoList/fundNetInfoListQuery\"").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  :alias=\"{value: 'branchNo', label: 'branchNo:branchName'}\"/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getBranchNoAddUpdateForm(StringBuilder addForm, String columnCode, Map<String, String> item) {
        String columnMulti = item.get(KEY_COLUMN_MULTI);
        addForm.append("          <simple-auto-select").append(SYMBOL_NEXT_LINE);
        addForm.append("            v-model='" + columnCode + "'").append(SYMBOL_NEXT_LINE);
        addForm.append("            transfer").append(SYMBOL_NEXT_LINE);
        if (STR_1.equals(columnMulti)) {
            addForm.append("            multiple").append(SYMBOL_NEXT_LINE);
            addForm.append("            multClearable").append(SYMBOL_NEXT_LINE);
            addForm.append("            isCheckall").append(SYMBOL_NEXT_LINE);
        } else {
            addForm.append("            :multiple='false'").append(SYMBOL_NEXT_LINE);
        }
        addForm.append("            :params=\"{'sellerCode':" + columnCode.replace(KEY_BRANCH_NO, KEY_SELLER_CODE) + "}\"").append(SYMBOL_NEXT_LINE);
        addForm.append("            apiHome=\"console-fund-ta-vue\" interFace=\"/fundNetInfoList/fundNetInfoListQuery\"").append(SYMBOL_NEXT_LINE);
        addForm.append("            :alias=\"{value: 'branchNo', label: 'branchNo:branchName'}\"/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getDictQueryForm(StringBuilder queryForm, String columnCode, Map<String, String> item) {
        String columnDict = item.get(KEY_COLUMN_DICT);
        queryForm.append("                <auto-select transfer multiple multClearable isCheckall v-model='" + columnCode + "'").append(SYMBOL_NEXT_LINE);
        queryForm.append("                             dictName='" + columnDict + "'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getDictAddUpdateForm(StringBuilder addForm, String columnCode, Map<String, String> item) {
        String columnMulti = item.get(KEY_COLUMN_MULTI);
        String columnDict = item.get(KEY_COLUMN_DICT);
        addForm.append("          <auto-select transfer");
        if (STR_1.equals(columnMulti)) {
            addForm.append(" multiple multClearable isCheckall");
        }
        addForm.append(" v-model='" + columnCode + "' dictName='" + columnDict + "'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getDateQueryForm(StringBuilder queryForm, String columnCode, Map<String, String> item) {
        queryForm.append("                <h-date-picker transfer type='daterange' v-model='" + columnCode + "' autoPlacement />").append(SYMBOL_NEXT_LINE);
    }

    private static void getDateAddUpdateForm(StringBuilder addForm, String columnCode, Map<String, String> item) {
        addForm.append("          <h-date-picker transfer type='date' v-model='" + columnCode + "' autoPlacement />").append(SYMBOL_NEXT_LINE);
    }

    private static void getNumberQueryForm(StringBuilder queryForm, String columnCode, Map<String, String> item) {
        queryForm.append("                <scope-input v-model='" + columnCode + "' suffixNum='-1' placeholder='请输入'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getNumberAddUpdateForm(StringBuilder addForm, String columnCode, Map<String, String> item) {
        addForm.append("          <h-input v-model='" + columnCode + "'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getTextQueryForm(StringBuilder queryForm, String columnCode, Map<String, String> item) {
        queryForm.append("                <h-input v-model='" + columnCode + "'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getTextAddUpdateForm(StringBuilder addForm, String columnCode, Map<String, String> item) {
        addForm.append("          <h-input v-model='" + columnCode + "'/>").append(SYMBOL_NEXT_LINE);
    }
}
