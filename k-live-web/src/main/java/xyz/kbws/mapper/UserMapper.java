package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.entity.User;

/**
 * @author fangyuan
 * @description 针对表【user(用户表)】的数据库操作Mapper
 * @createDate 2024-11-24 22:14:25
 * @Entity generator.domain.User
 */
public interface UserMapper extends BaseMapper<User> {

    Integer updateCoinCount(@Param("userId") String userId, @Param("changeCount") Integer changeCount);
}




