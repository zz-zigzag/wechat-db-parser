package site.xiaodong.wechat;


import org.junit.Test;

public class BillTest {

	@Test
	public void testHelp() {
		String args[] = { "-f", "EnMicroMsg.db", "-m", "张晓东", "-c", "没坏账饭友群", "-s", "2017-09-01", "-e",
				"2017-10-01" };
		Bill.main(args);
	}

}
