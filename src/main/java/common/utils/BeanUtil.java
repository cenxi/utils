package common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * bean转工具
 * @author duxing
 *
 */
public class BeanUtil extends cn.hutool.core.bean.BeanUtil {


	/**
	 * 新增一个列表转接口
	 * @param <U>
	 * @param <T>
	 * @param uList
	 * @param cls
	 * @return
	 */
	public static <U, T> List<T> toBean(List<U> uList, Class<T> cls) {
		if (uList == null || uList.isEmpty()) {
			return new ArrayList<>();
		}
		List<T> tList = new ArrayList<>(uList.size());
		uList.forEach(e -> {
			tList.add(toBean(e, cls));
		});
		return tList;
	}

	public static List<Map<?,?>> beanListToMapList(List<?> metaInfoList) {
		if(metaInfoList == null || metaInfoList.isEmpty()) {
			return new ArrayList<>();
		}
		List<Map<?,?>> list = new ArrayList<Map<?,?>>(metaInfoList.size());
		metaInfoList.forEach(e->{
			list.add(BeanUtil.beanToMap(e));
		});
		return list;
	}

	public static boolean isWrapperPrimitive(Object o) {
		o.getClass().isPrimitive();
		boolean isPrimitive = false;
		try {
			isPrimitive = ((Class) (o.getClass().getField("TYPE").get(null))).isPrimitive();
		} catch (Exception e) {

		}
		return isPrimitive;
	}

	public static void main(String[] args) {
		long l = 5;
		System.out.println();
		boolean b = isWrapperPrimitive(l);
		System.out.println(b);
	}
	
	

}
