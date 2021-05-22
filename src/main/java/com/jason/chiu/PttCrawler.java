package com.jason.chiu;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class PttCrawler {
	private static final String PTT = "https://www.ptt.cc";
//	private static final String GOSSPING_MAIN = "https://www.ptt.cc/bbs/Gossiping/index.html";
	private static final String MACSHOP_MAIN = "https://www.ptt.cc/bbs/MacShop/index.html";

	public static void main(String[] args) throws IOException {
		// ***************** 1. 取得最新文章頁面編號 ***************** //
		HashMap<String, String> cookies = new HashMap<String, String>();
		cookies.put("over18", "1");
//		Document doc = Jsoup.connect(GOSSPING_MAIN).cookies(cookies).get();
		Document doc = Jsoup.connect(MACSHOP_MAIN).cookies(cookies).get();
		if (doc != null) {
			Elements elements = doc.select(".btn-group-paging > a");
//			String str = elements.get(1).attr("href").replaceAll("/bbs/Gossiping/index([0-9]+).html", "$1");
			String str = elements.get(1).attr("href").replaceAll("/bbs/MacShop/index([0-9]+).html", "$1");
			int newIndex = Integer.parseInt(str) + 1;
			System.out.println(newIndex);

			// ********************* 2. 取得最新頁面文章標題 ********************* //
//			String gossipingURL = "https://www.ptt.cc/bbs/Gossiping/index" + newIndex + ".html";
			String macshopURL = "https://www.ptt.cc/bbs/MacShop/index" + newIndex + ".html";
			Document doc2 = Jsoup.connect(macshopURL).cookies(cookies).get();
			if (doc2 != null) {
				Elements elements2 = doc2.select(".title > a");
				for (Element e : elements2) {
					String title = e.text();
					String href = e.attr("href");
					if (title.contains("[公告]")) {
						break;
					}
					System.out.println(title);

					// ********************* 3. 取得各篇文章內文 ******************* //

					String contentURL = PTT + href;
					Document doc3 = Jsoup.connect(contentURL).cookies(cookies).get();

					Element e2 = doc3.select(".article-metaline").last();
					Node node = e2.nextSibling();
					System.out.print(node.toString());
					Element e3 = null;
					Node node2 = null;
					if (!title.startsWith("Re")) {
						e3 = doc3.select("span[class=f2]").first();
						node2 = e3.nextSibling();
					} else {
						e3 = doc3.select("span[class=f2]").get(1);
						node2 = e3.previousSibling();
					}
					System.out.println(node2.toString());
					System.out.println("=======");
				}
			}

		}

	}

}
