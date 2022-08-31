package com.akka.tools.pool;

public class PoolTestObject {
    private long id;
    private long id1;
    private long id2;
    private long id3;
    private boolean id4;
    private long id5;
    private String id6;
    private String id7;
    private String id8;
    private long id9;
    private long id10;
    private String id11;
    private long id12;
    private String id13;
    private long id14;
    private boolean id15;
    private long id16;
    private short id17;
    private double id18;


    public PoolTestObject(long id10) {
        this.id10 = id10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PoolTestObject that = (PoolTestObject) o;

        if (id != that.id) return false;
        if (id1 != that.id1) return false;
        if (id2 != that.id2) return false;
        if (id3 != that.id3) return false;
        if (id4 != that.id4) return false;
        if (id5 != that.id5) return false;
        if (id9 != that.id9) return false;
        if (id10 != that.id10) return false;
        if (id12 != that.id12) return false;
        if (id14 != that.id14) return false;
        if (id15 != that.id15) return false;
        if (id16 != that.id16) return false;
        if (id17 != that.id17) return false;
        if (Double.compare(that.id18, id18) != 0) return false;
        if (id6 != null ? !id6.equals(that.id6) : that.id6 != null) return false;
        if (id7 != null ? !id7.equals(that.id7) : that.id7 != null) return false;
        if (id8 != null ? !id8.equals(that.id8) : that.id8 != null) return false;
        if (id11 != null ? !id11.equals(that.id11) : that.id11 != null) return false;
        return id13 != null ? id13.equals(that.id13) : that.id13 == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (id1 ^ (id1 >>> 32));
        result = 31 * result + (int) (id2 ^ (id2 >>> 32));
        result = 31 * result + (int) (id3 ^ (id3 >>> 32));
        result = 31 * result + (id4 ? 1 : 0);
        result = 31 * result + (int) (id5 ^ (id5 >>> 32));
        result = 31 * result + (id6 != null ? id6.hashCode() : 0);
        result = 31 * result + (id7 != null ? id7.hashCode() : 0);
        result = 31 * result + (id8 != null ? id8.hashCode() : 0);
        result = 31 * result + (int) (id9 ^ (id9 >>> 32));
        result = 31 * result + (int) (id10 ^ (id10 >>> 32));
        result = 31 * result + (id11 != null ? id11.hashCode() : 0);
        result = 31 * result + (int) (id12 ^ (id12 >>> 32));
        result = 31 * result + (id13 != null ? id13.hashCode() : 0);
        result = 31 * result + (int) (id14 ^ (id14 >>> 32));
        result = 31 * result + (id15 ? 1 : 0);
        result = 31 * result + (int) (id16 ^ (id16 >>> 32));
        result = 31 * result + (int) id17;
        temp = Double.doubleToLongBits(id18);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
