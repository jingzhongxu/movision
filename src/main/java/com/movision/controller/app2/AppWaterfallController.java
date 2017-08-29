package com.movision.controller.app2;

import com.movision.common.Response;
import com.movision.facade.circle.CircleAppFacade;
import com.movision.facade.comment.FacadeComments;
import com.movision.facade.index.FacadeHeatValue;
import com.movision.facade.index.FacadePost;
import com.movision.facade.label.LabelFacade;
import com.movision.facade.msgCenter.MsgCenterFacade;
import com.movision.facade.user.UserFacade;
import com.movision.mybatis.circle.entity.CircleVo;
import com.movision.mybatis.comment.entity.CommentVo;
import com.movision.mybatis.labelSearchTerms.service.LabelSearchTermsService;
import com.movision.mybatis.post.entity.PostVo;
import com.movision.mybatis.postLabel.entity.PostLabelTz;
import com.movision.mybatis.user.entity.UserVo;
import com.movision.mybatis.userRefreshRecord.service.UserRefreshRecordService;
import com.movision.utils.pagination.model.Paging;
import com.movision.utils.pagination.model.ServicePaging;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author zhanglei
 * @Date 2017/7/17 20:32
 */
@RestController
@RequestMapping("/app/waterfall/")
public class AppWaterfallController {

    @Autowired
    private FacadePost facadePost;

    @Autowired
    private MsgCenterFacade msgCenterFacade;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private LabelFacade labelFacade;

    @Autowired
    private FacadeComments facadeComments;

    @Autowired
    private CircleAppFacade circleAppFacade;
    /**
     * 下拉刷新
     *
     * @return
     */
    /** @ApiOperation(value = "下拉刷新", notes = "下拉刷新", response = Response.class)
    @RequestMapping(value = "userRefreshListNew", method = RequestMethod.POST)
    public Response userRefreshList(@ApiParam(value = "用户id ") @RequestParam(required = false) String userid,
                                    @ApiParam(value = "类型 1：推荐2：关注3：本地 4：圈子 5：标签") @RequestParam(required = false) int type,
                                    @ApiParam(value = "地区") @RequestParam(required = false) String area,
                                    @ApiParam(value = "圈子id") @RequestParam(required = false) String circleid,
                                    @ApiParam(value = "标签id") @RequestParam(required = false) String labelid,
                                    @ApiParam(value = "设备号") @RequestParam(required = false) String device) {
        Response response = new Response();
        List map = facadePost.userRefreshListNew(userid, device, type, area, circleid, labelid);
        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        response.setData(map);
        return response;

                                    }*/
    /**
     * 下拉刷新
     *
     * @return
     */
    @ApiOperation(value = "下拉刷新", notes = "下拉刷新", response = Response.class)
    @RequestMapping(value = "userRefreshListNew", method = RequestMethod.POST)
    public Response userRefreshList(@ApiParam(value = "用户id ") @RequestParam(required = false) String userid,
                                    @ApiParam(value = "第几页") @RequestParam(required = false, defaultValue = "1") String pageNo,
                                    @ApiParam(value = "每页多少条") @RequestParam(required = false, defaultValue = "10") String pageSize,
                                    @ApiParam(value = "类型 1：推荐2：关注3：本地 4：圈子 5：标签") @RequestParam(required = false) int type,
                                    @ApiParam(value = "地区") @RequestParam(required = false) String area,
                                    @ApiParam(value = "圈子id") @RequestParam(required = false) String circleid,
                                    @ApiParam(value = "标签id") @RequestParam(required = false) String labelid) {
        Response response = new Response();
        ServicePaging<PostVo> pager = new ServicePaging<PostVo>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List map = facadePost.userRefreshListNew(userid, pager, type, area, circleid, labelid);
        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        pager.setRows(map);
        response.setData(pager);
        return response;

    }
    /**
     * 首页滑动列表
     *
     * @return
     */
    @ApiOperation(value = "首页滑动列表", notes = "首页滑动列表", response = Response.class)
    @RequestMapping(value = "indexHomeList", method = RequestMethod.POST)
    public Response indexHomeList() {
        Response response = new Response();
        List<CircleVo> list = facadePost.indexHomeList();
        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        response.setData(list);
        return response;
    }

