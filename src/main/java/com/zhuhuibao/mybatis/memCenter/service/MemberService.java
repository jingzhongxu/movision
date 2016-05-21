package com.zhuhuibao.mybatis.memCenter.service;

import com.zhuhuibao.common.*;
import com.zhuhuibao.mybatis.memCenter.entity.*;
import com.zhuhuibao.mybatis.memCenter.mapper.*;
import com.zhuhuibao.utils.pagination.model.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员中心业务处理
 * @author cxx
 *
 */
@Service
@Transactional
public class MemberService {
	private static final Logger log = LoggerFactory.getLogger(MemberService.class);

	@Autowired
	private MemberMapper memberMapper;

	@Autowired
	private CertificateMapper certificateMapper;

	@Autowired
	private WorkTypeMapper workTypeMapper;

	@Autowired
	private EnterpriseTypeMapper enterpriseTypeMapper;

	@Autowired
	private IdentityMapper identityMapper;

	@Autowired
	private ProvinceMapper provinceMapper;

	@Autowired
	private CityMapper cityMapper;

	@Autowired
	private AreaMapper areaMapper;

	@Autowired
	private CertificateRecordMapper certificateRecordMapper;

	@Autowired
	private EmployeeSizeMapper employeeSizeMapper;

	@Autowired
	private MessageMapper messageMapper;
	/**
	 * 会员信息保存
	 */
	public int updateMemInfo(Member member)
	{
		try{
			return memberMapper.updateMemInfo(member);
		}catch (Exception e){
			throw e;
		}
	}

	/**
	 * 更新会员状态
	 */
	public int updateStatus(Member member)throws Exception
	{
		try{
			return memberMapper.updateStatus(member);
		}catch (Exception e){
			throw e;
		}
	}

	/**
	 * 根据会员ID查询会员信息
	 */
	public Member findMemById(String id)throws Exception
	{
		try{
			return memberMapper.findMemById(id);
		}catch (Exception e){
			throw e;
		}
	}

	/**
	 * 新建员工
	 */
	public int addMember(Member member)throws Exception
	{
		try{
			return memberMapper.addMember(member);
		}catch (Exception e){
			throw e;
		}
	}

	/**
	 * 修改员工
	 */
	public int updateMember(Member member)throws Exception
	{
		try{
			return memberMapper.updateMember(member);
		}catch (Exception e){
			throw e;
		}
	}

	/**
	 * 禁用员工
	 */
	/*public int disableMember(Member member)
	{
		log.debug("修改员工");
		int result = 0;
		result = memberMapper.disableMember(member);
		return result;
	}*/

	/**
	 * 删除员工
	 */
	public int deleteMember(String id)throws Exception
	{
		try{
			return memberMapper.deleteMember(id);
		}catch (Exception e){
			throw e;
		}
	}

	/**
	 * 员工密码重置
	 */
	public int resetPwd(Member member)throws Exception
	{
		try{
			return memberMapper.resetPwd(member);
		}catch (Exception e){
			throw e;
		}
	}

	/**
	 * 会员头像修改
	 */
	public int uploadHeadShot(Member member)throws Exception
	{
		try{
			return memberMapper.uploadHeadShot(member);
		}catch (Exception e){
			throw e;
		}
	}

	/**
	 * 公司logo修改
	 */
	public int uploadLogo(Member member)throws Exception
	{
		try{
			return memberMapper.uploadLogo(member);
		}catch (Exception e){
			throw e;
		}
	}

	/**
	 * 查询代理商
	 */
	public List<AccountBean> findAgentMember(String account,String type)throws Exception
	{
		try{
			return memberMapper.findAgentMember(account,type);
		}catch (Exception e){
			throw e;
		}
	}

	/**
	 * 根据会员账号查询会员
	 */
	public Member findMember(Member member)throws Exception
	{
		try{
			return memberMapper.findMember(member);
		}catch (Exception e){
			throw e;
		}
	}

	/**
	 * 根据会员账号查询会员
	 */
	public Member findMemer(Member member)throws Exception
	{
		try{
			return memberMapper.findMember(member);
		}catch (Exception e){
			throw e;
		}
	}

