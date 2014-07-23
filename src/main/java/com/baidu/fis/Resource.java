package com.baidu.fis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class Resource {
	private static final Logger logger = LoggerFactory
			.getLogger(Resource.class);

	public static final String CONTEXT_ATTR_NAME = "com.baidu.fis.resource";
	public static final String STYLE_PLACEHOLDER = "<!--FIS_STYLE_PLACEHOLDER-->";
	public static final String SCRIPT_PLACEHOLDER = "<!--FIS_SCRIPT_PLACEHOLDER-->";

	public static enum ScriptPoolRenderOrder {
		important, normal, optional;
	}

	private String mapDir;
	@SuppressWarnings("rawtypes")
	private Map<String, Map> mapJsonMap;
	private Map<String, String> loaded;
	private Map<String, Set<String>> resourceTypeCollection;
	private ScriptPoolRenderOrder[] scriptPoolRenderOrder = {
			ScriptPoolRenderOrder.important, ScriptPoolRenderOrder.normal,
			ScriptPoolRenderOrder.optional };

	private Map<String, StringBuilder> scriptPool;

	public Boolean debug = false;

	public void setMapDir(String dir) {
		this.mapDir = dir;
	}

	@SuppressWarnings("rawtypes")
	public Resource() {
		this.mapJsonMap = new HashMap<String, Map>();
		this.loaded = new HashMap<String, String>();
		this.resourceTypeCollection = new HashMap<String, Set<String>>();
		this.scriptPool = new HashMap<String, StringBuilder>();
	}

	public Resource(String dir) {
		this();
		this.setMapDir(dir);
	}

	private static ConcurrentHashMap<String, MapJsonCache> mapJsonCached = 
		new ConcurrentHashMap<>();

	static class MapJsonCache {

		String key;
		long timestamp;
		@SuppressWarnings("rawtypes")
		Map<String, Map> data;

	}

	@SuppressWarnings("rawtypes")
	private static Map<String, Map> loadMapJson(File file)
			throws UnsupportedEncodingException, FileNotFoundException {
		String canonicalPath = null;
		try {
			canonicalPath = file.getCanonicalPath();
		} catch (IOException e) {
			logger.warn("getCanonicalPath error", e);
		}

		if (canonicalPath == null) {
			return loadMapJsonFromFile(file);
		}

		MapJsonCache cache = mapJsonCached.get(canonicalPath);
		if (cache == null || cache.timestamp != file.lastModified()) {
			cache = new MapJsonCache();
			cache.key = canonicalPath;
			cache.timestamp = file.lastModified();
			cache.data = loadMapJsonFromFile(file);
			mapJsonCached.put(cache.key, cache);
		}
		return cache.data;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static Map<String, Map> loadMapJsonFromFile(File file)
			throws UnsupportedEncodingException, FileNotFoundException {
		logger.info("load map.json from file : {}", file);
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "utf-8");
		Gson gson = new Gson();
		Map<String, Map> mapJson = gson.fromJson(reader, Map.class);
		return mapJson;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Map> getMapJson(String id) throws FileNotFoundException,
			UnsupportedEncodingException {
        String namespace = "__global__";
        int pos = id.indexOf(':');
        if (pos != -1) {
            namespace = id.substring(0, pos);
        }
        if(!mapJsonMap.containsKey(namespace)){
            String filename = namespace.equals("__global__") ? "map.json" : namespace + "-map.json";
            File file = new File(mapDir + "/" + filename);
			logger.debug("use map.json file : {}", file);
			Map<String, Map> mapJson = loadMapJson(file);
            mapJsonMap.put(namespace, mapJson);
        }
        return mapJsonMap.get(namespace);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes"})
	public String require(String id) throws FileNotFoundException,
			UnsupportedEncodingException {
        String uri = loaded.get(id);
        if(uri != null){
			logger.debug("require : {} {}", id, uri);
            return uri;
        } 

		Map<String, Map> mapJson = this.getMapJson(id);
		Map<String, Map> res = mapJson.get("res");
		Map<String, Object> info = res.get(id);
		if(info == null){
			logger.warn("require missing resource : {}", id);
		} else {
			String pkg = (String) info.get("pkg");
			if(!debug && pkg != null){
				res = mapJson.get("pkg");
				info = res.get(pkg);
				uri = (String) info.get("uri");
				if(info.containsKey("has")){
					List has = (List)info.get("has");
					for(Object obj:has){
						loaded.put((String) obj, uri);
					}
				}
			} else {
				uri = (String) info.get("uri");
				loaded.put(id, uri);
			}
			logger.debug("require : {} {}", id, uri);

			if(info.containsKey("deps")){
				List<String> deps = (List<String>)info.get("deps");
				logger.info("get deps {} {}", id, deps);
				String parentPath = null;
				for(String dep:deps){
					if (dep.startsWith("./") || dep.startsWith("../")) {
						if (parentPath == null)
							parentPath = new File(id).getParent();
						dep = FilenameUtils.concat(parentPath, dep);
					}
					this.require((String) dep);
				}
			}

			String type = (String) info.get("type");
			Set<String> list = resourceTypeCollection.get(type);
			if(list == null){
				list = new LinkedHashSet<String>();
				resourceTypeCollection.put(type, list);
			}
			list.add(uri);
		}
        return uri;
    }

	public String justGetUrl(String id) throws FileNotFoundException,
			UnsupportedEncodingException {
        String uri = loaded.get(id);
        if(uri != null){
			logger.debug("justGetUrl : {} {}", id, uri);
            return uri;
        } 

		Map<String, Map> mapJson = this.getMapJson(id);
		Map<String, Map> res = mapJson.get("res");
		Map<String, Object> info = res.get(id);
		if(info == null){
			logger.warn("justGetUrl missing resource : {}", id);
		} else {
			String pkg = (String) info.get("pkg");
			if(!debug && pkg != null){
				res = mapJson.get("pkg");
				info = res.get(pkg);
				uri = (String) info.get("uri");
			} else {
				uri = (String) info.get("uri");
			}
			logger.debug("justGetUrl : {} {}", id, uri);
		}
        return uri;
	}

    public String getMapDir() {
        return mapDir;
    }

    public void reset() {
        mapJsonMap.clear();
        loaded.clear();
        resourceTypeCollection.clear();
        scriptPool.clear();
    }

    public void addScriptPool(String code, String type) {
        StringBuilder pool = scriptPool.get(type);
        if (pool == null) {
            pool = new StringBuilder();
            scriptPool.put(type, pool);
        }
        pool.append("<script type=\"text/javascript\">!function(){");
        pool.append(code);
        pool.append("}()</script>\n");
    }

    public void addScriptPool(String code) {
        addScriptPool(code, "normal");
    }

    public void setScriptPoolRenderOrder(ScriptPoolRenderOrder[] order) {
        scriptPoolRenderOrder = order;
    }

    public String renderScriptPool() {
        StringBuilder code = new StringBuilder();
        for (int i = 0, len = scriptPoolRenderOrder.length; i < len; i++) {
            String type = scriptPoolRenderOrder[i].name();
            StringBuilder pool = scriptPool.get(type);
            if (pool != null) {
                code.append(pool);
            }
        }
        scriptPool = new HashMap<String, StringBuilder>();
        return code.toString();
    }
    
    public String render(String type){
        return render(type, true);
    }
    
    public String replace(String html){
        html = html.replace(Resource.STYLE_PLACEHOLDER, this.render("css"));
        html = html.replace(Resource.SCRIPT_PLACEHOLDER, this.render("js") + this.renderScriptPool());
        return html;
    }
    
    public String render(String type, Boolean reset){
        Set<String> list = this.getCollection(type);
        StringBuffer buffer = new StringBuffer();
        if(list != null){
            if(type.equals("js")){
                for(String uri:list){
                    buffer.append("<script type=\"text/javascript\" src=\"").append(uri).append("\"></script>\n");
                }
            } else if(type.equals("css")){
                for(String uri:list){
                    buffer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(uri).append("\"/>\n");
                }
            }
            if(reset){
                list.clear();
            }
        }
        return buffer.toString();
    }
    
    public Set<String> getCollection(String type){
        return resourceTypeCollection.get(type);
    }

}
