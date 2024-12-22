package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;
import xyz.kbws.api.consumer.VideoClient;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.DanmuMapper;
import xyz.kbws.model.entity.Danmu;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.enums.SearchOrderTypeEnum;
import xyz.kbws.model.enums.UserActionTypeEnum;
import xyz.kbws.model.query.DanmuQuery;
import xyz.kbws.service.DanmuService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【danmu(弹幕表)】的数据库操作Service实现
 * @createDate 2024-12-07 12:13:14
 */
@Service
public class DanmuServiceImpl extends ServiceImpl<DanmuMapper, Danmu>
        implements DanmuService {

    @Resource
    private DanmuMapper danmuMapper;

    @Resource
    private VideoClient videoClient;

    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void saveDanmu(Danmu danmu) {
        Video video = videoClient.selectById(danmu.getVideoId());
        if (video == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该视频不存在");
        }
        if (video.getInteraction() != null && video.getInteraction().contains(UserConstant.ONE.toString())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该 UP 主已关闭弹幕");
        }
        this.save(danmu);
        videoClient.updateCountInfo(danmu.getVideoId(), UserActionTypeEnum.VIDEO_DANMU.getField(), 1);
        // 更新 ES 弹幕数量
        videoClient.updateDocCount(danmu.getVideoId(), SearchOrderTypeEnum.VIDEO_DANMU, 1);
    }

    @Override
    public void deleteDanmu(String userId, Integer danmuId) {
        Danmu danmu = this.getById(danmuId);
        if (danmu == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Video video = videoClient.selectById(danmu.getVideoId());
        if (video == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (userId != null && !video.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        this.removeById(danmuId);
    }

    @Override
    public List<Danmu> selectListByParam(DanmuQuery danmuQuery) {
        return danmuMapper.selectList(danmuQuery);
    }
}




