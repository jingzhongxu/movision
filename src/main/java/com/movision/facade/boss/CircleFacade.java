package com.movision.facade.boss;

import com.movision.mybatis.circle.entity.Circle;
import com.movision.mybatis.circle.entity.CircleVo;
import com.movision.mybatis.circle.service.CircleService;
import com.movision.mybatis.post.entity.Post;
import com.movision.mybatis.post.entity.PostNum;
import com.movision.mybatis.post.service.PostService;
import com.movision.mybatis.user.entity.User;
import com.movision.mybatis.user.service.UserService;
import com.movision.utils.pagination.model.Paging;
import com.movision.utils.pagination.util.StringUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author zhurui
 * @Date 2017/2/8 17:56
 */
@Service
public class CircleFacade {
    @Autowired
    CircleService circleService;
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;

    /**
     * 后台管理-圈子列表
     *
     * @param pager
     * @return
     */
    public List<CircleVo> queryCircleByList(Paging<CircleVo> pager) {
        List<CircleVo> list = circleService.queryCircleByList(pager);//获取圈子列表的部分数据
        List<CircleVo> circleVoslist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CircleVo vo = new CircleVo();
            Integer circleid = list.get(i).getId();
            String circlemaster = circleService.queryCircleBycirclemaster(list.get(i).getPhone());//查询圈主
            List<User> users = userService.queryCircleManagerList(circleid);//查询出圈子管理员列表
            for (int j = 0; j < users.size(); j++) {
                if (users.get(i).getNickname() == null) {
                    users.get(i).setNickname("用户" + users.get(i).getPhone().substring(7));
                }
            }
            Integer followsum = circleService.queryFollowSum(circleid);//查询圈子关注数
            PostNum postnumandisessencesum = postService.queryPostNumAndisessenceByCircleid(circleid);//查询圈子下的所有帖子数
            Integer postsum = 0;
            Integer isessencesum = 0;
            if (postnumandisessencesum.getPostnum() != null) {
                postsum = postnumandisessencesum.getPostnum();//圈子中帖子数
            }
            if (postnumandisessencesum.getIsessence() != null) {
                isessencesum = postnumandisessencesum.getIsessence();//圈子中精贴数
            }
            vo.setId(list.get(i).getId());//圈子id
            if (list.get(i).getName() != null) {
                vo.setName(list.get(i).getName());//圈子名称
            }
            if (list.get(i).getCategory() != null) {
                vo.setCategory(list.get(i).getCategory());//圈子分类
            }
            if (circlemaster != null) {
                vo.setCategoryname(circlemaster);//圈主
            }
            if (users != null) {
                vo.setCirclemanagerlist(users);//圈子管理员列表
            }
            if (followsum != null) {
                vo.setIsfollow(followsum);//圈子关注数量
            }
            vo.setPostnum(postsum);
            vo.setIsessencenum(isessencesum);
            if (list.get(i).getSupportnum() != null) {
                vo.setSupportnum(list.get(i).getSupportnum());//支持数
            }
            if (list.get(i).getStatus() != null) {
                vo.setStatus(list.get(i).getStatus());
            }
            vo.setCreatetime(list.get(i).getCreatetime());//圈子创建时间
            circleVoslist.add(vo);
        }
        return circleVoslist;
    }
}
