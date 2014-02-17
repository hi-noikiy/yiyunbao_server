package com.maogousoft.wuliu.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-6-26 下午10:00:17
 */
public class IdGenerator {

	private final int category;
	private AtomicInteger counter = new AtomicInteger(0);
	private int lastDays;

	public IdGenerator(int category) {
		this.category = category;
	}

	public synchronized int nextId() {
		int days = Days.daysBetween(new DateTime("2013-01-01T00:00:01"), DateTime.now()).getDays();

		if(lastDays != days) {
			counter.set(0);
			lastDays = days;
		}
		int i = counter.getAndIncrement();
		String stri = String.format("%d%04d%05d", category, days, i);
//		int result = NumberUtils.toInt(str, RandomUtils.nextInt());
		int result = NumberUtils.toInt(stri);
		return result;
	}

	public static void main(String[] args) {
		final IdGenerator generator = new IdGenerator(1);
		for (int i = 0; i < 5; i++) {
			Thread t = new Thread() {
				public void run() {
					for (int j = 0; j < 10; j++) {
						int id = generator.nextId();
						System.out.println(id);
					}

				};
			};
			t.start();
		}
	}

}
