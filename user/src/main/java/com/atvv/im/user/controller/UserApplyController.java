package com.atvv.im.user.controller;

import com.atvv.im.common.model.vo.ResponseVO;
import com.atvv.im.user.model.dto.ApplyDTO;
import com.atvv.im.user.model.dto.ApplyReplyDTO;
import com.atvv.im.user.service.UserApplyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户申请控制层
 */
@RestController
@RequestMapping("/user/apply")
public class UserApplyController {

    @Resource
    private UserApplyService userApplyService;

    /**
     * 添加申请请求
     * @return
     */
    @PostMapping("/add")
    public ResponseVO<?> addFriendApply(@RequestBody ApplyDTO applyDto){
        userApplyService.addApply(applyDto);
        return ResponseVO.success();
    }

    /**
     * 回应申请请求
     * @return
     */
    @PostMapping("/reply")
    public ResponseVO<?> replyApply(@RequestBody ApplyReplyDTO applyReplyDTO){
        userApplyService.replyApply(applyReplyDTO);
        return ResponseVO.success();
    }
}
