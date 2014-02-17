package com.maogousoft.wuliu.common.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * 数据库工具
 * @author yangfan(kenny0x00@gmail.com) 2013-3-25 下午11:24:52
 */
public class DBUtils {

	public static void dumpResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = rsmd.getColumnCount();
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i <= count; i++) {
			sb.append(rsmd.getColumnName(i));
			sb.append("=");
			sb.append(rs.getString(i));
			sb.append("\n");
		}
		System.out.println(sb);//NOSONAR
	}
}
