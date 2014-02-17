package com.maogousoft.wuliu.domain;

import com.maogousoft.wuliu.WuliuConstants;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-4-8 下午10:40:16
 */
public class Area extends BaseModel<Area> {

	private static final long serialVersionUID = 1L;

	public static final Area dao = new Area();

	public static final int ID_ShangHai = 310000;
	public static final int ID_JiangShu = 320000;
	public static final int ID_ZheJiang = 330000;
	public static final int ID_Beijing = 110000;
	public static final int ID_TianJin = 120000;
	public static final int ID_HeBei = 120000;

	public static Area getAreaByIdWithCache(final Integer areaId) {
		String sql = "select id,pid,name,short_name,deep,status from logistics_area where id=? and status=0";
		Area area = Area.dao.findFirstByCache(WuliuConstants.CACHE_AREA, areaId, sql, areaId);
		return area;
	}

}
