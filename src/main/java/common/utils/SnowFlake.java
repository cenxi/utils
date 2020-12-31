package common.utils;


/**
 * @ClassName: SnowFlake
 * @Description: 生产64位自增数字，适合分布式方式
 * @date 2017年9月25日 下午6:13:04
 */
public class SnowFlake {
    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 10; //序列号占用的位数
    private final static long MACHINE_BIT = 6;  //机器标识占用的位数
    //private final static long DATACENTER_BIT = 5;//数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    //private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT;

    //private long datacenterId;  //数据中心
//		private long machineId;    //机器标识
    private static long machineId = 1L;    //机器标识
    private static long sequence = 0L; //序列号
    private static long lastStmp = -1L;//上一次时间戳

    public SnowFlake(long machineId) {
//	        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
//	            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
//	        }
//			machineId = 1;
        this.machineId = machineId;
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        //this.datacenterId = datacenterId;
//	        this.machineId = machineId;
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public static synchronized Long nextId() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;

        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
                | machineId << MACHINE_LEFT            //机器标识部分
                | sequence;                            //序列号部分
    }

    /**
     * 产生下一个ID字符串
     *
     * @return
     */
    public static synchronized String nextIdStr() {

        return nextId() + "";
    }

    private static long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private static long getNewstmp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        System.out.println(nextId());
        System.out.println(-1L ^ (-1L << MACHINE_BIT));

    }
}
