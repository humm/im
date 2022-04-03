package com.hoomoomoo.im.service;

import com.hoomoomoo.im.dto.ColumnInfoDto;
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
        content.append("            <h-form :model=\"searchForm\" :label-width=\"120\" ref=\"searchForm\" :cols=\"manualForm.cols\">").append(SYMBOL_NEXT_LINE);
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
        content.append("      <h-form label-width=\"170\" cols=\"2\" ref=\"addForm\" :model=\"addForm\" :rules=\"validateRules\">").append(SYMBOL_NEXT_LINE);
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
        content.append("      <h-form label-width=\"170\" cols=\"2\" ref=\"updateForm\" :model=\"updateForm\" :rules=\"validateRules\">").append(SYMBOL_NEXT_LINE);
        content.append(component.get(KEY_UPDATE_FORM));
        content.append("      </h-form>").append(SYMBOL_NEXT_LINE);
        content.append("      <div slot='footer'>").append(SYMBOL_NEXT_LINE);
        content.append("        <h-button type='ghost' @click='handleUpdateCancel()'>取消</h-button>").append(SYMBOL_NEXT_LINE);
        content.append("        <h-button type='primary' @click='handleUpdateSubmit()'>提交</h-button>").append(SYMBOL_NEXT_LINE);
        content.append("      </div>").append(SYMBOL_NEXT_LINE);
        content.append("      <h-spin size='large' fix v-if='spinEditShow'></h-spin>").append(SYMBOL_NEXT_LINE);
        content.append("    </h-msg-box>").append(SYMBOL_NEXT_LINE_2);
        if (GenerateCommon.hasComponent(generateCodeDto, STR_3)) {
            content.append("    <!-- 批量修改弹框 -->").append(SYMBOL_NEXT_LINE);
            content.append("    <h-msg-box width=\"800\" v-model=\"showBatchEditModal\" title=\"批量修改\">").append(SYMBOL_NEXT_LINE);
            content.append("      <h-form label-width=\"170\" cols=\"2\" ref=\"batchUpdateForm\" :model=\"batchUpdateForm\" :rules=\"validateRulesBatch\">").append(SYMBOL_NEXT_LINE);
            content.append(component.get(KEY_BATCH_UPDATE_FORM));
            content.append("      </h-form>").append(SYMBOL_NEXT_LINE);
            content.append("      <div slot='footer'>").append(SYMBOL_NEXT_LINE);
            content.append("        <h-button type='ghost' @click='handleBatchUpdateCancel()'>取消</h-button>").append(SYMBOL_NEXT_LINE);
            content.append("        <h-button type='primary' @click='handleBatchUpdateSubmit()'>提交</h-button>").append(SYMBOL_NEXT_LINE);
            content.append("      </div>").append(SYMBOL_NEXT_LINE);
            content.append("      <h-spin size='large' fix v-if='spinBatchEditShow'></h-spin>").append(SYMBOL_NEXT_LINE);
            content.append("    </h-msg-box>").append(SYMBOL_NEXT_LINE_2);
        }
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
        content.append("    fundPost,").append(SYMBOL_NEXT_LINE);
        content.append("    getValidator,").append(SYMBOL_NEXT_LINE);
        content.append("    addTitleSearchCondition,").append(SYMBOL_NEXT_LINE);
        content.append("    selectCurrentRow,").append(SYMBOL_NEXT_LINE);
        content.append("    openDetail,").append(SYMBOL_NEXT_LINE);
        content.append("    translateRender,").append(SYMBOL_NEXT_LINE);
        content.append("    dateRender,").append(SYMBOL_NEXT_LINE);
        content.append("    amountRender,").append(SYMBOL_NEXT_LINE);
        if (GenerateCommon.hasComponent(generateCodeDto, STR_2)) {
            content.append("    percentRender,").append(SYMBOL_NEXT_LINE);
        }
        if (GenerateCommon.hasComponent(generateCodeDto, STR_5) && !GenerateCommon.hasComponent(generateCodeDto, STR_4)) {
            content.append("    getJsonCartesian,").append(SYMBOL_NEXT_LINE);
            content.append("    multiCartesian,").append(SYMBOL_NEXT_LINE);
        }
        content.append("  } from '@ConsoleFundTaVue/api/bizSys/fundCommonUtil'").append(SYMBOL_NEXT_LINE);
        content.append("  import uploadExcel from '@ConsoleFundTaVue/components/uploadExcel'").append(SYMBOL_NEXT_LINE);
        content.append("  import HPanel from '@frame/components/HPanel'").append(SYMBOL_NEXT_LINE);
        content.append("  import HDatagrid from '@frame/components/HDatagrid'").append(SYMBOL_NEXT_LINE);
        content.append("  import {_pickFund, formatDate, formatDateRange, formatPostDate, hasBtnAuth,  off, on} from '@frame/api/bizSys/commonUtil'").append(SYMBOL_NEXT_LINE);
        content.append("  import multTaSelect from '@frame/components/multTaSelect'").append(SYMBOL_NEXT_LINE);
        if (GenerateCommon.hasComponent(generateCodeDto, STR_1)) {
            content.append("  import DialogSelect from '@ConsoleFundTaVue/components/DialogSelect'").append(SYMBOL_NEXT_LINE);
        }
        if (GenerateCommon.hasComponent(generateCodeDto, STR_2)) {
            content.append("  import HPercentInput from '@frame/components/HPercentInput'").append(SYMBOL_NEXT_LINE);
        }

        return content.toString();
    }

    private static String buildExport(GenerateCodeDto generateCodeDto) {
        String functionCode = generateCodeDto.getFunctionCode();
        StringBuilder content = new StringBuilder();
        content.append("  export default {").append(SYMBOL_NEXT_LINE);
        content.append("    name: '" + generateCodeDto.getFunctionCode() + "',").append(SYMBOL_NEXT_LINE);
        content.append("    components: {").append(SYMBOL_NEXT_LINE);
        content.append("      multTaSelect,").append(SYMBOL_NEXT_LINE);
        content.append("      uploadExcel,").append(SYMBOL_NEXT_LINE);
        content.append("      HPanel,").append(SYMBOL_NEXT_LINE);
        content.append("      HDatagrid,").append(SYMBOL_NEXT_LINE);
        if (GenerateCommon.hasComponent(generateCodeDto, STR_1)) {
            content.append("      DialogSelect,").append(SYMBOL_NEXT_LINE);
        }
        if (GenerateCommon.hasComponent(generateCodeDto, STR_2)) {
            content.append("      HPercentInput,").append(SYMBOL_NEXT_LINE);
        }
        content.append("    },").append(SYMBOL_NEXT_LINE);
        content.append("    data() {").append(SYMBOL_NEXT_LINE);
        content.append("      const commonValidator = getValidator(this, ['showModal', 'addForm', 'updateForm'], 2)").append(SYMBOL_NEXT_LINE);
        if (GenerateCommon.hasComponent(generateCodeDto, STR_3)) {
            content.append("      const batchUpdateFormValidator = getValidator(this, 'batchUpdateForm')").append(SYMBOL_NEXT_LINE);
        }
        content.append("      return {").append(SYMBOL_NEXT_LINE);
        content.append("        authObj: {").append(SYMBOL_NEXT_LINE);
        content.append("          query: true,").append(SYMBOL_NEXT_LINE);
        content.append("          add: true,").append(SYMBOL_NEXT_LINE);
        content.append("          edit: true,").append(SYMBOL_NEXT_LINE);
        content.append("          delete: true,").append(SYMBOL_NEXT_LINE);
        content.append("          import: true,").append(SYMBOL_NEXT_LINE);
        content.append("          export: true").append(SYMBOL_NEXT_LINE);
        content.append("        },").append(SYMBOL_NEXT_LINE);
        content.append("        dataSource: JSON.parse(window.sessionStorage.getItem('multTaList')),").append(SYMBOL_NEXT_LINE);
        content.append("        isMultTa: window.LOCAL_CONFIG.isMultTa,").append(SYMBOL_NEXT_LINE);
        content.append("        showImport: false,").append(SYMBOL_NEXT_LINE);
        content.append("        validateRules: {").append(SYMBOL_NEXT_LINE);
        content.append(getValidateRules(generateCodeDto.getColumnMap(), STR_1));
        content.append("        },").append(SYMBOL_NEXT_LINE);
        if (GenerateCommon.hasComponent(generateCodeDto, STR_3)) {
            content.append("        validateRulesBatch: {").append(SYMBOL_NEXT_LINE);
            content.append(getValidateRules(generateCodeDto.getColumnMap(), STR_2));
            content.append("        },").append(SYMBOL_NEXT_LINE);
        }
        content.append("        spinShow: false,").append(SYMBOL_NEXT_LINE);
        content.append("        spinEditShow: false,").append(SYMBOL_NEXT_LINE);
        content.append("        spinBatchEditShow: false,").append(SYMBOL_NEXT_LINE);
        content.append("        tableRef: 'selfTable', ").append(SYMBOL_NEXT_LINE);
        content.append("        ids: [], ").append(SYMBOL_NEXT_LINE);
        content.append("        queryDisabled: false,").append(SYMBOL_NEXT_LINE);
        content.append("        pageSizeOpts: [5, 10, 15, 20],").append(SYMBOL_NEXT_LINE);
        content.append("        showModal: false,").append(SYMBOL_NEXT_LINE);
        content.append("        showEditModal: false,").append(SYMBOL_NEXT_LINE);
        content.append("        showBatchEditModal: false,").append(SYMBOL_NEXT_LINE);
        content.append("        dataList: [],").append(SYMBOL_NEXT_LINE);
        content.append("        downloadParams: {").append(SYMBOL_NEXT_LINE);
        content.append("          resCode: '" + generateCodeDto.getFunctionCode() + "',").append(SYMBOL_NEXT_LINE);
        content.append("          opCode: '" + generateCodeDto.getFunctionCode() + "Export',").append(SYMBOL_NEXT_LINE);
        content.append("          title: '" + generateCodeDto.getFunctionName() + "',").append(SYMBOL_NEXT_LINE);
        content.append("          serviceName: '" + generateCodeDto.getServicePackageName() + ".queryService'").append(SYMBOL_NEXT_LINE);
        content.append("        },").append(SYMBOL_NEXT_LINE);
        content.append("        searchForm: {").append(SYMBOL_NEXT_LINE);
        content.append(buildColumn(generateCodeDto.getColumnMap(), STR_1));
        content.append("        },").append(SYMBOL_NEXT_LINE);
        content.append("        addForm: {").append(SYMBOL_NEXT_LINE);
        content.append(buildColumn(generateCodeDto.getColumnMap(), STR_2));
        content.append("        },").append(SYMBOL_NEXT_LINE);
        content.append("        updateForm: {").append(SYMBOL_NEXT_LINE);
        content.append(buildColumn(generateCodeDto.getColumnMap(), STR_3));
        content.append("        },").append(SYMBOL_NEXT_LINE);
        if (GenerateCommon.hasComponent(generateCodeDto, STR_3)) {
            content.append("        batchUpdateForm: {").append(SYMBOL_NEXT_LINE);
            content.append(buildColumn(generateCodeDto.getColumnMap(), STR_5));
            content.append("        },").append(SYMBOL_NEXT_LINE);
        }
        content.append("        addAndUpdateRules: {").append(SYMBOL_NEXT_LINE);
        content.append("        },").append(SYMBOL_NEXT_LINE);
        content.append("        manualForm: {").append(SYMBOL_NEXT_LINE);
        content.append("          cols: 3").append(SYMBOL_NEXT_LINE);
        content.append("        },").append(SYMBOL_NEXT_LINE);
        content.append("        columns: [").append(SYMBOL_NEXT_LINE);
        content.append(buildColumn(generateCodeDto.getColumnMap(), STR_4));
        content.append("        ],").append(SYMBOL_NEXT_LINE);
        content.append("      }").append(SYMBOL_NEXT_LINE);
        content.append("    },").append(SYMBOL_NEXT_LINE);
        content.append("    computed: {").append(SYMBOL_NEXT_LINE);
        content.append("      searchParams() {").append(SYMBOL_NEXT_LINE);
        content.append(getSearchParams(generateCodeDto.getColumnMap()));
        content.append("      }").append(SYMBOL_NEXT_LINE);
        content.append("    },").append(SYMBOL_NEXT_LINE);
        content.append("    watch: {},").append(SYMBOL_NEXT_LINE);
        content.append("    created() {").append(SYMBOL_NEXT_LINE);
        content.append("      this.authObj.query = hasBtnAuth('" + functionCode + "$" + functionCode + "Query')").append(SYMBOL_NEXT_LINE);
        content.append("      this.authObj.add = hasBtnAuth('" + functionCode + "$" + functionCode + "Add')").append(SYMBOL_NEXT_LINE);
        if (GenerateCommon.hasComponent(generateCodeDto, STR_3)) {
            content.append("      this.authObj.edit = hasBtnAuth('" + functionCode + "$" + functionCode + "Edit')").append(SYMBOL_NEXT_LINE);
        }
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
        content.append("      // 导入").append(SYMBOL_NEXT_LINE);
        content.append("      handleExcelImport() {").append(SYMBOL_NEXT_LINE);
        content.append("        this.showImport = true").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.uploadExcel.templateShow = true").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.uploadExcel.value = true").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.uploadExcel.fileName = ''").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.uploadExcel.$refs.fileinput.value = null").append(SYMBOL_NEXT_LINE);
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
        content.append("        this.$refs.datagrid.dataChange(pageNo)").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 重置").append(SYMBOL_NEXT_LINE);
        content.append("      formSearchReset() {").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.searchForm.resetFields()").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 打开新增弹框").append(SYMBOL_NEXT_LINE);
        content.append("      handleOpenAdd() {").append(SYMBOL_NEXT_LINE);
        content.append("        this.showModal = true").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.addForm.resetFields()").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 取消新增弹框").append(SYMBOL_NEXT_LINE);
        content.append("      handleCancel() {").append(SYMBOL_NEXT_LINE);
        content.append("        this.showModal = false").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 新增提交").append(SYMBOL_NEXT_LINE);
        content.append("      handleAddSubmit() {").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.addForm.validate(valid => {").append(SYMBOL_NEXT_LINE);
        content.append("          if (valid) {").append(SYMBOL_NEXT_LINE);
        content.append("            this.spinShow = !this.spinShow").append(SYMBOL_NEXT_LINE);
        content.append("            const postData = Object.assign({}, this.addForm)").append(SYMBOL_NEXT_LINE);
        content.append(convertSubmitColumn(generateCodeDto.getColumnMap()));
        if (GenerateCommon.hasComponent(generateCodeDto, STR_5) && !GenerateCommon.hasComponent(generateCodeDto, STR_4)) {
            content.append("            const dtoListData = JSON.stringify(multiCartesian(getJsonCartesian(postData)))").append(SYMBOL_NEXT_LINE);
        } else {
            content.append("            const dtoListData = '[' + JSON.stringify(postData) + ']'").append(SYMBOL_NEXT_LINE);
        }
        content.append("            fundPost(this, {dtoList: dtoListData, multTaCode: postData.multTaCode, feEditType: 'ADD'}, '" + generateCodeDto.getFunctionCode() + "/" + generateCodeDto.getFunctionCode() + "Add').then(").append(SYMBOL_NEXT_LINE);
        content.append("              ({data}) => {").append(SYMBOL_NEXT_LINE);
        content.append("                this.spinShow = !this.spinShow").append(SYMBOL_NEXT_LINE);
        content.append("                if (data.returnCode === '0') {").append(SYMBOL_NEXT_LINE);
        content.append("                  this.showModal = false").append(SYMBOL_NEXT_LINE);
        content.append("                  this.searchForm.multTaCode = this.addForm.multTaCode").append(SYMBOL_NEXT_LINE);
        content.append("                  this.formSearch()").append(SYMBOL_NEXT_LINE);
        content.append("                }").append(SYMBOL_NEXT_LINE);
        content.append("              }").append(SYMBOL_NEXT_LINE);
        content.append("            ).catch(error => {").append(SYMBOL_NEXT_LINE);
        content.append("              this.spinShow = false;").append(SYMBOL_NEXT_LINE);
        content.append("              this.$hMessage.error({").append(SYMBOL_NEXT_LINE);
        content.append("                content: error.message,").append(SYMBOL_NEXT_LINE);
        content.append("                closable: true,").append(SYMBOL_NEXT_LINE);
        content.append("                duration: window.LOCAL_CONFIG.errorTime,").append(SYMBOL_NEXT_LINE);
        content.append("              });").append(SYMBOL_NEXT_LINE);
        content.append("            });").append(SYMBOL_NEXT_LINE);
        content.append("          }").append(SYMBOL_NEXT_LINE);
        content.append("        })").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 打开修改弹框").append(SYMBOL_NEXT_LINE);
        content.append("      handleOpenEdit(data) {").append(SYMBOL_NEXT_LINE);
        content.append("        selectCurrentRow(this, data)").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.updateForm.resetFields()").append(SYMBOL_NEXT_LINE);
        content.append("        this.showEditModal = true").append(SYMBOL_NEXT_LINE);
        content.append("        const editObj = Object.assign(").append(SYMBOL_NEXT_LINE);
        content.append("          {},").append(SYMBOL_NEXT_LINE);
        content.append("          _pickFund(data, [").append(SYMBOL_NEXT_LINE);
        content.append(getUpdateColumn(generateCodeDto.getColumnMap())).append(SYMBOL_NEXT_LINE);
        content.append("          ])").append(SYMBOL_NEXT_LINE);
        content.append("        )").append(SYMBOL_NEXT_LINE);
        content.append(convertShowColumn(generateCodeDto.getColumnMap()));
        content.append("        this.updateForm = editObj").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 取消修改").append(SYMBOL_NEXT_LINE);
        content.append("      handleUpdateCancel() {").append(SYMBOL_NEXT_LINE);
        content.append("        this.showEditModal = false").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        content.append("      // 提交修改").append(SYMBOL_NEXT_LINE);
        content.append("      handleUpdateSubmit() {").append(SYMBOL_NEXT_LINE);
        content.append("        this.$refs.updateForm.validate(valid => {").append(SYMBOL_NEXT_LINE);
        content.append("          if (valid) {").append(SYMBOL_NEXT_LINE);
        content.append("            this.spinEditShow = !this.spinEditShow").append(SYMBOL_NEXT_LINE);
        content.append("            const postData = Object.assign({}, this.updateForm)").append(SYMBOL_NEXT_LINE);
        content.append(convertSubmitColumn(generateCodeDto.getColumnMap()));
        content.append("            fundPost(this, {dtoList: '[' + JSON.stringify(postData) + ']', multTaCode: postData.multTaCode, feEditType: 'EDIT'},").append(SYMBOL_NEXT_LINE);
        content.append("              '" + generateCodeDto.getFunctionCode() + "/" + generateCodeDto.getFunctionCode() + "Edit').then(").append(SYMBOL_NEXT_LINE);
        content.append("              ({data}) => {").append(SYMBOL_NEXT_LINE);
        content.append("                this.spinEditShow = !this.spinEditShow").append(SYMBOL_NEXT_LINE);
        content.append("                if (data.returnCode === '0') {").append(SYMBOL_NEXT_LINE);
        content.append("                  this.showEditModal = false").append(SYMBOL_NEXT_LINE);
        content.append("                  this.formSearch(this.$refs.datagrid.pageInfo.pageNo)").append(SYMBOL_NEXT_LINE);
        content.append("                }").append(SYMBOL_NEXT_LINE);
        content.append("              }").append(SYMBOL_NEXT_LINE);
        content.append("            ).catch(error => {").append(SYMBOL_NEXT_LINE);
        content.append("              this.spinEditShow = false;").append(SYMBOL_NEXT_LINE);
        content.append("              this.$hMessage.error({").append(SYMBOL_NEXT_LINE);
        content.append("                content: error.message,").append(SYMBOL_NEXT_LINE);
        content.append("                closable: true,").append(SYMBOL_NEXT_LINE);
        content.append("                duration: window.LOCAL_CONFIG.errorTime,").append(SYMBOL_NEXT_LINE);
        content.append("              });").append(SYMBOL_NEXT_LINE);
        content.append("            });").append(SYMBOL_NEXT_LINE);
        content.append("          }").append(SYMBOL_NEXT_LINE);
        content.append("        })").append(SYMBOL_NEXT_LINE);
        content.append("      },").append(SYMBOL_NEXT_LINE);
        if (GenerateCommon.hasComponent(generateCodeDto, STR_3)) {
            content.append("      // 打开批量修改弹窗").append(SYMBOL_NEXT_LINE);
            content.append("      handleOpenBatchEdit(row) {").append(SYMBOL_NEXT_LINE);
            content.append("        // 未选择修改的数据").append(SYMBOL_NEXT_LINE);
            content.append("        if (!row && this.ids.length === 0) {").append(SYMBOL_NEXT_LINE);
            content.append("          this.$hMessage.info({").append(SYMBOL_NEXT_LINE);
            content.append("            content: this.$t('m.i.common.chooseOneData'),").append(SYMBOL_NEXT_LINE);
            content.append("            duration: 3,").append(SYMBOL_NEXT_LINE);
            content.append("            closable: true").append(SYMBOL_NEXT_LINE);
            content.append("          })").append(SYMBOL_NEXT_LINE);
            content.append("          return false").append(SYMBOL_NEXT_LINE);
            content.append("        }").append(SYMBOL_NEXT_LINE);
            content.append("        if (this.ids.length === 1) {").append(SYMBOL_NEXT_LINE);
            content.append("          this.handleOpenEdit(this.ids[0])").append(SYMBOL_NEXT_LINE);
            content.append("        } else {").append(SYMBOL_NEXT_LINE);
            content.append("          this.$refs.batchUpdateForm.resetFields()").append(SYMBOL_NEXT_LINE);
            content.append("          this.showBatchEditModal = true").append(SYMBOL_NEXT_LINE);
            content.append("          this.batchUpdateForm.multTaCode = this.ids[0].multTaCode").append(SYMBOL_NEXT_LINE);
            content.append("        }").append(SYMBOL_NEXT_LINE);
            content.append("      },").append(SYMBOL_NEXT_LINE);
            content.append("      // 取消批量修改").append(SYMBOL_NEXT_LINE);
            content.append("      handleBatchUpdateCancel() {").append(SYMBOL_NEXT_LINE);
            content.append("        this.showBatchEditModal = false").append(SYMBOL_NEXT_LINE);
            content.append("      },").append(SYMBOL_NEXT_LINE);
            content.append("      // 提交批量修改").append(SYMBOL_NEXT_LINE);
            content.append("      handleBatchUpdateSubmit() {").append(SYMBOL_NEXT_LINE);
            content.append("        this.$refs.batchUpdateForm.validate(valid => {").append(SYMBOL_NEXT_LINE);
            content.append("          if (valid) {").append(SYMBOL_NEXT_LINE);
            content.append("            this.spinBatchEditShow = !this.spinBatchEditShow").append(SYMBOL_NEXT_LINE);
            content.append(convertSubmitBatchColumn(generateCodeDto.getColumnMap())).append(SYMBOL_NEXT_LINE);
            content.append("            fundPost(this, {dtoList: JSON.stringify(this.ids), multTaCode: this.ids[0].multTaCode, feEditType: 'EDIT'},").append(SYMBOL_NEXT_LINE);
            content.append("              '" + generateCodeDto.getFunctionCode() + "/" + generateCodeDto.getFunctionCode() + "Edit').then(").append(SYMBOL_NEXT_LINE);
            content.append("              ({data}) => {").append(SYMBOL_NEXT_LINE);
            content.append("                this.spinBatchEditShow = !this.spinBatchEditShow").append(SYMBOL_NEXT_LINE);
            content.append("                if (data.returnCode === '0') {").append(SYMBOL_NEXT_LINE);
            content.append("                  this.showBatchEditModal = false").append(SYMBOL_NEXT_LINE);
            content.append("                  this.formSearch(this.$refs.datagrid.pageInfo.pageNo)").append(SYMBOL_NEXT_LINE);
            content.append("                }").append(SYMBOL_NEXT_LINE);
            content.append("              }").append(SYMBOL_NEXT_LINE);
            content.append("            ).catch(error => {").append(SYMBOL_NEXT_LINE);
            content.append("              this.spinBatchEditShow = false;").append(SYMBOL_NEXT_LINE);
            content.append("              this.$hMessage.error({").append(SYMBOL_NEXT_LINE);
            content.append("                content: error.message,").append(SYMBOL_NEXT_LINE);
            content.append("                closable: true,").append(SYMBOL_NEXT_LINE);
            content.append("                duration: window.LOCAL_CONFIG.errorTime,").append(SYMBOL_NEXT_LINE);
            content.append("              });").append(SYMBOL_NEXT_LINE);
            content.append("            });").append(SYMBOL_NEXT_LINE);
            content.append("          }").append(SYMBOL_NEXT_LINE);
            content.append("        })").append(SYMBOL_NEXT_LINE);
            content.append("      },").append(SYMBOL_NEXT_LINE);
        }
        content.append("      // 打开删除弹框").append(SYMBOL_NEXT_LINE);
        content.append("      handleDelete(row) {").append(SYMBOL_NEXT_LINE);
        content.append("        // 未选择删除的数据").append(SYMBOL_NEXT_LINE);
        content.append("        if (!row && this.ids.length === 0) {").append(SYMBOL_NEXT_LINE);
        content.append("          this.$hMessage.info({").append(SYMBOL_NEXT_LINE);
        content.append("            content: this.$t('m.i.common.chooseOneData'),").append(SYMBOL_NEXT_LINE);
        content.append("            duration: 3,").append(SYMBOL_NEXT_LINE);
        content.append("            closable: true").append(SYMBOL_NEXT_LINE);
        content.append("          })").append(SYMBOL_NEXT_LINE);
        content.append("          return false").append(SYMBOL_NEXT_LINE);
        content.append("        }").append(SYMBOL_NEXT_LINE);
        content.append("        this.$hMsgBox.confirm({").append(SYMBOL_NEXT_LINE);
        content.append("          title: '确认删除该数据？',").append(SYMBOL_NEXT_LINE);
        content.append("          onOk: () => {").append(SYMBOL_NEXT_LINE);
        content.append("            fundPost(this, {").append(SYMBOL_NEXT_LINE);
        content.append("              dtoList: JSON.stringify(row ? [row] : this.ids),").append(SYMBOL_NEXT_LINE);
        content.append("              multTaCode: this.ids[0].multTaCode,").append(SYMBOL_NEXT_LINE);
        content.append("              feEditType: 'DEL'").append(SYMBOL_NEXT_LINE);
        content.append("            }, '" + generateCodeDto.getFunctionCode() + "/" + generateCodeDto.getFunctionCode() + "Delete').then(").append(SYMBOL_NEXT_LINE);
        content.append("              ({data}) => {").append(SYMBOL_NEXT_LINE);
        content.append("                if (data.returnCode !== '0') {").append(SYMBOL_NEXT_LINE);
        content.append("                  return false").append(SYMBOL_NEXT_LINE);
        content.append("                }").append(SYMBOL_NEXT_LINE);
        content.append("                this.formSearch()").append(SYMBOL_NEXT_LINE);
        content.append("              }").append(SYMBOL_NEXT_LINE);
        content.append("            ).catch(error => {").append(SYMBOL_NEXT_LINE);
        content.append("              this.spinShow = false;").append(SYMBOL_NEXT_LINE);
        content.append("              this.$hMessage.error({").append(SYMBOL_NEXT_LINE);
        content.append("                content: error.message,").append(SYMBOL_NEXT_LINE);
        content.append("                closable: true,").append(SYMBOL_NEXT_LINE);
        content.append("                duration: window.LOCAL_CONFIG.errorTime,").append(SYMBOL_NEXT_LINE);
        content.append("              });").append(SYMBOL_NEXT_LINE);
        content.append("           });").append(SYMBOL_NEXT_LINE);
        content.append("         }").append(SYMBOL_NEXT_LINE);
        content.append("        })").append(SYMBOL_NEXT_LINE);
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
        StringBuilder batchUpdateForm = new StringBuilder();
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

        batchUpdateForm.append("        <h-form-item label='TA代码' prop='multTaCode' required v-if='isMultTa'>").append(SYMBOL_NEXT_LINE);
        batchUpdateForm.append("          <mult-ta-select v-model='batchUpdateForm.multTaCode' disabled/>").append(SYMBOL_NEXT_LINE);
        batchUpdateForm.append("        </h-form-item>").append(SYMBOL_NEXT_LINE);

        while (iterator.hasNext()) {
            String columnCode = iterator.next();
            ColumnInfoDto item = columnInfo.get(columnCode);
            if (GenerateCommon.skipColumn(item)) {
                continue;
            }
            String columnName = item.getColumnName();
            String columnDate = item.getColumnDate();
            String columnDict = item.getColumnDict();
            String columnType = item.getColumnType();
            if (columnQuery(item)) {
                queryForm.append("              <h-form-item label='" + columnName + "' prop='" + columnCode + "'>").append(SYMBOL_NEXT_LINE);
            }
            if (columnBatchUpdate(item)) {
                batchUpdateForm.append("        <h-form-item label='" + columnName + "' prop='" + columnCode + "'>").append(SYMBOL_NEXT_LINE);
            }
            addForm.append("        <h-form-item label='" + columnName + "' prop='" + columnCode + "'>").append(SYMBOL_NEXT_LINE);
            updateForm.append("        <h-form-item label='" + columnName + "' prop='" + columnCode + "'>").append(SYMBOL_NEXT_LINE);

            if (columnCode.contains(KEY_PRD_CODE)) {
                getPrdCodeQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getPrdCodeAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getPrdCodeAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
                getPrdCodeAddUpdateForm(batchUpdateForm, KEY_BATCH_UPDATE_FORM + columnCode, item);
            } else if (columnCode.contains(KEY_SELLER_CODE)) {
                getSellerCodeQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getSellerCodeAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getSellerCodeAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
                getSellerCodeAddUpdateForm(batchUpdateForm, KEY_BATCH_UPDATE_FORM + columnCode, item);
            } else if (columnCode.contains(KEY_BRANCH_NO)) {
                getBranchNoQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getBranchNoAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getBranchNoAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
                getBranchNoAddUpdateForm(batchUpdateForm, KEY_BATCH_UPDATE_FORM + columnCode, item);
            } else if (StringUtils.isNotBlank(columnDict)) {
                getDictQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getDictAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getDictAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
                getDictAddUpdateForm(batchUpdateForm, KEY_BATCH_UPDATE_FORM + columnCode, item);
            } else if (columnName.contains(SYMBOL_PERCENT)) {
                getRateQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getRateAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getRateAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
                getRateAddUpdateForm(batchUpdateForm, KEY_BATCH_UPDATE_FORM + columnCode, item);
            } else if (STR_1.equals(columnDate)) {
                getDateQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getDateAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getDateAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
                getDateAddUpdateForm(batchUpdateForm, KEY_BATCH_UPDATE_FORM + columnCode, item);
            } else if (KEY_COLUMN_TYPE_NUMBER.equals(columnType) || KEY_COLUMN_TYPE_INTEGER.equals(columnType)) {
                getNumberQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getNumberAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getNumberAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
                getNumberAddUpdateForm(batchUpdateForm, KEY_BATCH_UPDATE_FORM + columnCode, item);
            } else {
                getTextQueryForm(queryForm, KEY_SEARCH_FORM + columnCode, item);
                getTextAddUpdateForm(addForm, KEY_ADD_FORM + columnCode, item);
                getTextAddUpdateForm(updateForm, KEY_UPDATE_FORM + columnCode, item);
                getTextAddUpdateForm(batchUpdateForm, KEY_BATCH_UPDATE_FORM + columnCode, item);
            }

            if (columnQuery(item)) {
                queryForm.append("              </h-form-item>").append(SYMBOL_NEXT_LINE);
            }
            if (columnBatchUpdate(item)) {
                batchUpdateForm.append("        </h-form-item>").append(SYMBOL_NEXT_LINE);
            }
            addForm.append("        </h-form-item>").append(SYMBOL_NEXT_LINE);
            updateForm.append("        </h-form-item>").append(SYMBOL_NEXT_LINE);
        }
        component.put(KEY_SEARCH_FORM, queryForm.toString());
        component.put(KEY_ADD_FORM, addForm.toString());
        component.put(KEY_UPDATE_FORM, updateForm.toString());
        component.put(KEY_BATCH_UPDATE_FORM, batchUpdateForm.toString());
        return component;
    }

    private static void getPrdCodeQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        if (!columnQuery(item)) {
            return;
        }
        queryForm.append("                <dialog-select").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  v-model='" + columnCode + "'").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  multiple").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  multClearable").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  type='prd'").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  apiHome=\"console-fund-ta-vue\" interFace=\"/fundPrdCodeList/fundPrdCodeListQuery\"").append(SYMBOL_NEXT_LINE);
        queryForm.append("                  :alias=\"{value: 'prdCode', label: 'prdCode:prdName'}\"/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getPrdCodeAddUpdateForm(StringBuilder addForm, String columnCode, ColumnInfoDto item) {
        if (columnCode.startsWith(KEY_BATCH_UPDATE_FORM) && !columnBatchUpdate(item)) {
           return;
        }
        String dict = "/fundPrdCodeList/fundPrdCodeListQuery";
        if (StringUtils.isNotBlank(item.getColumnDict())) {
            dict = item.getColumnDict();
        }
        String columnMulti = item.getColumnMulti();
        addForm.append("          <dialog-select").append(SYMBOL_NEXT_LINE);
        addForm.append("            v-model='" + columnCode + "'").append(SYMBOL_NEXT_LINE);
        if (STR_1.equals(columnMulti)) {
            addForm.append("            multiple").append(SYMBOL_NEXT_LINE);
            addForm.append("            multClearable").append(SYMBOL_NEXT_LINE);
        }
        if (columnDisabled(addForm, item)) {
            addForm.append("            disabled").append(SYMBOL_NEXT_LINE);
        }
        addForm.append("            type='prd'").append(SYMBOL_NEXT_LINE);
        addForm.append("            :params=\"{prdStatus: '6,9'}\"").append(SYMBOL_NEXT_LINE);
        addForm.append("            apiHome=\"console-fund-ta-vue\" interFace=\"" + dict + "\"").append(SYMBOL_NEXT_LINE);
        addForm.append("            :alias=\"{value: 'prdCode', label: 'prdCode:prdName'}\"/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getSellerCodeQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        if (!columnQuery(item)) {
            return;
        }
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
        if (columnCode.startsWith(KEY_BATCH_UPDATE_FORM) && !columnBatchUpdate(item)) {
            return;
        }
        String dict = "/fundSellerCodeList/fundSellerCodeListQuery";
        if (StringUtils.isNotBlank(item.getColumnDict())) {
            dict = item.getColumnDict();
        }
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
        if (columnDisabled(addForm, item)) {
            addForm.append("            disabled").append(SYMBOL_NEXT_LINE);
        }
        addForm.append("            apiHome=\"console-fund-ta-vue\" interFace=\"" + dict + "\"").append(SYMBOL_NEXT_LINE);
        addForm.append("            :alias=\"{value: 'sellerCode', label: 'sellerCode:sellerName'}\"/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getBranchNoQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        if (!columnQuery(item)) {
            return;
        }
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
        if (columnCode.startsWith(KEY_BATCH_UPDATE_FORM) && !columnBatchUpdate(item)) {
            return;
        }
        String dict = "/fundNetInfoList/fundNetInfoListQuery";
        if (StringUtils.isNotBlank(item.getColumnDict())) {
            dict = item.getColumnDict();
        }
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
        if (columnDisabled(addForm, item)) {
            addForm.append("            disabled").append(SYMBOL_NEXT_LINE);
        }
        addForm.append("            :params=\"{'sellerCode':" + columnCode.replace(KEY_BRANCH_NO, KEY_SELLER_CODE) + "}\"").append(SYMBOL_NEXT_LINE);
        addForm.append("            apiHome=\"console-fund-ta-vue\" interFace=\"" + dict + "\"").append(SYMBOL_NEXT_LINE);
        addForm.append("            :alias=\"{value: 'branchNo', label: 'branchNo:branchName'}\"/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getDictQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        if (!columnQuery(item)) {
            return;
        }
        String columnDict = item.getColumnDict();
        queryForm.append("                <auto-select transfer multiple multClearable isCheckall v-model='" + columnCode + "'").append(SYMBOL_NEXT_LINE);
        queryForm.append("                             dictName='" + columnDict + "'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getDictAddUpdateForm(StringBuilder addForm, String columnCode, ColumnInfoDto item) {
        if (columnCode.startsWith(KEY_BATCH_UPDATE_FORM) && !columnBatchUpdate(item)) {
            return;
        }
        String columnMulti = item.getColumnMulti();
        String columnDict = item.getColumnDict();
        addForm.append("          <auto-select transfer");
        if (STR_1.equals(columnMulti)) {
            addForm.append(" multiple multClearable isCheckall");
        }
        if (columnDisabled(addForm, item)) {
            addForm.append(" disabled");
        }
        addForm.append(" v-model='" + columnCode + "' dictName='" + columnDict + "'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getDateQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        if (!columnQuery(item)) {
            return;
        }
        queryForm.append("                <h-date-picker transfer type='daterange' v-model='" + columnCode + "' autoPlacement />").append(SYMBOL_NEXT_LINE);
    }

    private static void getDateAddUpdateForm(StringBuilder addForm, String columnCode, ColumnInfoDto item) {
        if (columnCode.startsWith(KEY_BATCH_UPDATE_FORM) && !columnBatchUpdate(item)) {
            return;
        }
        addForm.append("          <h-date-picker transfer type='date' v-model='" + columnCode + "' autoPlacement ");
        if (columnDisabled(addForm, item)) {
            addForm.append(" disabled");
        }
        addForm.append(" />").append(SYMBOL_NEXT_LINE);
    }

    private static void getNumberQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        if (!columnQuery(item)) {
            return;
        }
        queryForm.append("                <scope-input v-model='" + columnCode + "' suffixNum='-1' placeholder='请输入'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getNumberAddUpdateForm(StringBuilder addForm, String columnCode, ColumnInfoDto item) {
        if (columnCode.startsWith(KEY_BATCH_UPDATE_FORM) && !columnBatchUpdate(item)) {
            return;
        }
        addForm.append("          <h-input v-model='" + columnCode + "'");
        if (columnDisabled(addForm, item)) {
            addForm.append(" disabled");
        }
        addForm.append(" />").append(SYMBOL_NEXT_LINE);
    }

    private static void getRateQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        if (!columnQuery(item)) {
            return;
        }
        queryForm.append("                <h-percent-input v-model='" + columnCode + "' />").append(SYMBOL_NEXT_LINE);
    }

    private static void getRateAddUpdateForm(StringBuilder addForm, String columnCode, ColumnInfoDto item) {
        if (columnCode.startsWith(KEY_BATCH_UPDATE_FORM) && !columnBatchUpdate(item)) {
            return;
        }
        addForm.append("          <h-percent-input v-model='" + columnCode + "'");
        if (columnDisabled(addForm, item)) {
            addForm.append(" disabled");
        }
        addForm.append(" />").append(SYMBOL_NEXT_LINE);
    }

    private static void getTextQueryForm(StringBuilder queryForm, String columnCode, ColumnInfoDto item) {
        if (!columnQuery(item)) {
            return;
        }
        queryForm.append("                <h-input v-model='" + columnCode + "'/>").append(SYMBOL_NEXT_LINE);
    }

    private static void getTextAddUpdateForm(StringBuilder addForm, String columnCode, ColumnInfoDto item) {
        if (columnCode.startsWith(KEY_BATCH_UPDATE_FORM) && !columnBatchUpdate(item)) {
            return;
        }
        if (item.getColumnName().contains(SYMBOL_PERCENT)) {
            addForm.append("          <h-typefield v-model='" + columnCode + "' type='money' integerNum='" + GenerateCommon.getColumnPrecision(item) + "' divided ");
        } else {
            addForm.append("          <h-input v-model='" + columnCode + "'");
        }
        if (columnDisabled(addForm, item)) {
            addForm.append(" disabled");
        }
        addForm.append(" />").append(SYMBOL_NEXT_LINE);
    }

    private static String buildColumn(Map<String, ColumnInfoDto> columnInfo, String type) {
        StringBuilder column = new StringBuilder();
        if (STR_4.equals(type)) {
            column.append("         {").append(SYMBOL_NEXT_LINE);
            column.append("           type: 'selection',").append(SYMBOL_NEXT_LINE);
            column.append("           width: 48,").append(SYMBOL_NEXT_LINE);
            column.append("           align: 'center'").append(SYMBOL_NEXT_LINE);
            column.append("         },").append(SYMBOL_NEXT_LINE);
            column.append("         {").append(SYMBOL_NEXT_LINE);
            column.append("           title: 'TA代码',").append(SYMBOL_NEXT_LINE);
            column.append("           key: 'multTaCode',").append(SYMBOL_NEXT_LINE);
            column.append("           hiddenCol: window.LOCAL_CONFIG.isMultTa ? false : true,").append(SYMBOL_NEXT_LINE);
            column.append("           minWidth: 140,").append(SYMBOL_NEXT_LINE);
            column.append("           sortable: true,").append(SYMBOL_NEXT_LINE);
            column.append("           render: (h, {row}) => h('span', window.LOCAL_CONFIG.isMultTa ? this.dataSource.find(item => item.val == row.multTaCode).prompt : '')").append(SYMBOL_NEXT_LINE);
            column.append("         },").append(SYMBOL_NEXT_LINE);
        } else {
            column.append("          multTaCode: window.LOCAL_CONFIG.isMultTa ? JSON.parse(window.sessionStorage.getItem('multTaList'))[0].val : '',").append(SYMBOL_NEXT_LINE);
        }
        Iterator<String> iterator = columnInfo.keySet().iterator();
        while (iterator.hasNext()) {
            String columnCode = iterator.next();
            ColumnInfoDto item = columnInfo.get(columnCode);
            if (GenerateCommon.skipColumn(item)) {
                continue;
            }
            switch (type) {
                case STR_1:
                    if (STR_1.equals(item.getColumnMulti())) {
                        column.append("          " + item.getColumnCode() + ": [],").append(SYMBOL_NEXT_LINE);
                    } else {
                        column.append("          " + item.getColumnCode() + ": '',").append(SYMBOL_NEXT_LINE);
                    }
                    break;
                case STR_2:
                    String columnDefault = item.getColumnDefault();
                    if (StringUtils.isBlank(columnDefault)) {
                        columnDefault = SYMBOL_EMPTY;
                    }
                    column.append("          " + item.getColumnCode() + ": '" + columnDefault + "',").append(SYMBOL_NEXT_LINE);
                    break;
                case STR_3:
                     STR_5:
                    column.append("          " + item.getColumnCode() + ": '',").append(SYMBOL_NEXT_LINE);
                    break;
                case STR_4:
                    int columnWidth = 150;
                    if (KEY_PRD_CODE.equals(columnCode)) {
                        columnWidth = 200;
                    }
                    if (StringUtils.isNotBlank(item.getColumnWidth())) {
                        columnWidth = Integer.valueOf(item.getColumnWidth());
                    }
                    String columnName = item.getColumnName();
                    if (KEY_PRD_CODE.equals(columnCode)) {
                        columnName = KEY_PRD_CODE_NAME;
                    }
                    column.append("         {").append(SYMBOL_NEXT_LINE);
                    column.append("           title: '" + columnName + "',").append(SYMBOL_NEXT_LINE);
                    column.append("           key: '" + item.getColumnCode()+ "',").append(SYMBOL_NEXT_LINE);
                    column.append("           hiddenCol: false,").append(SYMBOL_NEXT_LINE);
                    column.append("           minWidth: " + columnWidth + ",").append(SYMBOL_NEXT_LINE);
                    column.append("           sortable: true,").append(SYMBOL_NEXT_LINE);
                    if (STR_1.equals(item.getColumnDate())) {
                        column.append("           render: dateRender").append(SYMBOL_NEXT_LINE);
                    } else if (StringUtils.isNotEmpty(item.getColumnDict()) || columnCode.contains(KEY_PRD_CODE) || columnCode.contains(KEY_SELLER_CODE) || columnCode.contains(KEY_BRANCH_NO)) {
                        column.append("           render: translateRender").append(SYMBOL_NEXT_LINE);
                    } else if (StringUtils.isNotBlank(item.getColumnPrecision())) {
                        int columnPrecision = GenerateCommon.getColumnPrecision(item);
                        if (columnPrecision != 2) {
                            column.append("           precision: " + columnPrecision + ",").append(SYMBOL_NEXT_LINE);
                        }
                        if(item.getColumnName().contains(SYMBOL_PERCENT)) {
                            column.append("           render: percentRender").append(SYMBOL_NEXT_LINE);
                        } else {
                            column.append("           render: amountRender").append(SYMBOL_NEXT_LINE);
                        }
                    }
                    column.append("         },").append(SYMBOL_NEXT_LINE);
                    break;
                default:
                    break;
            }

        }
        if (STR_4.equals(type)) {
            column.append("         {").append(SYMBOL_NEXT_LINE);
            column.append("           title: '操作',").append(SYMBOL_NEXT_LINE);
            column.append("           key: 'quote',").append(SYMBOL_NEXT_LINE);
            column.append("           hiddenCol: false,").append(SYMBOL_NEXT_LINE);
            column.append("           align: 'center',").append(SYMBOL_NEXT_LINE);
            column.append("           fixed: 'right',").append(SYMBOL_NEXT_LINE);
            column.append("           width: 145,").append(SYMBOL_NEXT_LINE);
            column.append("           render: (h, {row}) => {").append(SYMBOL_NEXT_LINE);
            column.append("             return h('div', [").append(SYMBOL_NEXT_LINE);
            column.append("               h(").append(SYMBOL_NEXT_LINE);
            column.append("                 'span',").append(SYMBOL_NEXT_LINE);
            column.append("                 {").append(SYMBOL_NEXT_LINE);
            column.append("                   on: {").append(SYMBOL_NEXT_LINE);
            column.append("                     click: () => {").append(SYMBOL_NEXT_LINE);
            column.append("                       this.handleOpenEdit(row)").append(SYMBOL_NEXT_LINE);
            column.append("                     }").append(SYMBOL_NEXT_LINE);
            column.append("                   },").append(SYMBOL_NEXT_LINE);
            column.append("                   class: {").append(SYMBOL_NEXT_LINE);
            column.append("                     'h-omc-table-btn': true,").append(SYMBOL_NEXT_LINE);
            column.append("                     'btn-hidden': !this.authObj.edit").append(SYMBOL_NEXT_LINE);
            column.append("                   }").append(SYMBOL_NEXT_LINE);
            column.append("                 },").append(SYMBOL_NEXT_LINE);
            column.append("                 '修改'").append(SYMBOL_NEXT_LINE);
            column.append("               ),").append(SYMBOL_NEXT_LINE);
            column.append("               h(").append(SYMBOL_NEXT_LINE);
            column.append("                 'span',").append(SYMBOL_NEXT_LINE);
            column.append("                 {").append(SYMBOL_NEXT_LINE);
            column.append("                   on: {").append(SYMBOL_NEXT_LINE);
            column.append("                     click: () => {").append(SYMBOL_NEXT_LINE);
            column.append("                       this.handleOpenDetail(row)").append(SYMBOL_NEXT_LINE);
            column.append("                     }").append(SYMBOL_NEXT_LINE);
            column.append("                   },").append(SYMBOL_NEXT_LINE);
            column.append("                   class: {").append(SYMBOL_NEXT_LINE);
            column.append("                    'h-omc-table-btn': true").append(SYMBOL_NEXT_LINE);
            column.append("                   }").append(SYMBOL_NEXT_LINE);
            column.append("                 },").append(SYMBOL_NEXT_LINE);
            column.append("                 '详情'").append(SYMBOL_NEXT_LINE);
            column.append("               )").append(SYMBOL_NEXT_LINE);
            column.append("             ])").append(SYMBOL_NEXT_LINE);
            column.append("           }").append(SYMBOL_NEXT_LINE);
            column.append("         }").append(SYMBOL_NEXT_LINE);
        }
        return column.toString();
    }

    private static String getUpdateColumn(Map<String, ColumnInfoDto> columnInfo) {
        StringBuilder content = new StringBuilder();
        Iterator<String> iterator = columnInfo.keySet().iterator();
        while (iterator.hasNext()) {
            String columnCode = iterator.next();
            if (GenerateCommon.skipColumn(columnInfo.get(columnCode))) {
                continue;
            }
            content.append("            '" + columnCode + "',").append(SYMBOL_NEXT_LINE);
        }
        return content.substring(0, content.toString().lastIndexOf(SYMBOL_COMMA));
    }

    private static String convertShowColumn(Map<String, ColumnInfoDto> columnInfo) {
        StringBuilder content = new StringBuilder();
        Iterator<String> iterator = columnInfo.keySet().iterator();
        while (iterator.hasNext()) {
            String columnCode = iterator.next();
            ColumnInfoDto columnInfoDto = columnInfo.get(columnCode);
            if (GenerateCommon.skipColumn(columnInfo.get(columnCode))) {
                continue;
            }
            if (STR_1.equals(columnInfoDto.getColumnDate())) {
                content.append("        editObj." + columnCode + " = formatDate(editObj." + columnCode + ")").append(SYMBOL_NEXT_LINE);
            }
        }
        return content.toString();
    }

    private static String convertSubmitColumn(Map<String, ColumnInfoDto> columnInfo) {
        StringBuilder content = new StringBuilder();
        Iterator<String> iterator = columnInfo.keySet().iterator();
        while (iterator.hasNext()) {
            String columnCode = iterator.next();
            ColumnInfoDto columnInfoDto = columnInfo.get(columnCode);
            if (GenerateCommon.skipColumn(columnInfo.get(columnCode))) {
                continue;
            }
            if (STR_1.equals(columnInfoDto.getColumnDate())) {
                content.append("            postData." + columnCode + " = formatPostDate(postData." + columnCode + ")").append(SYMBOL_NEXT_LINE);
            }
        }
        return content.toString();
    }

    private static String convertSubmitBatchColumn(Map<String, ColumnInfoDto> columnInfo) {
        StringBuilder content = new StringBuilder();
        Iterator<String> iterator = columnInfo.keySet().iterator();
        content.append("            for (let i = 0; i < this.ids.length; i++) {").append(SYMBOL_NEXT_LINE);
        while (iterator.hasNext()) {
            String columnCode = iterator.next();
            ColumnInfoDto columnInfoDto = columnInfo.get(columnCode);
            if (!columnBatchUpdate(columnInfoDto) || GenerateCommon.skipColumn(columnInfo.get(columnCode))) {
                continue;
            }
            if (STR_1.equals(columnInfoDto.getColumnDate())) {
                content.append("              this.ids[i]." + columnCode + " = formatPostDate(this.batchUpdateForm." + columnCode + ")").append(SYMBOL_NEXT_LINE);
            } else {
                content.append("              this.ids[i]." + columnCode + " = this.batchUpdateForm." + columnCode).append(SYMBOL_NEXT_LINE);
            }
        }
        content.append("            }");
        return content.toString();
    }

    private static boolean columnQuery(ColumnInfoDto columnInfoDto) {
        return STR_1.equals(columnInfoDto.getColumnQuery());
    }

    private static boolean columnBatchUpdate(ColumnInfoDto columnInfoDto) {
        return STR_1.equals(columnInfoDto.getColumnBatchUpdate());
    }

    private static boolean columnDisabled(StringBuilder content, ColumnInfoDto columnInfoDto) {
        return STR_1.equals(columnInfoDto.getColumnUpdate()) && content.toString().contains(KEY_UPDATE_FORM);
    }

    private static String getSearchParams(Map<String, ColumnInfoDto> columnInfo) {
        StringBuilder content = new StringBuilder("        let query = {...this.searchForm}").append(SYMBOL_NEXT_LINE);
        Iterator<String> iterator = columnInfo.keySet().iterator();
        while (iterator.hasNext()) {
            String columnCode = iterator.next();
            ColumnInfoDto columnInfoDto = columnInfo.get(columnCode);
            if (STR_1.equals(columnInfoDto.getColumnDate()) && columnQuery(columnInfoDto)) {
                content.append("        query." + columnCode + " = formatDateRange(query." + columnCode + ")").append(SYMBOL_NEXT_LINE);
            }
        }
        content.append("        return query").append(SYMBOL_NEXT_LINE);
        return content.toString();
    }

    private static String getValidateRules(Map<String, ColumnInfoDto> columnInfo, String type) {
        StringBuilder content = new StringBuilder();
        Iterator<String> iterator = columnInfo.keySet().iterator();
        while (iterator.hasNext()) {
            String columnCode = iterator.next();
            ColumnInfoDto columnInfoDto = columnInfo.get(columnCode);
            String tip = "请输入";
            if (STR_1.equals(columnInfoDto.getColumnDict())) {
                tip = "请选择";
            }
            if (STR_1.equals(columnInfoDto.getColumnRequired())) {
                content.append("          " + columnInfoDto.getColumnCode() + ": [").append(SYMBOL_NEXT_LINE);
                if (STR_1.equals(type)) {
                    content.append("            commonValidator.required('" + tip + columnInfoDto.getColumnName() + "'),").append(SYMBOL_NEXT_LINE);
                } else {
                    content.append("            batchUpdateFormValidator.required('" + tip + columnInfoDto.getColumnName() + "'),").append(SYMBOL_NEXT_LINE);
                }
                content.append("          ],").append(SYMBOL_NEXT_LINE);
            }
        }
        return content.toString();
    }
}
