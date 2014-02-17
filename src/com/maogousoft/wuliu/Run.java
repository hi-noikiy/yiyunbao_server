package com.maogousoft.wuliu;

import com.jfinal.core.JFinal;

/**
 *
 * @author yangfan (kenny0x00@gmail.com) 2013-1-14上午12:12:39
 */
public class Run {

	public static void main(String[] args) {
		JFinal.start("WebRoot", 80, "/service", 5);
	}

}
