package xyz.kbws.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.kbws.model.entity.SeriesContent;
import xyz.kbws.model.query.SeriesContentQuery;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【seriesContent(视频合集内容表)】的数据库操作Service
 * @createDate 2024-12-09 20:54:24
 */
public interface SeriesContentService extends IService<SeriesContent> {

    List<SeriesContent> selectList(SeriesContentQuery seriesContentQuery);
}
