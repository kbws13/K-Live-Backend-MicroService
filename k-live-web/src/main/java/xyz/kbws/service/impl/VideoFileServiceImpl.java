package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.mapper.VideoFileMapper;
import xyz.kbws.model.entity.VideoFile;
import xyz.kbws.service.VideoFileService;

/**
 * @author fangyuan
 * @description 针对表【videoFile(视频文件信息表)】的数据库操作Service实现
 * @createDate 2024-11-28 20:36:13
 */
@Service
public class VideoFileServiceImpl extends ServiceImpl<VideoFileMapper, VideoFile>
        implements VideoFileService {

}




