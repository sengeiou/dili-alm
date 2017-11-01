package com.dili.alm.controller;

import com.dili.alm.domain.Files;
import com.dili.alm.service.FilesService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-10-20 14:47:50.
 */
@Api("/files")
@Controller
@RequestMapping("/files")
public class FilesController {
    @Autowired
    FilesService filesService;

    private static final String MILESTONES_PATH_PREFIX = "fileupload/milestones/";

    @ApiOperation("跳转到Files页面")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "files/index";
    }

    @ApiOperation(value="查询Files", notes = "查询Files，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Files", paramType="form", value = "Files的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Files> list(Files files) {
        return filesService.list(files);
    }

    @ApiOperation(value="分页查询Files", notes = "分页查询Files，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Files", paramType="form", value = "Files的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(Files files) throws Exception {
        return filesService.listEasyuiPageByExample(files, true).toString();
    }

    @ApiOperation("新增Files")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Files", paramType="form", value = "Files的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Files files) {
        filesService.insertSelective(files);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改Files")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Files", paramType="form", value = "Files的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Files files) {
        filesService.updateSelective(files);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除Files")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Files的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        filesService.delete(id);
        return BaseOutput.success("删除成功");
    }

	/**
	 * 实现多文件上传
	 * */
	@RequestMapping(value = "filesUpload", method = RequestMethod.POST)
	@ResponseBody
	public void filesUpload(@RequestParam("file") MultipartFile[] files, HttpServletRequest request, HttpServletResponse response, @RequestParam String milestonesId) throws IOException {
		if(files == null || files.length == 0){
			response.getWriter().write("文件不存在");
		}
		//指定当前项目的相对路径
		String path = MILESTONES_PATH_PREFIX + milestonesId + "/";
		File pathFile = new File(path);
		//父目录不存在则创建
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		for(MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			int size = (int) file.getSize();
			if(StringUtils.isBlank(fileName) || size == 0){
				continue;
			}
			System.out.println(fileName + "-->" + size);
			File dest = new File(path + fileName);
			try {
				byte[] bytes = file.getBytes();
				BufferedOutputStream buffStream =
						new BufferedOutputStream(new FileOutputStream(dest));
				buffStream.write(bytes);
				buffStream.close();
			} catch (Exception e) {
				System.out.println("上传失败,文件名:" + fileName + ",消息:" + e.getMessage());
				response.getWriter().write("<script>parent.callback('上传失败,文件名:" + fileName + ",消息:" + e.getMessage() + "')</script>");
				break;
			}
			Files tmpFiles = DTOUtils.newDTO(Files.class);
			tmpFiles.setName(file.getOriginalFilename());
			tmpFiles.setLength(file.getSize());
			tmpFiles.setMilestonesId(Long.parseLong(milestonesId));
			tmpFiles.setSuffix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1));
			tmpFiles.setUrl(path);
			tmpFiles.setModified(new Date());
			UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
			//			先查找文件名是否存在，存在则覆盖
			Files filesCondition = DTOUtils.newDTO(Files.class);
			filesCondition.setMilestonesId(Long.parseLong(milestonesId));
			filesCondition.setName(file.getOriginalFilename());
			List<Files> files1 = filesService.list(filesCondition);
			if(ListUtils.emptyIfNull(files1).isEmpty()) {
				tmpFiles.setCreated(new Date());
				if(userTicket != null) {
					tmpFiles.setCreateMemberId(userTicket.getId());
				}
				filesService.insertSelective(tmpFiles);
			}else{
				tmpFiles.setId(files1.get(0).getId());
				if(userTicket != null) {
					tmpFiles.setModifyMemberId(userTicket.getId());
				}
				filesService.updateSelective(tmpFiles);
			}
		}
		response.getWriter().write("<script>parent.callback('上传成功')</script>");
	}

	@RequestMapping("download")
	public String downLoad(@RequestParam(value="name", required = true) String fileName, HttpServletRequest request, HttpServletResponse response, @RequestParam String milestonesId){
		String filePath = MILESTONES_PATH_PREFIX + milestonesId + "/";
		File file = new File(filePath + fileName);
		if(file.exists()){//判断文件父目录是否存在
			FileInputStream fis = null; //文件输入流
			BufferedInputStream bis = null;
			OutputStream os = null; //输出流
			try {
				os = response.getOutputStream();
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				byte[] buffer = new byte[bis.available()];
				// 清空response
				response.reset();
				response.setContentType("application/force-download");//或者使用二进制下载多种类型的文件:application/octet-stream
				response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes()));
				response.addHeader("Content-Length", "" + file.length());
				int i = bis.read(buffer);
				while(i != -1){
					os.write(buffer);
					i = bis.read(buffer);
				}
				os.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				System.out.println("----------file download" + fileName);
				try {
					bis.close();
					fis.close();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}