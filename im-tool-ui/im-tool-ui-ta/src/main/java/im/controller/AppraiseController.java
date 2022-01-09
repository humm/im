package im.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import im.consts.BaseConst;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author humm23693
 * @description TODO
 * @package im.controller
 * @date 2022/1/8
 */
public class AppraiseController {

    public static String cookie = "__jdv=76161171|direct|-|none|-|1641651497213; __jdu=16416514972121020121137; areaId=19; " +
            "ipLoc-djd=19-1607-0-0; PCSYCityID=CN_440000_440300_0; alc=p3tDcvvcF7N3X/Zf6nec5Q==; _t=fMVPqHDtyn87+R/+QmKLE2+eUjTjOicdEXkMSIx1XmM=; DeviceSeq=db0dc9026aa34be585d3dad17955a775; TrackID=181oc59trvg5UKfA-wswQfxaG3Ax-So95pckeLG2jxrzDUZFHcFX0-8l25R_WUNNXfLhVkK4GlEHTmkeDYS730o_jzcaCm_1Q3KoeAfO2Gf4; pinId=ZgFJT1_2rzaNl5uH84k_4rV9-x-f3wj7; pin=142636192-736689; unick=hoomoomoo; ceshi3.com=201; _tp=njGwuTwGKRqCsZcs2BMrGJQm/LHU2Nl0V5Jh8Hs9sOM=; _pst=142636192-736689; shshshfpa=d067d203-d91a-a3b3-f384-d241a3885e1d-1641651540; shshshfpb=sxRqL0wyp7slbNcSFHVTVMQ; __jdc=122270672; wxa_level=1; retina=0; cid=9; jxsid=16416533909555183807; webp=1; mba_muid=16416514972121020121137; visitkey=22760813791168198; TrackerID=mVp1PT7FjdwKkJrp8tlPa91gRF5PWuAvIQaHaagOHtufPrkxakFYFilds_LhZZJ3mjRQGlVhEp73m5SQ2GPIlWaC8KrhC3zoxWOiGH722W4; pt_key=AAJh2aTGADC2XL_hKgHAQQSz-5GPNUYNFPejaMGNrQLoYLcrzVclMG6yAW24WMkAh5h-nkOX69g; pt_pin=142636192-736689; pt_token=dig6h4s7; pwdt_id=142636192-736689; sfstoken=tk01mcafb1c19a8sMngyKzJ4MmZx8m0SEqXhxakyhVKi0EAP4s1u2tIFlcdZeQqh2H1KGMHX8u/UqxGKZf3TAc7aVTP1; wqmnx1=MDEyNjM2MXQvbW9kb3RjbGMyMzh6NWkgLm42cEs3SyBHIGUuMWYzMWY0ZkJLWUNGRigl; mba_sid=1641653391115385216011365348.3; __wga=1641653449541.1641653449541.1641653449541.1641653449541.1.1; PPRD_P=UUID.16416514972121020121137; __jda=122270672.16416514972121020121137.1641651497.1641651497.1641653449.2; jxsid_s_t=1641653449601; jxsid_s_u=https://home.m.jd.com/myJd/newhome.action; sc_width=1549; shshshfp=e9324b6ab372a93135f7be29704909b9; shshshsID=3776459e2ad195861260d6602e95cc55_2_1641653449754; thor=BF7442006F2A21DE7534A767230C17741A6F3602222F172CB7D76A971B6D35CE697328ADDCAE7F6BE456E8DCCE97782100715FD722643DF45C28BF99A109DFCB252D9BAA4C16DD52BBC2B476CB2BD024C7AF88D1CA87E3EF0FEC70AF5B9AA4002F73D2E65312DD1DC5CCF69B6FAF8AEBD5FE5EC513B4719B6926311581BCD4C937290FF38BF18F707396062EBA6F4F9060394CA945B28743A0960363E1171934; __jdb=122270672.21.16416514972121020121137|2.1641653449; 3AB9D23F7A4B3C9B=E2SOZ5QMKZW4GYEKHO6AWIING6MZHLYVMNKYEEOLOXCGMDBHF77PG6B4ALHRNH5QHWQ5Y756EETWWPDUXFKC6UTB7Y";
    public static int appraiseNum = 2;

    // url配置
    // 获取可评价商品
    // 评价间隔时间
    // cookie 配置
    // 评价组成个数

