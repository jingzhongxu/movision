package com.zhuhuibao.mybatis.memCenter.service;

import com.zhuhuibao.common.Response;
import com.zhuhuibao.common.constant.Constants;
import com.zhuhuibao.common.constant.MsgCodeConstant;
import com.zhuhuibao.common.pojo.AskPriceBean;
import com.zhuhuibao.common.pojo.AskPriceResultBean;
import com.zhuhuibao.common.pojo.AskPriceSearchBean;
import com.zhuhuibao.mybatis.memCenter.entity.AskPrice;
import com.zhuhuibao.mybatis.memCenter.entity.Member;
import com.zhuhuibao.mybatis.memCenter.mapper.AgentMapper;
import com.zhuhuibao.mybatis.memCenter.mapper.AskPriceMapper;
import com.zhuhuibao.mybatis.memCenter.mapper.MemberMapper;
import com.zhuhuibao.mybatis.memCenter.mapper.ProvinceMapper;
import com.zhuhuibao.utils.pagination.model.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 询报价业务处理
 * Created by cxx on 2016/3/29 0029.
 */
@Service
@Transactional
public class PriceService {

    private static final Logger log = LoggerFactory.getLogger(PriceService.class);

    @Autowired
    AskPriceMapper askPriceMapper;

    @Autowired
    ProvinceMapper provinceMapper;

    @Autowired
    AgentMapper agentMapper;

    @Autowired
    MemberMapper memberMapper;

    @Autowired
    com.zhuhuibao.common.constant.ApiConstants ApiConstants;
    /**
     * 询价保存
     */
    public Response saveAskPrice(AskPrice askPrice){
        log.debug("询价保存");
        Response result = new Response();
        if(askPrice.getBillurl()!=null && !askPrice.getBillurl().equals("")){
            String fileUrl = askPrice.getBillurl();
            fileUrl = ApiConstants.getUploadDoc()+ Constants.upload_price_document_url+"/"+fileUrl;
            File file = new File(fileUrl);
            if(file.exists()){
                askPriceMapper.saveAskPrice(askPrice);
                result.setCode(200);
            }
            else
            {
                result.setCode(400);
                result.setMessage("文件不存在");
                result.setMsgCode(MsgCodeConstant.file_not_exist);
            }
        }else{
            askPriceMapper.saveAskPrice(askPrice);
            result.setCode(200);
        }
        return result;
    }

    /**
     * 根据Id具体某条询价信息
     */
    public Response queryAskPriceByID(String id){
        log.debug("根据Id具体某条询价信息");
        Response result = new Response();
        try{
            AskPriceBean bean = askPriceMapper.queryAskPriceByID(id);
            SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(bean.getEndTime());
            if(date.before(new Date())){
                bean.setStatusName("结束");
            }else{
                bean.setStatusName("报价中");
            }

            if("1".equals(bean.getType())){
                bean.setTypeName("公开询价");
            }else{
                bean.setTypeName("定向询价");
            }

            if("1".equals(bean.getIsTax())){
                bean.setIsTaxName("含税报价");
            }else{
                bean.setIsTaxName("非含税报价");
            }
            if(!bean.getIsShow()){
                bean.setCompanyName("");
                bean.setLinkMan("");
                bean.setTelephone("");
                bean.setEmail("");
            }
            result.setCode(200);
            result.setData(bean);
        }catch (Exception e){
            log.error("queryAskPriceByID error",e.getMessage());
            e.printStackTrace();
        }
        return result;
    }



    /**
     * 获得我的联系方式（询报价者联系方式）
     */
    public Response getLinkInfo(String id){
        Response result = new Response();
        Map map = new HashMap();
        Member member = memberMapper.findMemById(id);
        if(member!=null){
            if("2".equals(member.getIdentify())){
                map.put(Constants.companyName,member.getPersonRealName());
                map.put(Constants.linkMan,"");
                map.put(Constants.telephone,member.getFixedTelephone());
                map.put(Constants.mobile,member.getFixedMobile());
            }else{
                map.put(Constants.companyName,member.getEnterpriseName());
                map.put(Constants.linkMan,member.getEnterpriseLinkman());
                map.put(Constants.telephone,member.getFixedTelephone());
                map.put(Constants.mobile,member.getFixedMobile());
            }
        }
        result.setCode(200);
        result.setData(map);
        return result;
    }


    /**
     * 根据条件查询询价信息（分页）
     */
    public List<AskPriceResultBean> findAllByPager(Paging<AskPriceResultBean> pager, AskPriceSearchBean askPriceSearch){
        log.debug("查询询价信息（分页）");
        List<AskPriceResultBean> resultBeanList = askPriceMapper.findAll(askPriceSearch);
        List<AskPriceResultBean> resultBeanList1 = askPriceMapper.findAllByPager1(pager.getRowBounds(),askPriceSearch);
        List askList = new ArrayList();
        for(int i=0;i<resultBeanList1.size();i++){
            AskPriceResultBean resultBean = resultBeanList1.get(i);
            Map askMap = new HashMap();
            askMap.put(Constants.id,resultBean.getId());
            askMap.put(Constants.title,resultBean.getTitle());
            askMap.put(Constants.status,resultBean.getStatus());
            askMap.put(Constants.type,resultBean.getType());
            askMap.put(Constants.publishTime,resultBean.getPublishTime().substring(0,10));
            askMap.put(Constants.area,resultBean.getArea());
            List offerList = new ArrayList();
            for(int y=0;y<resultBeanList.size();y++){
                AskPriceResultBean resultBean1 = resultBeanList.get(y);
                if(resultBean.getId().equals(resultBean1.getAskid())){
                    Map offerMap = new HashMap();
                    offerMap.put(Constants.id,resultBean1.getOfferid());
                    offerMap.put(Constants.offerTime,resultBean1.getOfferTime().substring(0,19));
                    offerMap.put(Constants.companyName,resultBean1.getCompanyName());
                    offerMap.put(Constants.address,resultBean1.getAddress());
                    offerList.add(offerMap);
                }
            }
            askMap.put("offerList",offerList);
            askList.add(askMap);
        }

        return askList;
    }

    /**
     * 最新公开询价(限六条)
     */
    public Response queryNewPriceInfo(int count, String createid){
        Response response = new Response();
        List<AskPrice> askPriceList = askPriceMapper.queryNewPriceInfo(count,createid);
        List list = new ArrayList();
        for(int i=0;i<askPriceList.size();i++){
            AskPrice askPrice = askPriceList.get(i);
            Map map = new HashMap();
            map.put(Constants.id,askPrice.getId());
            map.put(Constants.companyName,askPrice.getTitle());
            list.add(map);
        }
        response.setCode(200);
        response.setData(list);
        return response;
    }

    /**
     * 最新公开询价(分页)
     */
    public List<AskPrice> queryNewPriceInfoList(Paging<AskPrice> pager,AskPriceSearchBean askPriceSearch){
        List<AskPrice> askPriceList = askPriceMapper.findAllNewPriceInfoList(pager.getRowBounds(),askPriceSearch);
        List list = new ArrayList();
        for(int i=0;i<askPriceList.size();i++){
            AskPrice askPrice = askPriceList.get(i);
            Map map = new HashMap();
            map.put(Constants.id,askPrice.getId());
            map.put(Constants.title,askPrice.getTitle());
            map.put(Constants.publishTime,askPrice.getPublishTime().substring(0,10));
            map.put(Constants.area,askPrice.getProvinceCode());
            list.add(map);
        }
        return list;
    }
}
