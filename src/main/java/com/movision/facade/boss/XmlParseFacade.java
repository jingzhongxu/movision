package com.movision.facade.boss;

import com.google.gson.Gson;
import com.movision.common.constant.PointConstant;
import com.movision.facade.im.ImFacade;
import com.movision.facade.pointRecord.PointRecordFacade;
import com.movision.facade.user.AppRegisterFacade;
import com.movision.fsearch.utils.StringUtil;
import com.movision.mybatis.coupon.entity.Coupon;
import com.movision.mybatis.coupon.service.CouponService;
import com.movision.mybatis.couponTemp.entity.CouponTemp;
import com.movision.mybatis.imuser.entity.ImUser;
import com.movision.mybatis.post.entity.Post;
import com.movision.mybatis.postLabel.entity.PostLabel;
import com.movision.mybatis.postLabel.service.PostLabelService;
import com.movision.mybatis.systemLayout.service.SystemLayoutService;
import com.movision.mybatis.user.entity.RegisterUser;
import com.movision.mybatis.user.entity.User;
import com.movision.mybatis.user.service.UserService;
import com.movision.utils.StrUtil;
import com.movision.utils.im.CheckSumBuilder;
import com.movision.utils.oss.MovisionOssClient;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * @Author zhurui
 * @Date 2017/10/25 16:40
 */
@Service
public class XmlParseFacade {

    @Autowired
    private PostFacade postFacade;

    @Autowired
    private MovisionOssClient movisionOssClient;

    @Autowired
    private UserService userService;

    @Autowired
    private PointRecordFacade pointRecordFacade;

    @Autowired
    private AppRegisterFacade appRegisterFacade;

    @Autowired
    private CouponService couponService;

    @Autowired
    private ImFacade imFacade;

    @Autowired
    private PostLabelService postLabelService;

    @Autowired
    private SystemLayoutService systemLayoutService;


    @Transactional
    public Map analysisXml(HttpServletRequest request, MultipartFile file, String nickname, String phone) {
        Map resault = new HashMap();
        SAXReader reader = new SAXReader();
        Post post = new Post();
        try {
            //查询用户是否存在，不存在新增操作
            Integer usid = queryUser(nickname, phone, post);
            post.setUserid(usid);
            Document document = reader.read(file.getInputStream());
            System.out.println(document);
            //获取跟标签
            Element element = document.getRootElement();
            List<Element> elements = element.elements();
            //存储本地图片路径，以便做空间释放操作
            List list = new ArrayList();
            boolean flg = false;
            //循环所有父节点
            for (Element e : elements) {
                //用于拼接帖子内容
                String content = "[";
                //获取发帖时间并转换为long类型
                Long publishTime = Long.parseLong(e.element("publishTime").getText());
                Date intime = new Date(publishTime);
                post.setIntime(intime);
                //类型
                String type = e.element("type").getText();
                //标签
                String tag = e.element("tag").getText();

                //图片内容解析
                if (type.equals("Photo")) {
                    content = getImgContentAnalysis(post, list, e, content);
                    flg = true;
                }
                //视频内容解析
                if (type.equals("Video")) {
                    //视频内容
                    content = getVideoContentAnalysis(post, e, content);
                    flg = true;
                }
                //纯文本解析
                /*if (type.equals("Text")){
                    //文本
                    s = getTextContentAnalysis(post, e, s);
                    flg = true;
                }*/

                if (!flg) {
                    content = "";
                }
                post.setIntime(new Date());
                post.setCircleid(125);
                post.setPostcontent(content);
                System.out.println("---------" + content);

                if (content != "") {
                    //标签操作 //
                    String[] tags = tag.split(",");
                    String lbs = "";
                    for (int i = 0; i < tags.length; i++) {
                        //查询标签表中是否有此标签
                        Integer lbid = postLabelService.queryPostLabelByNameCompletely(tags[i]);
                        if (lbid == null) {
                            //insertPostLabel(post, tags[i]);
                            PostLabel postLabel = new PostLabel();
                            postLabel.setName(tag);
                            postLabel.setUserid(post.getUserid());
                            postLabel.setIntime(new Date());
                            postLabel.setIsdel(0);
                            postLabelService.insertPostLabel(postLabel);
                            lbs += postLabel.getId() + ",";
                        } else {
                            lbs += lbid + ",";
                        }
                        if (i == tags.length - 1) {
                            lbs.substring(0, lbs.lastIndexOf(","));
                        }
                    }

                    //新增帖子操作
                    postFacade.addPostTest(request, "", "", post.getCircleid().toString(), post.getUserid().toString(),
                            post.getCoverimg(), post.getPostcontent(), lbs, "", "1");
                }

            }

            //释放空间,删除本地图片
            for (int k = 0; k < list.size(); k++) {
                File fi = new File(list.get(k).toString());
                fi.delete();
            }
            resault.put("code", 200);
        } catch (Exception e) {
            e.printStackTrace();
            resault.put("code", 400);
        }
        return resault;
    }

