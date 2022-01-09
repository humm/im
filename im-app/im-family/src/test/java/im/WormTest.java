package im;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author Administrator
 * @Description
 * @package im.test
 * @Date 2020/04/18
 */
public class WormTest {

    private static final Logger logger = LoggerFactory.getLogger(WormTest.class);
    private static final String filePath = "D:\\worm\\list.txt";


    public static void main(String[] args) {
        FileUtils.deleteQuietly(new File(filePath));
        Long nums = 0L;
        String url=  "https://www.dy2018.com";
        // 获取首页导航栏
        Elements menuElements = getElements(url, "#menu ul li a");
        if (menuElements == null) {
            logger.error("地址[{}]获取元素信息失败", url);
            return;
        }
        // 遍历首页导航栏
        for (Element menuElement : menuElements) {
            // 获取导航栏地址
            String href = url + getHref(menuElement);
            // 获取分页信息
            List<String> page = getPageInfo(href, "div.co_content8 div.x select option");
            if (CollectionUtils.isNotEmpty(page)) {
                logger.info("影视作品类型[{}] 数据总页数[{}]", menuElement.text(), page.size());
                logToFile(String.format("影视作品类型[%s] 数据总页数[%s]", menuElement.text(), page.size()));
                for (int i=0; i<page.size(); i++) {
                    logger.info("当前页数[{}]", i + 1);
                    logToFile(String.format("当前页数[%s]", i + 1));
                    // 获取影视列表信息
                    String pageHref = url + page.get(i);
                    Elements filmListElements = getElements(pageHref,"div.co_content8 table b a[title]");
                    if (filmListElements == null) {
                        logger.error("地址[{}]获取元素信息失败", pageHref);
                        continue;
                    }
                    // 遍历影视列表
                    for (Element filmElement : filmListElements) {
                        // 获取影视地址
                        pageHref = url + getHref(filmElement);
                        // 获取影视下载链接
                        Elements film = getElements(pageHref,"#Zoom table tbody a");
                        if (film == null) {
                            logger.error("地址[{}]获取元素信息失败", pageHref);
                            continue;
                        }
                        // 获取影视所有下载链接地址
                        for (Element hrefElement : film) {
                            pageHref = hrefElement.attr("href");
                            logToFile(pageHref);
                            logger.info(pageHref);
                        }
                        nums++;
                    }
                }
            }
        }
        logger.info("获取影视作品总数[{}]", nums.toString());
        logToFile(String.format("获取影视作品总数[%s]", nums.toString()));
    }

    private static void logToFile(String content) {
        try {
            FileUtils.writeLines(new File(filePath), Collections.singleton(content), true);
        } catch (IOException e) {
            logger.error("文件记录失败[{}]", content);
            e.printStackTrace();
        }
    }

    private static Document getDocument(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).timeout(60000).get();
        } catch (IOException e) {
            logger.error("地址[{}]访问失败", url);
            e.printStackTrace();
        }
        return document;
    }

    private static Elements getElements(String url, String selectNode) {
        Document document = getDocument(url);
        if (document == null) {
            logger.error("地址[{}]获取Document信息失败", url);
            return null;
        }
        return document.body().select(selectNode);
    }

    private static List<String> getPageInfo (String url, String selectNode) {
        List<String> page = new ArrayList<>();
        Elements elements = getElements(url, selectNode);
        if (elements == null) {
            logger.error("地址[{}]获取分页信息失败", url);
            return page;
        }
        for (Element element : elements) {
            page.add(getHref(element, "value"));
        }
        return page;
    }

    private static String getHref(Element element) {
        return formatHref(element.attr("href"));
    }

    private static String getHref(Element element, String attribute) {
        return formatHref(element.attr(attribute));
    }

    private static String formatHref(String href) {
        if (href != null && !href.startsWith("/")) {
            href = "/" + href;
        }
        return href;
    }
}
