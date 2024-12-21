package xyz.kbws.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/24
 * @description:
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = -3514450647264804921L;
    /**
     * id
     */
    private Integer id;
}
