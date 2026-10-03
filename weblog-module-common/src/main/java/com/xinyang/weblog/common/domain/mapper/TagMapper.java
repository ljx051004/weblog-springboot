package com.xinyang.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyang.weblog.common.domain.dos.CategoryDO;
import com.xinyang.weblog.common.domain.dos.TagDO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public interface TagMapper extends BaseMapper<TagDO> {
    /**
     * 分页查询标签
     * @param current
     * @param size
     * @param name
     * @param startDate
     * @param endDate
     * @return
     */
    default Page<TagDO> selectPageList(long current, long size, String name, LocalDate startDate, LocalDate endDate){
        Page<TagDO> page=new Page<>(current,size);

        // 构建查询条件
        LambdaQueryWrapper<TagDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Objects.nonNull(name),TagDO::getName,name)
                .ge(Objects.nonNull(startDate),TagDO::getCreateTime, startDate)
                .le(Objects.nonNull(endDate),TagDO::getCreateTime, endDate)
                .orderByDesc(TagDO::getCreateTime);// 将新创建的标签放到最上面

        return selectPage(page,wrapper);
    }

    /**
     * 根据标签名模糊查询标签
     * @param key
     * @return
     */
    default List<TagDO> selectByKey(String key){
        LambdaQueryWrapper<TagDO> wrapper = new LambdaQueryWrapper<>();
        // 执行模糊查询
        wrapper.like(TagDO::getName,key).orderByDesc(TagDO::getCreateTime);

        return selectList(wrapper);
    }


    /**
     * 根据标签id列表查询标签
     * @param tagIds
     * @return
     */
    default List<TagDO> selectByIds(List<Long> tagIds){
        return selectList(Wrappers.<TagDO>lambdaQuery()
                .in(TagDO::getId, tagIds));
    }
}
