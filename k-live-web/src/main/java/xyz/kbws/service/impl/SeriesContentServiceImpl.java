package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.mapper.SeriesContentMapper;
import xyz.kbws.model.entity.SeriesContent;
import xyz.kbws.model.query.SeriesContentQuery;
import xyz.kbws.service.SeriesContentService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【seriesContent(视频合集内容表)】的数据库操作Service实现
 * @createDate 2024-12-09 20:54:24
 */
@Service
public class SeriesContentServiceImpl extends ServiceImpl<SeriesContentMapper, SeriesContent>
        implements SeriesContentService {

    @Resource
    private SeriesContentMapper seriesContentMapper;

    @Override
    public List<SeriesContent> selectList(SeriesContentQuery seriesContentQuery) {
        return seriesContentMapper.selectList(seriesContentQuery);
    }
}




