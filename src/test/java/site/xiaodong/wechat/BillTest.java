package site.xiaodong.wechat;

import org.junit.Test;

public class BillTest {

	@Test
	public void testHelp() {
		String args[] = { "-f", "EnMicroMsg.db", "-c", "没坏账饭友群", "-s", "2017-09-01", "-e", "2017-10-01" };
		Bill.main(args);
	}

	@Test
	public void testDeleted() {
		String args[] = { "-f", "EnMicroMsg.db", "-c", "没坏账饭友群", "-s", "2017-09-01", "-e", "2017-10-01", "-d" };
		Bill.main(args);
	}

	@Test
	public void testDetail() {
		String args[] = { "-f", "EnMicroMsg.db", "-c", "没坏账饭友群", "-s", "2017-09-01", "-e", "2017-10-01", "-i", "log.txt" };
		Bill.main(args);
	}
	
	//java -jar wechat-db-parser.jar -f EnMicroMsg.db -c "没坏账饭友群" -s 2017-09-01 -e 2017-10-01 -i 2017-09_detail.txt
}
