package ch.bfh.iam.oauth.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;
import com.google.gson.Gson;

import ch.bfh.iam.oauth.common.AccessToken;
import ch.bfh.iam.oauth.init.Config;

/**
 * 
 * @author yandypiedra
 *
 */
@Controller
public class FlowController {

	private static final Logger logger = LogManager
			.getLogger(FlowController.class.getName());
	private static final int MAX_FILE_SIZE = 1000 * 2048;
	private static final int MAX_MEM_SIZE = 1000 * 2048;

	@RequestMapping(value = "/oauthAuthorization", method = RequestMethod.POST)
	public void oauthAuthorization(HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			FileUploadException, ServletException {

		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		if (isMultipart) {
			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();

			// Set factory constraints
			factory.setSizeThreshold(MAX_MEM_SIZE);
			factory.setRepository(new File("/Users/yandypiedra/Documents"));

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Set overall request size constraint
			upload.setSizeMax(MAX_FILE_SIZE);

			// Parse the request
			List<FileItem> items = upload.parseRequest(request);
			Iterator<FileItem> iter = items.iterator();
			String authRequest = null;
			HttpSession session = request.getSession();

			while (iter.hasNext()) {
				FileItem item = iter.next();

				if (item.isFormField()) {
					logger.info("Processing form fields");
					final String server = item.getString();

					final String state = UUID.randomUUID().toString();

					String authEndpoint = Config.getConfig().getString(server + ".authEndpoint");
					authRequest = authEndpoint
							.concat("?response_type=code")
							.concat("&client_id=")
							.concat(Config.getConfig().getString(server + ".client_id"))
							.concat("&redirect_uri=")
							.concat(Config.getConfig().getString("redirect_uri"))
							.concat("&state=" + state);

					session.setAttribute("server", server);
					session.setAttribute("state", state);
				} 
				else {
					logger.info("Processing file input");
					String fileName = item.getName();
					Long fileSize = item.getSize();
					if (fileName == null || fileName.isEmpty() || fileSize == 0) {
						logger.error("No file was selected");
						response.sendRedirect(request.getContextPath());
						return;
					}
					session.setAttribute("upFile", item);
				}
			}

			response.sendRedirect(response.encodeRedirectURL(authRequest));
		}
	}

	@RequestMapping(value = "/pesonalData", method = RequestMethod.GET)
	public String personalData(
			@RequestParam(value = "code", required = true) String code,
			@RequestParam(value = "state", required = true) String state,
			HttpServletRequest request, Model model) throws Exception {

		HttpSession session = request.getSession(false);
		if (session != null) {
			final String server = (String) session.getAttribute("server");
			final String saveState = (String) session.getAttribute("state");
			final FileItem item = (FileItem) session.getAttribute("upFile");
			session.invalidate();

			if (saveState == null || !saveState.equals(state) || code == null) {
				String error = "Error while processing the request!\nReason: State is invalid or the authorization fails.!";
				logger.error(error);
				return handleError(model, error);
			}
			
			model.addAttribute("code", code);
			model.addAttribute("state", state);

			String tokenRequest = Config.getConfig().getString(server + ".accessTokenEndpoint")
					.concat("?grant_type=authorization_code")
					.concat("&client_id=")
					.concat(Config.getConfig().getString(server + ".client_id"))
					.concat("&client_secret=")
					.concat(Config.getConfig().getString(server + ".client_secret"))
					.concat("&redirect_uri=")
					.concat(URLEncoder.encode(Config.getConfig().getString("redirect_uri"),"UTF-8")).concat("&code=").concat(code);

			HttpPost httpost = new HttpPost(tokenRequest);
			httpost.addHeader("Accept", "application/json");

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpResponse resp = httpClient.execute(httpost);

			if (resp.getStatusLine().getStatusCode() != 200) {
				String error = "Error, HTTP status code "
						+ resp.getStatusLine().getStatusCode() + "\nReason: "
						+ resp.getStatusLine().getReasonPhrase();
				logger.error(error);
				return handleError(model, error);
			}

			Gson gson = new Gson();
			Reader streamReader = new InputStreamReader(resp.getEntity().getContent());
			AccessToken accToken = gson.fromJson(streamReader,
					AccessToken.class);

			model.addAttribute("accessToken", accToken.getAccess_token());

			DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
					Locale.getDefault().toString());

			DbxClient client = new DbxClient(config, accToken.getAccess_token());

			model.addAttribute("userName", client.getAccountInfo().displayName);
			model.addAttribute("country", client.getAccountInfo().country);

			File inputFile = new File(item.getName());
			item.write(inputFile);
			FileInputStream inputStream = new FileInputStream(inputFile);

			try {
				DbxEntry.File uploadedFile = client.uploadFile(
						"/" + item.getName(), DbxWriteMode.add(),
						item.getSize(), inputStream);
				model.addAttribute("uploadedFile", uploadedFile.name);
				model.addAttribute("uploadedFileSize", uploadedFile.humanSize);
			} finally {
				inputStream.close();
			}

			DbxEntry.WithChildren listing = client.getMetadataWithChildren("/");
			List<DbxEntry> filesInRoot = listing.children;
			model.addAttribute("filesInRoot", filesInRoot);
			
			return "personalData";
		}
		else {
			logger.error("The session was null!");
			String error = "An error has occurred. Please start again.";
			return handleError(model, error);
		}	
	}
	
	
	private String handleError(Model model, String error) {
		  model.addAttribute("errorMessage", error);
		  return "error";
		}
}
