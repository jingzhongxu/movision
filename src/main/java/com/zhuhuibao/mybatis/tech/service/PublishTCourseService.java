package com.zhuhuibao.mybatis.tech.service;

import com.zhuhuibao.common.constant.MsgCodeConstant;
import com.zhuhuibao.common.constant.TechConstant;
import com.zhuhuibao.exception.BusinessException;
import com.zhuhuibao.mybatis.tech.entity.TrainPublishCourse;
import com.zhuhuibao.mybatis.tech.mapper.PublishTCourseMapper;
import com.zhuhuibao.utils.DateUtils;
import com.zhuhuibao.utils.pagination.model.Paging;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 技术，专家培训课程业务处理类
 *
 * @author pl
 * @create 2016/6/2 0002
 **/
@Service
@Transactional
public class PublishTCourseService {

    private final static Logger log = LoggerFactory.getLogger(PublishTCourseService.class);

    @Autowired
    PublishTCourseMapper pCourseMapper;

    /**
     * 插入专家培训，技术培训发布的课程
     *
     * @param course 专家或者技术发布的课程
     * @return
     */
    public int insertPublishCourse(TrainPublishCourse course) {
        int result;
        try {
            // 插入库存
            course.setStorageNumber(Integer.parseInt(course.getMaxBuyNumber()));
            // 截止日期
            course.setExpiryDate(course.getExpiryDate() + " 23:59:59");
            result = pCourseMapper.insertSelective(course);
            if (result != 1) {
                log.error("t_tech_expert_applyCourse insert error....count:>>>>" + result);
                throw new BusinessException(MsgCodeConstant.DB_INSERT_FAIL, "数据插入失败");
            }
        } catch (Exception e) {
            log.error("执行异常>>>", e);
            throw new BusinessException(MsgCodeConstant.DB_INSERT_FAIL, "数据插入失败");
        }
        return result;
    }

    /**
     * 查看已发布课程的详情
     *
     * @param condition 搜索条件
     * @return
     */
    public TrainPublishCourse selectTrainCourseInfo(Map<String, Object> condition) {
        TrainPublishCourse course;
        try {
            course = pCourseMapper.selectByPrimaryKey(condition);
        } catch (Exception e) {
            log.error("查询异常>>>", e);
            throw e;
        }
        return course;
    }

    /**
     * 更新发布的课程信息
     *
     * @param course 课程信息
     * @return
     */
    public int updatePublishCourse(TrainPublishCourse course) {
        int result = 0;
        try {

            if (course.getMaxBuyNumber() != null) {
                // 插入库存
                course.setStorageNumber(Integer.parseInt(course.getMaxBuyNumber()));
            }
            if (course.getExpiryDate() != null) {
                // 截止日期
                course.setExpiryDate(course.getExpiryDate() + " 23:59:59");
            }
            result = pCourseMapper.updateByPrimaryKeySelective(course);
        } catch (Exception e) {
            log.error("执行异常>>>", e);
            throw e;
        }
        return result;
    }

    /**
     * 课程上架
     *
     * @param courseId 课程ID
     * @return
     */
    public int publishCourse(String courseId, Long omsOperateId) {
        int result = 0;
        log.info("publish courseId " + courseId);
        try {
            TrainPublishCourse course = new TrainPublishCourse();
            course.setPublisherid(omsOperateId);
            course.setCourseid(Long.valueOf(courseId));
            course.setStatus(Integer.valueOf(TechConstant.PublishCourseStatus.SALING.toString()));
            result = pCourseMapper.updateByPrimaryKeySelective(course);
        } catch (Exception e) {
            log.error("执行异常>>>", e);
            throw e;
        }
        return result;
    }

    /**
     * 查询申请的课程
     *
     * @param pager
     * @param condition
     * @return
     */
    public List<Map<String, String>> findAllTrainCoursePager(Paging<Map<String, String>> pager, Map<String, Object> condition) {
        List<Map<String, String>> courseList;
        try {
            courseList = pCourseMapper.findAllTrainCoursePager(pager.getRowBounds(), condition);
        } catch (Exception e) {
            log.error("查询异常>>>", e);
            throw e;
        }
        return courseList;
    }

    /**
     * 查询已发布的课程
     *
     * @param pager
     * @param condition
     * @return
     */
    public List<Map<String, String>> findAllPublishCoursePager(Paging<Map<String, String>> pager, Map<String, Object> condition) {
        List<Map<String, String>> courseList;
        try {
            courseList = pCourseMapper.findAllPublishCoursePager(pager.getRowBounds(), condition);
        } catch (Exception e) {
            log.error("查询异常>>>", e);
            throw e;
        }
        return courseList;
    }

    /**
     * 查询最新发布的培训课程
     *
     * @param condition
     * @return
     */
    public List<Map<String, String>> findLatestPublishCourse(Map<String, Object> condition) {
        List<Map<String, String>> courseList;
        try {
            courseList = pCourseMapper.findLatestPublishCourse(condition);
        } catch (Exception e) {
            log.error("查询异常>>>", e);
            throw e;
        }
        return courseList;
    }

    /**
     * 根据课程详情情况处理返回的status，便于显示
     *
     * @param courseDetail
     * @return
     */
    public String getCourseStatus(Map<String, String> courseDetail) {
        String status = String.valueOf(courseDetail.get("status"));
        if (MapUtils.isNotEmpty(courseDetail) && !"5".equals(courseDetail.get("status")) || !"6".equals(courseDetail.get("status"))) {

            // 判断是否已经达到报名截止时间
            Date expDate = DateUtils.str2Date(org.apache.commons.lang.StringUtils.trimToEmpty(courseDetail.get("expiryDate")), "yyyy-MM-dd");
            Date currentDate = new Date();
            if (null != expDate && currentDate.after(expDate)) {
                // 若已经过了报名截止时间，则返回8（报名截止）
                status = "8";
            } else {
                // 判断课程报名是否已满
                String notBuyNumber = String.valueOf(courseDetail.get("notBuyNumber"));
                String storageNumber = String.valueOf(courseDetail.get("storageNumber"));
                int notBuyNumber1 = StringUtils.isNumeric(notBuyNumber) ? Integer.parseInt(notBuyNumber) : 0;
                int storageNumber1 = StringUtils.isNumeric(storageNumber) ? Integer.parseInt(storageNumber) : 0;
                if (storageNumber1 == 0 || (0 != storageNumber1 && (storageNumber1 - notBuyNumber1) <= 0)) {
                    // 课程报名已满
                    status = "7";
                }
            }

        }

        return status;
    }

    /**
     * 查看培训课程详情
     *
     * @param condition
     * @return
     */
    public Map<String, String> previewTrainCourseDetail(Map<String, Object> condition) {
        try {
            Map<String, String> courseDetail = pCourseMapper.previewTrainCourseDetail(condition);
            // 根据课程详情情况处理返回的status，便于显示
            courseDetail.put("status", getCourseStatus(courseDetail));
            return courseDetail;
        } catch (Exception e) {
            log.error("查询异常>>>", e);
            throw e;
        }
    }
}
