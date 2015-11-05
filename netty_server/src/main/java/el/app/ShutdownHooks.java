package el.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface ShutdownHooks {
	public static Map<Integer, List<Runnable>> preClose = new HashMap<>(); // Collections.synchronizedList(new ArrayList<>());
	public static Map<Integer, List<Runnable>> postClose = new HashMap<>(); // Collections.synchronizedList(new ArrayList<>());

	default public void addPreClose(Runnable run){
		Integer hash = this.hashCode();
		List<Runnable> list = preClose.get(hash);
		if (list == null){
			list = Collections.synchronizedList(new ArrayList<>());
			preClose.put(hash, list);
		}
		list.add(run);
	}

	default public void addPostClose(Runnable run){
		Integer hash = this.hashCode();
		List<Runnable> list = postClose.get(hash);
		if (list == null){
			list = Collections.synchronizedList(new ArrayList<>());
			postClose.put(hash, list);
		}
		list.add(run);
	}

	public void closeInside();

	default public void close(){
		this.preClose();
		this.close();
		this.postClose();
		removeSelf();
	}

	default void removeSelf(){
		Integer hash = this.hashCode();
		preClose.remove(hash);
		postClose.remove(hash);
	}

	default public void preClose(){
		for (Runnable run : preClose.get(this.hashCode())){
			run.run();
		}
	}

	default public void postClose(){
		for (Runnable run : postClose.get(this.hashCode())){
			run.run();
		}
	}

}
