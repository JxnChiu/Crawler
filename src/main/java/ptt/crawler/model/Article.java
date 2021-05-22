package ptt.crawler.model;

import java.util.*;

public class Article {
	private Board parent; // 所屬看板
	private String url; // 網址
	private String title; // 標題
	private String body; // 內容
	private String author; // 作者
	private Date date; // 時間

	public Article(Board parent, String url, String title, String author, Date date) {
		super();
		this.parent = parent;
		this.url = url;
		this.title = title;
		this.author = author;
		this.date = date;
	}

	public Board getParent() {
		return this.parent;
	}

	public void setParent(Board parent) {
		this.parent = parent;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return String.format("Article{ url='%s', title='%s', body='%s', author='%s', date='%s' }\n", url, title, body,
				author, date);
	}

}
