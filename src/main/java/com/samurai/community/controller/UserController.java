package com.samurai.community.controller;

import com.samurai.community.Service.UserService;
import com.samurai.community.Util.CommunityUtil;
import com.samurai.community.Util.HostHolder;
import com.samurai.community.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }

    /* A method which can be used to upload the files to local directory, an alternative is using online Image Hosting */
    // @LoginRequired
    @PostMapping("/upload")
    public String uploadAvatar(MultipartFile avatarImage, Model model) {
        if (avatarImage == null) {
            model.addAttribute("error", "You haven't selected a picture yet!");
            return "site/setting";
        }
        String fileName = avatarImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);

        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "The file format is not correct!");
            return "site/setting";
        }

        // Generate random file names
        fileName = CommunityUtil.generateUUID() + suffix;
        // Determine the path to the file
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // Storage files
            avatarImage.transferTo(dest);
        } catch (IOException e) {
            LOGGER.error("Failed to upload files: ", e.getMessage());
            throw new RuntimeException("File upload failed, server exception occurred!", e);
        }
        User user = hostHolder.getUser();
        String avatarUrl = domain + contextPath + "/user/avatar/" + fileName;
        userService.updateAvatar(user.getId(), avatarUrl);
        return "redirect:/";
    }

    /* deprecated method */
    @GetMapping("/avatar/{fileName}")
    public void getAvatar(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // Server storage path
        fileName = uploadPath + "/" + fileName;
        // suffix of the file
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // Response images
        response.setContentType("image/" + suffix);

        try (FileInputStream fis = new FileInputStream(fileName);
             OutputStream os = response.getOutputStream(); ) {
            //new feature in java7(called try-with-resources)
            //surrounding the fis with try() means resources will be closed automatically
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read file: ", e.getMessage());
        }
    }

    /* update avatar path */
    @PostMapping("/avatar/url")
    @ResponseBody
    public String updateAvatarUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJSONString(1, "file name cannot be empty");
        }
        String url = uploadPath + "/" + fileName;
        userService.updateAvatar(hostHolder.getUser().getId(), url);
        return CommunityUtil.getJSONString(0);
    }


}
