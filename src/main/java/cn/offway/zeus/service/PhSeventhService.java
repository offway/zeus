package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhSeventh;
import cn.offway.zeus.domain.PhSeventhRecord;
import cn.offway.zeus.repository.PhSeventhRecordRepository;
import cn.offway.zeus.repository.PhSeventhRepository;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import com.qiniu.util.Json;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PhSeventhService {

    @Autowired
    private PhSeventhRepository phSeventhRepository;

    @Autowired
    private PhSeventhRecordRepository phSeventhRecordRepository;

    @Autowired
    private PhVoucherInfoService phVoucherInfoService;

    @Autowired
    private JPushService jPushService;

    @Autowired
    private JsonResultHelper jsonResultHelper;



    public void share(Long userId){

        String now = DateFormatUtils.format(new Date(),"yyyy-MM-dd");

        //查询当天是否分享获得券
        int count = phSeventhRecordRepository.countByShareUserIdAndCreateDateAndType(userId,now,"0");
        if(count == 0){
            PhSeventhRecord phSeventhRecord = new PhSeventhRecord();
            phSeventhRecord.setCreateDate(now);
            phSeventhRecord.setShareUserId(userId);
            phSeventhRecord.setType("0");
            phSeventhRecord.setVersion(0L);
            phSeventhRecordRepository.save(phSeventhRecord);

            phVoucherInfoService.giveVoucherByConfig(userId, "VP_QIXI_5.20");
        }
    }

    public JsonResult receive(Long shareUserId, Long receiveUserId, String pushUrl){

        if(shareUserId.longValue() == receiveUserId.longValue()){
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
        }

        String now = DateFormatUtils.format(new Date(),"yyyy-MM-dd");
        int count = phSeventhRecordRepository.countByShareUserIdAndCreateDateAndType(shareUserId,now,"1");
        if(count == 0){
            PhSeventhRecord phSeventhRecord = new PhSeventhRecord();
            phSeventhRecord.setCreateDate(now);
            phSeventhRecord.setShareUserId(shareUserId);
            phSeventhRecord.setReceiveUserId(receiveUserId);
            phSeventhRecord.setType("1");
            phSeventhRecord.setVersion(0L);
            phSeventhRecordRepository.save(phSeventhRecord);

            phVoucherInfoService.giveVoucherByConfig(receiveUserId, "VP_QIXI_5.20");

            PhSeventh phSeventh = phSeventhRepository.findByUserId(shareUserId);
            if(null == phSeventh){
                phSeventh = new PhSeventh();
                phSeventh.setCreateTime(new Date());
                phSeventh.setLotteryNum(0L);
                phSeventh.setUserId(shareUserId);
                phSeventh.setVersion(0L);
            }
            phSeventh.setLotteryNum(phSeventh.getLotteryNum().longValue()+1L);
            phSeventhRepository.save(phSeventh);

            Map<String, String> args = new HashMap<>();
            args.put("type", "0");
            args.put("id", "");
            args.put("url", pushUrl);
            jPushService.sendPushUser("通知", "TA已收到你的七夕祝福。恭喜！解锁一次抽大奖机会，快去领取", args, ""+shareUserId);

            return jsonResultHelper.buildSuccessJsonResult(null);
        }else{
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.VOUCHER_GIVED);
        }

    }

    public String lottery(Long userId){
        String index = "";
        int count = phSeventhRepository.subLotteryNum(userId);
        if(count > 0){
            int random = RandomUtils.nextInt(1, 100);
            if(random <=40){
                index = "10";
                phVoucherInfoService.giveVoucherByConfig(userId, "VP_QIXI_10");
            }else if(random >=41 && random<=75){
                index = "30";
                phVoucherInfoService.giveVoucherByConfig(userId, "VP_QIXI_30");
            }else if(random >=76 && random<=95){
                index = "50";
                phVoucherInfoService.giveVoucherByConfig(userId, "VP_QIXI_50");
            }else{
                index = "100";
                phVoucherInfoService.giveVoucherByConfig(userId, "VP_QIXI_100");
            }
        }
        return index;
    }

}
