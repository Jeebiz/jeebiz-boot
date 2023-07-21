package io.hiwepy.boot.api.dao.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class BaseBean implements Cloneable, Serializable {

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @SuppressWarnings("unchecked")
	public <T extends BaseBean> T shallowClone() {
        try {
            return (T)clone();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
