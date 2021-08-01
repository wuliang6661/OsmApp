package com.heloo.android.osmapp.utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlFormat {


    public static String getNewContent(String htmltext) {
        if (StringUtils.isEmpty(htmltext)) {
            return "";
        }
        Document document = Jsoup.parse(htmltext);

        Element element = document.head();
        Element meta = element.appendElement("meta");
        meta.attr(" name", "viewport").attr(" content", "width=device-width")
                .attr("initial-scale", "1.0").attr("user-scalable", "no");
        Elements pElements = document.select("p:has(img)");
        for (Element pElement : pElements) {
            pElement.attr("style", "text-align:center");
            pElement.attr("max-width", String.valueOf(ScreenUtils.getScreenWidth() + "px"))
                    .attr("height", "auto");
        }
        Elements imgElements = document.select("img");
        for (Element imgElement : imgElements) {
            //重新设置宽高
            imgElement.attr("max-width", "100%")
                    .attr("height", "auto");
            imgElement.attr("style", "max-width:100%;width:100%;height:auto");
        }
        Log.i("newData:", document.toString());
        return document.toString();
    }

}
