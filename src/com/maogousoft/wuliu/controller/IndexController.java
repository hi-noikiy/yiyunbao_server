package com.maogousoft.wuliu.controller;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.jfinal.aop.ClearInterceptor;
import com.jfinal.aop.ClearLayer;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.maogousoft.wuliu.common.collections.KV;
import com.maogousoft.wuliu.common.json.Result;
import com.maogousoft.wuliu.service.image.FileInfo;
import com.maogousoft.wuliu.service.image.ImageService;

/**
 * @description 首页
 */
public class IndexController extends Controller {

	private Logger log = Logger.getLogger(this.getClass());

	public void index() throws SQLException {
	}

	/**
	 * 文件上传3.3.10公共接口
	 */
//	@ClearInterceptor(ClearLayer.ALL)
	public void upload() {
		try {
			UploadFile uf = getFile();
			FileInfo fileInfo = ImageService.saveTempFile(uf.getFile());

			String token = getPara("token");
			int file_type = getParaToInt("file_type",1);//默认为图片

			log.debug("upload file,token=" + token + ",file_type=" + file_type + ",url=" + fileInfo.getVirtualUrl());

			Result result = Result.success().data("item", new KV("url",fileInfo.getVirtualUrl()));
			renderHtml(result.toJsonString());
		}catch(Throwable t) {
			log.error("上传接口失败:" + t.getMessage(), t);
			Result result = Result.fail();
			renderHtml(result.toJsonString());
		}
	}
}
