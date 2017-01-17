/**
 * @Title: Utils.java
 * @Package shuhelper.web
 * @Description: TODO
 * @author Roll (roll@busyliving.me)
 * @date 2017年1月14日 下午4:27:39
 * @version V1.0
 */
package shuhelper.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @ClassName: Utils
 * @Description: TODO
 * @author Roll (roll@busyliving.me)
 * @date 2017年1月14日 下午4:27:39
 *
 */
public class Utils {
	/**
	 * @Fields properties : 属性文件缓存
	 */
	private static Map<String, String> properties = null;

	/**
	 * @Title: parseFile
	 * @Description: 从文件中加载HTML文档
	 * @param @param filePath
	 * @param @return
	 * @param @throws Exception
	 * @return Document
	 * @throws
	 */
	public static Document parseFile(String filePath) throws Exception {
		File inputFile = new File(filePath);
		return Jsoup.parse(inputFile, "UTF-8");
	}

	/**
	 * @Title: getString
	 * @Description: TODO
	 * @param: @param 返回GET页面的结果String
	 * @param: @param url
	 * @param: @return
	 * @param: @throws ClientProtocolException
	 * @param: @throws IOException
	 * @return: String
	 */
	public static String getString(CloseableHttpClient client, String url)
	throws ClientProtocolException, IOException {
		HttpGet getPage = new HttpGet(url);
		String htmlContent = null;
		CloseableHttpResponse response = client.execute(getPage);
		try {
			HttpEntity entity = response.getEntity();
			htmlContent = EntityUtils.toString(entity);
		} finally {
			response.close();
		}
		return htmlContent;
	}

	/**
	 * @Title: getDocument
	 * @Description: 返回GET页面的结果Document
	 * @param: @param client
	 * @param: @param url
	 * @param: @return
	 * @param: @throws ClientProtocolException
	 * @param: @throws IOException
	 * @return: Document
	 */
	public static Document getDocument(CloseableHttpClient client, String url)
	throws ClientProtocolException, IOException {
		return Jsoup.parse(getString(client, url));
	}

	/**
	 * @Title: postString
	 * @Description: 返回POST页面的结果String
	 * @param: @param client
	 * @param: @param url
	 * @param: @param data
	 * @param: @return
	 * @param: @throws ClientProtocolException
	 * @param: @throws IOException
	 * @return: String
	 */
	public static String postString(CloseableHttpClient client, String url, List<NameValuePair> data)
	throws ClientProtocolException, IOException {
		HttpPost postPage = new HttpPost(url);
		String htmlContent = null;
		postPage.setEntity(new UrlEncodedFormEntity(data));
		CloseableHttpResponse response = client.execute(postPage);
		try {
			HttpEntity entity = response.getEntity();
			htmlContent = EntityUtils.toString(entity);
		} finally {
			response.close();
		}
		return htmlContent;
	}

	/**
	 * @Title: postDocument
	 * @Description: 返回POST页面的结果Document
	 * @param: @param client
	 * @param: @param url
	 * @param: @param data
	 * @param: @return
	 * @param: @throws ClientProtocolException
	 * @param: @throws IOException
	 * @return: Document
	 */
	public static Document postDocument(CloseableHttpClient client, String url, List<NameValuePair> data)
	throws ClientProtocolException, IOException {
		return Jsoup.parse(postString(client, url, data));
	}

	/**
	 * @Title: parseTable2ArrayList
	 * @Description: Document中的表格转换为二维数组
	 * @param: @param doc
	 * @param: @param selectorRow
	 * @param: @param selectorCol
	 * @param: @return
	 * @return: ArrayList<String[]>
	 */
	public static ArrayList<String[]> parseTable2ArrayList(Document doc, String selectorRow, String selectorCol) {
		Elements rows = doc.select(selectorRow);
		ArrayList<String[]> arrayList = new ArrayList<String[]>();
		for (Element row : rows) {
			Elements cols = row.select(selectorCol);
			String[] array = new String[cols.size()];
			for (int i = 0; i < cols.size(); i++) {
				array[i] = cols.get(i).html();
			}
			arrayList.add(array);
		}
		return arrayList;
	}

	public static String[] getLoginFields(Document doc) {
		String[] fields = new String[3];
		Elements eleInputFields = doc.select("input[id~=^txt\\w+]");
		for (int i = 0; i < 3; i++) {
			fields[i] = eleInputFields.get(i).attr("id");
		}
		return fields;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getProperty(String key) throws IOException {
		if (properties == null) {
			InputStream is = Utils.class.getResourceAsStream("web.properties");
			Properties prop = new Properties();
			prop.load(is);
			properties =  new HashMap<String, String>((Map) prop);
			is.close();
		}
		return properties.get(key);
	}
}
