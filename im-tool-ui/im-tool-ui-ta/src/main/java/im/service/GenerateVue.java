package im.service;

import im.dto.ColumnInfoDto;
import im.dto.GenerateCodeDto;
import im.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package im.service.generate
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
        content.append(buildExport(generateCodeDto)).append(SYMBOL_NEXT_LINE);
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
        content.append("  import {").append(SYMBOL_NEXT_LINE);
        content.append("    getValidator,").append(SYMBOL_NEXT_LINE);
        content.append("    addTitleSearchCondition,").append(SYMBOL_NEXT_LINE);
        content.append("    selectCurrentRow,").append(SYMBOL_NEXT_LINE);
        content.append("    openDetail,").append(SYMBOL_NEXT_LINE);
        content.append("    translateRender,").append(SYMBOL_NEXT_LINE);
        content.append("    dateRender,").append(SYMBOL_NEXT_LINE);
        content.append("    amountRender,").append(SYMBOL_NEXT_LINE);
        content.append("    percentRender").append(SYMBOL_NEXT_LINE);
        content.append("  } from '@ConsoleFundTaVue/api/bizSys/fundCommonUtil'").append(SYMBOL_NEXT_LINE);

        content.append("  import uploadExcel from '@ConsoleFundTaVue/components/uploadExcel'").append(SYMBOL_NEXT_LINE);
        content.append("  import HPanel from '@frame/components/HPanel'").append(SYMBOL_NEXT_LINE);
        content.append("  import HDatagrid from '@frame/components/HDatagrid'").append(SYMBOL_NEXT_LINE);
        content.append("  import {_pickFund, formatDate, formatPostDate, hasBtnAuth} from '@frame/api/bizSys/commonUtil'").append(SYMBOL_NEXT_LINE);
        content.append("  import multTaSelect from '@frame/components/multTaSelect'").append(SYMBOL_NEXT_LINE);
        return content.toString();
    }

    private static String buildExport(GenerateCodeDto generateCodeDto) {
        String functionCode = generateCodeDto.getFunctionCode();
        StringBuilder content = new StringBuilder();
        content.append("  export default {").append(SYMBOL_NEXT_LINE);
        content.append("    name: '" + generateCodeDto.getFunctionCode() + "',").append(SYMBOL_NEXT_LINE);
        content.append("    components: {").append(SYMBOL_NEXT_LINE);
        content.append("      DialogSelect,").append(SYMBOL_NEXT_LINE);
        content.append("      uploadExcel,").append(SYMBOL_NEXT_LINE);
        content.append("      HPanel,").append(SYMBOL_NEXT_LINE);
        content.append("      HDatagrid,").append(SYMBOL_NEXT_LINE);
        content.append("      multTaSelect").append(SYMBOL_NEXT_LINE);
        content.append("    },").append(SYMBOL_NEXT_LINE);
        content.append("    data() {").append(SYMBOL_NEXT_LINE);
        content.append("      return {").append(SYMBOL_NEXT_LINE);
        // todo data补充
        content.append("      }").append(SYMBOL_NEXT_LINE);
        content.append("    },").append(SYMBOL_NEXT_LINE);
        content.append("    computed: {},").append(SYMBOL_NEXT_LINE);
        content.append("    watch: {},").append(SYMBOL_NEXT_LINE);
        content.append("    created() {").append(SYMBOL_NEXT_LINE);
        content.append("      this.authObj.query = hasBtnAuth('" + functionCode + "$" + functionCode + "Query')").append(SYMBOL_NEXT_LINE);
        content.append("      this.authObj.add = hasBtnAuth('" + functionCode + "$" + functionCode + "Add')").append(SYMBOL_NEXT_LINE);
        content.append("      this.authObj.edit = hasBtnAuth('" + functionCode + "$" + functionCode + "Edit')").append(SYMBOL_NEXT_LINE);
        content.append("      this.authObj.delete = hasBtnAuth('" + functionCode + "$" + functionCode + "Delete')").append(SYMBOL_NEXT_LINE);
        content.append("      this.authObj.import = hasBtnAuth('" + functionCode + "$" + functionCode + "Import')").append(SYMBOL_NEXT_LINE);
        content.append("      this.authObj.export = hasBtnAuth('" + functionCode + "$" + functionCode + "Export')").append(SYMBOL_NEXT_LINE);
        content.append("    },").append(SYMBOL_NEXT_LINE);
        content.append("    mounted() {").append(SYMBOL_NEXT_LINE);
        content.append("      addTitleSearchCondition()").append(SYMBOL_NEXT_LINE);
        content.append("      this.handleResize()").append(SYMBOL_NEXT_LINE);
        content.append("      this.formSearch()").append(SYMBOL_NEXT_LINE);
        content.append("      on(window, 'resize', this.handleResize)").append(SYMBOL_NEXT_LINE);
        content.append("    },").append(SYMBOL_NEXT_LINE);
        content.append("    activated() {").append(SYMBOL_NEXT_LINE);
        content.append("      this.handleResize()").append(SYMBOL_NEXT_LINE);
        content.append("      on(window, 'resize', this.handleResize)").append(SYMBOL_NEXT_LINE);
        content.append("    },").append(SYMBOL_NEXT_LINE);
        content.append("    deactivated() {").append(SYMBOL_NEXT_LINE);
        content.append("      off(window, 'resize', this.handleResize)").append(SYMBOL_NEXT_LINE);
        content.append("    },").append(SYMBOL_NEXT_LINE);
        content.append("    beforeDestroy() {").append(SYMBOL_NEXT_LINE);
        content.append("      off(window, 'resize', this.handleResize)").append(SYMBOL_NEXT_LINE);
        content.append("    },").append(SYMBOL_NEXT_LINE);
        content.append("    methods: {").append(SYMBOL_NEXT_LINE);
        // todo 新增 修改 删除 补充
        content.append("      // 导入").append(SYMBOL_NEXT_LINE);
        content.append("      handleExcelImport() {").append(SYMBOL_NEXT_LINE);
        content.append("        this.showImport = true").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.uploadexcel.templateShow = true").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.uploadexcel.value = true").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.uploadexcel.fileName = ''").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.uploadexcel.$refs.fileinput.value = null").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 导出").append(SYMBOL_NEXT_LINE);
        content.append("      handleExcelExport(name) {").append(SYMBOL_NEXT_LINE);
        content.append("        this.$nextTick(() => {").append(SYMBOL_NEXT_LINE);
        content.append("          this.$refs.datagrid.downloadZip(name)").append(SYMBOL_NEXT_LINE);
        content.append("        })").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 查看详情").append(SYMBOL_NEXT_LINE);
        content.append("      handleOpenDetail(row) {").append(SYMBOL_NEXT_LINE);
        content.append("        openDetail(this, this.columns, row)").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 查询").append(SYMBOL_NEXT_LINE);
        content.append("      formSearch(pageNo = 1) {").append(SYMBOL_NEXT_LINE);
        content.append("        this.queryDisabled = true").append(SYMBOL_NEXT_LINE);
        content.append("        this.ids = []").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.datagrid.dataChange(1)").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 重置").append(SYMBOL_NEXT_LINE);
        content.append("      formSearchReset() {").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.searchForm.resetFields()").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 选中行").append(SYMBOL_NEXT_LINE);
        content.append("      handleSelectClick(arr) {").append(SYMBOL_NEXT_LINE);
        content.append("        this.ids = arr").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 点击分页，恢复按钮初始状态").append(SYMBOL_NEXT_LINE);
        content.append("      handlePageChange(pageNo, pageSize) {").append(SYMBOL_NEXT_LINE);
        content.append("        this.currentSelectRow = []").append(SYMBOL_NEXT_LINE);
        content.append("        this.currentSelectList = []").append(SYMBOL_NEXT_LINE);
        content.append("        this.currentSelectRowInx = []").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 自适应").append(SYMBOL_NEXT_LINE);
        content.append("      handleResize() {").append(SYMBOL_NEXT_LINE);
        content.append("        this.$nextTick(() => {").append(SYMBOL_NEXT_LINE);
        content.append("          this.$refs.datagrid.selfAdaption()").append(SYMBOL_NEXT_LINE);
        content.append("        })").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // TA代码联动").append(SYMBOL_NEXT_LINE);
        content.append("      searchChange(val) {").append(SYMBOL_NEXT_LINE);
        content.append("        this.downloadParams.multTaCode = val").append(SYMBOL_NEXT_LINE);
        content.append("      }").append(SYMBOL_NEXT_LINE);
        content.append("    },").append(SYMBOL_NEXT_LINE);
        content.append("  }").append(SYMBOL_NEXT_LINE);
        return content.toString();
    }

    private static Map<String, String> initComponent(GenerateCodeDto generateCodeDto) {
        Map<String, String> component = new HashMap<>(16);
        StringBuilder queryForm = new StringBuilder();
        StringBuilder addForm = new StringBuilder();
        StringBuilder updateForm = new StringBuilder();
        Map<String, ColumnInfoDto> columnInfo = generateCodeDto.getColumnMap();
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
            ColumnInfoDto item = columnInfo.get(columnCode);
            if (GenerateCommon.skipColumn(columnCode)) {
                continue;
            }
            String columnName = item.getColumnName();
            String columnType = item.getColumnType();
            String columnDict = item.getColumnDict();

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

    private static void getPrdCodeQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        queryForm.append("                <dialog-select").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  v-model='" + columnCode + "'").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  multiple").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  multClearable").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  type='prd'").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  apiHome=\"console-fund-ta-vue\" interFace=\"/fundPrdCodeList/fundPrdCodeListQuery\"").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  :alias=\"{value: 'prdCode', label: 'prdCode:prdName'}\"/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getPrdCodeAddUpdateForm(StringBuilder addForm, String columnCode, ColumnInfoDto item) {
        String columnMulti = item.getColumnMulti();
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

    private static void getSellerCodeQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        queryForm.append("                <simple-auto-select").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  v-model='" + columnCode + "'").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  transfer").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  multiple").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  multClearable").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  isCheckall").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  apiHome=\"console-fund-ta-vue\" interFace=\"/fundSellerCodeList/fundSellerCodeListQuery\"").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  :alias=\"{value: 'sellerCode', label: 'sellerCode:sellerName'}\"/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getSellerCodeAddUpdateForm(StringBuilder addForm, String columnCode, ColumnInfoDto item) {
        String columnMulti = item.getColumnMulti();
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

    private static void getBranchNoQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
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

    private static void getBranchNoAddUpdateForm(StringBuilder addForm, String columnCode, ColumnInfoDto item) {
        String columnMulti = item.getColumnMulti();
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

    private static void getDictQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        String columnDict = item.getColumnDict();
        queryForm.append("                <auto-select transfer multiple multClearable isCheckall v-model='" + columnCode + "'").append(SYMBOL_NEXT_LINE);
        queryForm.append("                             dictName='" + columnDict + "'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getDictAddUpdateForm(StringBuilder addForm, String columnCode, ColumnInfoDto item) {
        String columnMulti = item.getColumnMulti();
        String columnDict = item.getColumnDict();
        addForm.append("          <auto-select transfer");
        if (STR_1.equals(columnMulti)) {
            addForm.append(" multiple multClearable isCheckall");
        }
        addForm.append(" v-model='" + columnCode + "' dictName='" + columnDict + "'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getDateQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        queryForm.append("                <h-date-picker transfer type='daterange' v-model='" + columnCode + "' autoPlacement />").append(SYMBOL_NEXT_LINE);
    }

    private static void getDateAddUpdateForm(StringBuilder addForm, String columnCode, ColumnInfoDto item) {
        addForm.append("          <h-date-picker transfer type='date' v-model='" + columnCode + "' autoPlacement />").append(SYMBOL_NEXT_LINE);
    }

    private static void getNumberQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        queryForm.append("                <scope-input v-model='" + columnCode + "' suffixNum='-1' placeholder='请输入'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getNumberAddUpdateForm(StringBuilder addForm, String columnCode, ColumnInfoDto item) {
        addForm.append("          <h-input v-model='" + columnCode + "'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getTextQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        queryForm.append("                <h-input v-model='" + columnCode + "'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getTextAddUpdateForm(StringBuilder addForm, String columnCode, ColumnInfoDto item) {
        addForm.append("          <h-input v-model='" + columnCode + "'/>").append(SYMBOL_NEXT_LINE);
    }
}
