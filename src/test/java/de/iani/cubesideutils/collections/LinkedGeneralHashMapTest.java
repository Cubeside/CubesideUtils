package de.iani.cubesideutils.collections;

public class LinkedGeneralHashMapTest extends GeneralHashMapTest {

    public LinkedGeneralHashMapTest() {
        super();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void init() {
        this.initMaps((hasher, equality) -> new LinkedGeneralHashMap(hasher, equality));
    }

}
