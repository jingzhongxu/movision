package com.zhuhuibao.business.system.agent.site;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.zhuhuibao.common.Response;
import com.zhuhuibao.common.pojo.ResultBean;
import com.zhuhuibao.mybatis.memCenter.service.AgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 代理商管理
 * Created by cxx on 2016/3/22 0022.
 */
@RestController
public class AgentSiteController {
    private static final Logger log = LoggerFactory.getLogger(AgentSiteController.class);

    @Autowired
    private AgentService agentService;

    /**
     * 根据产品id查询代理商跟厂商（区域分组）
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/rest/agent/getAgentByProId", "/rest/system/site/agent/sel_agent_by_pro_id"}, method = RequestMethod.GET)
    public Response getAgentByProId(String id) {
        Response response = new Response();
        Map map = agentService.getAgentByProId(id);
        response.setData(map);
        return response;
    }

    /**
     * 根据子系统id查优秀代理商
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/rest/agent/getGreatAgentByScateid", "/rest/system/site/agent/sel_great_agent_by_scateId"}, method = RequestMethod.GET)
    public Response getGreatAgentByScateid(String id) {
        Response response = new Response();
        List<ResultBean> resultBeen = agentService.getGreatAgentByScateid(id);
        response.setData(resultBeen);
        return response;
    }



    /**
     * 根据品牌id查询代理商跟厂商（区域分组）
     *
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "根据品牌id查询代理商跟厂商（区域分组）", notes = "根据品牌id查询代理商跟厂商（区域分组）", response = Response.class)
    @RequestMapping(value = {"/rest/agent/getAgentByBrandid", "/rest/system/site/agent/sel_agent_by_brandId"}, method = RequestMethod.GET)
    public Response getAgentByBrandid(String id) {
        Response response = new Response();
        Map map = agentService.getAgentByBrandid(id);
        response.setData(map);
        return response;
    }

    /***
     * 根据品牌Id查询VIP代理商
     *
     * @return VIP代理商
     * @throws IOException
     * @author changxinwei
     * @time 2016/11/10
     */

    @ApiOperation(value = "尊贵盟友的本品牌VIP代理商", notes = "尊贵盟友的本品牌VIP代理商", response = Response.class)
    @RequestMapping(value = {"/rest/agent/getGreatAgentByBrandId", "/rest/system/site/agent/sel_great_agent_by_brandId"}, method = RequestMethod.GET)
    public Response getGreatAgentVIPByBrandId(@ApiParam(value = "品牌的Id") @RequestParam(required = true) String id) {
        Response response = new Response();
        List<ResultBean> resultBeen = agentService.getGreatAgentVIPByBrandId(id);
        response.setData(resultBeen);
        return response;
    }
}
