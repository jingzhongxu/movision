package com.zhuhuibao.business.member.base;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.zhuhuibao.common.Response;
import com.zhuhuibao.common.constant.MemberConstant;
import com.zhuhuibao.common.constant.MsgCodeConstant;
import com.zhuhuibao.exception.BusinessException;
import com.zhuhuibao.utils.MsgPropertiesUtils;
import com.zhuhuibao.utils.sms.SDKSendSms;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.taobao.api.ApiException;
import com.zhuhuibao.common.constant.Constants;
import com.zhuhuibao.mybatis.dictionary.service.DictionaryService;
import com.zhuhuibao.mybatis.memberReg.entity.Member;
import com.zhuhuibao.mybatis.memberReg.entity.Validateinfo;
import com.zhuhuibao.mybatis.memberReg.service.MemberRegService;
import com.zhuhuibao.mybatis.memberReg.service.RegisterValidateService;
import com.zhuhuibao.security.EncodeUtil;
import com.zhuhuibao.utils.DateUtils;
import com.zhuhuibao.utils.PropertiesUtils;
import com.zhuhuibao.utils.VerifyCodeUtils;

/**
 * @author jianglz
 * @since 15/12/12.
 */
@RestController
@Api(value="register", description="会员管理")
public class RegisterController {
	private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

	@Autowired
	private MemberRegService memberService;
	
	@Autowired
	private DictionaryService ds;
	
	/**
	 * 注册验证码业务类
	 */
	@Autowired
	private RegisterValidateService rvService;
	
	 /**
	  * 邮箱注册时的图形验证码
	  * @param response
	  */
	@ApiOperation(value="邮箱注册时的图形验证码",notes="邮箱注册时的图形验证码",response = Response.class)
	@RequestMapping(value = {"/rest/imgCode","rest/member/site/base/sel_imgCode"}, method = RequestMethod.GET)
	public void getCode(HttpServletResponse response) throws IOException {
		Subject currentUser = SecurityUtils.getSubject();
        Session sess = currentUser.getSession(false);
		String verifyCode = VerifyCodeUtils.outputHttpVerifyImage(100,40,response,Constants.CHECK_IMG_CODE_SIZE);
		log.debug("verifyCode == " + verifyCode);
		sess.setAttribute(MemberConstant.SESSION_TYPE_REGISTER, verifyCode);
	}

