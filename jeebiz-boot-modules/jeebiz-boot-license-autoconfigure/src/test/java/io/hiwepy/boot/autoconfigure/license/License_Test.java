package io.hiwepy.boot.autoconfigure.license;

import java.util.Calendar;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.hiwepy.boot.autoconfigure.license.creator.LicenseCreator;
import io.hiwepy.boot.autoconfigure.license.creator.LicenseCreatorParam;

public class License_Test {

	private LicenseVerify licenseVerify;

    @Autowired
    public void setLicenseVerify(LicenseVerify licenseVerify) {
        this.licenseVerify = licenseVerify;
    }

    @Test
    public void licenseVerify() {
       System.out.println("licese是否有效：" + licenseVerify.verify());
    }

    @Test
    public void licenseCreate() {
        // 生成license需要的一些参数
        LicenseCreatorParam param = new LicenseCreatorParam();
        param.setSubject("ioserver");
        param.setPrivateAlias("privateKey");
        param.setKeyPass("a123456");
        param.setStorePass("a123456");
        param.setLicensePath("D:\\licenseTest\\license.lic");
        param.setPrivateKeysStorePath("D:\\licenseTest\\privateKeys.keystore");
        Calendar issueCalendar = Calendar.getInstance();
        param.setIssuedTime(issueCalendar.getTime());
        Calendar expiryCalendar = Calendar.getInstance();
        expiryCalendar.set(2020, Calendar.DECEMBER, 31, 23, 59, 59);
        param.setExpiryTime(expiryCalendar.getTime());
        param.setConsumerType("user");
        param.setConsumerAmount(1);
        param.setDescription("测试");
        LicenseCreator licenseCreator = new LicenseCreator(param);
        // 生成license
        licenseCreator.generateLicense();
    }


}
