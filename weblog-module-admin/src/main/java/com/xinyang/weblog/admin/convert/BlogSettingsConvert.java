package com.xinyang.weblog.admin.convert;

import com.xinyang.weblog.admin.model.vo.blogsettings.FindBlogSettingsRspVO;
import com.xinyang.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.xinyang.weblog.common.domain.dos.BlogSettingsDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

// MapStruct , 用于对象转换
// TODO 这个东西搞清楚
@Mapper
public interface BlogSettingsConvert {
    /**
     * 初始化 convert 实例
     */
    // INSTANCE 接口实例，该实例可以用于在不同类型（例如 VO 到 DO 或者 DO 到 DTO）之间进行转换
    BlogSettingsConvert INSTANCE=Mappers.getMapper(BlogSettingsConvert.class);
    

    /**
     * 将 VO 转化为 DO
     * @param bean
     * @return
     */
    // convertVO2DO() 接口方法，用于将类型为 UpdateBlogSettingsReqVO 的对象转换为类型为 BlogSettingsDO 对象
    BlogSettingsDO convertVO2DO(UpdateBlogSettingsReqVO bean);

    /**
     * 将 DO 转化为 VO
     * @param bean
     * @return
     */
    FindBlogSettingsRspVO convertDO2VO(BlogSettingsDO bean);
}

