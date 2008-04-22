//package org.otherobjects.cms.views;
//
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.apache.velocity.context.Context;
//
///**
// * {@link ViewToolsService } Implementation that is supposed to get spring configured with a map of classes and beans that it can then 
// * use to populate a Velocity context.
// * 
// * @author joerg
// *
// */
//public class ViewToolsServiceImpl implements ViewToolsService {
//	
//	private Map<String, Class<?>> toolsMap;
//	private Map<String, Object> beansMap;
//	
//	public Map<String, Class<?>> getToolsMap() {
//		return toolsMap;
//	}
//
//	public void setToolsMap(Map<String, Class<?>> toolsMap) {
//		this.toolsMap = toolsMap;
//	}
//
//	public Map<String, Object> getBeansMap() {
//		return beansMap;
//	}
//
//	public void setBeansMap(Map<String, Object> beansMap) {
//		this.beansMap = beansMap;
//	}
//
//	public void populateContext(Context context) throws Exception {
//		for(Entry<String, Class<?>> entry: toolsMap.entrySet())
//		{
//			context.put(entry.getKey(), entry.getValue().newInstance());
//		}
//		
//		for(Entry<String, Object> entry: beansMap.entrySet())
//		{
//			context.put(entry.getKey(), entry.getValue());
//		}
//		
//	}
//	
//	
//
//}