    public static void main(String[] args){
        try {
            Document waitAppraise = getWaitAppraise();
            Elements orderList = waitAppraise.select("table.td-void.order-tb tbody");
            for (Element order : orderList){
                String orderId = order.select("tr td span.number a").text();
                Elements operateList = order.select("tr td div.operate a");
                boolean isOperate = false;
                for (Element operate : operateList) {
                    String operateName = operate.text();
                    if ("评价".equals(operateName)) {
                        isOperate = true;
                    }
                }
                if (!isOperate) {
                    continue;
                }
                Elements goodsList = order.select("tr td div.p-name a");
                for (Element goods : goodsList) {
                    String goodsName = goods.text();
                    String goodsHref = goods.attr("href");
                    int indexStart = goodsHref.lastIndexOf(BaseConst.SYMBOL_SLASH);
                    int indexEnd = goodsHref.lastIndexOf(BaseConst.SYMBOL_POINT);
                    String goodsId = BaseConst.SYMBOL_EMPTY;
                    if (indexEnd != -1 && indexEnd > indexStart) {
                        goodsId = goodsHref.substring(indexStart + 1, indexEnd);
                    }
                    System.out.println(orderId + " " + goodsId + " " + goodsName + " 评价 开始");
                    getGoodsAppraiseInfo(goodsId);
//                    goodsAppraise(orderId, goodsId);
//                    serviceAppraise(orderId);
                    System.out.println(orderId + " " + goodsId + " " + goodsName + "评价 成功");
                }
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getGoodsAppraiseInfo(String goodsId) throws IOException {
        String appraiseMsg = "大品牌值得信赖，很不错哦，物超所值。物流很快，包装完好。";
        Connection connection = Jsoup.connect("https://club.jd.com/comment/productPageComments.action?productId=" + goodsId + "&score=0&sortType=5&page=0&pageSize=10&isShadowSku=0&rid=0&fold=1");
        initCookie(connection);
        Document appraise = connection.get();
        JSONObject appraiseInfo = JSONObject.parseObject(appraise.select("body").text());
        if (appraiseInfo != null && !appraiseInfo.isEmpty()) {
            JSONArray appraiseList = (JSONArray)appraiseInfo.get("comments");
            if (appraiseList != null && !appraiseList.isEmpty()) {
                if (appraiseList.size() < appraiseNum) {
                    appraiseMsg = ((JSONObject) appraiseList.get(0)).get("content").toString();
                } else {
                    appraiseMsg = BaseConst.SYMBOL_EMPTY;
                    for (int i=0; i<appraiseNum; i++) {
                        appraiseMsg += ((JSONObject) appraiseList.get(i)).get("content").toString() + BaseConst.SYMBOL_NEXT_LINE;
                    }
                }
            }
        }
        return appraiseMsg;
    }

    public static Document doServiceAppraise(String orderId) throws IOException {
        Connection connection = Jsoup.connect("https://club.jd.com/myJdcomments/insertRestSurvey.action?voteid=145&ruleid=" + orderId);
        initCookie(connection);
        Map<String, String> requestData = new HashMap<>(6);
        requestData.put("oid", orderId);
        requestData.put("gid", BaseConst.STR_69);
        requestData.put("sid", BaseConst.STR_549656);
        requestData.put("stid", BaseConst.STR_0);
        requestData.put("tags", BaseConst.SYMBOL_EMPTY);
        requestData.put("ro1827", "1827A1");
        requestData.put("ro1828", "1828A1");
        requestData.put("ro591", "591A1");
        requestData.put("ro592", "592A1");
        requestData.put("ro593", "593A1");
        requestData.put("ro899", "899A1");
        requestData.put("ro900", "900A1");
        connection.data(requestData);
        return connection.post();
    }

    public static Document doGoodsAppraise(String orderId, String goodsId) throws IOException {
        Connection connection = Jsoup.connect("https://club.jd.com/myJdcomments/saveProductComment.action");
        initCookie(connection);
        Map<String, String> requestData = new HashMap<>(6);
        requestData.put("orderId", orderId);
        requestData.put("productId", goodsId);
        requestData.put("score", BaseConst.STR_5);
        requestData.put("saveStatus", BaseConst.STR_1);
        requestData.put("anonymousFlag", BaseConst.STR_1);
        requestData.put("content", URLEncoder.encode(getGoodsAppraiseInfo(goodsId)));
        connection.data(requestData);
        return connection.post();
    }

    public static Document getWaitAppraise() throws IOException {
        Connection connection = Jsoup.connect("https://club.jd.com/myJdcomments/myJdcomment.action?sort=0");
        initCookie(connection);
        return connection.get();
    }

    public static void initCookie(Connection connection) {
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36");
        String[] cookies = cookie.split(BaseConst.SYMBOL_SEMICOLON);
        for (String item : cookies) {
            String[] itemCookie = item.split(BaseConst.SYMBOL_EQUALS);
            if (itemCookie.length == 2) {
                connection.cookie(itemCookie[0], itemCookie[1]);
            }
        }
    }
}
