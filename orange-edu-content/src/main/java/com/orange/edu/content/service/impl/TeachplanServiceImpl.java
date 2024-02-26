package com.orange.edu.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.orange.base.exception.OrangeEduException;
import com.orange.edu.content.mapper.TeachplanMapper;
import com.orange.edu.content.mapper.TeachplanMediaMapper;
import com.orange.edu.content.model.dto.BindTeachplanMediaDto;
import com.orange.edu.content.model.dto.SaveTeachplanDto;
import com.orange.edu.content.model.dto.TeachplanDto;
import com.orange.edu.content.model.po.Teachplan;
import com.orange.edu.content.model.po.TeachplanMedia;
import com.orange.edu.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 课程计划service接口实现类
 * @author Mr.M
**/
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {

        //课程计划id
        Long id = teachplanDto.getId();
        //修改课程计划
        if(id!=null){
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }else{
            //取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            //设置排序号
            teachplanNew.setOrderby(count+1);
            BeanUtils.copyProperties(teachplanDto,teachplanNew);

            teachplanMapper.insert(teachplanNew);

        }

    }
    /**
     * @description 获取最新的排序号
     * @param courseId  课程id
     * @param parentId  父课程计划id
     * @return int 最新排序号
     *
     */
    private int getTeachplanCount(long courseId,long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }

    /**
     * 根据id删除课程计划及媒资信息
     *
     * @param id 课程id
     */
    @Transactional
    @Override
    public void deleteTeachplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        if(teachplan ==null){
            OrangeEduException.cast("课程计划信息不存在！");
        }
        //判断是否有父节点
        Long parentid = teachplan.getParentid();
        if(parentid == 0){
            //没有父节点需要检查是否有子节点
            LambdaQueryWrapper<Teachplan> qw = new LambdaQueryWrapper<>();
            qw.eq(Teachplan::getParentid,teachplan.getId());
            Integer count = teachplanMapper.selectCount(qw);
            //有子节点不允许删除
            if(count > 0){
                OrangeEduException.cast("课程计划信息还有子级信息，无法操作");
            }
            teachplanMapper.deleteById(id);
        }else{
            //删除课程计划以及媒资信息
            deleteTeachplanWithMedia(id);
        }
    }



    /**
     * 删除课程计划与媒资信息
     *
     * @param id id
     */
    @Transactional
    public void deleteTeachplanWithMedia(Long id) {
        //查询当前课程信息
        LambdaQueryWrapper<Teachplan> lqw =new LambdaQueryWrapper<>();
        lqw.eq(Teachplan::getId, id);
        Teachplan teachplan = teachplanMapper.selectById(id);
        if(teachplan==null){
            OrangeEduException.cast("课程计划不存在！");
        }
        //删除课程计划
        teachplanMapper.deleteById(id);
        //查询排序字段,在它下面课程计划全部上移一位
        lqw =new LambdaQueryWrapper<>();
        lqw
                .eq(Teachplan::getCourseId,teachplan.getCourseId())
                .eq(Teachplan::getCourseId,teachplan.getCourseId())
                .gt(Teachplan::getOrderby,teachplan.getOrderby());
        List<Teachplan> teachplans = teachplanMapper.selectList(lqw);
        teachplans.forEach(item->{
            item.setOrderby(item.getOrderby()-1);
            teachplanMapper.updateById(item);
        });
        //删除绑定媒资信息
        LambdaQueryWrapper<TeachplanMedia> qw = new LambdaQueryWrapper<>();
        qw.eq(TeachplanMedia::getTeachplanId, id);
        teachplanMediaMapper.delete(qw);

    }


    /**
     * 课程计划下移
     *
     * @param id 课程计划id
     */
    @Transactional
    @Override
    public void movedownTeachplan(Long id) {
        // 1等于与下一位互换
        swapTeachplan(id,1);
    }


    /**
     * 课程计划上移
     *
     * @param id 课程计划上移id
     */
    @Transactional
    @Override
    public void moveupTeachplan(Long id) {
        //-1等于与上一位互换
        swapTeachplan(id,-1);
    }
    /**
     * 交换课程计划
     *
     * @param id   课程计划id
     * @param type 交换课程距离
     */
    @Transactional
    public void swapTeachplan(Long id,Integer type) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        Integer orderby = teachplan.getOrderby();
        //获取要更改到的位置的数据
        Integer orderbyNew = orderby + type;
        LambdaQueryWrapper<Teachplan> qw = new LambdaQueryWrapper<>();
        qw
                .eq(Teachplan::getParentid,teachplan.getParentid())
                .eq(Teachplan::getOrderby,orderbyNew)
                .eq(Teachplan::getCourseId,teachplan.getCourseId());
        Teachplan teachplanNext = teachplanMapper.selectOne(qw);
        //如果没有那就是越出边界了
        if(teachplanNext == null){
            OrangeEduException.cast("无法移动了！");
        }
        //交换排序字段
        teachplan.setOrderby(orderbyNew);
        teachplanNext.setOrderby(orderby);
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(teachplanNext);
    }

    @Transactional
    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //教学计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            OrangeEduException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if(grade!=2){
            OrangeEduException.cast("只允许第二级教学计划绑定媒资文件");
        }
        //课程id
        Long courseId = teachplan.getCourseId();

        //先删除原来该教学计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplanId));

        //再添加教学计划与媒资的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFileName(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    /**
     * 课程计划和媒资信息解除绑定
     *
     * @param teachPlanId
     * @param mediaId
     */
    @Override
    public void deleteTeachplanMedia(Long teachPlanId, String mediaId) {
        int delete = teachplanMediaMapper.delete(new LambdaUpdateWrapper<TeachplanMedia>()
                .eq(TeachplanMedia::getTeachplanId, teachPlanId)
                .eq(TeachplanMedia::getMediaId, mediaId));
        if(delete <= 0){
            OrangeEduException.cast("删除失败！");
        }
    }

}

