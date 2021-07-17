package net.jeebiz.boot.extras.redis.setup;

/**
 *
 *https://www.233tw.com/lua/7033
 */
public class RedisLua {

	public static final String LOCK_LUA_SCRIPT = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then return redis.call('pexpire', KEYS[1], ARGV[2]) else return -1 end";
    
	public static final String UNLOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return -1 end";

    public static final String INCR_SCRIPT =" if (redis.call('exists', KEYS[1]) == 1) then "
											+ "	 local current = redis.call('incr', KEYS[1], ARGV[1]) "
											+ "	 if #current < 0 then "
											+ "	  redis.call('decr', KEYS[1], ARGV[1]) "
											+ "	  return 0 \n"
											+ "	 else "
											+ "	  return #current "
											+ "	 end "
											+ " else "
											+ "	 redis.call('set', KEYS[1], 0) "
											+ "  return 0 "
											+ "	end";
	
    public static final String HINCR_SCRIPT = " if redis.call('hget', KEYS[1]) == 1 then  "
			+ "		local current = redis.call('incr', KEYS[1] "
			+ "		if #current < 0 then "
			+ "			redis.call('decr', KEYS[1]) "
			+ "			return 0 "
			+ "		else "
			+ "			return #current "
			+ "		end "
			+ " else return 0 end";
	
    public static final String LOCK_STOCK_LUA=  "local counter = redis.call('hget',KEYS[1],ARGV[1] \n" +
            "local result  = counter - ARGV[2];" +
            "if(result>=0 ) then \n" +
            "   redis.call('hset',KEYS[1],ARGV[1],result\n" +
            "   redis.call('hincrby',KEYS[1],ARGV[3],ARGV[2]\n" +
            "   return 1;\n" +
            "end;\n" +
            "return 0;\n";
    
    
    /**
     * @params 库存key
     * @return
     *      -4:代表库存传进来的值是负数（非法值）
     *      -3:库存未初始化
     *      -2:库存不足
     *      -1:库存为0
     *      大于等于0:剩余库存（扣减之后剩余的库存）
     *      
     */
    public static final String LOCK_STOCK_LUA2 =  
      "if (redis.call('exists', KEYS[1]) == 1) then"
    + "    local stock = tonumber(redis.call('get', KEYS[1]));"
    + "    local num = tonumber(ARGV[1]);"
    + "    if (num <= 0) then"
    + "        return -4;"
    + "    end;"
    + "    if (stock <= 0) then"
    + "        return -1;"
    + "    end;"
    + "    if (stock >= num) then"
    + "        return redis.call('incrBy', KEYS[1], 0 - num);"
    + "    end;"
    + "    return -2;"
    + "end;"
    + "return -3;";
    
    /**
     * @params 库存key
     * @return
     *      -4:代表库存传进来的值是负数（非法值）
     *      -3:库存未初始化
     *      大于等于0:剩余库存（新增之后剩余的库存）
     */
    public static final String LOCK_STOCK_LUA3 = 
         "if (redis.call('exists', KEYS[1]) == 1) then"
	   + "    local num = tonumber(ARGV[1]"
	   + "    if (num < 0) then"
	   + "        return -4;"
	   + "    end;"
	   + "        return redis.call('incrBy', KEYS[1], num"
	   + "end;"
	   + "return -3;";
    
}
