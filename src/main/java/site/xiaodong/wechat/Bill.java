package site.xiaodong.wechat;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class Bill {
	private static String filePath;
	private static String chatroom;
	private static String start;
	private static String end;
	private static String myself;
	private static int myselfId;
	private static Connection conn;
	private static PreparedStatement ps;
	private static ResultSet rs;

	private static List<String> nameList = new ArrayList<String>();
	private static List<BigDecimal> sumList = new ArrayList<BigDecimal>();
	private static Map<String, Integer> nameIndex = new HashMap<String, Integer>();

	public static void main(String[] args) {
		try {
			getOption(args);
			getConnect();
			getAllMemberList();
			// getChatroomMemberList();
			getSum();
			output();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static void output() {
		for (int i = 0; i < sumList.size(); ++i) {
			if (Double.parseDouble(sumList.get(i).toString()) > 0) {
				System.out.println(nameList.get(i) + "\t" + sumList.get(i));
			}
		}
	}

	/**
	 * 统计
	 * 
	 * @throws Exception
	 */
	private static void getSum() throws Exception {
		Timestamp startDate, endDate;
		startDate = Timestamp.valueOf(start);
		endDate = Timestamp.valueOf(end);
		ps = conn.prepareStatement("select m.content from message m join chatroom c on m.talker = c.chatroomname "
				+ "left join rcontact r on r.username = c.chatroomname "
				+ "where r.nickname = ? and m.createTime > ? and m.createTime < ? ");
		ps.setString(1, chatroom);
		ps.setLong(2, startDate.getTime());
		ps.setLong(3, endDate.getTime());
		rs = ps.executeQuery();
		while (rs.next()) {
			String m = rs.getString(1);
			if (!m.matches(Util.messageRegex)) {
				continue;
			}
			String wxid, valueStr;
			int index;
			if (m.indexOf(":") != -1) {
				wxid = m.substring(0, m.indexOf(":"));
				valueStr = m.substring(m.indexOf(":") + 2, m.length()).trim();
				index = nameIndex.get(wxid);
			} else {
				index = myselfId;
				valueStr = m;
			}
			BigDecimal a = sumList.get(index).add(new BigDecimal(valueStr));
			sumList.set(index, a);
		}
	}

	/**
	 * 获取所有通讯录名单
	 * 
	 * @throws Exception
	 */
	private static void getAllMemberList() throws Exception {
		ps = conn.prepareStatement("select username, conRemark, nickname from rcontact ");
		rs = ps.executeQuery();
		int i = 0;
		while (rs.next()) {
			String name = (rs.getString(2).equals("") ? rs.getString(3) : rs.getString(2));
			nameList.add(name);
			sumList.add(new BigDecimal(0));
			nameIndex.put(rs.getString(1), i);
			if (name.equals(myself)) {
				myselfId = i;
			}
			i++;
		}
	}

	/**
	 * 获取群聊通讯录名单
	 * 
	 * @throws Exception
	 */
	private static void getChatroomMemberList() throws Exception {
		ps = conn.prepareStatement(
				"select memberlist from chatroom c left join rcontact r on r.username = c.chatroomname where r.nickname = ?");

		ps.setString(1, chatroom);
		rs = ps.executeQuery();
		String memberlist = null;
		if (rs.next()) {
			memberlist = rs.getString(1);
		} else {
			System.out.println(chatroom + " not found");
			System.exit(0);
		}
		memberlist = memberlist.replaceAll(";", "','");

		ps = conn.prepareStatement(
				"select username, conRemark, nickname from rcontact where username in ('" + memberlist + "')");
		rs = ps.executeQuery();
		int i = 0;
		while (rs.next()) {
			String name = (rs.getString(2).equals("") ? rs.getString(3) : rs.getString(2));
			nameList.add(name);
			sumList.add(new BigDecimal(0));
			nameIndex.put(rs.getString(1), i);
			if (name.equals(myself)) {
				myselfId = i;
			}
			i++;
		}
	}

	private static void getConnect() throws Exception {
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:" + filePath);

	}

	private static void getOption(String[] args) {
		Options options = new Options();
		options.addOption("h", "help", false, "print this usage information");
		options.addOption("f", "file", true, "path of wechat database file");
		options.addOption("c", "chatname", true, "chatroom name");
		options.addOption("m", "myname", true, "my name");
		options.addOption("s", "start", true, "start date eg. 2017-01");
		options.addOption("e", "end", true, "chatroom name eg. 2017-01");

		CommandLineParser parser = new BasicParser();
		CommandLine commandLine;
		try {
			commandLine = parser.parse(options, args);
			if (commandLine.hasOption('h')) {
				Usage(options);
			}
			filePath = commandLine.getOptionValue("f");
			if (Util.isBlank(filePath)) {
				Usage(options);
			}
			chatroom = commandLine.getOptionValue("c");
			if (Util.isBlank(chatroom)) {
				Usage(options);
			}
			myself = commandLine.getOptionValue("m");
			if (Util.isBlank(myself)) {
				Usage(options);
			}
			start = commandLine.getOptionValue("s");
			if (Util.isBlank(start) || !start.matches(Util.dateRegex)) {
				Usage(options);
			}
			start += " 00:00:00";
			end = commandLine.getOptionValue("e");
			if (Util.isBlank(end) || !end.matches(Util.dateRegex)) {
				Usage(options);
			}
			end += " 00:00:00";
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static void Usage(Options options) {
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("Options", options);
		System.exit(0);
	}
}
