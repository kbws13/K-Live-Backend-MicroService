package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.mapper.VideoFilePostMapper;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.service.VideoFilePostService;

/**
 * @author fangyuan
 * @description 针对表【videoFilePost(已发布视频文件信息表)】的数据库操作Service实现
 * @createDate 2024-11-28 20:36:17
 */
@Service
public class VideoFilePostServiceImpl extends ServiceImpl<VideoFilePostMapper, VideoFilePost>
        implements VideoFilePostService {

}




