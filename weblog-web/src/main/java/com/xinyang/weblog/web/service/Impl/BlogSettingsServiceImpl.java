package com.xinyang.weblog.web.service.Impl;

import com.xinyang.weblog.common.domain.dos.BlogSettingsDO;
import com.xinyang.weblog.common.domain.mapper.BlogSettingsMapper;
import com.xinyang.weblog.common.utils.Response;
import com.xinyang.weblog.web.convert.BlogSettingsConvert;
import com.xinyang.weblog.web.model.vo.blogsettings.FindBlogSettingsDetailRspVO;
import com.xinyang.weblog.web.service.BlogSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BlogSettingsServiceImpl implements BlogSettingsService {
    @Autowired
    private BlogSettingsMapper blogSettingsMapper;

    /**
     * 前台获取博客详情
     * @return
     */
    @Override
    public Response findDetail() {
        // 查询博客设置信息（约定的 ID 为 1）的详细信息
        BlogSettingsDO blogSettingsDO = blogSettingsMapper.selectById(1L);
        // DO转VO 这里采用mapstruct 来做转换
        // 这里字段太多了 手动转换太麻烦
        FindBlogSettingsDetailRspVO vo = BlogSettingsConvert.INSTANCE.convertDO2VO(blogSettingsDO);
        return Response.success(vo);
    }
}