	/**
	 * 资质类型
	 */
	public JsonResult findCertificateList(String type)
	{
		log.debug("资质类型");
		JsonResult result = new JsonResult();
		List<Certificate> certificate = certificateMapper.findCertificateList(type);
		List list = new ArrayList();
		for(int i=0;i<certificate.size();i++){
			Certificate certificate1 = certificate.get(i);
			String [] stringArr= certificate1.getDegree().split(",");
			Map map = new HashMap();
			map.put("id",certificate1.getId());
			map.put("name",certificate1.getName());
			map.put("degree",stringArr);
			list.add(map);
		}
		result.setCode(200);
		result.setData(list);
		return result;
	}

	/**
	 * 工作类别
	 */
	public List<WorkType> findWorkTypeList()
	{
		log.debug("工作类别");
		List<WorkType> workType = workTypeMapper.findWorkTypeList();
		return workType;
	}

	/**
	 * 根据id查询工作类别
	 */
	public WorkType findWorkTypeById(String id)
	{
		log.debug("根据id查询工作类别");
		WorkType workType = workTypeMapper.findWorkTypeById(id);
		return workType;
	}

	/**
	 * 人员规模
	 */
	public List<EmployeeSize> findEmployeeSizeList()
	{
		log.debug("人员规模");
		List<EmployeeSize> employeeSize = employeeSizeMapper.findEmployeeSizeList();
		return employeeSize;
	}

	/**
	 * 企业性质
	 */
	public List<EnterpriseType> findEnterpriseTypeList()
	{
		log.debug("企业性质");
		List<EnterpriseType> enterpriseType = enterpriseTypeMapper.findEnterpriseTypeList();
		return enterpriseType;
	}

	/**
	 * 企业身份
	 */
	public List<Identity> findIdentityList()
	{
		log.debug("企业身份");
		List<Identity> identity = identityMapper.findIdentityList();
		return identity;
	}

	/**
	 * 查询省
	 */
	public List<ResultBean> findProvince()
	{
		log.debug("查询省");
		List<ResultBean> province = provinceMapper.findProvince();
		return province;
	}

	/**
	 * 根据省Code查询市
	 */
	public List<ResultBean> findCity(String provincecode)
	{
		log.debug("根据省Code查询市");
		List<ResultBean> city = cityMapper.findCity(provincecode);
		return city;
	}

	/**
	 * 根据市Code查询县区
	 */
	public List<ResultBean> findArea(String cityCode)
	{
		log.debug("根据市Code查询县区");
		List<ResultBean> area = areaMapper.findArea(cityCode);
		return area;
	}

	/**
	 * 资质保存
	 */
	public int saveCertificate(CertificateRecord record)
	{
		log.debug("资质保存");
		int isSave = certificateRecordMapper.saveCertificate(record);
		return isSave;
	}

	/**
	 * 资质编辑
	 */
	public int updateCertificate(CertificateRecord record)
	{
		log.debug("资质编辑");
		int isUpdate = certificateRecordMapper.updateCertificate(record);
		return isUpdate;
	}

	/**
	 * 资质删除
	 */
	public int deleteCertificate(CertificateRecord record)
	{
		log.debug("资质删除");
		int isDelete = certificateRecordMapper.deleteCertificate(record);
		return isDelete;
	}

	/**
	 * 查询资质
	 */
	public List<CertificateRecord> certificateSearch(CertificateRecord record)
	{
		log.debug("查询资质");
		List<CertificateRecord> list = certificateRecordMapper.certificateSearch(record);
		return list;
	}

