package org.fourthline.cling.support.shared;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public abstract class AbstractMap<K, V> implements Map<K, V> {
    Set<K> keySet;
    Collection<V> valuesCollection;

    @Override
    public abstract Set<Entry<K, V>> entrySet();


    public static class SimpleImmutableEntry<K, V> implements Entry<K, V>, Serializable {
        private static final long serialVersionUID = 7138329143949025153L;
        private final K key;
        private final V value;

        public SimpleImmutableEntry(K k, V v) {
            this.key = k;
            this.value = v;
        }

        public SimpleImmutableEntry(Entry<? extends K, ? extends V> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Map.Entry) {
                Entry entry = (Entry) obj;
                K k = this.key;
                if (k != null ? k.equals(entry.getKey()) : entry.getKey() == null) {
                    V v = this.value;
                    if (v == null) {
                        if (entry.getValue() == null) {
                            return true;
                        }
                    } else if (v.equals(entry.getValue())) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }

        @Override
        public int hashCode() {
            K k = this.key;
            int hashCode = k == null ? 0 : k.hashCode();
            V v = this.value;
            return hashCode ^ (v != null ? v.hashCode() : 0);
        }

        public String toString() {
            return this.key + "=" + this.value;
        }
    }


    public static class SimpleEntry<K, V> implements Entry<K, V>, Serializable {
        private static final long serialVersionUID = -8499721149061103585L;
        private final K key;
        private V value;

        public SimpleEntry(K k, V v) {
            this.key = k;
            this.value = v;
        }

        public SimpleEntry(Entry<? extends K, ? extends V> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V v) {
            V v2 = this.value;
            this.value = v;
            return v2;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Map.Entry) {
                Entry entry = (Entry) obj;
                K k = this.key;
                if (k != null ? k.equals(entry.getKey()) : entry.getKey() == null) {
                    V v = this.value;
                    if (v == null) {
                        if (entry.getValue() == null) {
                            return true;
                        }
                    } else if (v.equals(entry.getValue())) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }

        @Override
        public int hashCode() {
            K k = this.key;
            int hashCode = k == null ? 0 : k.hashCode();
            V v = this.value;
            return hashCode ^ (v != null ? v.hashCode() : 0);
        }

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    protected AbstractMap() {
    }

    @Override
    public void clear() {
        entrySet().clear();
    }

    @Override
    public boolean containsKey(Object obj) {
        Iterator<Entry<K, V>> it = entrySet().iterator();
        if (obj != null) {
            while (it.hasNext()) {
                if (obj.equals(it.next().getKey())) {
                    return true;
                }
            }
            return false;
        }
        while (it.hasNext()) {
            if (it.next().getKey() == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object obj) {
        Iterator<Entry<K, V>> it = entrySet().iterator();
        if (obj != null) {
            while (it.hasNext()) {
                if (obj.equals(it.next().getValue())) {
                    return true;
                }
            }
            return false;
        }
        while (it.hasNext()) {
            if (it.next().getValue() == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Map) {
            Map map = (Map) obj;
            if (size() != map.size()) {
                return false;
            }
            try {
                for (Entry<K, V> entry : entrySet()) {
                    K key = entry.getKey();
                    V value = entry.getValue();
                    Object obj2 = map.get(key);
                    if (value == null) {
                        if (obj2 != null || !map.containsKey(key)) {
                            return false;
                        }
                    } else if (!value.equals(obj2)) {
                        return false;
                    }
                }
                return true;
            } catch (ClassCastException | NullPointerException unused) {
            }
        }
        return false;
    }

    @Override
    public V get(Object obj) {
        Iterator<Entry<K, V>> it = entrySet().iterator();
        if (obj != null) {
            while (it.hasNext()) {
                Entry<K, V> next = it.next();
                if (obj.equals(next.getKey())) {
                    return next.getValue();
                }
            }
            return null;
        }
        while (it.hasNext()) {
            Entry<K, V> next2 = it.next();
            if (next2.getKey() == null) {
                return next2.getValue();
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        int i = 0;
        for (Entry<K, V> entry : entrySet()) {
            i += entry.hashCode();
        }
        return i;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new AbstractSet<K>() {
                @Override
                public boolean contains(Object obj) {
                    return AbstractMap.this.containsKey(obj);
                }

                @Override
                public int size() {
                    return AbstractMap.this.size();
                }

                @Override
                public Iterator<K> iterator() {
                    return new Iterator<K>() {
                        Iterator<Entry<K, V>> setIterator;

                        {
                            this.setIterator = AbstractMap.this.entrySet().iterator();
                        }

                        @Override
                        public boolean hasNext() {
                            return this.setIterator.hasNext();
                        }

                        @Override
                        public K next() {
                            return this.setIterator.next().getKey();
                        }

                        @Override
                        public void remove() {
                            this.setIterator.remove();
                        }
                    };
                }
            };
        }
        return this.keySet;
    }

    @Override
    public V put(K k, V v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object obj) {
        Iterator<Entry<K, V>> it = entrySet().iterator();
        if (obj != null) {
            while (it.hasNext()) {
                Entry<K, V> next = it.next();
                if (obj.equals(next.getKey())) {
                    it.remove();
                    return next.getValue();
                }
            }
            return null;
        }
        while (it.hasNext()) {
            Entry<K, V> next2 = it.next();
            if (next2.getKey() == null) {
                it.remove();
                return next2.getValue();
            }
        }
        return null;
    }

    @Override
    public int size() {
        return entrySet().size();
    }

    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(size() * 28);
        sb.append('{');
        Iterator<Entry<K, V>> it = entrySet().iterator();
        while (it.hasNext()) {
            Entry<K, V> next = it.next();
            K key = next.getKey();
            if (key != this) {
                sb.append(key);
            } else {
                sb.append("(this Map)");
            }
            sb.append('=');
            V value = next.getValue();
            if (value != this) {
                sb.append(value);
            } else {
                sb.append("(this Map)");
            }
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Collection<V> values() {
        if (this.valuesCollection == null) {
            this.valuesCollection = new AbstractCollection<V>() {
                @Override
                public int size() {
                    return AbstractMap.this.size();
                }

                @Override
                public boolean contains(Object obj) {
                    return AbstractMap.this.containsValue(obj);
                }

                @Override
                public Iterator<V> iterator() {
                    return new Iterator<V>() {
                        Iterator<Entry<K, V>> setIterator;

                        {
                            this.setIterator = AbstractMap.this.entrySet().iterator();
                        }

                        @Override
                        public boolean hasNext() {
                            return this.setIterator.hasNext();
                        }

                        @Override
                        public V next() {
                            return this.setIterator.next().getValue();
                        }

                        @Override
                        public void remove() {
                            this.setIterator.remove();
                        }
                    };
                }
            };
        }
        return this.valuesCollection;
    }

    protected Object clone() throws CloneNotSupportedException {
        AbstractMap abstractMap = (AbstractMap) super.clone();
        abstractMap.keySet = null;
        abstractMap.valuesCollection = null;
        return abstractMap;
    }
}
