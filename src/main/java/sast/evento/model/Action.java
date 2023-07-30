package sast.evento.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import sast.evento.enums.ActionState;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 17:44
 */
@Data
@AllArgsConstructor
public class Action {

    private String description;

    private final String methodName;//作为获取action唯一标准

    private final String method;

    private final String url;

    private String group = "default";

    private ActionState actionState;//操作状态


}
