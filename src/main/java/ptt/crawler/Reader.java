package ptt.crawler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ptt.crawler.config.Config;
import ptt.crawler.model.Article;
import ptt.crawler.model.Board;

public class Reader {
	private OkHttpClient okHttpClient;
	private final Map<String, List<Cookie>> cookieStore;
	private final CookieJar cookieJar;

	public Reader() throws IOException {
		// initialize
		cookieStore = new HashMap<String, List<Cookie>>();
		cookieJar = new CookieJar() {

			// 保存每次伺服器端回傳的 Cookie
			// @Override
			public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
				List<Cookie> cookies = cookieStore.getOrDefault(httpUrl.host(), new ArrayList<Cookie>());
				cookies.addAll(list);
				cookieStore.put(httpUrl.host(), cookies);
			}

			// 每次發送帶上儲存的 Cookie
			// @Override
			public List<Cookie> loadForRequest(HttpUrl httpUrl) {
				return cookieStore.getOrDefault(httpUrl.host(), new ArrayList<Cookie>());
			}
		};

		okHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();

		// 獲得網站的初始 Cookie
		Request request = new Request.Builder().get().url(Config.PTT_URL).build();
		okHttpClient.newCall(request).execute();
	}

	// 進行年齡確認
	private void runAdultCheck(String url) throws IOException {
		FormBody formBody = new FormBody.Builder().add("from", url).add("yes", "yes").build();

		Request request = new Request.Builder().url(Config.PTT_URL + "/ask/over18").post(formBody).build();

		okHttpClient.newCall(request).execute();
	}

	// 解析看板文章列表
	private List<Map<String, String>> parseArticle(String body) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		Document doc = Jsoup.parse(body);
		Elements articleList = doc.select(".r-ent");

		for (Element element : articleList) {
			String url = element.select(".title a").attr("href");
			String title = element.select(".title a").text();
			String author = element.select(".meta .author").text(); // 為何多一個點？
			String date = element.select(".meta .date").text();

//			// 為何這邊用他的寫法會錯，這是甚麼寫法？
//			result.add(new HashMap<>(){{
//	            put("url", url);
//	            put("title", title);
//	            put("author", author);
//	            put("date", date);
//	        }});

			Map<String, String> map = new HashMap<String, String>();
			map.put("url", url);
			map.put("title", title);
			map.put("author", author);
			map.put("date", date);

			result.add(map);
		}

		return result;
	}

	public List<Article> getList(String boardName) throws IOException, ParseException {
		Board board = Config.BOARD_LIST.get(boardName);

		// 如果找不到指定的看板
		if (board == null) {
			return null;
		}
		
		// 如果看板需要成年檢查
		if (board.getAdultCheck() == true) {
			runAdultCheck(board.getUrl());
		}
		
		// 抓取目標頁面
		Request request = new Request.Builder()
				.url(Config.PTT_URL + board.getUrl())
				.get()
				.build();
		
		Response response = okHttpClient.newCall(request).execute();
		String body = response.body().string();
		
		// 轉換 HTML 到 Article
		List<Map<String, String>> articles = parseArticle(body);
		List<Article> result = new ArrayList<Article>();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
		
		for(Map<String, String> article : articles) {
			String url = article.get("url");
			String title = article.get("title");
			String author = article.get("author");
			Date date = sdf.parse(article.get("date"));
			
			result.add(new Article(board, url, title, author, date));
		}
		
		return result;
	}

}
