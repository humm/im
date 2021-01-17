package com.hoomoomoo.im.util;

import com.hoomoomoo.im.model.SysSqlMode;
import com.hoomoomoo.im.model.base.QueryBaseModel;
import com.hoomoomoo.im.model.base.SessionBean;
import com.hoomoomoo.im.model.base.BaseModel;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hoomoomoo
 * @description 系统级工具类
 * @package com.hoomoomoo.im.util
 * @date 2019/08/08
 */

public class SysCommonUtils {

    private static final String COLON_CHINESE                                = "：";
    private static final String NEXT_LINE                                    = "\n";
    private static final String SQL_ID                                        = "id";
    private static final String SQL_MODEL                                    = "model";
    private static final String SQL_MODEL_SELECT                             = "select";
    private static final String TIP_SQL_ID_EXIST                             = "sql [%s] 已存在";
    private static final String TIP_SQL_ID                                   = "sql id 不能为空";
    private static final String TIP_SQL_VALUE                                = "sql [%s] value 不能为空";
    private static final String LESS_THAN_SEMICOLON                          = "&lt;";
    private static final String GREATER_THAN_SEMICOLON                       = "&gt;";
    private static final String NBSP                                         = "&nbsp;";
    private static final String LESS_THAN                                    = "<";
    private static final String GREATER_THAN                                 = ">";
    private static final String STR_EMPTY                                    = "";


    /**
     * 设置创建人修改人信息
     *
     * @param baseModel
     */
    public static void setCreateUserInfo(BaseModel baseModel) {
        SessionBean sessionBean = SysSessionUtils.getSession();
        if (sessionBean != null) {
            baseModel.setCreateUser(sessionBean.getUserId());
            baseModel.setModifyUser(sessionBean.getUserId());
        }
        Date date = new Date();
        baseModel.setCreateDate(date);
        baseModel.setModifyDate(date);
    }

    /**
     * 设置创建人修改人信息
     *
     * @param baseModel
     * @param userId
     */
    public static void setCreateUserInfo(BaseModel baseModel, String userId) {
        baseModel.setCreateUser(userId);
        baseModel.setModifyUser(userId);
        Date date = new Date();
        baseModel.setCreateDate(date);
        baseModel.setModifyDate(date);
    }

    /**
     * 设置修改人信息
     *
     * @param baseModel
     */
    public static void setModifyUserInfo(BaseModel baseModel) {
        SessionBean sessionBean = SysSessionUtils.getSession();
        if (sessionBean != null) {
            baseModel.setModifyUser(sessionBean.getUserId());
        }
        baseModel.setModifyDate(new Date());
    }

    /**
     * 设置修改人信息
     *
     * @param baseModel
     */
    public static void setModifyUserInfo(BaseModel baseModel, String userId) {
        baseModel.setModifyUser(userId);
        baseModel.setModifyDate(new Date());
    }

    /**
     * 设置查询实体session信息
     *
     * @param queryBaseModel
     */
    public static void setSessionInfo(QueryBaseModel queryBaseModel) {
        SessionBean sessionBean = SysSessionUtils.getSession();
        if (sessionBean != null) {
            queryBaseModel.setUserKey(sessionBean.getUserId());
            queryBaseModel.setIsAdminData(sessionBean.getIsAdminData());
        }
    }

    /**
     * 获取连接url
     *
     * @param httpServletRequest
     * @return
     */
    public static String getConnectUrl(HttpServletRequest httpServletRequest, String appName){
        String url = httpServletRequest.getRequestURL().toString();
        String uri = httpServletRequest.getRequestURI();
        return new StringBuffer(url.substring(0, url.indexOf(uri))).append(appName).toString();
    }

    /**
     * 获取字典描述内容
     * @param dictionaryCaption
     * @return
     */
    public static String getDictionaryCaption(String dictionaryCaption) {
        if (StringUtils.isNotBlank(dictionaryCaption) && dictionaryCaption.contains(COLON_CHINESE)) {
            String[] caption = dictionaryCaption.split(COLON_CHINESE);
            if (caption != null && caption.length == 2) {
                return caption[1];
            }
        }
        return dictionaryCaption;
    }

    /**
     * 数字格式化 保留2位小数
     * @param value
     * @return
     */
    public static String formatValue(String value) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(true);
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format(Double.valueOf(value));
    }

    /**
     * 获取配置sql
     *
     * @param filePath
     * @return
     */
    public static ConcurrentHashMap<String, SysSqlMode> getConfigSql(String filePath) {
        ConcurrentHashMap<String, SysSqlMode> xmlMap = new ConcurrentHashMap();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new ClassPathResource(filePath).getInputStream());
            DOMReader xmlReader = new DOMReader();
            org.dom4j.Document doc = xmlReader.read(document);
            Element root = doc.getRootElement();
            List<Element> elementList = root.elements();
            for (int i = 0; i < elementList.size(); i++) {
                Element element = elementList.get(i);
                String id = element.attribute(SQL_ID).getValue();
                String model = SQL_MODEL_SELECT;
                String value = element.getTextTrim();
                if (StringUtils.isBlank(id)) {
                    throw new RuntimeException(String.format(TIP_SQL_ID, id));
                }
                if (StringUtils.isBlank(value)) {
                    throw new RuntimeException(String.format(TIP_SQL_VALUE, id));
                }
                if (element.attribute(SQL_MODEL) != null) {
                    model = element.attribute(SQL_MODEL).getValue();
                }
                if (xmlMap.containsKey(id)) {
                    throw new RuntimeException(String.format(TIP_SQL_ID_EXIST, id));
                }
                xmlMap.put(id, new SysSqlMode(id, model, value));
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlMap;
    }

    /**
     * 获取邮件信息
     *
     * @param xml
     * @return
     */
    public static List<BaseModel> getMailXmlToBean(String xml, Class clazz) throws DocumentException {
        List<Map> content = new ArrayList<>();
        xml = xml.replace(LESS_THAN_SEMICOLON, LESS_THAN).replace(GREATER_THAN_SEMICOLON, GREATER_THAN).replace(NBSP, STR_EMPTY);
        org.dom4j.Document document = DocumentHelper.parseText(xml);
        org.dom4j.Element root = document.getRootElement();
        Iterator<Element> iterator = root.elementIterator();
        while (iterator.hasNext()) {
            // 单条数据
            org.dom4j.Element element = iterator.next();
            Iterator<Element> single = element.elementIterator();
            Map<String, String> request = new HashMap<>(16);
            while (single.hasNext()) {
                org.dom4j.Element node = single.next();
                request.put(node.getName(), node.getTextTrim());
            }
            content.add(request);
        }
        return SysBeanUtils.mapToBean(clazz, content);
    }
}
