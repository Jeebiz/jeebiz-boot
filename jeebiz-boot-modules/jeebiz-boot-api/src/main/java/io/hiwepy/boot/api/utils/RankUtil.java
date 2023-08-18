package io.hiwepy.boot.api.utils;

import io.hiwepy.boot.api.enums.RankSortTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 排名工具类
 */
public class RankUtil {

    private RankUtil() {
    }

    /**
     * 根据指定字段进行排名，返回排名后的数据列表
     *
     * @param sortedList   已排序数据列表
     * @param type         分数相同时排名策略
     * @param keyExtractor 键提取器, 计算排名的字段
     * @return 排名后的列表
     */
    public static <T, U> List<RankModel<T>> rank(RankSortTypeEnum type, List<T> sortedList, Function<? super T, ? extends U>
            keyExtractor) {
        return rank(type, null, sortedList, keyExtractor);
    }

    /**
     * 根据指定字段进行排名，返回排名前top条数据
     *
     * @param sortedList   已排序数据列表
     * @param type         分数相同时排名策略
     * @param top          取前top名数据
     * @param keyExtractor 键提取器, 计算排名的字段
     * @return 排名后的列表
     */
    public static <T, U> List<RankModel<T>> rank(RankSortTypeEnum type, Integer top, List<T> sortedList, Function<? super T, ? extends U> keyExtractor) {
        switch (type) {
            case SAME_CONTINUOUS:
                return doRankSameContinuous(top, sortedList, keyExtractor);
            case SAME_NON_CONTINUOUS:
                return doRankSameNonContinuous(top, sortedList, keyExtractor);
            default:
                return doRankNatural(top, sortedList);
        }
    }

    private static <T, U> List<RankModel<T>> doRankSameNonContinuous(Integer top, List<T> sortedList, Function<? super T, ? extends U> keyExtractor) {
        List<RankModel<T>> result = new ArrayList<>();
        int rankNumber = 0;
        int rowNumber = 1;
        U currentKey = null;
        for (T item : sortedList) {
            U loopKey = keyExtractor.apply(item);
            rankNumber = loopKey.equals(currentKey) ? rankNumber : rowNumber;
            if (null != top && top > 0 && rankNumber > top) {
                break;
            }
            result.add(new RankModel<>(rankNumber, item));
            currentKey = loopKey;
            rowNumber = rowNumber + 1;
        }
        return result;
    }

    private static <T, U> List<RankModel<T>> doRankSameContinuous(Integer top, List<T> sortedList, Function<? super T, ? extends U> keyExtractor) {
        List<RankModel<T>> result = new ArrayList<>();
        int rankNumber = 0;
        U currentKey = null;
        for (T item : sortedList) {
            U loopKey = keyExtractor.apply(item);
            rankNumber = loopKey.equals(currentKey) ? rankNumber : rankNumber + 1;
            if (null != top && top > 0 && rankNumber > top) {
                break;
            }
            result.add(new RankModel<>(rankNumber, item));
            currentKey = loopKey;
        }
        return result;
    }

    private static <T> List<RankModel<T>> doRankNatural(Integer top, List<T> sortedList) {
        List<RankModel<T>> result = new ArrayList<>();
        int rankNumber = 1;
        for (T item : sortedList) {
            if (null != top && top > 0 && rankNumber > top) {
                break;
            }
            result.add(new RankModel<>(rankNumber, item));
            rankNumber = rankNumber + 1;
        }
        return result;
    }

    /**
     * 排名实体模型
     *
     * @author HZC
     * @date 2019年07月29日
     */
    public static class RankModel<T> {
        /**
         * 排名
         */
        private Integer rank;

        /**
         * 数据对象
         */
        private T data;

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public RankModel() {
        }

        public RankModel(Integer rank, T data) {
            this.rank = rank;
            this.data = data;
        }
    }

}
