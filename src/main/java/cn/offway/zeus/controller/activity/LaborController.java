package cn.offway.zeus.controller.activity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhLabor;
import cn.offway.zeus.domain.PhLaborLucky;
import cn.offway.zeus.domain.PhLaborPrize;
import cn.offway.zeus.repository.PhLaborLuckyRepository;
import cn.offway.zeus.service.PhLaborLuckyService;
import cn.offway.zeus.service.PhLaborService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 劳动节活动
 * @author wn
 *
 */
@Api(tags={"劳动节活动"})
@RestController
@RequestMapping("/activity/labor")
public class LaborController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhLaborService phLaborService;
	
	@Autowired
	private PhLaborLuckyService phLaborLuckyService;
	
	@ApiOperation("签到搬砖")
	@PostMapping("/sign")
	public JsonResult sign(@ApiParam("用户ID") @RequestParam Long userId){
		boolean result = phLaborService.sign(userId);
		if(result){
			return jsonResultHelper.buildSuccessJsonResult(null);
		}
		return jsonResultHelper.buildFailJsonResult(CommonResultCode.SIGNED);

	}
	
	@ApiOperation("查询参与详情")
	@GetMapping("/info")
	public JsonResult info(@ApiParam("用户ID") @RequestParam Long userId){
		PhLabor phLabor = phLaborService.findByUserId(userId);
		return jsonResultHelper.buildSuccessJsonResult(phLabor);
	}
	
	@ApiOperation("抽奖")
	@PostMapping("/lottery")
	public JsonResult lottery(@ApiParam("用户ID") @RequestParam Long userId){
		try {
			return phLaborService.lottery(userId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("抽奖异常,userId：{}",userId,e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation("我的奖品")
	@GetMapping("/luckly/user")
	public JsonResult lucklyuser(@ApiParam("用户ID") @RequestParam Long userId){
		return jsonResultHelper.buildSuccessJsonResult(phLaborLuckyService.findByUserIdOrderByCreateTimeDesc(userId));
	}
	
	@ApiOperation("获奖记录")
	@GetMapping("/luckly/list")
	public JsonResult lucklylist(){
		
		List<PhLaborLucky> laborLuckies = phLaborLuckyService.findByOrderByCreateTimeDesc();
		
		
		String[] prices = {"营养液","发蜡","磨砂杯","哑光火岩发泥","口腔漱口水","倍林斯"
				,"Air Jordan 1  x Travis Scott","SupremeT恤",
				"Air Jordan 1  x Off-White北卡蓝","YEEZY 350V2亚洲限定",
				"OFFWAY项链","Nike Air Max 97“万花筒”"};
		String[] names = {"来不及道别","你再难遇我","把思念吞下","尝尽温柔","乜许、伱卟懂","心悸","把爱放生吧","媚骨","等我把愚勇熬成温柔","满心欢喜","倾慕","寄没有地址的信","心誶","凉了时光薄了情","安若","欠莪一个拥抱","称霸幼儿园","稚念","此用户已被腾讯注销","顾妄","甜味少女心","孑然","卖萌惯犯","該落的眼淚始終會落下","时光过客","宿命","苁心茾始","学不会伪装","漠念","久浪不归","傲娇萝莉","言初","醉后方知爱意浓","浅念","咖啡不解酒的醉","偏执的温柔","吹梦到西洲","陌九","一路小跑跑进你心里","心如柠檬自然酸","天使之魅","顾清辞","月亮快递员","哥的世界、闲人免进","久不愈","森屿暮歌","浮生未歇","OFFWAY_640186","OFFWAY_612200","OFFWAY_765886","OFFWAY_255847","OFFWAY_772764","OFFWAY_325513","OFFWAY_113136","OFFWAY_516238","OFFWAY_929578","OFFWAY_254744","OFFWAY_270070","OFFWAY_580174","OFFWAY_734586","OFFWAY_240954","OFFWAY_487092","OFFWAY_868363","OFFWAY_638904","OFFWAY_141138","OFFWAY_215713","OFFWAY_629802","OFFWAY_139256","OFFWAY_571726","OFFWAY_813862","OFFWAY_578299"};
		String[] heads = {"http://qiniu.offway.cn/image/head/一口啾啾咪+.png","http://qiniu.offway.cn/image/head/与爱错过.jpg","http://qiniu.offway.cn/image/head/仙女东东与花是宝藏.png","http://qiniu.offway.cn/image/head/以大橘为重.png","http://qiniu.offway.cn/image/head/伊娃.jpg","http://qiniu.offway.cn/image/head/勿.jpg","http://qiniu.offway.cn/image/head/吃瓜散人.png","http://qiniu.offway.cn/image/head/啊AW.png","http://qiniu.offway.cn/image/head/奈囍.png","http://qiniu.offway.cn/image/head/奶油可乐饼.png","http://qiniu.offway.cn/image/head/如果我问心有愧呢.jpg","http://qiniu.offway.cn/image/head/如果有如果.png","http://qiniu.offway.cn/image/head/孙甜蟹.png","http://qiniu.offway.cn/image/head/小华仔.jpg","http://qiniu.offway.cn/image/head/小明.jpg","http://qiniu.offway.cn/image/head/小男佣.jpg","http://qiniu.offway.cn/image/head/小蛮的水仙花.png","http://qiniu.offway.cn/image/head/小顽童.jpg","http://qiniu.offway.cn/image/head/就叫角角吧.png","http://qiniu.offway.cn/image/head/张大丹.jpg","http://qiniu.offway.cn/image/head/微信图片_20190408184728.png","http://qiniu.offway.cn/image/head/思思.jpg","http://qiniu.offway.cn/image/head/我爱台妹.jpg","http://qiniu.offway.cn/image/head/搞错性别.jpg","http://qiniu.offway.cn/image/head/春眠是个好名字哦.png","http://qiniu.offway.cn/image/head/林西柚-.png","http://qiniu.offway.cn/image/head/柳泫雅.png","http://qiniu.offway.cn/image/head/欲晞sir.png","http://qiniu.offway.cn/image/head/江月.jpg","http://qiniu.offway.cn/image/head/深海里的彼得潘.png","http://qiniu.offway.cn/image/head/爱吃面包的Sandy.png","http://qiniu.offway.cn/image/head/王大锤.jpg","http://qiniu.offway.cn/image/head/糖拌大橙子.png","http://qiniu.offway.cn/image/head/红彤彤的少女心.png","http://qiniu.offway.cn/image/head/纽扣少女的居.png","http://qiniu.offway.cn/image/head/罗懵鲨鱼.png","http://qiniu.offway.cn/image/head/美少女壮士lulu.png","http://qiniu.offway.cn/image/head/胡成阳.jpg","http://qiniu.offway.cn/image/head/腹黑少女.jpg","http://qiniu.offway.cn/image/head/药.jpg","http://qiniu.offway.cn/image/head/萌作作作汉子.jpg","http://qiniu.offway.cn/image/head/蜜桃桃硬糖__.png","http://qiniu.offway.cn/image/head/螃蟹先生.jpg","http://qiniu.offway.cn/image/head/转身变超人sunny.png","http://qiniu.offway.cn/image/head/达四零.jpg","http://qiniu.offway.cn/image/head/酸妮耶.png","http://qiniu.offway.cn/image/head/里德尔树下.png","http://qiniu.offway.cn/image/head/钟乐一生.jpg","http://qiniu.offway.cn/image/head/飘飘飘飘.jpg","http://qiniu.offway.cn/image/head/黄‘宗’泽.jpg"};
		
		int randp = RandomUtils.nextInt(0, prices.length-1);
		int randn = RandomUtils.nextInt(0, names.length-1);
		String name = prices[randp];
		String nickName = names[randn];
		
		PhLaborLucky phLaborLucky = new PhLaborLucky();
		phLaborLucky.setCreateTime(new Date());
		phLaborLucky.setName(name);
		phLaborLucky.setNickname(nickName);
		if(nickName.indexOf("OFFWAY")<0){
			int randh = RandomUtils.nextInt(0, heads.length-1);
			phLaborLucky.setHeadimgurl(heads[randh]);
		}
		
		int index = RandomUtils.nextInt(0, laborLuckies.size()-1);
		laborLuckies.add(index, phLaborLucky);
		
		
		return jsonResultHelper.buildSuccessJsonResult(laborLuckies);
	}
	
	/*public static void main(String[] args) {
		
		
		String[] aa = {"OFFWAY_459735","OFFWAY_593013","OFFWAY_114549","OFFWAY_584795","OFFWAY_756299","OFFWAY_242415","OFFWAY_484542","OFFWAY_253658","OFFWAY_252176","OFFWAY_412957","OFFWAY_362203","OFFWAY_612898","OFFWAY_635787","OFFWAY_110196","OFFWAY_394848","OFFWAY_762134","OFFWAY_896660","OFFWAY_181189","OFFWAY_368173","OFFWAY_237179","OFFWAY_718464","OFFWAY_543787","OFFWAY_111982","OFFWAY_248149","OFFWAY_625496","OFFWAY_765703","OFFWAY_257765","OFFWAY_166386","OFFWAY_360297","OFFWAY_291597","OFFWAY_204677","OFFWAY_682179","OFFWAY_679068","OFFWAY_588556","OFFWAY_457463","OFFWAY_927019","OFFWAY_748786","OFFWAY_471147","OFFWAY_206138","OFFWAY_766200","OFFWAY_252874","OFFWAY_177753","OFFWAY_552254","OFFWAY_897884","OFFWAY_685058","OFFWAY_291020","OFFWAY_701927","OFFWAY_889179","OFFWAY_843564","OFFWAY_578352","从此江山别","无爱一身轻","川水往事","〒漂bo灵魂","酒者煙","风格不统一","杀生予夺","心字情书","一身干净味","尘世凡间","莫忘初心","再酷也只撩你","放逐","谁的青春不折腾","不必、谁懂我","不再回忆谁%","温柔的酒","坚定人生梦","遇见更好的自己","犯贱的青春","一切随意","涉世深交","时光是个坏老人","携剑笑红尘","逆流者","清风,饮酒","寂寞、很高调","我永远记得","偏执","痞子@","排斥","骗吃骗喝骗老婆","弃疗之后精神倍棒","早恋肆无忌惮者","归戾","洒脱","故我","离人毁心","懵柠未迟","伊人月下","死不了就还好","野性入骨","学霸的芯","从此、陌路","空城空等","捻字、成伤","忘了去懂你","莪的付出、卻得卟到呐愛","记忆爱过后的破损","半真半假"};
		
		for (String string : aa) {
			int random = RandomUtils.nextInt(1, 100);
			String name = "";
			if(random <= 3){
				//OFFWAY惊喜礼包
				String[] prices = {"营养液","发蜡","磨砂杯","哑光火岩发泥","口腔漱口水","倍林斯"};
				random = RandomUtils.nextInt(0, prices.length-1);
				name = prices[random];
				
			}else if(random >=4 && random <= 68){
				//5-200元现金礼包
				String[] prices = {"满100-5","满300-15","满500-30","满1000-60","满1500-100","满2000-140"};
				random = RandomUtils.nextInt(0, prices.length-1);
				name = prices[random];
			}else{
				//5元无门槛优惠券
				name = "5元无门槛优惠券";
			}
			System.out.println("insert into ph_labor_lucky(id,name,create_time,nickname) values(null,'"+name+"',now(),'"+string+"');");
		}
	}*/

}
