/**
 * @Title: WebAPI.java
 * @Package shuhelper.web
 * @Description: TODO
 * @author Roll (roll@busyliving.me)
 * @date 2017年1月13日 下午10:56:52
 * @version V1.0
 */
package shuhelper.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @ClassName: WebAPI
 * @Description:
 *  1、基本的网络API抽象类，应该被继承为更高级的API，如CJWebAPI和XKWebAPI。
 *  2、urlLogin/urlIndex/urlCaptcha应该在子类的构造函数中定义。
 * @author Roll (roll@busyliving.me)
 * @date 2017年1月13日 下午10:56:52
 *
 */
public abstract class WebAPI {
	/**
	 * @Fields urlLogin : 登录页面的URL
	 */
	protected String urlLogin;

	/**
	 * @Fields urlCaptcha : 验证码图片的URL
	 */
	protected String urlCaptcha;

	/**
	 * @Fields urlIndex : 登陆后的主页URL
	 */
	protected String urlIndex;

	/**
	 * @Fields httpClient : http客户端
	 */
	protected CloseableHttpClient httpClient;

	/**
	 * @Fields strUserNo : 学号
	 */
	protected String strUserNo;

	/**
	 * <p>Title: WebAPI</p>
	 * <p>Description: 构造函数</p>
	 * @throws ClassNotFoundException 
	 */
	public WebAPI() throws Exception {
		httpClient = HttpClients.createDefault();
	}

	/**
	 * @Title: getCaptcha
	 * @Description: 保存验证码图片到本地文件，并返回文件绝对路径
	 * @param: @return
	 * @param: @throws IOException
	 * @return: String
	 */
	public String getCaptcha() throws IOException {
		String fileName = Utils.getProperty("CaptchaFileName");
		String filePath = null;
		HttpGet getCaptcha = new HttpGet(urlCaptcha);
		CloseableHttpResponse response = httpClient.execute(getCaptcha);
		try {
			File fileCaptcha = new File(fileName);
			FileOutputStream fileOutputStream = new FileOutputStream(fileCaptcha);
			try {
				response.getEntity().writeTo(fileOutputStream);
				filePath = fileCaptcha.getCanonicalPath();
			} finally {
				fileOutputStream.close();
			}
		} finally {
			response.close();
		}
		return filePath;
	}

	/**
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @Title: login
	 * @Description: 指定学号、密码、验证码登录，登录成功返回"OK"，否则返回错误信息
	 * @param @param strUserNo
	 * @param @param strPasswd
	 * @param @param strCaptcha
	 * @param @return
	 * @return String
	 * @throws
	 */
	public String login(String strUserNo, String strPasswd, String strCaptcha)
	throws ClientProtocolException, IOException {
		// 访问登录页面
		this.strUserNo = strUserNo;
		Document docLogin = Utils.getDocument(httpClient, urlLogin);

		// 设定Post参数
		String[] loginFields = Utils.getLoginFields(docLogin);
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add(new BasicNameValuePair(loginFields[0], strUserNo));
		postData.add(new BasicNameValuePair(loginFields[1], strPasswd));
		postData.add(new BasicNameValuePair(loginFields[2], strCaptcha));

		// 登录
		String strLoginResult = "OK";
		Document docLoginPage = Utils.postDocument(httpClient, urlLogin, postData);
		Element errLogin = docLoginPage.getElementById("divLoginAlert");
		if (errLogin != null) {
			strLoginResult = errLogin.html();
		}
		return strLoginResult;
	}

	/**
	 * @Title: isLogin
	 * @Description: 访问主页，判断是否处于登录状态
	 * @param @return
	 * @param @throws Exception
	 * @return boolean
	 * @throws
	 */
	public boolean isLogin() throws Exception {
		Document docIndexPage = Utils.getDocument(httpClient, urlIndex);
		return docIndexPage.getElementsMatchingText(strUserNo).size() != 0;
	}
}

