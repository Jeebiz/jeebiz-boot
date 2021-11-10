package net.jeebiz.boot.extras.external.region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ProvinceEnum {

    P110000("110000", "北京市"),
    P120000("120000", "天津市"),
    P130000("130000", "河北省"),
    P140000("140000", "山西省"),
    P150000("150000", "内蒙古自治区"),
    P210000("210000", "辽宁省"),
    P220000("220000", "吉林省"),
    P230000("230000", "黑龙江省"),
    P310000("310000", "上海市"),
    P320000("320000", "江苏省"),
    P330000("330000", "浙江省"),
    P340000("340000", "安徽省"),
    P350000("350000", "福建省"),
    P360000("360000", "江西省"),
    P370000("370000", "山东省"),
    P410000("410000", "河南省"),
    P420000("420000", "湖北省"),
    P430000("430000", "湖南省"),
    P440000("440000", "广东省"),
    P450000("450000", "广西壮族自治区"),
    P460000("460000", "海南省"),
    P500000("500000", "重庆市"),
    P510000("510000", "四川省"),
    P520000("520000", "贵州省"),
    P530000("530000", "云南省"),
    P540000("540000", "西藏自治区"),
    P610000("610000", "陕西省"),
    P620000("620000", "甘肃省"),
    P630000("630000", "青海省"),
    P640000("640000", "宁夏回族自治区"),
    P650000("650000", "新疆维吾尔自治区"),
    P710000("710000", "台湾省"),
    P810000("810000", "香港特别行政区"),
    P820000("820000", "澳门特别行政区"),

	UK("999999", "未知行政区"),
	
	;
	
    // 行政区划代码
    private String code;

    // 单位名称
    private String cname;

	private static Logger log = LoggerFactory.getLogger(ProvinceEnum.class);

    private ProvinceEnum( String code, String cname) {
        this.code = code;
        this.cname = cname;
    }

    public String getCode() {
		return code;
	}
    
	public String getCname() {
		return cname;
	}

	public static ProvinceEnum getByCode(String code) {
		for (ProvinceEnum region : ProvinceEnum.values()) {
			if (region.getCode().equalsIgnoreCase(code)) {
				return region;
			}
		}
		log.error("Cannot found ProvinceEnum with code '" + code + "'.");
		return ProvinceEnum.UK;
	}
	
	public static ProvinceEnum getByCnName(String cn_name) {
		for (ProvinceEnum region : ProvinceEnum.values()) {
			if (region.getCname().equalsIgnoreCase(cn_name)) {
				return region;
			}
		}
		log.error("Cannot found ProvinceEnum with cn_name '" + cn_name + "'.");
		return ProvinceEnum.UK;
	}
	
}
