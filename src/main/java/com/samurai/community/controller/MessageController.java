package com.samurai.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.samurai.community.entity.Message;
import com.samurai.community.entity.Page;
import com.samurai.community.entity.User;
import com.samurai.community.service.MessageService;
import com.samurai.community.service.UserService;
import com.samurai.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {
    @Autowired private MessageService messageService;

    @Autowired private HostHolder hostHolder;

    @Autowired private UserService userService;

    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user));

        List<Message> conversationList =
                messageService.findConversations(user, page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();

        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put(
                        "unreadCount",
                        messageService.findLetterUnreadCount(
                                user, message.getConversationId()));
                User targetUser =
                        user.getId() == message.getFromUser().getId()
                                ? message.getToUser()
                                : message.getFromUser();//find the user that current login user is talking to
                map.put("target", targetUser);
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        // find unread msg count
        int letterUnreadCount = messageService.findLetterUnreadCount(user, null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
/*        int noticeUnreadCount =
                messageService.findNoticeUnreadCount(hostHolder.getUser().getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);*/
        return "site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(
            @PathVariable("conversationId") String conversationId, Page page, Model model) {
        // pagination
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // msg list
        List<Message> letterList =
                messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("letter", message);
                map.put("fromUser",message.getFromUser());
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("letters", letters);
        model.addAttribute("target", getLetterTarget(conversationId));

        // set the msg as "read"
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            //messageService.readMessage(ids);
        }

        return "site/letter-detail";
    }

/*    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));
        List<Message> noticeList =
                messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVolist = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>(16);
                // notification
                map.put("notice", notice);
                // content
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                // display the sender
                map.put("fromUser", userService.findUserById(notice.getFromId()));
                noticeVolist.add(map);
            }
        }
        model.addAttribute("notices", noticeVolist);
        // set as read
        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "site/notice-detail";
    }*/

    // get the user who send the msg to current login user
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToUser().getId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
}
