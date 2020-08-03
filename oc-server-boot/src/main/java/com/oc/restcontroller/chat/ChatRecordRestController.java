package com.oc.restcontroller.chat;

import com.github.pagehelper.Page;
import com.oc.auth.UserStore;
import com.oc.auth.WaiterInfo;
import com.oc.restcontroller.AbstractBasicRestController;
import com.oc.service.chat.ChatRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天记录操作
 * @author chuangyeifang
 */
@RestController
@RequestMapping(value = "chatRecord")
public class ChatRecordRestController extends AbstractBasicRestController {

    @Autowired
    private ChatRecordService chatRecordService;

    @PostMapping(value = "before")
    public Object beforeChatRecord(Long id, String customerCode) {
        WaiterInfo waiterInfo = UserStore.get();

        Page<Object> page = startPage();

        chatRecordService.obtainBefore(waiterInfo.getTenantCode(), id, customerCode);

        return success(page);
    }

}
