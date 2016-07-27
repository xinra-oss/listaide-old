package com.xinra.listaide.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/** 
 * Creates dynamic proxies instead of using actual implementations of DTO interfaces.
 * The proxy manages all properties in a map according to the getter and setter names.
 * <i>This may not be suitable for large-scale production applications!</i>
 * 
 * @author erikhofer
 */
public class DynamicProxyDTOFactory implements DTOFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DTO> T createDTO(Class<T> type) {
		return (T) Proxy.newProxyInstance(
		        type.getClassLoader(),
		        new Class<?>[] { type },
		        new DynamicProxy());
	}
	
	private static class DynamicProxy implements InvocationHandler {
		
		private Map<String, Object> properties = new HashMap<>();

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if(method.getName().startsWith("set")) {
				properties.put(method.getName().substring(3), args[0]);
				return null;
			} else if(method.getName().startsWith("get")) {
				return properties.get(method.getName().substring(3));
			} else if(method.getName().startsWith("is")) {
				return properties.get(method.getName().substring(2));
			} else {
				return method.invoke(this, args); //for equals, hashCode etc.
			}
		}	
	}

}
