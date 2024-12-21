package xyz.kbws.model.query;

import lombok.Data;
import xyz.kbws.model.entity.Focus;

/**
 * @author kbws
 * @date 2024/12/10
 * @description:
 */
@Data
public class FocusQuery extends Focus {
    private Integer queryType;

    private Integer pageNo;

    private Integer pageSize;

    private String orderBy;
}
