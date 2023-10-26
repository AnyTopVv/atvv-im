package com.atvv.im.user.service.impl;

import com.atvv.im.common.constant.enums.common.CommonType;
import com.atvv.im.common.constant.enums.common.ErrorCode;
import com.atvv.im.common.model.dto.CurrentUserInfo;
import com.atvv.im.common.model.po.FriendShip;
import com.atvv.im.user.dao.UserApplyDao;
import com.atvv.im.common.dao.FriendShipDao;
import com.atvv.im.common.model.po.UserApply;
import com.atvv.im.user.exception.UserServiceException;
import com.atvv.im.user.model.dto.ApplyDTO;
import com.atvv.im.user.model.dto.ApplyReplyDTO;
import com.atvv.im.user.service.UserApplyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserApplyServiceImpl implements UserApplyService {

    @Resource
    private UserApplyDao userApplyDao;

    @Resource
    private FriendShipDao friendShipDao;

    @Override
    public void addApply(ApplyDTO applyDto) {
        Long applicantId = CurrentUserInfo.get().getUserId();
        //判断是否已加为好友或进入群聊
        if (applyDto.getType().equals(CommonType.FRIEND.getCode())){
            if (friendShipDao.isFriend(applicantId,applyDto.getTargetId())){
                throw new UserServiceException(ErrorCode.ALREADY_FRIEND);
            }
        }else if (applyDto.getType().equals(CommonType.GROUP.getCode())){
            
        }
        //判断是否存在申请,不存在则插入,存在则返回“已发送过申请请求”
        UserApply userApply = new UserApply();
        userApply.setApplicantId(applicantId);
        userApply.setTargetId(applyDto.getTargetId());
        userApply.setType(applyDto.getType());
        List<UserApply> userApplies = userApplyDao.find(userApply);
        if (!userApplies.isEmpty()){
            throw new UserServiceException(ErrorCode.USER_APPLY_REPEAT);
        }else {
            //插入
            userApply.setAttach(applyDto.getAttach());
            userApplyDao.insert(userApply);
            //TODO 用户在线推送,离线存入redis
            if (true){

            }else {

            }
        }
    }

    @Override
    public void replyApply(ApplyReplyDTO applyReplyDTO) {
        //查询是否存在
        UserApply userApply = userApplyDao.findById(applyReplyDTO.getUserApplyId());
        if (userApply != null){
            //判断是否有权利处理
            if (userApply.getType().equals(CommonType.FRIEND.getCode()) && userApply.getTargetId().equals(CurrentUserInfo.get().getUserId())){
                FriendShip friendShip = new FriendShip();
                friendShip.setTargetId(userApply.getTargetId());
                friendShip.setOriginId(userApply.getApplicantId());
                friendShipDao.insert(friendShip);
                friendShipDao.insert(friendShip.another());
                userApplyDao.updateHandle(applyReplyDTO.getUserApplyId(),applyReplyDTO.getOption());
            }
        }else {

        }
    }
}
