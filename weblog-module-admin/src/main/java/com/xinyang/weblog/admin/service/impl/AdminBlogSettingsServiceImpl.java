package com.xinyang.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyang.weblog.admin.convert.BlogSettingsConvert;
import com.xinyang.weblog.admin.model.vo.blogsettings.FindBlogSettingsRspVO;
import com.xinyang.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.xinyang.weblog.admin.service.AdminBlogSettingsService;
import com.xinyang.weblog.common.domain.dos.BlogSettingsDO;
import com.xinyang.weblog.common.domain.mapper.BlogSettingsMapper;
import com.xinyang.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
// TODO VO转DO这个要理解
public class AdminBlogSettingsServiceImpl extends ServiceImpl<BlogSettingsMapper, BlogSettingsDO> implements AdminBlogSettingsService {
    @Autowired
    private BlogSettingsMapper blogSettingsMapper;

    /**
     * 更新博客设置
     *
     * @param updateBlogSettingsReqVO
     * @return
     */
    @Override
    public Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO) {
        // VO转DO
        // 使用 BlogSettingsConvert 工具类进行 VO 到 DO 的转换
        BlogSettingsDO blogSettingsDO = BlogSettingsConvert.INSTANCE.convertVO2DO(updateBlogSettingsReqVO);
        blogSettingsDO.setId(1L);

        // 保存或更新（当数据库中存在 ID 为 1 的记录时，则执行更新操作，否则执行插入操作）
        // 因为这里要使用保存或者更新这个方法(saveOrUpdate)所以要继承BaseMapper
        saveOrUpdate(blogSettingsDO);

        return Response.success();
    }

    /**
     * 获取博客设置详情
     *
     * @return
     */
    @Override
    public Response findDetail() {
        // 查询ID为1的数据
        BlogSettingsDO blogSettingsDO = blogSettingsMapper.selectById(1L);

        // 将DO转VO
        FindBlogSettingsRspVO findBlogSettingsRspVO = BlogSettingsConvert.INSTANCE.convertDO2VO(blogSettingsDO);
        return Response.success(findBlogSettingsRspVO);
    }
}
