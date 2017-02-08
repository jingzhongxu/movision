package com.movision.mybatis.post.mapper;

import com.movision.mybatis.circle.entity.Circle;
import com.movision.mybatis.post.entity.ActiveVo;
import com.movision.mybatis.post.entity.Post;
import com.movision.mybatis.post.entity.PostVo;
import com.movision.mybatis.postShareGoods.entity.PostShareGoods;
import com.movision.utils.pagination.model.Paging;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

public interface PostMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Post record);

    int insertSelective(Post record);

    Post selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Post record);

    int updateByPrimaryKey(Post record);

    List<PostVo> queryTodayEssence();

    List<PostVo> queryDayAgoEssence(int dayago);

    List<Circle> queryMayLikeCircle(int userid);

    List<Circle> queryRecommendCircle();

    List<Post> queryHotActiveList();

    List<Post> queryCircleSubPost(int circleid);

    List<PostVo> personPost(RowBounds rowBounds, int userid);

    List<ActiveVo> personActive(RowBounds rowBounds, int userid);

    int queryPostNumByCircleid(int circleid);

    PostVo queryPostDetail(Map<String, Object> parammap);

    String queryVideoUrl(int postid);

    List<PostVo> queryPastPostList(Map<String, Object> parammap);

    List<PostVo> queryPostList(RowBounds rowBounds, int circleid);

    List<PostVo> queryAllActive(RowBounds rowBounds);

    int queryActivePartSum(int postid);

    int queryUserPartSum(Map<String, Object> parammap);

    int saveActiveRecord(Map<String, Object> parammap);

    ActiveVo queryNoticeActive(int postid);

    int queryPostByCircleid(int id);

    int releasePost(Post post);

    void insertPostShareGoods(List<PostShareGoods> postShareGoodsList);

    int updatePostByZanSum(int id);

    int queryPostByZanSum(int id);

    int updatePostBycommentsum(int postid);

    List<Post> queryPostByList(RowBounds rowBounds);

    int deletePost(Integer postid);

    Post queryPostParticulars(Integer postid);

    int addPost(Map map);

    /*List postSearch(Map map);*/
}