    /**
     * 用户刷新的历史列表
     *
     * @param userid
     * @param pageNo
     * @param pageSize
     * @return
     */
    /**@ApiOperation(value = "用户刷新的历史记录列表", notes = "用户刷新的历史记录列表", response = Response.class)
    @RequestMapping(value = "userReflushHishtoryRecord", method = RequestMethod.POST)
    public Response userReflushHishtoryRecord(@ApiParam(value = "用户id") @RequestParam(required = false) String userid,
                                              @ApiParam(value = "1：推荐2：关注3：本地 4：圈子 5：标签") @RequestParam int type,
                                              @ApiParam(value = "设备号") @RequestParam(required = false) String device,
                                              @ApiParam(value = "第几页") @RequestParam(required = false, defaultValue = "1") String pageNo,
                                              @ApiParam(value = "每页多少条") @RequestParam(required = false, defaultValue = "10") String pageSize) {
        Response response = new Response();
        Paging<PostVo> pager = new Paging<PostVo>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List map = facadePost.userReflushHishtoryRecord(userid, pager, type, device);
        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        pager.result(map);
        response.setData(pager);
        return response;
                                              }*/
    /**
     * 用户刷新的历史列表
     *
     * @param userid
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "用户刷新的历史记录列表", notes = "用户刷新的历史记录列表", response = Response.class)
    @RequestMapping(value = "userReflushHishtoryRecord", method = RequestMethod.POST)
    public Response userReflushHishtoryRecord(@ApiParam(value = "用户id") @RequestParam String userid,
                                              @ApiParam(value = "第几页") @RequestParam(required = false, defaultValue = "1") String pageNo,
                                              @ApiParam(value = "每页多少条") @RequestParam(required = false, defaultValue = "10") String pageSize) {
        Response response = new Response();
        Paging<PostVo> pager = new Paging<PostVo>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List map = facadePost.userReflushHishtoryRecord(userid, pager);
        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        pager.result(map);
        response.setData(pager);
        return response;
    }

    /**
     * 所有未读消息
     * @param userid
     * @return
     */
     @ApiOperation(value = "所有未读消息", notes = "所有未读消息", response = Response.class)
     @RequestMapping(value = "queryUserAllUnreadMessage", method = RequestMethod.POST)
     public Response queryUserAllUnreadMessage(@ApiParam(value = "用户id") @RequestParam(required = false) String userid){
     Response response = new Response();
         Map count = msgCenterFacade.queryUserAllUnreadMessage(userid);
     if(response.getCode()==200){
     response.setMessage("返回成功");
     }
     response.setData(count);
         return response;
     }

    /**
     * 所有未读消息
     *
     * @param userid
     * @return
     */
    @ApiOperation(value = "红点", notes = "红点", response = Response.class)
    @RequestMapping(value = "isRead", method = RequestMethod.POST)
    public Response isRead(@ApiParam(value = "用户id") @RequestParam String userid,
                           @ApiParam(value = "1 动态 2 通知") @RequestParam int type) {
        Response response = new Response();
        Map count = msgCenterFacade.isRead(type, userid);
        if (response.getCode() == 200) {
            response.setMessage("返回成功");
        }
        response.setData(count);
        return response;
    }

    /**
     * @return
     */
    @ApiOperation(value = "我的主页上半部分", notes = "我的主页上半部分", response = Response.class)
    @RequestMapping(value = "queryPersonalHomepage", method = RequestMethod.POST)
    public Response queryPersonalHomepage(@ApiParam(value = "用户id") @RequestParam String userid) {
        Response response = new Response();
        UserVo list = userFacade.queryPersonalHomepage(userid);
        if (response.getCode() == 200) {
            response.setMessage("返回成功");
        }
        response.setData(list);
        return response;
     }

    @ApiOperation(value = "个人主页上半部分", notes = "个人主页上半部分", response = Response.class)
    @RequestMapping(value = "queryOtherPersonHomepage", method = RequestMethod.POST)
    public Response queryOtherPersonHomepage(@ApiParam(value = "用户id") @RequestParam String userid) {
        Response response = new Response();
        UserVo list = userFacade.queryOtherPersonHomepage(userid);
        if (response.getCode() == 200) {
            response.setMessage("返回成功");
        }
        response.setData(list);
        return response;
    }

