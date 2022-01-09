package im.util;

import im.model.SysWeChatTextModel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hoomoomoo
 * @description
 * @package com.hoomoomoo.im.util
 * @date 2020/02/27
 */
public class SysWeChatUtil {

    private static final String XML                                          = "xml";
    private static final String CDATA_LEFT                                   = "<![CDATA[";
    private static final String CDATA_RIGHT                                  = "]]>";

    /**
     * 解析微信发来的请求（XML）
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseXml(HttpServletRequest request){
        Map<String, String> map = new HashMap<>(16);
        try {
            InputStream inputStream = request.getInputStream();
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            Element root = document.getRootElement();
            List<Element> elementList = root.elements();
            for (Element e : elementList){
                map.put(e.getName(), e.getText());
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 文本消息对象转换成xml
     *
     * @param sysWeChatTextModel 文本消息对象
     * @return xml
     */
    public static String textMessageToXml(SysWeChatTextModel sysWeChatTextModel) {
        xstream.alias(XML, sysWeChatTextModel.getClass());
        return xstream.toXML(sysWeChatTextModel);
    }

    /**
     * 扩展xstream，使其支持CDATA块
     *
     */
    private static XStream xstream = new XStream(new XppDriver() {
        @Override
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                boolean cdata = true;
                @Override
                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write(CDATA_LEFT);
                        writer.write(text);
                        writer.write(CDATA_RIGHT);
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });
}
