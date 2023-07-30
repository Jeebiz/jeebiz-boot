package io.hiwepy.boot.api.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class LotteryUtils {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LotteryElement {

        /**
         * 奖励数量
         */
        private Long amount;
        /**
         * 奖励出现概率
         */
        private Long weight;
        /**
         * 奖励每天出现的限额
         */
        private Integer quota;

    }

    public static <T extends LotteryElement> T lottery(List<T> elements, Function<T, Long> quotaChecker) {
        // 1、打乱元素集合
        Collections.shuffle(elements);
        // 2、定义总的概率区间
        float totalWeight = 0f;
        // 3、存储每个元素新的概率区间
        List<Float> weightSection = new ArrayList<Float>();
        weightSection.add(0F);
        // 4、遍历每个元素，设置概率区间，总的概率区间为每个概率区间的总和
        for (LotteryElement element : elements) {
            totalWeight += element.getWeight();
            weightSection.add(totalWeight);
        }
        // 5、获取总的概率区间中的随机数
        return lottery(totalWeight, weightSection, elements, quotaChecker);
    }

    protected static <T extends LotteryElement> T lottery(float totalWeight, List<Float> weightSection, List<T> elements, Function<T, Long> quotaChecker) {
        // 1、获取总的概率区间中的随机数
        ThreadLocalRandom random = ThreadLocalRandom.current();
        float randomWeight = (float) random.nextInt((int) Math.max(1, totalWeight));
        // 2、判断取到的随机数在哪个元素的概率区间中
        for (int i = 0; i < weightSection.size(); i++) {
            if (randomWeight >= weightSection.get(i) && randomWeight < weightSection.get(i + 1)) {
                T element = elements.get(i);
                // 2.1、检查抽中的元素是否有设置限额
                if (Objects.nonNull(element.getQuota())) {
                    // 2.2、检查限额是否用完（即库存是否还有）
                    Long leaveAmount = quotaChecker.apply(element);
                    // 2.3、库存不足则重新获取一次随机元素
                    if (leaveAmount < 0) {
                        return lottery(totalWeight, weightSection, elements, quotaChecker);
                    }
                }
                //2.4、未设置限额，则返回匹配的元素
                return element;
            }
        }
        // 7、逻辑上不会找不到，以下代码是备用逻辑取奖励数量最少的元素
        return elements.stream().filter(el -> Objects.isNull(el.getQuota())).min(Comparator.comparing(LotteryElement::getAmount)).get();
    }

}