	/**
	 * 根据父类ID查询公司下属员工
	 */
	public JsonResult findStaffByParentId(Paging<Member> pager, Member member)
	{
		log.debug("根据父类ID查询公司下属员工");
		JsonResult result = new JsonResult();
		List<Member> memberList = memberMapper.findAllByPager(pager.getRowBounds(),member);
		List list = new ArrayList();
		for(int i=0;i<memberList.size();i++){
			Map map = new HashMap();
			Member member1 = memberList.get(i);
			map.put("id",member1.getId());
			if(member1.getMobile()!=null){
				map.put("account",member1.getMobile());
			}else{
				map.put("account",member1.getEmail());
			}

			String workTypeName = "";
			if(member1.getWorkType()!=null){
				WorkType workType = workTypeMapper.findWorkTypeById(member1.getWorkType().toString());
				workTypeName = workType.getName();
			}

			map.put("role",workTypeName);
			map.put("roleId",member1.getWorkType());
			map.put("name",member1.getPersonRealName());
			map.put("status",member1.getStatus());
			String statusName = "";
			if(member1.getStatus()==0){
				statusName = "未激活";
			}else if(member1.getStatus()==1){
				statusName = "正常";
			}else{
				statusName = "注销";
			}
			map.put("statusName",statusName);
			list.add(map);
		}
		pager.result(list);
		result.setCode(200);
		result.setData(pager);
		return result;
	}

	/**
	 *查询最新供应商 工程商
	 * @return
     */
	public JsonResult findNewEngineerOrSupplier(String type){
		JsonResult result = new JsonResult();
		List<Member> memberList = memberMapper.findNewEngineerOrSupplier(type);
		List list = new ArrayList();
		for(int i=0;i<memberList.size();i++){
			Member member = memberList.get(i);
			Map map = new HashMap();
			map.put(Constant.id,member.getId());
			map.put(Constant.companyName,member.getEnterpriseName());
			map.put(Constant.webSite,member.getEnterpriseWebSite());
			map.put(Constant.address,member.getAddress());
			map.put(Constant.saleProductDesc,member.getSaleProductDesc());
			map.put(Constant.logo,member.getEnterpriseLogo());
			map.put(Constant.status,member.getStatus());
			list.add(map);
		}
		result.setCode(200);
		result.setData(list);
		return result;
	}


