package io.hiwepy.boot.api.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 同分排序规则枚举类
 */
public enum RankSortTypeEnum {

    /**
     * 同分并列占名次: 1
     * 示例：90, 90, 90, 80, 80, 70, 60, 60, 50, 40
     * 则排名为：1, 1, 1, 4, 4, 6, 6, 6, 9, 10
     */
    SAME_NON_CONTINUOUS("同分并列占名次", 1),

    /**
     * 同分并列不占名次: 2
     * 示例：90, 90, 90, 80, 80, 70, 60, 60, 50, 40
     * 则排名为：1, 1, 1, 2, 2, 3, 4, 4, 5, 6
     */
    SAME_CONTINUOUS("同分并列不占名次", 2),

    /**
     * 同分非并列（可能存在同分两次排名不一样）: 3
     * 示例：90, 90, 90, 80, 80, 70, 60, 60, 50, 40
     * 则排名为：1, 2, 3, 4, 5, 6, 7, 8, 9, 10
     */
    NATURAL("同分非并列", 3);

    private static final RankSortTypeEnum DEFAULT_SORT_TYPE = RankSortTypeEnum.SAME_CONTINUOUS;

    private final String name;

    private final Integer value;

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    RankSortTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public boolean equals(RankSortTypeEnum sortType) {
        if (Objects.isNull(sortType)) {
            return Boolean.FALSE;
        }
        return this.compareTo(sortType) == 0;
    }

    @Override
    public String toString() {
        return name + " -> " + value;
    }

    public static Integer getValueByName(String name) {
        Map<String, Integer> map = Arrays.stream(RankSortTypeEnum.values()).collect(Collectors.toMap(RankSortTypeEnum::getName, RankSortTypeEnum::getValue));
        return map.get(name);
    }

    public static String getNameByValue(Integer value) {
        Map<Integer, String> map = Arrays.stream(RankSortTypeEnum.values()).collect(Collectors.toMap(RankSortTypeEnum::getValue, RankSortTypeEnum::getName));
        return map.get(value);
    }

    public static RankSortTypeEnum getEnumByValue(Integer value) {
        for (RankSortTypeEnum loop : RankSortTypeEnum.values()) {
            if (loop.getValue().equals(value)) {
                return loop;
            }
        }
        return DEFAULT_SORT_TYPE;
    }

}
