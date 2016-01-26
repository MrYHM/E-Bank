package com.bank.controller.index;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bank.model.admin.AdminModel;
import com.bank.model.index.IndexModel;
import com.bank.model.index.LabelModel;
import com.bank.model.other.Page;
import com.bank.service.index.IndexService;
import com.bank.utils.JsonUtil;
import com.bank.utils.TimeUtil;

@Controller
@RequestMapping("/index")
public class IndexController {

	@Autowired
	private IndexService is;
	
	@ResponseBody
	@RequestMapping("/Allnotice")
	public JSONObject getNotice(Page page,HttpServletRequest req) throws ParseException{
		if ("index_uptime_format".equals(page.getSort())) {
			page.setSort("index_uptime");
		}
		if (page.getTimefmt()==null || "".equals(page.getTimefmt())) {
			page.setTimefmt("yyyy-MM-dd HH:mm:ss");
		}
		List<IndexModel> list = is.findeByPage(page);
		IndexModel im = is.findTopByState(page.getIndex_pid());//获取置顶
		Integer pid = page.getIndex_pid();
		int count = 0;
		if (page.getSearch()==null||"".equals(page.getSearch())) {
			count = is.findCountByType(pid==null?-1:pid,page.getIsView());
		}else{
			count = list.size();
		}
		return JsonUtil.getNotice(list,im,count,page.getTimefmt());
	}

