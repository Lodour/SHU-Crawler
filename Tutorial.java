import java.util.ArrayList;
import java.util.Scanner;

import shuhelper.web.*;

public class Tutorial {
	
	private static Scanner in = new Scanner(System.in);
	
	public static void main(String[] args) throws Exception {
		testCJWebAPI();
		testXKWebAPI();
	}
	
	private static boolean login(WebAPI web) throws Exception {
		// 获取验证码图片
		String validatePath = web.getCaptcha();
		System.out.println("验证码图片存储在: " + validatePath);
		
		// 登录参数
		System.out.print("学号: ");
		String username = in.next();;
		System.out.print("密码: ");
		String password = in.next();;
		System.out.print("验证码: ");
		String validate = in.next();
		
		// 尝试登录
		String res = web.login(username, password, validate);
		System.out.println("登录结果: " + res);
		
		// 返回登录状态
		return web.isLogin();
	}
	
	public static void testCJWebAPI() throws Exception {
		// 实例化 CJWebAPI
		CJWebAPI CJ = new CJWebAPI();
		
		// 登录
		if (!login(CJ)) return;
		
		// 获取2016年冬季学期(20162)课程安排
		ArrayList<String[]> schedule = CJ.getScheduleArrayList("20162");
		output(schedule, "课程安排 - 2016冬");
		
		// 获取2016年秋季学期(20161)学期成绩
		ArrayList<String[]> scoreTerm = CJ.getScoreTermArrayList("20161");
		output(scoreTerm, "学期成绩 - 2016秋");
		
		// 获取成绩大表
		ArrayList<String[]> scoreSummary = CJ.getScoreSummaryArrayList();
		output(scoreSummary, "成绩大表");	
	}
	
	public static void testXKWebAPI() throws Exception {
		// 实例化 XKWebAPI
		XKWebAPI XK = new XKWebAPI();
		
		// 查看学期
		String[] termInfo = XK.getTermInfo();
		for (int i = 0; i < termInfo.length; i++) {
			System.out.printf("[%d] %s\n", i, termInfo[i]);
		}
		
		// 选择
		System.out.print("请选择学期编号: ");
		int termNo = in.nextInt();
		XK.setTerm(termNo);
		
		// 登录
		if (!login(XK)) return;
		
		// 已选课程
		ArrayList<String[]> courseTable = XK.getCourseTableArrayList();
		output(courseTable, "已选课程");
		
		// 选课排名
		ArrayList<String[]> enrollRank = XK.getEnrollRankArrayList();
		output(enrollRank, "选课排名");
		
		// 查询所有"0830"课程
		ArrayList<String[]> queryCourse = XK.getAllCourseArray("0830");
		output(queryCourse, "0830课程");		
	}
	
	private static void output(ArrayList<String[]> arrayList, String title) {
		System.out.println("================================");
		System.out.println(title);
		System.out.println("================================");
		for (String[] row : arrayList) {
			for (String col : row)
				System.out.print(col + "\t");
			System.out.println();
		}
		System.out.printf("-------- Total %d row(s) --------\n", arrayList.size());
	}
}
