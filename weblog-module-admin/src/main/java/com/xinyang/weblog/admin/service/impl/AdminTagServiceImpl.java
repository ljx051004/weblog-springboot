package com.xinyang.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyang.weblog.admin.model.vo.tag.*;
import com.xinyang.weblog.admin.service.AdminTagService;
import com.xinyang.weblog.common.domain.dos.ArticleTagRelDO;
import com.xinyang.weblog.common.domain.dos.TagDO;
import com.xinyang.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.xinyang.weblog.common.domain.mapper.TagMapper;
import com.xinyang.weblog.common.enums.ResponseCodeEnum;
import com.xinyang.weblog.common.exception.BizException;
import com.xinyang.weblog.common.model.vo.SelectRspVO;
import com.xinyang.weblog.common.utils.PageResponse;
import com.xinyang.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminTagServiceImpl extends ServiceImpl<TagMapper, TagDO> implements AdminTagService {
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    /**
     * 批量添加标签
     * @param addTagReqVO
     * @return
     */
    // 为什么要用extends ServiceImpl<TagMapper, TagDO> 还有就是这个转do中为什么是tagName->TagDO
    // 因为要批量插入 继承了ServiceImpl中有saveBatch方法 可以批量插入
    @Override
    public Response addTag(AddTagReqVO addTagReqVO) {
        // 将VO转DO
        List<TagDO> tagDOS=addTagReqVO.getTags().stream()
                .map(tagName -> TagDO.builder()
                        .name(tagName.trim())
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        try{
            // 批量插入
            saveBatch(tagDOS);
        }catch (Exception e){
            log.warn("该标签已经存在",e);
        }
        return Response.success();
    }

    /**
     * 分页查询标签列表
     * @param findTagPageListReqVO
     * @return
     */
    @Override
    public PageResponse findTagList(FindTagPageListReqVO findTagPageListReqVO) {
        // 拿一下vo中的数据
        Long current=findTagPageListReqVO.getCurrent();
        Long size = findTagPageListReqVO.getSize();
        String name = findTagPageListReqVO.getName();
        LocalDate startDate = findTagPageListReqVO.getStartDate();
        LocalDate endDate = findTagPageListReqVO.getEndDate();

        // 将这个数据传入mapper中
        Page<TagDO> tagDOPage = tagMapper.selectPageList(current, size, name, startDate, endDate);

        List<TagDO> records = tagDOPage.getRecords();

        List<FindTagPageListRspVO> vos=null;
        // 判断是否为空
        if(!CollectionUtils.isEmpty(records)){
            // 将DO转VO 因为DO->VO是返回给后端查看的
            vos=records.stream().map(tagDO -> FindTagPageListRspVO.builder()
                    .id(tagDO.getId())
                    .name(tagDO.getName())
                    .createTime(tagDO.getCreateTime())
                    .build()  // 这里面要的数据全是FindTagPageListRspVO中的
            ).collect(Collectors.toList());
        }
        return PageResponse.success(tagDOPage,vos);
    }

    /**
     * 删除标签
     * @param deleteTagReqVO
     * @return
     */
    // 根据用户要删除标签，判断该标签下是否有关联的文章，若有，则不允许删除，提示用户需要先删除标签下的文章
    @Override
    public Response deleteTag(DeleteTagReqVO deleteTagReqVO) {
        // 获取标签id
        Long tagId = deleteTagReqVO.getId();

        // 校验该标签下是否有关联的文章，若有，则不允许删除，提示用户需要先删除标签下的文章
        ArticleTagRelDO articleTagRelDO = articleTagRelMapper.selectOneByTagId(tagId);
        // 判断是否已经存在关联的文章
        if(Objects.isNull(articleTagRelDO)){
            log.warn("==> 此标签下包含文章，无法删除，tagId: {}", tagId);
            throw new BizException(ResponseCodeEnum.TAG_CAN_NOT_DELETE);
        }

        // 根据id删除标签
        int count = tagMapper.deleteById(tagId);
        // 判断是否删除成功
        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.TAG_NOT_EXISTED);
    }

    /**
     * 根据标签名模糊查询标签下拉列表
     * @param searchTagReqVO
     * @return
     */
    @Override
    public Response searchTag(SearchTagReqVO searchTagReqVO) {
        String key = searchTagReqVO.getKey();
        // 执行查询
        List<TagDO> tagDOS = tagMapper.selectByKey(key);
        // 将DO转VO
        List<SelectRspVO> vos=null;
        if(!CollectionUtils.isEmpty(tagDOS)){
            vos=tagDOS.stream().map(
                    tagDO -> SelectRspVO.builder()
                            .label(tagDO.getName())
                            .value(tagDO.getId())
                            .build()
            ).collect(Collectors.toList());
        }
        return Response.success(vos);
    }
}
