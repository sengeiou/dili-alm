package com.dili.alm.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.Files;
import com.dili.alm.service.FilesService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.metadata.ValueProviderUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-20 14:47:50.
 */
@Api("/files")
@Controller
@RequestMapping("/files")
public class FilesController {
	@Autowired
	FilesService filesService;

	@ApiOperation("跳转到Files页面")
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "files/index";
	}

	@ApiOperation(value = "查询Files", notes = "查询Files，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Files", paramType = "form", value = "Files的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Map> list(Files files) {
		List<Files> list = filesService.list(files);
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject fileTypeProvider = new JSONObject();
		fileTypeProvider.put("provider", "fileTypeProvider");
		metadata.put("type", fileTypeProvider);

		JSONObject projectVersionProvider = new JSONObject();
		projectVersionProvider.put("provider", "projectVersionProvider");
		metadata.put("versionId", projectVersionProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("createMemberId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("created", datetimeProvider);

		try {
			return ValueProviderUtils.buildDataByProvider(metadata, list);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@ApiOperation(value = "分页查询Files", notes = "分页查询Files，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Files", paramType = "form", value = "Files的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(Files files) throws Exception {
		return filesService.listEasyuiPageByExample(files, true).toString();
	}

	@ApiOperation("新增Files")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Files", paramType = "form", value = "Files的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(Files files) {
		filesService.insertSelective(files);
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改Files")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Files", paramType = "form", value = "Files的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(Files files) {
		filesService.updateSelective(files);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除Files")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "Files的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		Files files = this.filesService.get(id);
		if (files != null) {
			filesService.delete(id);
		}
		return BaseOutput.success("删除成功").setData(files.getName());
	}

	/**
	 * 实现多文件上传
	 */
	@RequestMapping(value = "/filesUpload", method = RequestMethod.POST)
	@ResponseBody
	public BaseOutput<Object> filesUpload(@RequestParam("file") MultipartFile[] files,
			@RequestParam(required = false) Long projectId, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		List<Files> uploadFiles;
		try {
			uploadFiles = this.filesService.uploadFile(files, projectId);
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
		return BaseOutput.success().setData(uploadFiles);
	}

	@RequestMapping("/download")
	public void downLoad(@RequestParam Long id, HttpServletRequest request, HttpServletResponse response) {
		String filePath = FilesService.MILESTONES_PATH_PREFIX + "/";
		Files files = this.filesService.get(id);
		File file = new File(filePath + files.getName());
		if (file.exists()) {// 判断文件父目录是否存在
			FileInputStream fis = null; // 文件输入流
			BufferedInputStream bis = null;
			OutputStream os = null; // 输出流
			try {
				os = response.getOutputStream();
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				byte[] buffer = new byte[bis.available()];
				// 清空response
				response.reset();
				// 设置为下载application/x-download
				response.setContentType("application/x-download charset=UTF-8");
				//
				response.setContentType("application/force-download");// 或者使用二进制下载多种类型的文件:application/octet-stream
				response.setHeader("Content-Disposition",
						"attachment;fileName=" + URLEncoder.encode(files.getName(), "UTF-8"));
				response.addHeader("Content-Length", "" + file.length());
				int i = bis.read(buffer);
				while (i != -1) {
					os.write(buffer);
					i = bis.read(buffer);
				}
				os.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("----------file download" + files.getName());
				try {
					bis.close();
					fis.close();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@RequestMapping(value = "/images")  
    @ResponseBody  
	public String imagesReader(HttpServletRequest request,  
            HttpServletResponse response, @RequestParam Long id) {  
		String filePath = FilesService.MILESTONES_PATH_PREFIX + "/";
		Files files = this.filesService.get(id);
		String path = filePath + files.getName();
        FileInputStream fis = null;  
        OutputStream os = null;  
        try {  
            fis = new FileInputStream(path);  
            os = response.getOutputStream();  
            int count = 0;  
            byte[] buffer = new byte[1024 * 8];  
            while ((count = fis.read(buffer)) != -1) {  
                os.write(buffer, 0, count);  
                os.flush();  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        try {  
            fis.close();  
            os.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return "ok";  
    }  

}