    /**
     * @return
     */
    @ApiOperation(value = "个人主页下半部分", notes = "个人主页下半部分", response = Response.class)
    @RequestMapping(value = "mineBottle", method = RequestMethod.POST)
    public Response mineBottle(@ApiParam(value = "类型 0 帖子 1活动 2 收藏（必填）") @RequestParam int type,
                               @ApiParam(value = "用户id（必填，被查看的这个人的userid，被被被！！！）") @RequestParam String userid,
                               @ApiParam(value = "第几页") @RequestParam(required = false, defaultValue = "1") String pageNo,
                               @ApiParam(value = "每页多少条") @RequestParam(required = false, defaultValue = "10") String pageSize) {
        Response response = new Response();
        Paging<PostVo> pager = new Paging<>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List list = userFacade.mineBottle(type, userid, pager);
        if (response.getCode() == 200) {
            response.setMessage("返回成功");
        }
        pager.result(list);
        response.setData(pager);
        return response;
    }

    /**
     * @return
     */
    @ApiOperation(value = "关注标签", notes = "关注标签", response = Response.class)
    @RequestMapping(value = "attentionLabel", method = RequestMethod.POST)
    public Response attentionLabel(@ApiParam(value = "标签id") @RequestParam int labelid,
                                   @ApiParam(value = "用户id ") @RequestParam int userid) {
        Response response = new Response();
        int result = labelFacade.attentionLabel(userid, labelid);
        if (result == 0) {
            response.setCode(200);
            response.setMessage("关注成功");
        } else if (result == 1) {
            response.setCode(300);
            response.setMessage("已关注该标签，请刷新重试");
        }
        return response;
    }


    /**
     * 点击标签页上半部分
     *
     * @return
     */
    @ApiOperation(value = "点击标签页上半部分", notes = "点击标签页上半部分", response = Response.class)
    @RequestMapping(value = "labelPage", method = RequestMethod.POST)
    public Response labelPage(@ApiParam(value = "标签id") @RequestParam int labelid) {
        Response response = new Response();
        PostLabelTz result = labelFacade.labelPage(labelid);
        if (response.getCode() == 200) {
            response.setMessage("返回成功");
        }
        response.setData(result);
        return response;
    }


    /**
     * 点击标签页下半部分
     *
     * @return
     */
    @ApiOperation(value = "点击标签页下半部分", notes = "点击标签页下半部分", response = Response.class)
    @RequestMapping(value = "postLabelList", method = RequestMethod.POST)
    public Response postLabelList(@ApiParam(value = "标签id") @RequestParam int labelid,
                                  @ApiParam(value = "类型 1 推荐 2 最新 3精华 ") @RequestParam int type,
                                  @ApiParam(value = "第几页") @RequestParam(required = false, defaultValue = "1") String pageNo,
                                  @ApiParam(value = "每页多少条") @RequestParam(required = false, defaultValue = "10") String pageSize) {
        Response response = new Response();
        Paging<PostVo> pager = new Paging<>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List result = labelFacade.postLabelList(labelid, pager, type);
        if (response.getCode() == 200) {
            response.setMessage("返回成功");
        }
        pager.result(result);
        response.setData(pager);
        return response;
    }


    /**
     * 标签达人
     *
     * @return
     */
    @ApiOperation(value = "标签达人", notes = "标签达人", response = Response.class)
    @RequestMapping(value = "tagMan", method = RequestMethod.POST)
    public Response tagMan(@ApiParam(value = "标签id") @RequestParam int labelid) {
        Response response = new Response();
        List result = labelFacade.tagMan(labelid);
        if (response.getCode() == 200) {
            response.setMessage("返回成功");
        }
        response.setData(result);
        return response;
    }



    @ApiOperation(value = "活动详情中的最热最新", notes = "活动详情中的最热最新", response = Response.class)
    @RequestMapping(value = "activePostDetailHot", method = RequestMethod.POST)
    public Response activePostDetailHot(@ApiParam(value = "类型 0 最热 1 最新") @RequestParam int type,
                                        @ApiParam(value = "活动id") @RequestParam String postid,
                                        @ApiParam(value = "第几页") @RequestParam(required = false, defaultValue = "1") String pageNo,
                                        @ApiParam(value = "每页多少条") @RequestParam(required = false, defaultValue = "10") String pageSize) {
        Response response = new Response();
        Paging<PostVo> pager = new Paging<>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List result = facadePost.activePostDetailHot(type, postid, pager);
        if (response.getCode() == 200) {
            response.setMessage("返回成功");
        }
        pager.result(result);
        response.setData(pager);
        return response;
    }


