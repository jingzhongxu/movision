package com.movision.facade.index;

import com.movision.mybatis.post.service.PostService;
import com.movision.mybatis.userRefreshRecord.service.UserRefreshRecordService;
import com.movision.test.SpringTestCase;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author zhuangyuhao
 * @Date 2017/10/17 17:27
 */
public class FacadeHeatValueTest extends SpringTestCase {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRefreshRecordService userRefreshRecordService;



    public void updateOnlinePostHeatvalue() throws Exception {
        /*List<PostVo> list = postService.queryPostListByHeatValue();

        for (PostVo postVo : list) {
            int count = 0;
            int originHeatValue = postVo.getHeatvalue();
            int postid = postVo.getId();
            //判断是否是首页精选 isessence
            int isessence = postVo.getIsessence();
            if (isessence == 1) {
                count += HeatValueConstant.POINT.home_page_selection.getCode();
            }

            //判断是否是帖子精选 ishot
            int ishot = postVo.getIshot();
            if (ishot == 1) {
                count += HeatValueConstant.POINT.circle_selection.getCode();
            }

            //查出该帖子的评论数量，进行热度操作
            int commentsum = postVo.getCommentsum();
            int comPoint = commentsum * HeatValueConstant.POINT.comments_number.getCode();

            //查出该帖子的点赞数量，进行热度操作
            int zansum = postVo.getZansum();
            int zanPoint = zansum * HeatValueConstant.POINT.zan_number.getCode();

            //查看转发数
            int forwardsum = postVo.getForwardsum();
            int forwardPoint = forwardsum * HeatValueConstant.POINT.forwarding_number.getCode();

            //查出该帖子的收藏数量，进行热度操作
            int collectsum = postVo.getCollectsum();
            int collectPoint = collectsum * HeatValueConstant.POINT.collection_number.getCode();

            //查出该帖子的浏览数量，进行热度操作
            int countView = userRefreshRecordService.postcount(postid);
            int viewPoint = countView * HeatValueConstant.POINT.view_post.getCode();

            count = count + comPoint + zanPoint + forwardPoint + collectPoint + viewPoint;

            PostTo postTo = new PostTo();
            postTo.setId(postid);
            postTo.setHeatvalue(originHeatValue + count);

            postService.updatePostById(postTo);
        }*/
    }
}