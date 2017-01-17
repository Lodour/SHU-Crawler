/**
 * @Title: CJWebAPI.java
 * @Package shuhelper.web
 * @Description: 实现http://cj.shu.edu.cn的网络API
 * @author Roll (roll@busyliving.me)
 * @date 2017年1月13日 下午11:01:32
 * @version V1.0
 */
package shuhelper.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @ClassName: CJWebAPI
 * @Description: 实现http://cj.shu.edu.cn的网络API
 * @author Roll (roll@busyliving.me)
 * @date 2017年1月13日 下午11:01:32
 *
 */
public class CJWebAPI extends WebAPI {
	/**
	 * <p>Title: CJWebAPI</p>
	 * <p>Description: 构造函数</p>
	 * @throws IOException
	 */
	public CJWebAPI() throws Exception {
		super();
		urlLogin = Utils.getProperty("CJ_urlLogin");
		urlIndex = urlLogin + Utils.getProperty("CJ_urlIndexSuffix");
		urlCaptcha = urlLogin + Utils.getProperty("CJ_urlCaptchaSuffix");
	}

	/**
	 * @Title: getScheduleDocument
	 * @Description: 根据TermID返回课表页面的文档
	 * @param @param strTermID
	 * @param @return
	 * @param @throws ParseException
	 * @param @throws IOException
	 * @return Document
	 * @throws
	 */
	private Document getScheduleDocument(String strTermID)
	throws ParseException, IOException {
		String urlGetSchedule = urlLogin + Utils.getProperty("CJ_urlGetScheduleSuffix");
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add(new BasicNameValuePair("academicTermID", strTermID));
		return Utils.postDocument(httpClient, urlGetSchedule, postData);
	}

	/**
	 * @Title: getScoreTermDocument
	 * @Description: 返回某学期成绩的文档
	 * @param: @param strTermID
	 * @param: @return
	 * @param: @throws IOException
	 * @return: Document
	 */
	private Document getScoreTermDocument(String strTermID)
	throws IOException {
		String urlGetTermScore = urlLogin + Utils.getProperty("CJ_urlGetTermScoreSuffix");
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add(new BasicNameValuePair("academicTermID", strTermID));
		return Utils.postDocument(httpClient, urlGetTermScore, postData);
	}

	/**
	 * @Title: getScoreSummaryDocument
	 * @Description: 返回成绩大表的文档
	 * @param: @return
	 * @param: @throws ParseException
	 * @param: @throws IOException
	 * @return: Document
	 */
	private Document getScoreSummaryDocument()
	throws ParseException, IOException {
		String urlGetScoreSummary = urlLogin + Utils.getProperty("CJ_urlGetScoreSummarySuffix");
		return Utils.getDocument(httpClient, urlGetScoreSummary);
	}

	/**
	 * @Title: getScheduleArrayList
	 * @Description: 以ArrayList<String[]>返回课程安排
	 * @param: @param strTermID
	 * @param: @return
	 * @param: @throws Exception
	 * @return: ArrayList<String[]> {{课程号, 课程名, 教师号, 教师名, 上课时间, 上课地点, 答疑时间, 答疑地点}, ...}
	 */
	public ArrayList<String[]> getScheduleArrayList(String strTermID) throws Exception {
		Document doc = getScheduleDocument(strTermID);
		String selectorRow = "tr:has(td:eq(7))";
		String selectorCol = "td:lt(8)";
		return Utils.parseTable2ArrayList(doc, selectorRow, selectorCol);
	}

	/**
	 * @Title: getScoreTermArrayList
	 * @Description: 以ArrayList<String[]>返回学期成绩
	 * @param: @param strTermID
	 * @param: @return
	 * @param: @throws Exception
	 * @return: ArrayList<String[]> {{课程号, 课程名, 学分, 成绩, 绩点}, ...}
	 */
	public ArrayList<String[]> getScoreTermArrayList(String strTermID) throws Exception {
		Document doc = getScoreTermDocument(strTermID);
		String selectorRow = "tr:has(td:eq(5))";
		String selectorCol = "td:lt(6):gt(0)";
		return Utils.parseTable2ArrayList(doc, selectorRow, selectorCol);
	}

	/**
	 * @Title: getScoreSummaryArrayList
	 * @Description: 以ArrayList<String[]>返回成绩大表
	 * @param: @return
	 * @param: @throws Exception
	 * @return: ArrayList<String[]> {{课程号, 课程名, 学分, 成绩, 绩点, 学期}, ...}
	 */
	public ArrayList<String[]> getScoreSummaryArrayList() throws Exception {
		Document doc = getScoreSummaryDocument();
		int colCount = 6;
		ArrayList<String[]> arrayList = new ArrayList<String[]>();
		// select rows
		Elements rows = doc.select("tr:has(td:eq(11)):not(tr:has(td:gt(11)))");
		for (Element row : rows) {
			// select cols twice
			Elements cols = null;
			String[] selectorCol = {"td:lt(" + colCount + ")", "td:gt(" + (colCount - 1) + ")"};
			for (String selector : selectorCol) {
				cols = row.select(selector);
				if (cols.get(0).html().compareTo("&nbsp;") != 0) {
					String[] items = new String[colCount];
					for (int j = 0; j < colCount; j++) {
						items[j] = cols.get(j).html();
					}
					arrayList.add(items);
				}
			}
		}
		return arrayList;
	}
}
