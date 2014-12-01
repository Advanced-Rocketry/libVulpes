package zmaster587.libVulpes.util;

import java.util.Map.Entry;

public class SingleEntry<K, V> implements Entry<K, V> {

	K key;
	V value;
	
	public SingleEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public K getKey() {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public V getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public V setValue(V value) {
		V ret = this.value;
		this.value = value;
		return ret;
	}

}
