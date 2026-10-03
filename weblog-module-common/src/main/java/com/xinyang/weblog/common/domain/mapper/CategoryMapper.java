package com.xinyang.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyang.weblog.common.domain.dos.CategoryDO;

import java.time.LocalDate;
import java.util.Objects;

public interface CategoryMapper extends BaseMapper<CategoryDO> {

    /**
     * 根据名称查询分类
     * @param name
     * @return
     */
    // TODO 这里为什么用default
    default CategoryDO selectByName(String name) {
        // 构建查询条件
        LambdaQueryWrapper<CategoryDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryDO::getName, name);

        // 执行查询
        return selectOne(wrapper);
    }
    /**
     * 分页查询分类列表
     * @param current
     * @param size
     * @param name
     * @param startDate
     * @param endDate
     * @return
     */
    default Page<CategoryDO> selectPageList(Long current, Long size, String name, LocalDate startDate, LocalDate endDate){
        Page<CategoryDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<CategoryDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Objects.nonNull(name), CategoryDO::getName, name.trim())
                .ge(Objects.nonNull(startDate), CategoryDO::getCreateTime, startDate)
                .le(Objects.nonNull(endDate), CategoryDO::getCreateTime, endDate);
        return selectPage(page, wrapper);
    }
}
