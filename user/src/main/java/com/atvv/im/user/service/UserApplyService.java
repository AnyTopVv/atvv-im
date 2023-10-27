package com.atvv.im.user.service;

import com.atvv.im.user.model.dto.ApplyDTO;
import com.atvv.im.user.model.dto.ApplyReplyDTO;

/**
 * 用户申请逻辑层
 */
public interface UserApplyService {

    void addApply(ApplyDTO applyDto);

    void replyApply(ApplyReplyDTO applyReplyDTO);
}