    /** @ApiOperation(value = "关注作者", notes = "关注作者", response = Response.class)
    @RequestMapping(value = "concernedAuthor", method = RequestMethod.POST)
    public Response concernedAuthor(@ApiParam(value = "用户id") @RequestParam int userid,
                                    @ApiParam(value = "帖子id") @RequestParam String postid) {
        Response response = new Response();
        int result = facadePost.concernedAuthor(userid, postid);
        if (result == 0) {
            response.setCode(200);
            response.setMessage("关注成功");
        } else if (result == 1) {
            response.setCode(300);
            response.setMessage("已关注该作者，请刷新重试");
        }
        return response;
                                    }*/


    @ApiOperation(value = "点击圈子标签页上半部分", notes = "点击圈子标签页上半部分", response = Response.class)
    @RequestMapping(value = "queryCircleByPostid", method = RequestMethod.POST)
    public Response queryCircleByPostid(@ApiParam(value = "圈子id") @RequestParam String circleid) {
        Response response = new Response();
        CircleVo result = labelFacade.queryCircleByPostid(circleid);
        if (response.getCode() == 200) {
            response.setMessage("返回成功");
        }
        response.setData(result);
        return response;
    }


    @ApiOperation(value = "点击圈子标签页上半部分箭头详情", notes = "点击圈子标签页上半部分箭头详情", response = Response.class)
    @RequestMapping(value = "queryCircleDetail", method = RequestMethod.POST)
    public Response queryCircleDetail(@ApiParam(value = "圈子id") @RequestParam String circleid) {
        Response response = new Response();
        CircleVo result = labelFacade.queryCircleDetail(circleid);
        if (response.getCode() == 200) {
            response.setMessage("返回成功");
        }
        response.setData(result);
        return response;
    }


    /**
     * 点击圈子标签页下半部分
     *
     * @return
     */
    @ApiOperation(value = "点击圈子标签页下半部分", notes = "点击圈子标签页下半部分", response = Response.class)
    @RequestMapping(value = "queryCircleBotton", method = RequestMethod.POST)
    public Response queryCircleBotton(@ApiParam(value = "圈子id") @RequestParam String circleid,
                                      @ApiParam(value = "类型 1 推荐 2 最新 3精华 ") @RequestParam int type,
                                      @ApiParam(value = "第几页") @RequestParam(required = false, defaultValue = "1") String pageNo,
                                      @ApiParam(value = "每页多少条") @RequestParam(required = false, defaultValue = "10") String pageSize,
                                      @ApiParam(value = "标签id") @RequestParam(required = false) String labelid) {
        Response response = new Response();
        Paging<PostVo> pager = new Paging<>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List result = labelFacade.queryCircleBotton(type, pager, circleid, labelid);
        if (response.getCode() == 200) {
            response.setMessage("返回成功");
        }
        pager.result(result);
        response.setData(pager);
        return response;
    }


    @ApiOperation(value = "圈子达人", notes = "圈子达人", response = Response.class)
    @RequestMapping(value = "circleOfPeople", method = RequestMethod.POST)
    public Response circleOfPeople(@ApiParam(value = "圈子id") @RequestParam int circleid) {
        Response response = new Response();
        List result = labelFacade.circleOfPeople(circleid);
        if (response.getCode() == 200) {
            response.setMessage("返回成功");
        }
        response.setData(result);
        return response;
    }


    /**
     * 帖子详情中的相关帖子
     *
     * @return
     */
    @ApiOperation(value = "帖子详情中的相关帖子", notes = "帖子详情中的相关帖子", response = Response.class)
    @RequestMapping(value = "queryRelatedPosts", method = RequestMethod.POST)
    public Response queryRelatedPosts(
            @ApiParam(value = "帖子id ") @RequestParam String postid,
            @ApiParam(value = "第几页") @RequestParam(required = false, defaultValue = "1") String pageNo,
            @ApiParam(value = "每页多少条") @RequestParam(required = false, defaultValue = "10") String pageSize) {
        Response response = new Response();
        ServicePaging<PostVo> pager = new ServicePaging<PostVo>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List result = facadePost.queryRelatedPosts(postid, pager);
        if (response.getCode() == 200) {
            response.setMessage("返回成功");
        }
        pager.setRows(result);
        response.setData(pager);
        return response;
    }



