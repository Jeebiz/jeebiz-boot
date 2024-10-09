package io.hiwepy.boot.autoconfigure.datascope;

import java.util.Objects;

/**
 * 数据权限提供者
 * @author wandl
 */
public interface DataScopeProvider {

    /**
     * 获取数据权限
     * @param dataType 数据类型
     * @param data 数据，被注解标注的数据
     * @return
     */
    default boolean hasPermissions(DataType dataType, Object data){
        return Objects.nonNull(data);
    }

}