	/**
	 * 生成图片码
	 * @param response
	 * @param verifyCode
	 * @param out
     */
	@ApiOperation(value="生成图片码",notes="生成图片码",response = Response.class)
	private void genImgCode(HttpServletResponse response, String verifyCode, ServletOutputStream out) {
		try {
			out = response.getOutputStream();
			int w = 100;// 定义图片的width
			int h = 40;// 定义图片的height
			VerifyCodeUtils.outputImage1(w, h, out, verifyCode);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	  * 找回密码的图形验证码
	  * @param response
	  */
	@RequestMapping(value = {"/rest/getSeekPwdImgCode","rest/member/site/base/sel_seekPwdImgCode"}, method = RequestMethod.GET)
	@ApiOperation(value="找回密码的图形验证码",notes="找回密码的图形验证码",response = Response.class)
	public void getSeekPwdImgCode( HttpServletResponse response) throws IOException {
		log.debug("找回密码的图形验证码");
		String verifyCode = VerifyCodeUtils.outputHttpVerifyImage(100,40,response,Constants.CHECK_IMG_CODE_SIZE);
		Subject currentUser = SecurityUtils.getSubject();
		Session sess = currentUser.getSession(false);
		sess.setAttribute(MemberConstant.SESSION_TYPE_SEEKPWD, verifyCode);
	}
	
	/**
	 * 手机注册账号时发送的验证码
	 * @throws IOException
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * @throws ApiException 
	 */
	@ApiOperation(value="手机注册账号时发送的验证码",notes="手机注册账号时发送的验证码",response = Response.class)
	@RequestMapping(value = {"/rest/mobileCode","rest/member/site/base/sel_mobileCode"}, method = RequestMethod.GET)
	public Response getMobileCode(@ApiParam(value = "验证的手机号") @RequestParam String mobile,
								  @ApiParam(value = "图形验证码") @RequestParam String imgCode) throws IOException, ApiException {
		log.debug("获得手机验证码  mobile=="+mobile);
		Subject currentUser = SecurityUtils.getSubject();
		Session sess = currentUser.getSession(true);
		String sessionImgCode = (String) sess.getAttribute(MemberConstant.SESSION_TYPE_REGISTER);
		Response response = new Response();
		if(imgCode.equalsIgnoreCase(sessionImgCode)) {
			// 生成随机字串
			String verifyCode = VerifyCodeUtils.generateVerifyCode(Constants.CHECK_MOBILE_CODE_SIZE, VerifyCodeUtils.VERIFY_CODES_DIGIT);
			log.debug("verifyCode == " + verifyCode);
			Map<String, String> map = new LinkedHashMap<>();
			map.put("code", verifyCode);
			map.put("time", Constants.sms_time);
			Gson gson = new Gson();
			String json = gson.toJson(map);
			SDKSendSms.sendSMS(mobile, json, PropertiesUtils.getValue("register_code_sms_template_code"));
			Validateinfo info = new Validateinfo();
			info.setCreateTime(DateUtils.date2Str(new Date(), "yyyy-MM-dd HH:mm:ss"));
			info.setCheckCode(verifyCode);
			info.setAccount(mobile);
			memberService.inserValidateInfo(info);
			sess.setAttribute("r" + mobile, verifyCode);
		}else {
			throw new BusinessException(MsgCodeConstant.validate_error, MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.validate_error)));
		}
		return response;
	}
	