    @ApiOperation(value = "删除评论", notes = "删除评论", response = Response.class)
    @RequestMapping(value = "deleteComment", method = RequestMethod.POST)
    public Response deleteComment(@ApiParam(value = "用户id") @RequestParam int userid,
                                  @ApiParam(value = "评论id") @RequestParam int id) {
        Response response = new Response();
        int result = facadeComments.deleteComment(id, userid);
        if (result == 0) {
            response.setCode(200);
            response.setMessage("删除成功");
        } else if (result == 1) {
            response.setCode(300);
            response.setMessage("该评论不是你发的，请刷新重试");
        }
        return response;
    }


    @ApiOperation(value = "关注用户", notes = "关注用户", response = Response.class)
    @RequestMapping(value = "concernedAuthorUser", method = RequestMethod.POST)
    public Response concernedAuthorUser(@ApiParam(value = "用户id") @RequestParam int userid,
                                        @ApiParam(value = "被关注的id") @RequestParam int interestedusers) {
        Response response = new Response();
        int result = facadePost.concernedAuthorUser(userid, interestedusers);
        if (result == 0) {
            response.setCode(200);
            response.setMessage("关注成功");
        } else if (result == 1) {
            response.setCode(300);
            response.setMessage("已关注该作者，请刷新重试");
        }
        return response;
    }


    @ApiOperation(value = "取消关注标签", notes = "取消关注标签", response = Response.class)
    @RequestMapping(value = "cancelFollowLabel", method = RequestMethod.POST)
    public Response cancelFollowLabel(@ApiParam(value = "用户id") @RequestParam String userid,
                                      @ApiParam(value = "标签id") @RequestParam String labelid) {
        Response response = new Response();
        int result = labelFacade.cancelFollowLabel(userid, labelid);
        if (response.getCode() == 200 && result == 1) {
            response.setCode(200);
            response.setMessage("取消关注成功");
        } else {
            response.setCode(400);
            response.setMessage("取消关注失败");
        }
        return response;
    }


    @ApiOperation(value = "取消关注用户", notes = "取消关注用户", response = Response.class)
    @RequestMapping(value = "cancelFollowUser", method = RequestMethod.POST)
    public Response cancelFollowUser(@ApiParam(value = "用户id") @RequestParam int userid,
                                     @ApiParam(value = "被关注人id") @RequestParam int interestedusers) {
        Response response = new Response();
        int result = facadePost.cancelFollowUser(userid, interestedusers);
        if (response.getCode() == 200 && result == 1) {
            response.setCode(200);
            response.setMessage("取消关注成功");
        } else {
            response.setCode(400);
            response.setMessage("取消关注失败");
        }
        return response;
    }


    @ApiOperation(value = "评论列表", notes = "评论列表", response = Response.class)
    @RequestMapping(value = "queryNewComment", method = RequestMethod.POST)
    public Response queryNewComment(@ApiParam(value = "帖子id") @RequestParam int postid,
                                    @ApiParam(value = "第几页") @RequestParam(required = false, defaultValue = "1") String pageNo,
                                    @ApiParam(value = "每页多少条") @RequestParam(required = false, defaultValue = "10") String pageSize) {
        Response response = new Response();
        Paging<CommentVo> pager = new Paging<>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List result = facadeComments.queryNewComment(postid, pager);
        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        pager.result(result);
        response.setData(pager);
        return response;
    }


    @ApiOperation(value = "评论列表（最新）", notes = "评论列表（最新）", response = Response.class)
    @RequestMapping(value = "queryLNewComment", method = RequestMethod.POST)
    public Response queryLNewComment(@ApiParam(value = "帖子id") @RequestParam int postid,
                                     @ApiParam(value = "第几页") @RequestParam(required = false, defaultValue = "1") String pageNo,
                                     @ApiParam(value = "每页多少条") @RequestParam(required = false, defaultValue = "10") String pageSize) {
        Response response = new Response();
        Paging<CommentVo> pager = new Paging<>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List result = facadeComments.queryLNewComment(postid, pager);
        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        pager.result(result);
        response.setData(pager);
        return response;
    }

    @ApiOperation(value = "评论列表（最新aaaa）", notes = "评论列表（最新aaaa）", response = Response.class)
    @RequestMapping(value = "asss", method = RequestMethod.POST)
    public Response asss(@ApiParam(value = "帖子id") @RequestParam int postid,
                         @ApiParam(value = "第几页") @RequestParam(required = false, defaultValue = "1") String pageNo,
                         @ApiParam(value = "每页多少条") @RequestParam(required = false, defaultValue = "10") String pageSize,
                         @ApiParam(value = "用户") @RequestParam(required = false) String userid
    ) {
        Response response = new Response();
        Paging<CommentVo> pager = new Paging<>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List result = facadeComments.asss(postid, pager, userid);
        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        pager.result(result);
        response.setData(pager);
        return response;
    }