    private void insertPostLabel(Post post, String tag) {

    }

    /**
     * 查询用户
     *
     * @param nickname
     * @param phone
     * @param post
     */
    private Integer queryUser(String nickname, String phone, Post post) {
        User user = new User();
        if (StringUtil.isNotEmpty(phone)) {
            user.setPhone(phone);
        }
        if (StringUtil.isNotEmpty(nickname)) {
            user.setNickname(nickname);
        }
        //根据手机号或昵称查询
        User userid = userService.queryUserByPhone(phone);
        if (userid != null) {
            post.setUserid(userid.getId());
            return userid.getId();
        } else {
            //注册用户
            int uid = newUserRegistration(phone);
            post.setUserid(uid);
            return uid;
        }
    }

    /**
     * 新用户注册
     * @param phone
     */
    private Integer newUserRegistration(String phone) {
        try {
            // 生成6位随机字串
            String verifyCode = (int) ((Math.random() * 9 + 1) * 100000) + "";
            //1 生成token
            UsernamePasswordToken newToken = new UsernamePasswordToken(phone, verifyCode.toCharArray());
            RegisterUser member = new RegisterUser();
            member.setPhone(phone);
            member.setMobileCheckCode(verifyCode);
            //2 注册用户/修改用户信息
            Gson gson = new Gson();
            String json = gson.toJson(newToken);
            member.setToken(json);
            //2.1 手机号不存在,则新增用户信息
            int uid = appRegisterFacade.registerMember(member);
            //3 如果用户当前手机号有领取过H5页面分享的优惠券，那么不管新老用户统一将优惠券临时表yw_coupon_temp中的优惠券信息全部放入优惠券正式表yw_coupon中
            processCoupon(phone, uid);
            //2.2 增加新用户注册积分流水
            pointRecordFacade.addPointRecord(PointConstant.POINT_TYPE.new_user_register.getCode(), PointConstant.POINT.new_user_register.getCode(), uid);
            //2.3 增加绑定手机号积分流水
            pointRecordFacade.addPointRecord(PointConstant.POINT_TYPE.binding_phone.getCode(), PointConstant.POINT.binding_phone.getCode(), uid);
            //4 判断该userid是否存在一个im用户，若不存在，则注册im用户;若存在，则查询
            getImuserForReturn(uid);
            return uid;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断该userid是否存在一个im用户，若不存在，则注册im用户
     *
     * @param
     * @throws IOException
     */
    private void getImuserForReturn(int userid) throws IOException {

        Boolean isExistImUser = imFacade.isExistAPPImuser(userid);
        if (!isExistImUser) {
            //若不存在，则注册im用户
            ImUser imUser = new ImUser();
            imUser.setUserid(userid);
            imUser.setAccid(CheckSumBuilder.getAccid(String.valueOf(userid)));  //根据userid生成accid
            imUser.setName(StrUtil.genDefaultNickNameByTime());
            imFacade.AddImUser(imUser);
        }
    }


    /**
     * 如果当前手机号在分享的H5页面领取过优惠券，那么不管新老用户统一在这里将优惠券临时表中的数据同步到优惠券正式表中
     *
     * @param phone
     * @param userid
     */
    @Transactional
    void processCoupon(String phone, int userid) {
        //首先检查当前手机号是否领取过优惠券
        List<CouponTemp> couponTempList = couponService.checkIsGetCoupon(phone);
        List<Coupon> couponList = new ArrayList<>();
        if (couponTempList.size() > 0) {
            //遍历替换phone为userid，放入List<Coupon>
            for (int i = 0; i < couponTempList.size(); i++) {
                CouponTemp couponTemp = couponTempList.get(i);
                Coupon coupon = new Coupon();
                coupon.setUserid(userid);
                coupon.setTitle(couponTemp.getTitle());
                coupon.setContent(couponTemp.getContent());
                coupon.setType(couponTemp.getType());
                if (null != couponTemp.getShopid()) {
                    coupon.setShopid(couponTemp.getShopid());
                }
                coupon.setStatue(couponTemp.getStatue());
                coupon.setBegintime(couponTemp.getBegintime());
                coupon.setEndtime(couponTemp.getEndtime());
                coupon.setIntime(couponTemp.getIntime());
                coupon.setTmoney(couponTemp.getTmoney());
                coupon.setUsemoney(couponTemp.getUsemoney());
                coupon.setIsdel(couponTemp.getIsdel());
                couponList.add(coupon);
            }

            //插入优惠券列表
            couponService.insertCouponList(couponList);
            //删除临时表中的优惠券领取记录
            couponService.delCouponTemp(phone);
        }
    }

    /**
     * 文本内容解析
     *
     * @param post
     * @param e
     * @param content
     * @return
     */
    private String getTextContentAnalysis(Post post, Element e, String content) {
        String caption = e.element("content").getText();
        String caps = caption.replace("<p>", "");
        caps = caps.replace("</p>", "");
        content += "{\"type\": 0,\"orderid\":" + 0 + ",\"value\":\"" + caps + "\",\"wh\": \"\",\"dir\": \"\"}";

        //用于内容拼接闭合
        String neirong = post.getPostcontent();
        content += "]";
        //System.out.println("---------"+s);
        post.setPostcontent(neirong);
        return content;
    }

    /**
     * 视频内容解析
     *
     * @param post
     * @param e
     * @param content
     * @return
     */
    private String getVideoContentAnalysis(Post post, Element e, String content) {
        String embed = e.element("embed").getText();
        embed = embed.replace("{", "");
        embed = embed.replace("}", "");
        embed = embed.replace("\"", "");
        String[] embeds = embed.split(",");
        int num = 0;
        for (int i = 0; i < embeds.length; i++) {
            if (embeds[i].substring(0, embeds[i].indexOf(":")).equals("originUrl")) {
                content += "{\"type\": 2,\"orderid\":" + num + ",";
                Map m = download(embeds[i].substring(embeds[i].indexOf(":") + 1, embeds[i].length()), "video");
                content += "\"value\":\"" + m.get("newurl") + "\",\"wh\": \"\",\"dir\": \"\"},";
                //System.out.println(originUrl);
                num++;
            }

        }
        //文本
        Element caption = e.element("caption");
        String caps = caption.getText().replace("<p>", "");
        caps = caps.replace("</p>", "");
        content += "{\"type\": 0,\"orderid\":" + num + ",\"value\":\"" + caps + "\",\"wh\": \"\",\"dir\": \"\"}";

        //用于内容拼接闭合
        String neirong = post.getPostcontent();
        content += "]";
        //System.out.println("---------"+s);
        post.setPostcontent(neirong);
        return content;
    }

    /**
     * 图片内容解析
     *
     * @param post
     * @param list
     * @param e
     * @param content
     * @return
     */
    private String getImgContentAnalysis(Post post, List list, Element e, String content) {
        Element photoLinks = e.element("photoLinks");
        String pho = photoLinks.getText();
        //pho = pho.substring(2, pho.lastIndexOf("]")-1);
        pho = pho.replace("[", "");
        pho = pho.replace("{", "");
        pho = pho.replace("}", "");
        pho = pho.replace("]", "");
        pho = pho.replace("\"", "");
        String[] substring = pho.split(",");
        int num = 0;
        //循环子节点拼接帖子内容
        for (int i = 0; i < substring.length; i++) {

            String wh = "\"wh\":";
            if (substring[i].substring(0, substring[i].indexOf(":")).equals("orign")) {
                content += "\"type\":1,";
                Map m = download(substring[i].substring(substring[i].indexOf(":") + 1, substring[i].indexOf("?")), "img");
                list.add(m.get("oldurl"));
                if (i == 0) {
                    post.setCoverimg(m.get("newurl").toString());
                }
                content += "\"value\":\"" + m.get("newurl") + "\",\"dir\": \"\"},";
            }
            if (substring[i].substring(0, substring[i].indexOf(":")).equals("ow")) {
                content += "{\"orderid\":" + num + ",";
                num++;
                content += wh + "\"" + substring[i].substring(substring[i].indexOf(":") + 1, substring[i].length()) + "×";
            }
            if (substring[i].substring(0, substring[i].indexOf(":")).equals("oh")) {
                content += substring[i].substring(substring[i].indexOf(":"), substring[i].length()) + "\",";
            }
        }

        //文本
        Element caption = e.element("caption");
        String caps = caption.getText().replace("<p>", "");
        caps = caps.replace("</p>", "");
        content += "{\"type\": 0,\"orderid\":" + num + ",\"value\":\"" + caps + "\",\"wh\": \"\",\"dir\": \"\"}";

        //用于内容拼接闭合
        String neirong = post.getPostcontent();
        content += "]";
        //System.out.println("---------"+s);
        post.setPostcontent(neirong);
        return content;
    }

    /**
     * 下载文件
     *
     * @param str
     */
    private Map download(String str, String type) {
        InputStream is = null;
        OutputStream os = null;
        Map map = new HashMap();
        String path = systemLayoutService.queryServiceUrl("file_xml_dwonload_img");
        if (type.equals("img")) {
            path += "img/";
        } else if (type.equals("video")) {
            path += "video/";
        }
        try {
            String url = str;
            URL u = new URL(url);
            //获取文件名
            String[] filename = url.split("/");
            String s = filename[filename.length - 1];
            is = u.openStream();
            os = new FileOutputStream(path + s);
            int buff = 0;
            while ((buff = is.read()) != -1) {
                os.write(buff);
            }
            map.put("oldurl", path + s);
            if (type.equals("img")) {
                //图片上传
                Map t = movisionOssClient.uploadFileObject(new File(path + s), "img", "post");
                map.put("newurl", t.get("url"));
            } else if (type.equals("video")) {
                //视频上传
                Map m = movisionOssClient.uploadFileObject(new File(path + s), "video", "post");
                map.put("newurl", m.get("url"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }
}