	/**
	 * 找回密码时手机发送的验证码
	 * @throws IOException
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * @throws ApiException 
	 */
	@ApiOperation(value="找回密码时手机发送的验证码",notes="找回密码时手机发送的验证码",response = Response.class)
	@RequestMapping(value = {"/rest/getSeekPwdMobileCode","rest/member/site/base/sel_seekPwdMobileCode"}, method = RequestMethod.GET)
	public Response getSeekPwdMobileCode(@ApiParam(value = "验证的手机号") @RequestParam String mobile,
										 @ApiParam(value = "图形验证码") @RequestParam String imgCode) throws IOException, ApiException {
		log.debug("获得手机验证码  mobile=="+mobile);
		Subject currentUser = SecurityUtils.getSubject();
        Session sess = currentUser.getSession(true);
		String sessImgCode = (String) sess.getAttribute(MemberConstant.SESSION_TYPE_SEEKPWD);
		if(imgCode.equalsIgnoreCase(sessImgCode)) {
			// 生成随机字串
			String verifyCode = VerifyCodeUtils.generateVerifyCode(Constants.CHECK_MOBILE_CODE_SIZE, VerifyCodeUtils.VERIFY_CODES_DIGIT);
			log.debug("verifyCode == " + verifyCode);
			//发送验证码到手机
			//SDKSendTemplateSMS.sendSMS(mobile, verifyCode);
			Map<String, String> map = new LinkedHashMap<>();
			map.put("code", verifyCode);
			map.put("time", Constants.sms_time);
			Gson gson = new Gson();
			String json = gson.toJson(map);
			SDKSendSms.sendSMS(mobile, json, PropertiesUtils.getValue("forget_pwd_sms_template_code"));
			//		SDKSendTaoBaoSMS.sendFindPwdSMS(mobile, verifyCode, Constants.sms_time);
			Validateinfo info = new Validateinfo();
			info.setCreateTime(DateUtils.date2Str(new Date(), "yyyy-MM-dd HH:mm:ss"));
			info.setCheckCode(verifyCode);
			info.setAccount(mobile);
			memberService.inserValidateInfo(info);
			sess.setAttribute("s" + mobile, verifyCode);
		}else{
			throw new BusinessException(MsgCodeConstant.validate_error, MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.validate_error)));
		}
		return new Response();
	}

    /**
     * 会员注册
     * @param member
     * @return
     * @throws IOException 
     */
	@ApiOperation(value="会员注册",notes="会员注册",response = Response.class)
	@RequestMapping(value = {"/rest/register","rest/member/site/base/add_register"}, method = RequestMethod.POST)
	public Response register(Member member) throws Exception{
		log.debug("注册  mobile=="+member.getMobile()+" email =="+member.getEmail());
		Response result = new Response();
//		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session sess = currentUser.getSession(false);
			//校验手机验证码是否正确
			if (member.getMobileCheckCode() != null) {
				String verifyCode = (String) sess.getAttribute("r" + member.getMobile());
				result = memberService.registerMobileMember(member, verifyCode);
			}
			if (member.getEmailCheckCode() != null) {
				String verifyCode = (String) sess.getAttribute(MemberConstant.SESSION_TYPE_REGISTER);
				result = memberService.registerMailMember(member, verifyCode);
			}
		return result;
	}
	
	 /**
     * 找回密码时填写账户名
     * @param member
     * @return
     * @throws IOException 
     */
	 @ApiOperation(value="找回密码时填写账户名",notes="找回密码时填写账户名",response = Response.class)
	@RequestMapping(value = {"/rest/writeAccount","rest/member/site/base/add_writeAccount"}, method = RequestMethod.POST)
	public Response writeAccount(Member member) throws IOException {
		log.debug("seek pwd write account");
		Response result = new Response();
		Subject currentUser = SecurityUtils.getSubject();
        Session sess = currentUser.getSession(false);
		String seekCode = (String) sess.getAttribute(MemberConstant.SESSION_TYPE_SEEKPWD);
		log.info("writeAccount seekCode === "+seekCode);
		result = memberService.writeAccount(member, seekCode);

		return result;
	}
	
	/**
	 * 手机验证身份
	 * @param member
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value="找回密码手机验证身份",notes="找回密码手机验证身份",response = Response.class)
	@RequestMapping(value = {"/rest/mobileValidate","rest/member/site/base/sel_mobileValidate"}, method = RequestMethod.POST)
	public Response mobileValidate(Member member) throws IOException {
		log.debug("找回密码  mobile =="+member.getMobile());
		Subject currentUser = SecurityUtils.getSubject();
        Session sess = currentUser.getSession(false);
		String seekMobileCode = (String) sess.getAttribute("s"+member.getMobile());

		return memberService.mobileValidate(member, seekMobileCode);
	}
	
	/**
	 * 发送验证邮件密码重置
	 * @param member
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value="发送验证邮件密码重置",notes="发送验证邮件密码重置",response = Response.class)
	@RequestMapping(value = {"/rest/sendValidateMail","rest/member/site/base/sel_sendValidateMail"}, method = RequestMethod.POST)
	public Response sendValidateMail(Member member) throws IOException {
		log.debug("找回密码  email =="+member.getEmail());
		Response result = new Response();
		rvService.sendValidateMail(member, PropertiesUtils.getValue("host.ip"));
		String mail = ds.findMailAddress(member.getEmail());
		Map<String,String> map = new HashMap<String,String>();
		if(mail != null && !mail.equals(""))
		{
			map.put("button", "true");
		}
		else
		{
			map.put("button", "false");
		}
		result.setData(map);

		return result;
	}
	
	/**
	 * 修改密码
	 * @param member
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value="修改密码",notes="修改密码",response = Response.class)
	@RequestMapping(value = {"/rest/modifyPwd","rest/member/site/base/upd_pwd"}, method = RequestMethod.POST)
	public Response modifyPwd(Member member) throws IOException {
		log.debug("重置密码");
		Response response = new Response();
		int result = memberService.modifyPwd(member);
		Validateinfo info = new Validateinfo();
		info.setAccount(member.getAccount());
		memberService.deleteValidateInfo(info);

		return response;
	}
	
	/**
	 * 邮件激活账户
	 * @param req
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@ApiOperation(value="邮件激活账户",notes="邮件激活账户",response = Response.class)
	@RequestMapping(value = {"/rest/activateEmail","rest/member/site/base/sel_activateEmail"}, method = RequestMethod.GET)
	public ModelAndView activateEmail(HttpServletRequest req) throws UnsupportedEncodingException {
		log.debug("email activate start.....");
		Response response = new Response();
		ModelAndView modelAndView = new ModelAndView(); 
		try
        {
			String vm = req.getParameter("vm");//获取email
			if(vm != null & !vm.equals(""))
			{
				String decodeVM = new String (EncodeUtil.decodeBase64(vm));
	        	response = rvService.processActivate(decodeVM);
	        	modelAndView.addObject("email", EncodeUtil.encodeBase64ToString(String.valueOf(response.getData()).getBytes()));
	        	RedirectView rv = new RedirectView(rvService.getRedirectUrl(response, "active"));
	        	modelAndView.setView(rv);
			}
        }
        catch(Exception e)
        {
        	log.error("email activate member error!",e);
        }
       
        return modelAndView; 
	}
	
	/**
	 * 点击重置密码邮件链接验证身份
	 * @param req
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@ApiOperation(value="点击重置密码邮件链接验证身份",notes="点击重置密码邮件链接验证身份",response = Response.class)
	@RequestMapping(value = {"/rest/validateMail","rest/member/site/base/sel_validateMail"}, method = RequestMethod.GET)
	public ModelAndView validateMail(HttpServletRequest req) throws UnsupportedEncodingException {
		log.debug("validate mail start.....");
		String vm = req.getParameter("vm");//获取email
		ModelAndView modelAndView = new ModelAndView();  
		if(vm != null & !vm.equals(""))
		{
			Response response = new Response();
	        try
	        {
	        	response = rvService.processValidate(vm);
	        	String[] array = (String[]) response.getData();
	        	modelAndView.addObject("email", EncodeUtil.encodeBase64ToString(array[0].getBytes()));
	        	modelAndView.addObject("id",EncodeUtil.encodeBase64ToString(array[1].getBytes()));
	        	RedirectView rv = new RedirectView(rvService.getRedirectUrl(response,"validate"));
	 	        modelAndView.setView(rv);
	        }
	        catch(Exception e)
	        {
	        	log.error("email activate member error!",e);
	        }
		}
        return modelAndView;
	}


	/**
	 * 找回密码是否验证通过
	 * @param req
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value="找回密码是否验证通过",notes="找回密码是否验证通过",response = Response.class)
	@RequestMapping(value = {"/rest/isValidatePass","rest/member/site/base/sel_isValidatePass"}, method = RequestMethod.GET)
	public Response isValidatePass(HttpServletRequest req) throws IOException {
		log.debug("找回密码是否验证");
		Response response = new Response();
		String id = req.getParameter("account");
		int int_id = Integer.parseInt(EncodeUtil.decodeBase64ToString(id));
		int result = memberService.isValidatePass(int_id);
		if(result == 1)
		{
			Validateinfo vinfo = new Validateinfo();
	    	vinfo.setId(int_id);
		    vinfo.setValid(1);
		    memberService.updateValidateInfo(vinfo);
		}
		response.setData(result);

		return response;
	}
	
	/**
	 * 查看激活邮件
	 * @param req
	 * @throws IOException
	 */
	@ApiOperation(value="查看激活邮件",notes="查看激活邮件",response = Response.class)
	@RequestMapping(value = {"/rest/watchMail","rest/member/site/base/sel_watchMail"}, method = RequestMethod.GET)
	public Response watchMail(HttpServletRequest req) throws IOException {
		log.debug("找回密码是否验证");
		Response response = new Response();
		String account = req.getParameter("email");
		String result = ds.findMailAddress(EncodeUtil.decodeBase64ToString(account));
		response.setData(result);

		return response;
	}
	
}
