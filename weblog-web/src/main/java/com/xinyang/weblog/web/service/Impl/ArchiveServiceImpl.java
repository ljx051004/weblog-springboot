package com.xinyang.weblog.web.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.xinyang.weblog.common.domain.dos.ArticleDO;
import com.xinyang.weblog.common.domain.mapper.ArticleMapper;
import com.xinyang.weblog.common.utils.PageResponse;
import com.xinyang.weblog.common.utils.Response;
import com.xinyang.weblog.web.convert.ArticleConvert;
import com.xinyang.weblog.web.model.vo.archive.FindArchiveArticlePageListReqVO;
import com.xinyang.weblog.web.model.vo.archive.FindArchiveArticlePageListRspVO;
import com.xinyang.weblog.web.model.vo.archive.FindArchiveArticleRspVO;
import com.xinyang.weblog.web.service.ArchiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArchiveServiceImpl implements ArchiveService {
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 获取文章归档分页查询
     * @param findArchiveArticlePageListReqVO
     * @return
     */
    // TODO 博客前台:归档页、分类列表页、标签列表页这里面有三个分页查询 一定要弄清楚
    @Override
    public Response findArchivePageList(FindArchiveArticlePageListReqVO findArchiveArticlePageListReqVO) {
        // 获取分页参数
        Long current = findArchiveArticlePageListReqVO.getCurrent();
        Long size = findArchiveArticlePageListReqVO.getSize();

        // 分页查询
        IPage<ArticleDO> page = articleMapper.selectArticleList(current, size, null, null, null);
        List<ArticleDO> articleDOS = page.getRecords();// 获取分页数据

        // TODO 从这里开始没看懂
        List<FindArchiveArticlePageListRspVO> vos= Lists.newArrayList();
        if(!CollectionUtils.isEmpty(articleDOS)){
            // 将DO转VO
            // 因为手动转太麻烦 采用convert进行转换
            List<FindArchiveArticleRspVO> archiveArticleRspVOS  =  articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2ArchiveArticleVO(articleDO))
                    .collect(Collectors.toList());

            // 按创建的月份进行分组
            Map<YearMonth, List<FindArchiveArticleRspVO>> map = archiveArticleRspVOS.stream().collect(Collectors.groupingBy(FindArchiveArticleRspVO::getCreateMonth));

            // 使用 TreeMap 按月份倒序排列
            TreeMap<YearMonth, List<FindArchiveArticleRspVO>> sortedMap  = new TreeMap<>(Collections.reverseOrder());
            sortedMap.putAll(map);

            // 遍历排序后的 Map，将其转换为归档 VO
            sortedMap.forEach((k, v) -> vos.add(FindArchiveArticlePageListRspVO.builder().month(k).articles(v).build()));
        }

        return PageResponse.success(page, vos);
    }
}