	/**
	 *查询最新认证供应商 工程商
	 * @return
	 */
	public JsonResult findnewIdentifyEngineer(String type){
		JsonResult result = new JsonResult();
		List list = new ArrayList();
		if("2".equals(type)){
			List<Member> list1 = memberMapper.findnewIdentifyEngineer("4");
			for(int i=0;i<list1.size();i++){
				Member member = list1.get(i);
				Map map = new HashMap();
				map.put(Constant.id,member.getId());
				map.put(Constant.companyName,member.getEnterpriseName());
				list.add(map);
			}
		}else{
			List<Member> list2 = memberMapper.findnewIdentifyEngineer("2");
			List<Member> list3 = memberMapper.findnewIdentifyEngineer("1");
			List<Member> list4 = memberMapper.findnewIdentifyEngineer("3");
			list2.removeAll(list3);
			list2.addAll(list3);
			list2.removeAll(list4);
			list2.addAll(list4);
			for(int a=0;a<list2.size();a++){
				Member member = list2.get(a);
				Map map1 = new HashMap();
				map1.put(Constant.id,member.getId());
				map1.put(Constant.companyName,member.getEnterpriseName());
				list.add(map1);
			}
		}
		if(list.size()>=6){
			list = list.subList(0,6);
		}
		result.setCode(200);
		result.setData(list);
		return result;
	}
	/**
	 *供应商 工程商简版介绍
	 * @return
	 */
	public JsonResult introduce(String id,String type){
		JsonResult result = new JsonResult();
		Member member = memberMapper.findMemById(id);
		String enterpriseTypeName = "";
		String provinceName= "";
		String cityName="";
		String areaName="";
		String address = "";
		String employeeSizeName="";
		String identifyName = "";
		if(member.getEnterpriseType()!=null){
			EnterpriseType enterpriseType = enterpriseTypeMapper.selectByPrimaryKey(member.getEnterpriseType());
			enterpriseTypeName = enterpriseType.getName();
		}
		if(member.getProvince()!=null){
			Province province = provinceMapper.getProInfo(member.getProvince());
			provinceName = province.getName();
		}
		if(member.getCity()!=null){
			City city = cityMapper.getCityInfo(member.getCity());
			cityName = city.getName();
		}
		if(member.getArea()!=null){
			Area area = areaMapper.getAreaInfo(member.getArea());
			areaName = area.getName();
		}
		if(member.getEmployeeNumber()!=null){
			EmployeeSize employeeSize = employeeSizeMapper.selectByPrimaryKey(Integer.parseInt(member.getEmployeeNumber()));
			employeeSizeName = employeeSize.getName();
		}
		if(member.getIdentify()!=null){
			String identifys[] = member.getIdentify().split(",");
			for(int i=0;i<identifys.length;i++){
				String identify = identifys[i];
				Identity identityInfo = identityMapper.selectByPrimaryKey(Integer.parseInt(identify));
				identifyName = identityInfo.getName() + "  " + identifyName;
			}
		}
		address = provinceName + cityName + areaName;
		CertificateRecord certificateRecord = new CertificateRecord();
		certificateRecord.setMem_id(Integer.parseInt(id));
		certificateRecord.setType(type);
		List<CertificateRecord> certificateRecordList = certificateRecordMapper.certificateSearch1(certificateRecord);
		Map map = new HashMap();
		String createTime = "";
		if(member.getEnterpriseCreaterTime()!=null){
			createTime = member.getEnterpriseCreaterTime().substring(0,10);
		}
		map.put(Constant.companyName,member.getEnterpriseName());
		map.put(Constant.enterpriseTypeName,enterpriseTypeName);
		map.put(Constant.area,address);
		map.put(Constant.enterpriseCreaterTime,createTime);
		map.put(Constant.registerCapital,member.getRegisterCapital());
		map.put(Constant.employeeNumber,employeeSizeName);
		map.put(Constant.identifyName,identifyName);
		map.put(Constant.enterpriseDesc,member.getEnterpriseDesc());
		map.put(Constant.saleProductDesc,member.getSaleProductDesc());
		map.put(Constant.address,member.getAddress());
		map.put(Constant.webSite,member.getEnterpriseWebSite());
		map.put(Constant.telephone,member.getEnterpriseTelephone());
		map.put(Constant.fax,member.getEnterpriseFox());
		map.put(Constant.certificateRecord,certificateRecordList);
		result.setCode(200);
		result.setData(map);
		return result;
	}

	/**
	 *名企展示
	 * @return
	 */
	public JsonResult greatCompany(String type){
		JsonResult result = new JsonResult();
		List list = new ArrayList();
		if("2".equals(type)){
			List<ResultBean> list1 = memberMapper.findGreatCompany("4");
			for(int i=0;i<list1.size();i++){
				ResultBean resultBean = list1.get(i);
				Map map = new HashMap();
				map.put(Constant.id,resultBean.getCode());
				map.put(Constant.companyName,resultBean.getName());
				map.put(Constant.logo,resultBean.getSmallIcon());
				list.add(map);
			}
		}else{
			List<ResultBean> list2 = memberMapper.findGreatCompany("2");
			List<ResultBean> list3 = memberMapper.findGreatCompany("1");
			List<ResultBean> list4 = memberMapper.findGreatCompany("3");
			list2.removeAll(list3);
			list2.addAll(list3);
			list2.removeAll(list4);
			list2.addAll(list4);
			for(int a=0;a<list2.size();a++){
				ResultBean resultBean = list2.get(a);
				Map map1 = new HashMap();
				map1.put(Constant.id,resultBean.getCode());
				map1.put(Constant.companyName,resultBean.getName());
				map1.put(Constant.logo,resultBean.getSmallIcon());
				list.add(map1);
			}
		}
		if(list.size()>=12){
			list = list.subList(0,12);
		}
		result.setCode(200);
		result.setData(list);
		return result;
	}

	/**
	 *留言
	 * @return
	 */
	public JsonResult saveMessage(Message message){
		JsonResult result = new JsonResult();
		messageMapper.saveMessage(message);
		result.setCode(200);
		return result;
	}
}
