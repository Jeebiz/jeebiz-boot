package io.hiwepy.boot.autoconfigure.datascope;

/**
 * 数据类型
 */
public enum DataType {

    USER( "用户"),
    ACCOUNT( "账号"),
    SCHOOL( "学校"),
    GRADE( "年级"),
    CLASS( "班级"),
    TEACHER( "教师"),
    STUDENT( "学生"),
    COURSE( "课程"),
    ;

    private final String desc;

    DataType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return name() + ":" + desc;
    }

}
