package net.jeebiz.boot.extras.redis.setup;



/**
 *
 *https://www.233tw.com/lua/7033
 */
public class RedisLua {

	public static final String LOCK_LUA_SCRIPT = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then return redis.call('pexpire', KEYS[1], ARGV[2]) else return -1 end";
    
	public static final String UNLOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return -1 end";
 
    /**
     * 库存增加
     * @return
     *      -4:代表库存传进来的值是负数（非法值）
     *      -3:库存未初始化
     *      大于等于0:剩余库存（新增之后剩余的库存）
     */
    public static final String INCR_SCRIPT = 
		"if (redis.call('EXISTS', KEYS[1]) == 1) then"
	   + "    local num = tonumber(ARGV[1]);"
	   + "    if (num < 0) then "
	   + "    	  return -4;" 
	   + "    end;"
	   + "    return redis.call('INCRBY', KEYS[1], num);"
	   + "end;"
	   + "return -3;";

    public static final String INCR_BYFLOAT_SCRIPT = 
		"if (redis.call('EXISTS', KEYS[1]) == 1) then"
	   + "    local num = tonumber(ARGV[1]);" 
	   + "    if (num < 0) then "
	   + "    	  return -4;" 
	   + "    end;"
	   + "    return redis.call('INCRBYFLOAT', KEYS[1], num);"
	   + "end;"
	   + "return -3;";
    
    /**
     * 库存扣减
     * @return
     *      -4:代表库存传进来的值是负数（非法值）
     *      -3:库存未初始化
     *      -2:库存不足
     *      -1:库存为0
     *      大于等于0:剩余库存（扣减之后剩余的库存）
     *      
     */
    public static final String DECR_SCRIPT =  
    	  "if (redis.call('EXISTS', KEYS[1]) == 1) then"
	    + "    local stock = tonumber(redis.call('GET', KEYS[1]));"
	    + "    local num = tonumber(ARGV[1]);"
	    + "    if (num <= 0) then"
	    + "        return -4;"
	    + "    end;"
	    + "    if (stock <= 0) then"
	    + "        return -1;"
	    + "    end;"
	    + "    if (stock >= num) then"
	    + "        return redis.call('INCRBY', KEYS[1], 0 - num);"
	    + "    end;"
	    + "    return -2;"
	    + "end;"
	    + "return -3;";
    

    public static final String DECR_BYFLOAT_SCRIPT =  
    	  "if (redis.call('EXISTS', KEYS[1]) == 1) then"
	    + "    local stock = tonumber(redis.call('GET', KEYS[1]));"
	    + "    local num = tonumber(ARGV[1]);"
	    + "    if (num <= 0) then"
	    + "        return -4;"
	    + "    end;"
	    + "    if (stock <= 0) then"
	    + "        return -1;"
	    + "    end;"
	    + "    if (stock >= num) then"
	    + "        return redis.call('INCRBYFLOAT', KEYS[1], 0 - num);"
	    + "    end;"
	    + "    return -2;"
	    + "end;"
	    + "return -3;";

    public static final String HINCR_SCRIPT = 
		  "if (redis.call('HEXISTS', KEYS[1], KEYS[2]) == 1) then"
	    + "    local num = tonumber(ARGV[1]);"
	    + "    if (num < 0) then "
	    + "    	  return -4;" 
	    + "    end;"
	    + "    return redis.call('HINCRBY', KEYS[1], KEYS[2], num);"
	    + "end;"
	    + "return -3;";
	
    public static final String HDECR_SCRIPT =  
		  "if (redis.call('HEXISTS', KEYS[1], KEYS[2]) == 1) then"
	    + "    local stock = tonumber(redis.call('HGET', KEYS[1], KEYS[2]));"
	    + "    local num = tonumber(ARGV[1]);"
	    + "    if (num <= 0) then"
	    + "        return -4;"
	    + "    end;"
	    + "    if (stock <= 0) then"
	    + "        return -1;"
	    + "    end;"
	    + "    if (stock >= num) then"
	    + "        return redis.call('HINCRBY', KEYS[1], KEYS[2], 0 - num);"
	    + "    end;"
	    + "    return -2;"
	    + "end;"
	    + "return -3;";   
    
    public static final String HINCR_BYFLOAT_SCRIPT = 
  		  "if (redis.call('HEXISTS', KEYS[1], KEYS[2]) == 1) then"
  	    + "    local num = tonumber(ARGV[1]);"
  	    + "    if (num < 0) then "
  	    + "    	  return -4;" 
  	    + "    end;"
  	    + "    return redis.call('HINCRBYFLOAT', KEYS[1], KEYS[2], num);"
  	    + "end;"
  	    + "return -3;";
  	
      public static final String HDECR_BYFLOAT_SCRIPT =  
  		  "if (redis.call('HEXISTS', KEYS[1], KEYS[2]) == 1) then"
  	    + "    local stock = tonumber(redis.call('HGET', KEYS[1], KEYS[2]));"
  	    + "    local num = tonumber(ARGV[1]);"
  	    + "    if (num <= 0) then"
  	    + "        return -4;"
  	    + "    end;"
  	    + "    if (stock <= 0) then"
  	    + "        return -1;"
  	    + "    end;"
  	    + "    if (stock >= num) then"
  	    + "        return redis.call('HINCRBYFLOAT', KEYS[1], KEYS[2], 0 - num);"
  	    + "    end;"
  	    + "    return -2;"
  	    + "end;"
  	    + "return -3;"; 
    
}
