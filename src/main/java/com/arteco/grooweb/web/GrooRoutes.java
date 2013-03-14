package com.arteco.grooweb.web;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class GrooRoutes {

	private Map<String, String> routes;

	private Map<String, Map<String, String>> routesByController = new HashMap<String, Map<String, String>>();
	private Map<String, Long> mods = new HashMap<String, Long>();

	public Map<String, String> getAllRoutes() {
		if (routes == null) {
			routes = new HashMap<String, String>();
			for (Entry<String, Map<String, String>> controllersMap : routesByController.entrySet()) {
				for (Entry<String, String> urls : controllersMap.getValue().entrySet()) {
					if (routes.containsKey(urls.getKey())) {
						throw new IllegalArgumentException("La url " + urls.getKey() + " mapeada en " + controllersMap.getKey() + " ya existe");
					}
					routes.put(urls.getKey(), urls.getValue());
				}
			}
		}
		return routes;
	}

	public Long getLasModification(File controllerFile) {
		return mods.get(controllerFile.getAbsolutePath());
	}

	public void updateRoutes(File controllerFile, Map<String, String> controllerUrls) {
		routesByController.put(controllerFile.getAbsolutePath(), controllerUrls);
		mods.put(controllerFile.getAbsolutePath(), controllerFile.lastModified());
		routes = null;
	}

	public Map<String, String> update(Collection<File> allFindedFiles) {
		Set<File> toRemove = new HashSet<File>();
		for (File f : allFindedFiles) {
			if (!mods.containsKey(f)) {
				toRemove.add(f);
			}
		}
		for (File f : toRemove) {
			mods.remove(f);
			routesByController.remove(f);
		}
		return getAllRoutes();
	}

}
