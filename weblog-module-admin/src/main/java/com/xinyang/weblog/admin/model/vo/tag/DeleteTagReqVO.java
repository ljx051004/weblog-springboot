package com.xinyang.weblog.admin.model.vo.tag;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "删除标签 VO")
public class DeleteTagReqVO {

    @NotNull(message = "分类 ID 不能为空")
    private Long id;
}
