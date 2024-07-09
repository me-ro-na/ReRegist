package egovframework.iChat.common.view;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;

public class FileDownloadView extends AbstractView {

	public FileDownloadView() {
		setContentType("applicaiton/download;charset=utf-8");
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		File file = (File) model.get("downloadFile");
		String orgFileName = (String) model.get("orgFileName");
		response.setContentType(getContentType());
		response.setContentLength((int) file.length());
		String userAgent = request.getHeader("User-Agent");
		String fileName = file.getName();
		boolean ie = userAgent.indexOf("MSIE") > -1;

		if (ie) {
			fileName = URLEncoder.encode(fileName, "utf-8");
		} else {
			fileName = new String(fileName.getBytes("utf-8"));
		}
		if (orgFileName != null && !orgFileName.equals("")) {
			if (ie || userAgent.indexOf("Trident") > -1) {
				fileName = URLEncoder.encode(orgFileName, "utf-8").replaceAll("\\+", "%20");
			} else {
				fileName = new String(orgFileName.getBytes("utf-8"), "ISO-8859-1");
				response.setContentType("application/octer-stream");
			}
		}

		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
		response.setHeader("Content-Transfer-Encoding", "binary");
		OutputStream out = response.getOutputStream();

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);
			FileCopyUtils.copy(fis, out);
			out.flush();
		} catch (IOException e) {
			System.out.println("[ERROR] Exception - IOException");
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ex) {
					System.out.println("[ERROR] Exception - IOException");
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					System.out.println("[ERROR] Exception - IOException");
				}
			}
		}
	}

}
