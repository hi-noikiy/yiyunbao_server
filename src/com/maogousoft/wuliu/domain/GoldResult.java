package com.maogousoft.wuliu.domain;

/**
 * 帐户金额调整结果
 * @author yangfan(kenny0x00@gmail.com) 2013-6-5 上午12:50:32
 */
public class GoldResult {

	private double beforeGold;
	private double afterGold;

	public double getBeforeGold() {
		return beforeGold;
	}

	public void setBeforeGold(double beforeGold) {
		this.beforeGold = beforeGold;
	}

	public double getAfterGold() {
		return afterGold;
	}

	public void setAfterGold(double afterGold) {
		this.afterGold = afterGold;
	}

	/**
	 * 获取交易金额
	 * @return
	 */
	public double getAmount() {
		return afterGold - beforeGold;
	}

	/**
	 * 1-收入
	 * 0-支出
	 * @return
	 */
	public int getIncomeType() {
		if(afterGold > beforeGold) {
			return 1;
		}else {
			return 0;
		}
	}

}