	/**
	 * 根据标签获得相关的文章
	 * @param label
	 * @param num
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping("/aboutnotice")
	public JSONObject getNoticeAboutByLabel(Page page) throws ParseException{
		List<IndexModel> list = is.findAboutByLabel(page);
		return JsonUtil.getNotice(list,null,is.findAboutByLabelCount(page),"MM/dd");
	}
	@ResponseBody
	@RequestMapping("/getAllLabel")
	public JSONArray getAllLabel() throws ParseException{
		List<LabelModel> list = is.findAllLabel();
		return JsonUtil.getAllLabel(list);
	}
	@ResponseBody
	@RequestMapping("/getHotLabel")
	public JSONArray getHotLabel(int num) throws ParseException{
		List<LabelModel> list = is.findeHotLabel(num);
		return JsonUtil.getAllLabel(list);
	}
	
	
	@ResponseBody
	@RequestMapping("/addnotice")
	public JSONObject addNewNotice(IndexModel im,HttpServletRequest req){
		HttpSession session = req.getSession();
		AdminModel am = (AdminModel) session.getAttribute("admin");
		JSONObject jo = new JSONObject();
		if (im==null||am==null) {
			jo.put("error", "403");
			jo.put("msg", "非法操作");
			return jo;
		}
		
		if (Base64.decodeBase64(im.getIndex_preview_image_url()).length>=307200) {
			jo.put("error", "303");
			jo.put("msg", "您上传的图片过大("+Base64.decodeBase64(im.getIndex_preview_image_url()).length/1024+"KB),请控制在300KB以内");
			return jo;
		}
		im.setIndex_uptime(new Date());
		im.setUpfrom(am.getAdmin_id());
		im.setIndex_state("00");//设置初始状态
		int isSuC = is.add(im);
		if (isSuC!=-1) {
			jo.put("error", "200");
			jo.put("msg", "上传成功");
		}else{
			jo.put("error", "203");
			jo.put("msg", "上传失败");
		}
		return jo;
	}
	
	@ResponseBody
	@RequestMapping("/updateNoticeState")
	public JSONObject updateIndexState(String row,String state){
		JSONObject jo = new JSONObject();//返回给页面的json
		IndexModel im = JSON.parseObject(row, IndexModel.class);//转换对象
		JSONObject jrow = JSON.parseObject(row);//传递回来的json
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			im.setIndex_uptime(sdf.parse(jrow.getString("index_uptime_format")));
		} catch (ParseException e) {
			e.printStackTrace();
			jo.put("error", "203");
			jo.put("msg", "修改失败");
		}
		im.setIndex_state(state);
		int isSuC = is.alterById(im);
		if (isSuC==1) {
			jo.put("error", "200");
			jo.put("msg", "修改成功");
		}else{
			jo.put("error", "203");
			jo.put("msg", "修改失败");
		}
		return jo;
	}
	
	@ResponseBody
	@RequestMapping("/add2View")
	public JSONObject add2View(int id){
		JSONObject jo = new JSONObject();//返回给页面的json
		int isSuC = is.add2View(id);
		if (isSuC==1) {
			jo.put("error", "200");
			jo.put("msg", "添加成功");
		}else{
			jo.put("error", "203");
			jo.put("msg", "添加失败");
		}
		return jo;
	}
	
	@ResponseBody
	@RequestMapping("/getTopByState")
	public JSONObject getCountByState(Integer type,String fmt) throws ParseException{
		JSONObject jo = new JSONObject();//返回给页面的json
		IndexModel im = is.findTopByState(type);
		if (im==null) {
			jo.put("error", "203");
			jo.put("msg", "没有置顶项");
		}else{
			jo.put("error", "200");
			jo.put("top", JsonUtil.getSingleNotice(im, fmt));
		}
		return jo;
	}

	/**
	 * @param id
	 * @param type 0总表 1预览表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/removeById")
	public JSONObject deleteById(int id,int type){
		JSONObject jo = new JSONObject();//返回给页面的json
		int isSuc = 0;
		if (type==0) {
			isSuc = is.RemoveById(id);
		}else{
			isSuc = is.removeByIdView(id);
		}
		if (isSuc==1) {
			jo.put("error", "200");
			jo.put("msg", "删除成功");
		}else{
			jo.put("error", "203");
			jo.put("msg", "删除失败");
		}
		return jo;
	}
	
	@ResponseBody
	@RequestMapping("/getArticleById")
	public JSONObject getArticleById(int id) throws ParseException{
		JSONObject jo = new JSONObject();//返回给页面的json
		IndexModel im = is.findById(id);
		if (im==null) {
			jo.put("error", "203");
			jo.put("msg", "查询失败");
			return jo;
		}
		jo = JsonUtil.getSingleNotice(im,"yyyy-MM-dd HH:mm");
		return jo;
	}

	@ResponseBody
	@RequestMapping("/getArticleByBorA")
	public JSONObject getArticleByBorA(int id,String ba) throws ParseException{
		JSONObject jo = new JSONObject();//返回给页面的json
		IndexModel im = is.findByBoA(ba, id);
		if (im==null) {
			jo.put("error", "203");
			jo.put("msg", "查询失败");
			return jo;
		}
		jo = JsonUtil.getSingleNotice(im,"yyyy-MM-dd HH:mm");
		return jo;
	}

	@RequestMapping("/articledetail")
	public ModelAndView gotoArticleDetails(int id) throws ParseException{
		ModelAndView mv = new ModelAndView();
		IndexModel im = is.findById(id);
		is.addHits(id);//增加点击量
		im.setIndex_uptime_format(TimeUtil.Date2String(im.getIndex_uptime(), "yyyy-MM-dd HH:mm"));
		mv.addObject("article",im);
		mv.setViewName("../page/article/articledetails");;
		return mv;
	}
	
	@RequestMapping("/myBank")
	public String gotoMyBank(){
		return "bus_index";
	}
	@RequestMapping("/index")
	public String gotoIndex(){
		return "index";
	}
	
	private Page getPage(String page,String rows){
		//当前页  
        int intPage = Integer.parseInt((page == null || page == "0") ? "1":page);  
        //每页显示条数  
        int number = Integer.parseInt((rows == null || rows == "0") ? "10":rows);  
        //每页的开始记录  第一页为1  第二页为number +1   
        int start = (intPage-1)*number; 
        Page p = new Page();
        p.setLimit(start);
        p.setOffset(number);
        return p;
	}
}
