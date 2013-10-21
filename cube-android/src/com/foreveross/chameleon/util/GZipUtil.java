package com.foreveross.chameleon.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 压缩/解压缩工具类
 * 
 * @author chencao 2012-11-8 created
 * 
 */
public final class GZipUtil {
	/**
	 * 压缩操作
	 * 
	 * @param data
	 *            待压缩的数据
	 * @return 压缩后的数据
	 * @throws IOException
	 */
	public static byte[] gzip(byte[] data) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipEntry ze = new ZipEntry("servletservice");
		ZipOutputStream zos = new ZipOutputStream(baos);
		zos.putNextEntry(ze);
		zos.write(data, 0, data.length);
		zos.close();
		byte[] zipBytes = baos.toByteArray();
		return zipBytes;
	}

	/**
	 * 解压缩操作
	 * 
	 * @param zipBytes
	 *            待解压缩的数据
	 * @return 解压后的数据
	 * @throws IOException
	 */
	public static byte[] unzip(byte[] zipBytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
		ZipInputStream zis = new ZipInputStream(bais);
		zis.getNextEntry();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final int BUFSIZ = 4096;
		byte inbuf[] = new byte[BUFSIZ];
		int n;
		try {
			while ((n = zis.read(inbuf, 0, BUFSIZ)) != -1) {
				baos.write(inbuf, 0, n);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		byte[] data = baos.toByteArray();
		zis.close();
		return data;
	}

	public static void main(String[] args) throws IOException {

		for (int i = 0; i < 1000; i++) {
			String a = "<pda>  <pda_customer_info>    <pda_id></pda_id>    <o_name>深圳分公司党委工作部</o_name>    <food_love></food_love>    <t_flt_time></t_flt_time>    <headship>部长</headship>    <mobile></mobile>    <t_flown_carrier></t_flown_carrier>    <address>南城区格林小城香橙院３栋      １４０３室</address>    <flt_date>08-8-25</flt_date>    <origin>PEK</origin>    <check_in_love></check_in_love>    <sex>女</sex>    <drink_love></drink_love>    <customer_tier>80</customer_tier>    <english_name>MAJUN</english_name>    <member_no></member_no>    <customer_id>520000609968    </customer_id>    <t_flown_flt_no></t_flown_flt_no>    <flt_no>3112 </flt_no>    <destination>CAN</destination>    <seat_love></seat_love>    <t_destination></t_destination>    <seatno>3D</seatno>    <chineseName>马军</chineseName>    <transfer></transfer>    <ffp_tier></ffp_tier>    <certificate_id>-2300990302-T</certificate_id>    <email></email>    <corp_id>CZ  </corp_id>    <pda_flt_id>1</pda_flt_id>    <mileage>";

			byte[] abyte = a.getBytes("UTF-8");

			byte[] testbyte1 = null;

			byte[] testbyte2 = null;

			long begin = System.currentTimeMillis();

			testbyte1 = gzip(abyte);

			File file = new File("D:\\gziptest");

			FileOutputStream fos = new FileOutputStream(file);

			fos.write(testbyte1);

			fos.close();

			long end = System.currentTimeMillis();

			long time = end - begin;
		}
		// ??1

		// testbyte2 = unzip(testbyte1);

		// ??2

		// byte[]<->String<->byte[]

		// String b = new sun.misc.BASE64Encoder().encode(testbyte1);
		//
		// byte[] testbyte3 = new sun.misc.BASE64Decoder().decodeBuffer(b);
		//
		// long desbegin = System.currentTimeMillis();
		//
		// testbyte2 = unzip(testbyte3);
		//
		// String test = new String(testbyte2, "UTF-8");
		//
		// long desend = System.currentTimeMillis();
		//
		// long endtime = desend - desbegin;
		//
		// System.out.println(testbyte1.length
		//
		// + "   " + a.length() + "压缩时间是：" + time + "解压时间是：" + endtime);

	}
}
