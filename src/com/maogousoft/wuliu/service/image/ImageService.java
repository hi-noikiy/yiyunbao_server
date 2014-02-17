package com.maogousoft.wuliu.service.image;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Model;
import com.maogousoft.wuliu.BaseConfig;
import com.maogousoft.wuliu.common.exception.BusinessException;
import com.maogousoft.wuliu.common.util.TimeUtil;

/**
 * @author yangfan(kenny0x00@gmail.com) 2013-5-1 下午3:15:55
 */
public class ImageService {

	private static final Logger log = Logger.getLogger(ImageService.class);

	private static final AtomicLong counter = new AtomicLong(0);

	/**
	 * 保存文件，并返回文件信息
	 * @param fileName
	 * @param file
	 * @return
	 */
	public static FileInfo saveFile(String fileName, File file) {
		//获取上传的根目录
		String uploadBasePath = BaseConfig.me().getProperty("upload.basepath");
		String uploadVirtualUrlRoot = BaseConfig.me().getProperty("upload.baseurl");

		return saveFileInternal(file, uploadBasePath, uploadVirtualUrlRoot);
	}

	/**
	 * 保存临时文件（用于聊天上传之类）
	 * @param file
	 * @return
	 */
	public static FileInfo saveTempFile(File file) {
		//获取上传的根目录
		String uploadBasePath = BaseConfig.me().getProperty("upload.temp.basepath");
		String uploadBaseUrl = BaseConfig.me().getProperty("upload.temp.baseurl");

		return saveFileInternal(file, uploadBasePath, uploadBaseUrl);
	}

	private static FileInfo saveFileInternal(File file, String basepath, String baseurl) {
		//获取当前上传的目录
		final String uploadDirName = TimeUtil.format(new Date(), "yyyy/MM/dd");

		//创建上传目录
		File uploadDir = new File(FilenameUtils.concat(basepath, uploadDirName));
		if(!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		//文件名
		String filenameSuffix = TimeUtil.format(new Date(), "yyyyMMdd_HHmmss") + "_" + counter.getAndIncrement();
		String fileExt = FilenameUtils.getExtension(file.getName());
		final String fileName = filenameSuffix + "." + fileExt;

		String filePath = FilenameUtils.concat(uploadDir.getAbsolutePath(), fileName);
		log.debug("save temp file to " + filePath);
		file.renameTo(new File(filePath));

		//文件信息
		FileInfo info = new FileInfo();
		String virtualUrl = baseurl + "/" + uploadDirName + "/" + fileName;
		info.setVirtualUrl(virtualUrl.replaceAll("\\\\", "/"));
		info.setFilename(fileName);
		return info;
	}

	public static FileInfo saveImage(byte[] bytes) {
		if(bytes == null) {
			return null;
		}
		File file;
		try {
			file = File.createTempFile("wuliu_photo_", ".jpg");
			FileUtils.writeByteArrayToFile(file, bytes);
		} catch (IOException e) {
			throw new BusinessException("无法保存图片", e);
		}
		return saveFile(file.getName(), file);
	}

	/**
	 * 保存二进制的图片信息，并将图片的url写入到model的字段中
	 * @param model model
	 * @param field 字段名
	 * @param bytes 图片的二进制数组
	 */
	public static <M extends Model<M>> void saveImageToModel(Model<M> model, String field, byte[] bytes) {
		FileInfo fileInfo = ImageService.saveImage(bytes);
		if(fileInfo != null) {
			model.set(field, fileInfo.getVirtualUrl());
		}
	}

}