    @ApiOperation(value = "首页点X", notes = "首页点X", response = Response.class)
    @RequestMapping(value = "userDontLike", method = RequestMethod.POST)
    public Response userDontLike(@ApiParam(value = "帖子id") @RequestParam int postid,
                                 @ApiParam(value = "用户id") @RequestParam int userid,
                                 @ApiParam(value = "类型 1 内容差 2 不喜欢作者 3 不喜欢圈子 4 就是不喜欢") @RequestParam int type) {
        Response response = new Response();
        int result = labelFacade.userDontLike(type, userid, postid);
        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        response.setData(result);
        return response;
    }


    @ApiOperation(value = "活动详情中的报名", notes = "活动详情中的报名", response = Response.class)
    @RequestMapping(value = "takePartInPost", method = RequestMethod.POST)
    public Response takePartInPost(
            @ApiParam(value = "用户id") @RequestParam String userid,
            @ApiParam(value = "微信") @RequestParam(required = false) String weixin,
            @ApiParam(value = "QQ") @RequestParam(required = false) String QQ,
            @ApiParam(value = "手机") @RequestParam(required = false) String phone,
            @ApiParam(value = "帖子id") @RequestParam String postid) {
        Response response = new Response();
        int result = facadePost.takePartInPost(userid, weixin, QQ, phone, postid);
        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        response.setData(result);
        return response;
    }


    @ApiOperation(value = "插入", notes = "插入", response = Response.class)
    @RequestMapping(value = "insert", method = RequestMethod.POST)
    public Response insert(@ApiParam(value = "类型") @RequestParam String type) {
        Response response = new Response();
        int result = facadePost.insert(type);
        if (response.getCode() == 200) {
            response.setMessage("插入成功");
        }
        response.setData(result);
        return response;
    }


    @ApiOperation(value = "更多活动", notes = "更多活动", response = Response.class)
    @RequestMapping(value = "ActivePost", method = RequestMethod.POST)
    public Response ActivePost(@ApiParam(value = "帖子id") @RequestParam String id,
                               @ApiParam(value = "第几页") @RequestParam(required = false, defaultValue = "1") String pageNo,
                               @ApiParam(value = "每页多少条") @RequestParam(required = false, defaultValue = "10") String pageSize) {
        Response response = new Response();
        Paging<PostVo> pager = new Paging<>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List result = facadePost.ActivePost(id, pager);
        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        pager.result(result);
        response.setData(pager);
        return response;
    }


    @ApiOperation(value = "所有圈子", notes = "所有圈子", response = Response.class)
    @RequestMapping(value = "findAllCircle", method = RequestMethod.POST)
    public Response findAllCircle(
            @ApiParam(value = "第几页") @RequestParam(required = false, defaultValue = "1") String pageNo,
            @ApiParam(value = "每页多少条") @RequestParam(required = false, defaultValue = "12") String pageSize) {
        Response response = new Response();
        Paging<CircleVo> pager = new Paging<>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List result = circleAppFacade.findAllCircle(pager);
        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        pager.result(result);
        response.setData(pager);
        return response;
    }


    @ApiOperation(value = "选择5个圈子", notes = "选择5个圈子", response = Response.class)
    @RequestMapping(value = "followCircleUser", method = RequestMethod.POST)
    public Response followCircleUser(@ApiParam(value = "用户id") @RequestParam int userid,
                                     @ApiParam(value = "圈子id") @RequestParam String circleid) {
        Response response = new Response();
        int result = circleAppFacade.followCircleUser(userid, circleid);
        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        response.setData(result);
        return response;
    }

    @ApiOperation(value = "返回APP开屏图或开屏动画", notes = "返回当前APP用户开屏图或开屏动画", response = Response.class)
    @RequestMapping(value = "getOpenAppImg", method = RequestMethod.POST)
    public Response getOpenAppImg(){
        Response response = new Response();
        String imgurl = circleAppFacade.getOpenAppImg();
        if (response.getCode() == 200) {
            response.setMessage("获取成功");
        }else {
            response.setMessage("获取失败");
        }
        response.setData(imgurl);
        return response;
    }
}
