package com.movision.controller.app;

import com.movision.common.Response;
import com.movision.facade.cart.CartFacade;
import com.movision.mybatis.cart.entity.CartVo;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @Author shuxf
 * @Date 2017/2/18 17:12
 * 用于提供所有的购物车相关接口
 */
@RestController
@RequestMapping("/app/cart/")
public class AppCartController {

    @Autowired
    private CartFacade cartFacade;

    /**
     * 商品加入购物车接口（租用的和出售的共用）
     */
    @ApiOperation(value = "商品加入购物车接口（租用的和出售的共用）", notes = "商品被加入购物车", response = Response.class)
    @RequestMapping(value = "addGoodsCart", method = RequestMethod.POST)
    public Response addGoodsCart(@ApiParam(value = "用户id") @RequestParam String userid,
                                 @ApiParam(value = "商品id") @RequestParam String goodsid,
                                 @ApiParam(value = "套餐id,为空时表示不选择套餐") @RequestParam(required = false) String comboid,
                                 @ApiParam(value = "活动id,为空时表示不参加活动") @RequestParam(required = false) String discountid,
                                 @ApiParam(value = "是否需要跟机员（0 不需要 1 需要）租赁时必填，购买和二手时传空") @RequestParam(required = false) String isdebug,
                                 @ApiParam(value = "数量") @RequestParam String sum,
                                 @ApiParam(value = "商品定位：0 租赁 1 出售 2 二手") @RequestParam String type,
                                 @ApiParam(value = "租赁日期（租赁商品时为必填。多天使用年-月-日，并用英文逗号隔开）") @RequestParam(required = false) String rentdate) throws ParseException {
        Response response = new Response();

        int count = cartFacade.addGoodsCart(userid, goodsid, comboid, discountid, isdebug, sum, type, rentdate);

        if (count == 1) {
            response.setCode(200);
            response.setMessage("加入成功");
        } else if (count == 0) {
            response.setCode(300);
            response.setMessage("加入失败或商品库存不足");
        }
        return response;
    }

    /**
     * 查询用户购物车中的商品列表
     */
    @ApiOperation(value = "查询用户购物车中的商品列表", notes = "用户点击购物车图标或者点击去结算，跳转到购物车时返回购物车中所有商品列表", response = Response.class)
    @RequestMapping(value = "queryCartByUser", method = RequestMethod.POST)
    public Response queryCartByUser(@ApiParam(value = "用户id") @RequestParam String userid) {
        Response response = new Response();

        List<CartVo> cartList = cartFacade.queryCartByUser(userid);

        if (response.getCode() == 200) {
            response.setMessage("查询成功");
        }
        response.setData(cartList);
        return response;
    }

    /**
     * 购物车删除接口（批量删除和单个删除）
     */
    @ApiOperation(value = "购物车删除接口（批量删除和单个删除）", notes = "用于用户购物车中的商品批量删除和单个删除的接口", response = Response.class)
    @RequestMapping(value = "deleteCartGoods", method = RequestMethod.POST)
    public Response deleteCartGoods(@ApiParam(value = "用户id") @RequestParam String userid,
                                    @ApiParam(value = "购物车id字符串（多个用英文逗号隔开）") @RequestParam String cartids) {
        Response response = new Response();

        cartFacade.deleteCartGoods(userid, cartids);

        if (response.getCode() == 200) {
            response.setMessage("删除成功");
        }
        return response;
    }

    /**
     * 购物车商品————修改商品数量接口
     */
    @ApiOperation(value = "购物车商品————修改商品数量接口", notes = "用于用户在购物车中直接对单个商品的数量进行修改", response = Response.class)
    @RequestMapping(value = "updateCartGoodsSum", method = RequestMethod.POST)
    public Response updateCartGoodsSum(@ApiParam(value = "购物车id") @RequestParam String cartid,
                                       @ApiParam(value = "修改类型：0 减 1 加") @RequestParam String type) {
        Response response = new Response();

        int sum = cartFacade.updateCartGoodsSum(cartid, type);//返回修改后的商品数量

        if (response.getCode() == 200 && sum > 0) {
            response.setMessage("修改成功");
            response.setData(sum);
        }
        if (sum == -1) {
            response.setMessage("无库存");
            response.setData(sum);
        }
        return response;
    }

    /**
     * 购物车商品————修改商品租用日期
     */
    @ApiOperation(value = "购物车商品————修改商品租用日期", notes = "用于用户在购物车中直接对单个商品的租用日期进行修改", response = Response.class)
    @RequestMapping(value = "updateCartGoodsRentDate", method = RequestMethod.POST)
    public Response updateCartGoodsRentDate(@ApiParam(value = "购物车id") @RequestParam String cartid,
                                            @ApiParam(value = "租用日期") @RequestParam String rentdate) throws ParseException {
        Response response = new Response();

        cartFacade.updateCartGoodsRentDate(cartid, rentdate);

        if (response.getCode() == 200) {
            response.setMessage("修改成功");
        }
        return response;
    }

    /**
     * 购物车商品————点击“去结算”————跳转订单确认页
     */
    @ApiOperation(value = "购物车商品————点击“去结算”————跳转订单确认页", notes = "用于用户在购物车中选择商品后结算操作，跳转订单确认页", response = Response.class)
    @RequestMapping(value = "cartBilling", method = RequestMethod.POST)
    public Response cartBilling(@ApiParam(value = "购物车id(多件商品的购物车id用英文逗号','隔开)") @RequestParam String cartids,
                                @ApiParam(value = "用户id") @RequestParam String userid,
                                @ApiParam(value = "结算总价") @RequestParam String totalprice,
                                @ApiParam(value = "当前选择的定位地址-省code") @RequestParam String provincecode,
                                @ApiParam(value = "当前选择的定位地址-市code") @RequestParam String citycode) {
        Response response = new Response();

        Map<String, Object> map = cartFacade.cartBilling(cartids, userid, totalprice, provincecode, citycode);

        if ((int) map.get("code") == 200) {
            response.setMessage("可结算");
        } else if ((int) map.get("code") == -1) {
            response.setCode(-1);
            response.setMessage("不可结算");
        }
        response.setData(map);
        return response;
    }
}
