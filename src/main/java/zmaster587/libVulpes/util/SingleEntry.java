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
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V ret = this.value;
		this.value = value;
		return ret;
	}
	@Override
	public boolean equals(Object paramObject) {
		if(paramObject instanceof SingleEntry<?, ?>) {
			return ((SingleEntry) paramObject).getKey().equals(this.getKey()) && ((SingleEntry) paramObject).getValue().equals(this.getValue());
		}
		return super.equals(paramObject);
	}

}
