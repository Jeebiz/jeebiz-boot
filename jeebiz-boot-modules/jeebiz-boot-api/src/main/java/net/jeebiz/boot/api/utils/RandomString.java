package net.jeebiz.boot.api.utils;


import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A provider of randomized {@link java.lang.String} values.
 */
public class RandomString {

    /**
     * The default length of a randomized {@link java.lang.String}.
     */
    public static final int DEFAULT_LENGTH = 8;

    /**
     * The symbols which are used to create a random {@link java.lang.String}.
     */
    private static final char[] SYMBOL;

    /**
     * The amount of bits to extract out of an integer for each key generated.
     */
    private static final int KEY_BITS;

    /*
     * Creates the symbol array.
     */
    static {
        StringBuilder symbol = new StringBuilder();
        for (char character = '0'; character <= '9'; character++) {
            symbol.append(character);
        }
        for (char character = 'a'; character <= 'z'; character++) {
            symbol.append(character);
        }
        for (char character = 'A'; character <= 'Z'; character++) {
            symbol.append(character);
        }
        SYMBOL = symbol.toString().toCharArray();
        int bits = Integer.SIZE - Integer.numberOfLeadingZeros(SYMBOL.length);
        KEY_BITS = bits - (Integer.bitCount(SYMBOL.length) == bits ? 0 : 1);
    }

    /**
     * A provider of random values.
     */
    private final Random random;

    /**
     * The length of the random strings that are created by this instance.
     */
    private final int length;

    /**
     * Creates a random {@link java.lang.String} provider where each {@link java.lang.String} is of
     * {@link net.bytebuddy.utility.RandomString#DEFAULT_LENGTH} length.
     */
    public RandomString() {
        this(DEFAULT_LENGTH);
    }

    /**
     * Creates a random {@link java.lang.String} provider where each value is of the given length.
     *
     * @param length The length of the random {@link String}.
     */
    public RandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("A random string's length cannot be zero or negative");
        }
        this.length = length;
        random = new Random();
    }

    /**
     * Creates a random {@link java.lang.String} of {@link net.bytebuddy.utility.RandomString#DEFAULT_LENGTH} length.
     *
     * @return A random {@link java.lang.String}.
     */
    public static String make() {
        return make(DEFAULT_LENGTH);
    }

    /**
     * Creates a random {@link java.lang.String} of the given {@code length}.
     *
     * @param length The length of the random {@link String}.
     * @return A random {@link java.lang.String}.
     */
    public static String make(int length) {
        return new RandomString(length).nextString();
    }

    /**
     * Represents an integer value as a string hash. This string is not technically random but generates a fixed character
     * sequence based on the hash provided.
     *
     * @param value The value to represent as a string.
     * @return A string representing the supplied value as a string.
     */
    public static String hashOf(int value) {
        char[] buffer = new char[(Integer.SIZE / KEY_BITS) + ((Integer.SIZE % KEY_BITS) == 0 ? 0 : 1)];
        for (int index = 0; index < buffer.length; index++) {
            buffer[index] = SYMBOL[(value >>> index * KEY_BITS) & (-1 >>> (Integer.SIZE - KEY_BITS))];
        }
        return new String(buffer);
    }

    /**
     * Creates a new random {@link java.lang.String}.
     *
     * @return A random {@link java.lang.String} of the given length for this instance.
     */
    public String nextString() {
        char[] buffer = new char[length];
        for (int index = 0; index < length; index++) {
            buffer[index] = SYMBOL[random.nextInt(SYMBOL.length)];
        }
        return new String(buffer);
    }
    
    /**
	 * 得到八位数随机唯一Id
	 *
	 * @return
	 */
	public String getRandomNum8() {
		String all = "00000000";
		while (true) {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			Pattern pattern = Pattern.compile("[^0-9]");
			Matcher matcher = pattern.matcher(uuid);
			/*
			 * Calendar c = Calendar.getInstance(); int seconds = c.get(Calendar.SECOND);
			 * all = matcher.replaceAll("").substring(0,6)+seconds; Pattern compile =
			 * Pattern.compile("^(\\d)\\1{7}$"); if(!compile.matcher(all).matches()){ break;
			 * }
			 */
			all = matcher.replaceAll("").substring(0, 8);
			if (!isMather(all)) {
				break;
			}
		}
		return all;
	}

	// 正则过滤
	private boolean isMather(String oldStr) {
		String w1 = "^(\\d)\\1{7}$";
		// 不能以 520结尾
		String w2 = "^[0-9]*(520)$";
		// 4-8 位置重复
		String w3 = "^\\d*(\\d)\\1{2,}\\d*$";
		// AAABBB
		String w4 = "^\\d*(\\d)\\1\\1(\\d)\\2\\2\\d*$";
		// AABB
		String w5 = "^\\d*(\\d)\\1(\\d)\\2\\d*$";
		// 匹配4-9位连续的数字
		String w6 = "(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){3,}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){3,})\\d";
		Pattern compile1 = Pattern.compile(w1);
		if (compile1.matcher(oldStr).matches()) {
			return true;
		}
		Pattern compile2 = Pattern.compile(w2);
		if (compile2.matcher(oldStr).matches()) {
			return true;
		}
		Pattern compile3 = Pattern.compile(w3);
		if (compile3.matcher(oldStr).matches()) {
			return true;
		}
		Pattern compile4 = Pattern.compile(w4);
		if (compile4.matcher(oldStr).matches()) {
			return true;
		}
		Pattern compile5 = Pattern.compile(w5);
		if (compile5.matcher(oldStr).matches()) {
			return true;
		}
		Pattern compile6 = Pattern.compile(w6);
		if (compile6.matcher(oldStr).matches()) {
			return true;
		}
		if (oldStr.startsWith("520")) {
			return true;
		}
		return false;
	}
}
